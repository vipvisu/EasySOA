package org.easysoa.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.EasySOAConstants;
import org.easysoa.rest.scraping.ScraperRest;
import org.easysoa.test.EasySOACoreFeature;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;

@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Deploy({
    "org.easysoa.registry.rest"
})
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@LocalDeploy({"org.easysoa.registry.rest:OSGI-INF/login-contrib.xml",
    "org.easysoa.registry.rest:OSGI-INF/ServiceScraperComponent.xml",
    "org.easysoa.registry.rest:OSGI-INF/scraping-contrib.xml"})
public class ScraperTest {

    static final Log log = LogFactory.getLog(ScraperTest.class);
    
    @Test
    public void testScraper() throws Exception {
        
        // Make request
        ScraperRest scraper = new ScraperRest();
        Object obj = scraper.doGet(mockUriInfo("http://ec2-79-125-45-33.eu-west-1.compute.amazonaws.com:8080/services/"));
        
        // Check result data
        Assert.assertNotNull(obj);
        JSONObject json = new JSONObject(obj.toString());
        JSONObject foundLinks = (JSONObject) json.get("foundLinks");
        Assert.assertNotNull(foundLinks);
        Iterator<?> it = foundLinks.keys();
        
        // Output found links
        while (it.hasNext()) {
            String linkName = (String) it.next();
            log.info("Found service: "+linkName);
            log.info(foundLinks.getString(linkName));
        }
        //log.info(json);
        
    }
    
    private UriInfo mockUriInfo(String uri) throws URISyntaxException {
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI("http://127.0.0.1:8080/nuxeo/site/easysoa/wsdlscraper/"+uri));
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://127.0.0.1:8080/nuxeo/site/"));
        return uriInfo;
    }

}