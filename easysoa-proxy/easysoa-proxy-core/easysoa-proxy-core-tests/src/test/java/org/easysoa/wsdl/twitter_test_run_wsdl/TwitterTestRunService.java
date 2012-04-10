/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.wsdl.twitter_test_run_wsdl;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * WSDL File for Twitter_test_run Service
 *
 * This class was generated by Apache CXF 2.4.1
 * 2012-01-25T10:02:41.948+01:00
 * Generated source version: 2.4.1
 * 
 */
@WebServiceClient(name = "Twitter_test_run_Service", 
                  wsdlLocation = "http://localhost:8092/runManager/target/Twitter_test_run?wsdl",
                  targetNamespace = "http://www.easysoa.org/wsdl/Twitter_test_run.wsdl") 
public class TwitterTestRunService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.easysoa.org/wsdl/Twitter_test_run.wsdl", "Twitter_test_run_Service");
    public final static QName TwitterTestRunPort = new QName("http://www.easysoa.org/wsdl/Twitter_test_run.wsdl", "Twitter_test_run_Port");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8090/runManager/target/Twitter_test_run?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(TwitterTestRunService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:8092/runManager/target/Twitter_test_run?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public TwitterTestRunService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public TwitterTestRunService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TwitterTestRunService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public TwitterTestRunService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE/*, features*/);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public TwitterTestRunService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE/*, features*/);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public TwitterTestRunService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName/*, features*/);
    }

    /**
     *
     * @return
     *     returns TwitterTestRunPortType
     */
    @WebEndpoint(name = "Twitter_test_run_Port")
    public TwitterTestRunPortType getTwitterTestRunPort() {
        return super.getPort(TwitterTestRunPort, TwitterTestRunPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TwitterTestRunPortType
     */
    @WebEndpoint(name = "Twitter_test_run_Port")
    public TwitterTestRunPortType getTwitterTestRunPort(WebServiceFeature... features) {
        return super.getPort(TwitterTestRunPort, TwitterTestRunPortType.class, features);
    }

}
