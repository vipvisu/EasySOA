/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpResponseException;
import org.apache.log4j.Logger;
import org.easysoa.message.Header;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configurator.ProxyConfigurator;
import org.easysoa.proxy.core.api.exchangehandler.HandlerManager;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.properties.ProxyPropertyManager;
import org.easysoa.proxy.core.api.util.RequestForwarder;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * HTTP Proxy
 *
 * Work on the top of FraSCAti
 *
 * Does : - Detect GET WSDL request messages, then register the WSDL in Nuxeo
 * Easysoa model - Detect GET REST request messages with parameters but no
 * dynamic path parts, then register the REST service in Nuxeo Easysoa model -
 * Detect GET REST request messages with dynamic URL (in development) - Detect
 * POST SOAP request messages, check if a WSDL is associated with a message and
 * register it in Nuxeo Easysoa model
 *
 * @author jguillemotte
 */
@SuppressWarnings("serial")
@Scope("composite")
public class HttpDiscoveryProxy extends HttpServlet {

    // Reference on handler manager
    @Reference
    HandlerManager handlerManager;
    // Property manager
    public static PropertyManager propertyManager;
    // Port the proxy use (used in proxy loop detection).
    @Property
    public int proxyPort;
    // Message forward Timeouts
    @Property
    public int forwardHttpConnexionTimeoutMs;
    @Property
    public int forwardHttpSocketTimeoutMs;
    // proxyID - automatically generated - used for exchange store naming
    // To pass this information to the runManager => pass it to handlerManager/MessagerecordHandler/runManager/ProxyFileStore
    // Another way to pass this data to runManager ??
    // Or just a prefix/suffix (user, environment, ...) from outside with a part generated directly in the proxy itself (timestamp, date ...)
    //@Property
    //public String proxyId;
    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(HttpDiscoveryProxy.class.getName());

    /**
     * Log system initialization
     */
    static {
        ProxyConfigurator.configure(HttpDiscoveryProxy.class);
    }

    /**
     * Constructor
     */
    public HttpDiscoveryProxy() {
        // Loading property manager
        try {
            propertyManager = new ProxyPropertyManager();
        } catch (Exception ex) {
            logger.warn("Error when loading the property manager, trying another method");
            try {
                propertyManager = new ProxyPropertyManager(ProxyPropertyManager.PROPERTY_FILE_NAME, this.getClass().getResourceAsStream("/" + ProxyPropertyManager.PROPERTY_FILE_NAME));
            } catch (Exception exc) {
                logger.error("Error when loading the proxy property manager", exc);
            }
        }
    }

    /* (non-Javadoc)
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /* (non-Javadoc)
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doTrace(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doHttpMethod(request, response);
    }

    /**
     * Get the HTTP request, build an exchange record and send it to the
     * discovery API, forward and send back the response
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException, IOException If a problem occurs
     */
    public final void doHttpMethod(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("------------------");
        logger.debug("Method: " + request.getMethod());
        logger.debug("RequestURI : " + request.getRequestURI());
        logger.debug("Query : " + request.getQueryString());
        logger.debug("server: " + request.getServerName());
        logger.debug("port: " + request.getServerPort());
        logger.debug("request URL: " + request.getRequestURL());
        PrintWriter respOut = response.getWriter();
        // re-route request to the provider and send the response to the consumer
        try {
            // Detect infinite request loop (proxy send a request to itself)
            infiniteLoopDetection(request);
            // Listening the message
            InMessage inMessage = new InMessage(request);
            OutMessage outMessage = forward(inMessage, response);
            this.handlerManager.handleMessage(inMessage, outMessage);
        } catch (HttpResponseException rex) {
            // error in the actual server : return it back to the client
            // attempting to reset response
            response.reset();
            response.setStatus(rex.getStatusCode());

            // filling response :
            // TODO get expected content type from request
            // TODO also handle bad result content ex. "Internal Server Error" from demo MS WS
            response.setContentType("text/xml");
            respOut.write(rex.getMessage());
            respOut.close();

            // debugging
            logger.debug("httpProxy : error in proxied server : "
                    + rex.getStatusCode() + " - " + rex.getMessage());
        } catch (Throwable ex) {
            // error in the internals of the httpProxy : building & returning it
            logger.error("An error occurs in doHttpMethod method.", ex);

            // attempting to reset response
            response.reset();
            response.setStatus(500);

            // filling response
            HashMap<String, Object> headers = new HashMap<String, Object>();
            if (headers.containsKey("SOAPAction")) {
                response.setContentType("text/xml");
                // Returns SOAP Fault
                logger.debug("Returns a SOAP Fault");
                respOut.print(this.buildSoapFault(ex.getMessage()));
            } else {
                // Returns HTML response
                response.setContentType("text/html");
                logger.debug("returns an html response");
                respOut.println("<html><body>HTTP Discovery Proxy : unknown error in proxy, doHttpMethod :<br/>");
                respOut.println(ex.getMessage());
                ex.printStackTrace(respOut);
                respOut.println("</body></html>");
            }
        } finally {
            logger.debug("--- Closing response flow");
            respOut.close();
        }
    }

    /**
     * Send back the request to the original recipient and get the response
     *
     * @param response
     * @param request HTTP Request
     * @param response HTTP response
     * @return A message object to be used by the discovery API
     * @throws Exception If a problem occurs during the forward
     */
    private OutMessage forward(InMessage inMessage, HttpServletResponse response) throws Exception {
        PrintWriter respOut = response.getWriter();

        // Request forwarder
        RequestForwarder forwarder = new RequestForwarder();
        forwarder.setForwardHttpConnexionTimeoutMs(forwardHttpConnexionTimeoutMs);
        forwarder.setForwardHttpSocketTimeoutMs(forwardHttpSocketTimeoutMs);
        // Use HttpRetryHandler default value for retry
        forwarder.setRetryHandler(new HttpRetryHandler());

        // forward the message
        OutMessage outMessage = forwarder.send(inMessage);

        // return response :

        // copy headers
        for (Header header : outMessage.getHeaders().getHeaderList()) {
            response.setHeader(header.getName(), header.getValue());
        }

        // copy response
        logger.debug("clientResponse : " + outMessage.getMessageContent().getRawContent());
        respOut.write(outMessage.getMessageContent().getRawContent());
        respOut.close();

        return outMessage;
    }

    /**
     * Builds an XML SoapFault
     *
     * @param error Error to integrate to the returned XML SOAP Fault
     * @return A XML <code>String</code> representing a SOAP fault
     */
    private String buildSoapFault(String error) {
        StringBuilder fault = new StringBuilder();
        fault.append("<?xml version=\"1.0\"?>");
        fault.append("<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">");
        fault.append("<SOAP-ENV:Body><SOAP-ENV:Fault>");
        fault.append("<faultcode>SOAP-ENV:Client</faultcode>");
        fault.append("<faultstring>");
        fault.append(error.replace("\"", "\\\""));
        fault.append("</faultstring>");
        fault.append("</SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>");
        return fault.toString();
    }

    /**
     * Detect if the request call directly the proxy, if true, an exception is
     * throw
     *
     * @param request The request to check
     * @throws Exception If an infinite loop is detected
     */
    private void infiniteLoopDetection(HttpServletRequest request) throws Exception {
        // Get the server name and port to detect the loop
        if (proxyPort == request.getServerPort()
                && ("localhost".equalsIgnoreCase(request.getServerName())
                || "127.0.0.1".equals(request.getServerName())
                || InetAddress.getLocalHost().getHostName().equals(request.getServerName()))) {
            throw new Exception("Request on proxy itself detected on port " + proxyPort + " ! Infinite loop aborted !");
        }
    }
}
