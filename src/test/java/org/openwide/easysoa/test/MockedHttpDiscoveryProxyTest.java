package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwide.easysoa.test.monitoring.apidetector.UrlMock;
import org.ow2.frascati.util.FrascatiException;

import com.openwide.easysoa.esperpoc.registration.NuxeoRegistrationService;

/**
 * Complete test suite of HTTP Discovery Proxy
 * - Starts FraSCATi and the HTTP Discovery Proxy
 * - Test the Discovery mode for REST requests (OK)
 * - Test the Discovery mode for SOAP requests (OK)
 * - Test the Validated mode for REST requests (TODO)
 * - Test the validated mode for SOAP requests (TODO)
 *
 * @author jguillemotte
 *
 */
public class MockedHttpDiscoveryProxyTest extends AbstractProxyTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(MockedHttpDiscoveryProxyTest.class.getName());    
    
	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws FrascatiException, InterruptedException 
	 */
    @BeforeClass
	public final static void setUp() throws FrascatiException, InterruptedException {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   // Clean Nuxeo registery
	   //cleanNuxeoRegistery();
	   // Start fraSCAti
	   startFraSCAti();
	   // Start HTTP Proxy
	   startHttpProxy();
	   // Start services mock
	   startMockServices();
    }
	
	/**
	 * 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	public final void testRestDiscoveryMode() throws Exception {
		logger.info("Test REST Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		logger.info("Set proxy mode to Discovery");
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/setMonitoringMode/discovery"), responseHandler);
		assertEquals("Monitoring mode set", resp);
		//logger.info("mode setting : " + resp);
		
		// Start a new run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/startNewRun/discoveryTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'discoveryTestRun' started !", resp);
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);		
		
		// Send Http Rest requests
		// TODO 2 Separated tests : one with real services, one with mock services
		// TODO : A twitter mock service because requests on Twitter api are limited to 150 per hour
		// TODO : A Nuxeo mock to avoid to have a started Nuxeo to launch the test
		UrlMock urlMock = new UrlMock();
		for(String url : urlMock.getTwitterUrlData("localhost:8081")){
			logger.info("Request send : " + url);			
			HttpUriRequest httpUriRequest = new HttpGet(url);
			try {
				String response = httpProxyClient.execute(httpUriRequest, responseHandler);
				logger.debug("response : " + response);
			}
			catch (HttpResponseException ex){
				logger.info("Error occurs, status code : " + ex.getStatusCode() + ", message : " + ex.getMessage(), ex);
			}
		}

		// Stop the run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/stopCurrentRun"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);
		
		// Wait for the proxy register the discovered services in Nuxeo
		logger.info("Wait for the proxy finish to register services ...");
		Thread.sleep(5000);
		
		// Check discovery results
		// Send a request to Nuxeo to check that services are well registered
		logger.info("Check that registration is correct in Nuxeo");
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND dc:title = 'show' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.debug("Nuxeo response : " + nuxeoResponse);
		try{
			// Get the property JSON Object
			String entries = new JSONObject(nuxeoResponse).getString("entries");
			String firstEntry = new JSONArray(entries).getJSONObject(0).toString();
			JSONObject jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
			// Do to same thing but less readable
			//JSONObject jsonObject = new JSONObject(new JSONObject(new JSONArray(new JSONObject(nuxeoResponse).getString("entries")).getJSONObject(0).toString()).getString("properties"));
			assertEquals("http://localhost:8081/1/users/show", jsonObject.get("serv:url"));			
		}
		catch(Exception ex){
			logger.info("An error occurs while reading the nuxeo response", ex);
		}

		logger.info("Test REST Discovery mode ended successfully !");
	}

	// TODO tests for validated mode
	// Set Validated mode
	// Send http rest requests
	
	/**
	 * 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	public final void testSoapDiscoveryMode() throws Exception {
		logger.info("Test SOAP Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();		
		
		/*
		// Need to use the proxy
		// Doesn't work because an infinite loop is triggered in the proxy
		ProxySelector.setDefault(new SoapClientProxySelector());		
		// Set the WSDL references
		String serviceUrl = "http://localhost:8085/meteo?wsdl";
		String TNS = "http://meteomock.mock.test.easysoa.openwide.org/";		
		QName serviceName = new QName(TNS, "MeteoMock");
		QName portName = new QName(TNS, "MeteoMockPort");
		Service jaxwsService = Service.create(new URL(serviceUrl), serviceName);
		Dispatch<SOAPMessage> disp = jaxwsService.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
		// Get the XML request to send
		FileInputStream fis = new FileInputStream(new File("src/test/resources/meteoMockMessages/meteoMockRequest.xml"));
	    // Send the message
		SOAPMessage reqMsg = MessageFactory.newInstance().createMessage(null, fis);
	    assertNotNull(reqMsg);
		SOAPMessage response = disp.invoke(reqMsg);
		logger.debug("Response : " + response.getSOAPBody().getTextContent().trim());		
		// Wait for the proxy finish to register services
		logger.info("Wait for the proxy finish to register services ...");
		Thread.sleep(5000);
		// Send a request to Nuxeo to check the registration
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);		
		logger.debug("Nuxeo response : " + nuxeoResponse);
		*/
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		logger.info("Set proxy mode to Discovery");
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/setMonitoringMode/discovery"), responseHandler);
		assertEquals("Monitoring mode set", resp);
		//logger.info("mode setting : " + resp);
		
		// Start a new run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/startNewRun/discoveryTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'discoveryTestRun' started !", resp);		
		
		FileInputStream fis = new FileInputStream(new File("src/test/resources/meteoMockMessages/meteoMockRequest.xml"));
		BasicHttpEntity soapRequest = new BasicHttpEntity();
		soapRequest.setContent(fis);
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);		
		
		HttpPost httpPost = new HttpPost("http://localhost:8085/meteo");
		httpPost.setEntity(soapRequest);
		//httpPost.setHeader("Content-Type", "text/xml");
		try {
			String response = httpProxyClient.execute(httpPost, responseHandler);		
			logger.info(response);
		}
		catch(Exception ex){
			logger.error("Error occurs during the soap message test", ex);
		}
		
		// Stop the run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/stopCurrentRun"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);		
		
		logger.info("Wait for the proxy finish to register WSDL service ...");
		Thread.sleep(1000);		
		logger.info("Check that registration is correct in Nuxeo");
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:primaryType = 'Service' AND dc:title = 'meteo'";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.info("Nuxeo response : " + nuxeoResponse);
		
		try{
			// Get the property JSON Object
			String entries = new JSONObject(nuxeoResponse).getString("entries");
			String firstEntry = new JSONArray(entries).getJSONObject(0).toString();
			JSONObject jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
			assertEquals("http://localhost:8085/meteo", jsonObject.get("serv:url"));			
		}
		catch(Exception ex){
			logger.info("An error occurs while reading the nuxeo response", ex);
		}		

		logger.info("Test SOAP Discovery mode ended successfully !");
	}
	
	/**
	 * Clean Nuxeo registery before to launch the tests
	 * TODO Find an other method to clean because it is not possible with using only NXQL 
	 */
	private static void cleanNuxeoRegistery(){
		String nuxeoQuery = "DELETE * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:primaryType = 'Service' AND ecm:primaryType = 'ServiceAPI'";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.info("Delete response : " + nuxeoResponse);
	}
	
	
	
}
