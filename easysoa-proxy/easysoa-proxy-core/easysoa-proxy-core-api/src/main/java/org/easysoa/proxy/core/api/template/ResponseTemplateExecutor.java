/**
 * 
 */
package org.easysoa.proxy.core.api.template;

import org.apache.log4j.Logger;
import org.easysoa.message.MessageContent;
import org.easysoa.message.OutMessage;

/**
 * @author jguillemotte
 *
 */
public class ResponseTemplateExecutor implements TemplateExecutor {

    // Logger
    private static Logger logger = Logger.getLogger(ResponseTemplateExecutor.class.getName());    
    
    @Override
    public OutMessage execute(String renderedTemplate) throws Exception {
        logger.debug("ResponseTemplateExecutor Rendered template => " +  renderedTemplate);
        OutMessage outMessage = new OutMessage();
        MessageContent messageContent = new MessageContent();
        messageContent.setRawContent(renderedTemplate);
        outMessage.setMessageContent(messageContent);
        outMessage.setProtocol("HTTP");
        outMessage.setStatus(200);
        return outMessage;
    }

}
