package org.talend.services.esb.locator.v1;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.1
 * 2012-10-18T16:20:08.099+02:00
 * Generated source version: 2.4.1
 * 
 */
@WebServiceClient(name = "LocatorService", 
                  wsdlLocation = "file:LocatorService.wsdl",
                  targetNamespace = "http://www.talend.org/services/esb/locator/v1") 
public class LocatorService_Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.talend.org/services/esb/locator/v1", "LocatorService");
    public final static QName LocatorServiceSoap = new QName("http://www.talend.org/services/esb/locator/v1", "LocatorServiceSoap");
    static {
        URL url = null;
        try {
            url = new URL("file:LocatorService.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(LocatorService_Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:LocatorService.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public LocatorService_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public LocatorService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public LocatorService_Service() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public LocatorService_Service(WebServiceFeature ... features) {
        //super(WSDL_LOCATION, SERVICE, features);
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public LocatorService_Service(URL wsdlLocation, WebServiceFeature ... features) {
        //super(wsdlLocation, SERVICE, features);
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public LocatorService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        //super(wsdlLocation, serviceName, features);
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns LocatorService
     */
    @WebEndpoint(name = "LocatorServiceSoap")
    public LocatorService getLocatorServiceSoap() {
        return super.getPort(LocatorServiceSoap, LocatorService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns LocatorService
     */
    @WebEndpoint(name = "LocatorServiceSoap")
    public LocatorService getLocatorServiceSoap(WebServiceFeature... features) {
        return super.getPort(LocatorServiceSoap, LocatorService.class, features);
    }

}