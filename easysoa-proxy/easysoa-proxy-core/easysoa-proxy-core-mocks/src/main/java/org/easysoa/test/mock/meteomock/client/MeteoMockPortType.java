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

package org.easysoa.test.mock.meteomock.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.4.1
 * 2012-01-06T18:35:22.656+01:00
 * Generated source version: 2.4.1
 * 
 */
@WebService(targetNamespace = "http://meteomock.mock.test.easysoa.openwide.org/", name = "MeteoMockPortType")
@XmlSeeAlso({ObjectFactory.class})
public interface MeteoMockPortType {

    @WebResult(name = "return", targetNamespace = "http://meteomock.mock.test.easysoa.openwide.org/")
    @RequestWrapper(localName = "getTomorrowForecast", targetNamespace = "http://meteomock.mock.test.easysoa.openwide.org/", className = "org.easysoa.test.mock.meteomock.client.GetTomorrowForecast")
    @WebMethod
    @ResponseWrapper(localName = "getTomorrowForecastResponse", targetNamespace = "http://meteomock.mock.test.easysoa.openwide.org/", className = "org.easysoa.test.mock.meteomock.client.GetTomorrowForecastResponse")
    public java.lang.String getTomorrowForecast(
        @WebParam(name = "arg0", targetNamespace = "http://meteomock.mock.test.easysoa.openwide.org/")
        java.lang.String arg0
    );
}
