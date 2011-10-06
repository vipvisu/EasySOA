package org.openwide.easysoa.scaffolding;

import java.util.List;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSField;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSService;
import org.ow2.easywsdl.schema.api.XmlException;

public interface TemplateFormGeneratorInterface {

	/**
	 * Set the WSDl to parse : First thing to do before to call the other methods
	 * @param wsdlXmlSource
	 */
	public void setWsdl(String wsdlXmlSource) throws Exception;
	
	/**
	 * Returns the service list
	 * @return
	 */
	public List<WSService> getServices();
	
	/**
	 * Returns the list of endpoints (or ports) for the service
	 * @return
	 */
	public List<WSEndpoint> getEndpoints(WSService wsService);
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public String getBindingName(WSEndpoint wsEndpoint);
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public List<WSOperation> getOperations(WSEndpoint wsEndpoint);
	
	/**
	 * 
	 * @param operation
	 * @return
	 */
	public String getOperationName(WSOperation wsOperation);

	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public String getOutputMessageName(WSEndpoint wsEndpoint, WSOperation wsOperation);	
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<WSField> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) throws XmlException;
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<WSField> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) throws XmlException;	
	
}