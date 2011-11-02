/**
 * EasySOA Registry
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

package org.easysoa.sca.visitors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationRequest;
import org.easysoa.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.IScaImporter;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
// TODO: Refactor visitor implementations
public class ApiReferenceBindingVisitor extends ScaVisitorBase {

    private static Log log = LogFactory.getLog(ApiReferenceBindingVisitor.class);
    
    protected DocumentModel referenceModel;

    public ApiReferenceBindingVisitor(IScaImporter scaImporter) {
        super(scaImporter);
    }
    
    @Override
    public String getDescription() {
        StringBuffer sbuf = new StringBuffer(this.toString());
        sbuf.append("[path=");
        sbuf.append(referenceModel.getPathAsString());
        sbuf.append(",type=");
        sbuf.append(referenceModel.getType());
        sbuf.append(",title=");
        try {
            sbuf.append(referenceModel.getPropertyValue("dc:title"));
        } catch (Exception ex) {
            String msg = "error while getting title";
            sbuf.append("(" + msg + ")");
            log.error(msg, ex);
        }
        sbuf.append("]");
        return sbuf.toString();
    }
    
    /*@Override
    public void visit(BindingInfoProvider bindingInfoProvider) throws ClientException {

        // Notify service reference
        Map<String, String> properties = new HashMap<String, String>();
        String refUrl = bindingInfoProvider.getBindingUrl();
        properties.put(ServiceReference.PROP_REFURL, refUrl); // getting referenced service url
        properties.put(ServiceReference.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
        properties.put(ServiceReference.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
        properties.put(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
        properties.put(ServiceReference.PROP_PARENTURL, 
                (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        referenceModel = notificationService.notifyServiceReference(documentManager, properties);
        
        // Notify referenced service
        properties = new HashMap<String, String>();
        properties.put(Service.PROP_URL, refUrl);
        properties.put(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
        try {
            notificationService.notifyService(documentManager, properties);
        } catch (MalformedURLException e) {
            log.warn("Failed to register referenced service, composite seems to link to an invalid URL");
        } 
    }*/
    
    @Override
    public void visit(BindingInfoProvider bindingInfoProvider) throws ClientException, IOException {
        
    	// Using default value for API url
	    RestNotificationFactory factory = new RestNotificationFactory();
	    
        String refUrl = bindingInfoProvider.getBindingUrl();	    
	    
        // Notify service reference
        // TODO : Add a rest service for service references, not existing in the REST notification server
	    /*
        RestNotificationRequest requestServiceReference = factory.createNotification(RestNotificationService.);
	    requestServiceReference.setProperty(ServiceReference.PROP_REFURL, refUrl); // getting referenced service url
	    requestServiceReference.setProperty(ServiceReference.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
	    requestServiceReference.setProperty(ServiceReference.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
	    requestServiceReference.setProperty(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
	    requestServiceReference.setProperty(ServiceReference.PROP_PARENTURL, 
                (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL));	    
	    requestServiceReference.send();
	    
        // Notify referenced service
	    RestNotificationRequest requestService = factory.createNotification(RestNotificationService.SERVICE);
        requestService.setProperty(Service.PROP_URL, refUrl);
        requestService.setProperty(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
        try {
        	requestService.send();
        } catch (Exception e) {
            log.warn("Failed to register referenced service, composite seems to link to an invalid URL");
        }*/	    
    }

    @Override
    public void postCheck() throws ClientException {
        
        DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
        // find referenced service
        String refUrl = (String) referenceModel.getProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFURL);
        DocumentModel refServiceModel;
        try {
            refServiceModel = docService.findService(documentManager, refUrl);
            if (refServiceModel != null) {
                referenceModel.setProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFPATH, refServiceModel.getPathAsString());
                documentManager.saveDocument(referenceModel);
            }
        } catch (MalformedURLException e) {
            log.warn("Reference URL is invalid", e);
        }
    }

}
