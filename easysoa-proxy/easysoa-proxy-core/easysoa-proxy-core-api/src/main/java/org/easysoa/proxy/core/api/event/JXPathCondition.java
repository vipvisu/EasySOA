/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.event;

import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.easysoa.message.InMessage;
import org.easysoa.records.ExchangeRecord;

/**
 * 
 * @author fntangke
 */
@XmlRootElement
public class JXPathCondition implements Condition {

	private String jxPathRequest;

	public JXPathCondition() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of the field we want to compare with
	 * @param value
	 *            the value we wand to compare with the field
	 */

	public JXPathCondition(String name, String value) {
		this.jxPathRequest = "[" + name + "='"+value +"']";
	}

	@Override
	public boolean matches() {
		return false;
	}

	/**
	 * 
	 * @param inMessage
	 * @return true if the inMessage matches with the JXPathCOndition else false
	 */
	@Override
	public boolean matches(ExchangeRecord exchangeRecord) throws JXPathException {
		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		Object result  = (String) context.getValue(jxPathRequest);
		return (Boolean) result;
	}

	public void setJxPathRequest(String jxPathRequest) {
		this.jxPathRequest = jxPathRequest;
	}

	public String getJxPathRequest() {
		return jxPathRequest;
	}

}
