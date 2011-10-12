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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.sca.visitors;

import javax.xml.stream.XMLStreamReader;

import org.easysoa.sca.IScaImporter;
import org.easysoa.services.NotificationService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
// TODO: Refactor visitor implementations
public abstract class ScaVisitorBase implements ScaVisitor {

    protected CoreSession documentManager;
    protected IScaImporter scaImporter;
    protected XMLStreamReader compositeReader;
    protected NotificationService notificationService;
    
    public ScaVisitorBase(IScaImporter scaImporter) {
        this.documentManager = scaImporter.getDocumentManager();
        this.scaImporter = scaImporter;
        this.compositeReader = scaImporter.getCompositeReader();
        this.notificationService = Framework.getRuntime().getService(NotificationService.class);
    }
    
    @Override
    public String getDescription() {
        return this.toString();
    }

}
