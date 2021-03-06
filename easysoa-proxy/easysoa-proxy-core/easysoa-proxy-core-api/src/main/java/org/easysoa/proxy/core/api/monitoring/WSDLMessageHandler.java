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

package org.easysoa.proxy.core.api.monitoring;

import org.apache.log4j.Logger;
import org.easysoa.message.QueryParam;
import org.easysoa.proxy.core.api.esper.EsperEngine;
import org.easysoa.proxy.core.api.monitoring.soa.Node;
import org.easysoa.proxy.core.api.monitoring.soa.Service;
import org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.Exchange.ExchangeType;

/**
 * Handler for WSDL file requests
 * Detect in the request query string if there is the substring '?wsdl'. If true, a new service is created in Nuxeo.
 *
 * @author jguillemotte
 *
 */
public class WSDLMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(WSDLMessageHandler.class.getName());
	
	@Override
	public boolean isOkFor(ExchangeRecord exchangeRecord) {
		boolean returnValue = false;
		if(exchangeRecord != null){
		    String pattern;
		    try {
		         pattern = PropertyManager.getPropertyManager().getProperty("proxy.wsdl.request.detect", ".*?wsdl");
		    }
		    catch (Exception ex){
		        logger.warn("An error occurs when getting the 'proxy.wsdl.request.detect' value, using default value => .*?wsdl", ex);
		        pattern = ".*?wsdl";
		    }
			for(QueryParam queryParam: exchangeRecord.getInMessage().getQueryString().getQueryParams()){
				if(queryParam.getName().toLowerCase().matches(pattern)){
					return true;
				}
			}
		}
		return returnValue;
	}

	@Override
	public boolean handle(ExchangeRecord exchangeRecord, MonitoringService monitoringService, EsperEngine esperEngine) {
		// enrich the exchange record
		exchangeRecord.getExchange().setExchangeType(ExchangeType.WSDL);		
		logger.debug("WSDL found");
		
		// Service construction
		Service service = new Service(exchangeRecord.getInMessage().buildCompleteUrl());
		// parent url ??
		service.setCallCount(1);
		service.setTitle(exchangeRecord.getInMessage().getPath());
		service.setDescription(exchangeRecord.getInMessage().getPath());
		service.setHttpMethod(exchangeRecord.getInMessage().getMethod()); // GET
		
        try {
            new NuxeoRegistrationService().registerRestService(service);  
        } catch (Exception e) {
            logger.error("Failed to register WSDL service", e);
        }
        
        // For discovery mode => each service is considered as a new service
        // No need to trigger an esper event to update the call count value.
        if(MonitoringService.MonitoringMode.VALIDATED.equals(monitoringService.getMode())){
            Node soaNode = null;
            for(Node node : monitoringService.getModel().getSoaNodes()){
                if(node.getUrl().equals(exchangeRecord.getInMessage().buildCompleteUrl())){
                    soaNode = node;
                    logger.debug("Node found ! " + soaNode.getTitle());
                    break;
                }
            }        
            esperEngine.sendEvent(soaNode);
        }
		return true;
	}

}
