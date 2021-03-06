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

/**
 * 
 */
package org.easysoa.proxy.core.api.template.parsers;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.records.correlation.CandidateField;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggester;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author jguillemotte
 *
 */
public class JSONParser implements TemplateParser {

    // Logger
    private static Logger logger = Logger.getLogger(JSONParser.class.getName());    
    
    @Override
    public boolean canParse(OutMessage outMessage) {
        logger.debug("Out message contains JSON => " + outMessage.getMessageContent().isJSONContent());
        return outMessage.getMessageContent().isJSONContent();
    }
    
    @Override
    public HashMap<String,CandidateField> parse(OutMessage outMessage, HashMap<String,CandidateField> fieldMap) {
        logger.debug("Parsing JSON message");        
        JSONObject jsonOutObject = (JSONObject) JSONSerializer.toJSON(outMessage.getMessageContent().getRawContent());
        findJSONOutFields("root", jsonOutObject, -1, fieldMap);
        return fieldMap;
    }
 
    /**
     * Recursive method to fill the OutFieldMap from JSON Datastructure
     * @param objectKey
     * @param jsonObject
     * @param index
     * @param fieldMap
     */
    private void findJSONOutFields(String objectKey, Object jsonObject, int index, HashMap<String,CandidateField> fieldMap){
        if(jsonObject instanceof JSONObject){
            // Get all the key contained in the object and for each key, get the associated object and call this recursive method
            logger.debug("Instance of JSONObject found");
            JSONObject jObject = (JSONObject) jsonObject;
            @SuppressWarnings("unchecked")
            Iterator<String> keyIterator = jObject.keys();
            while(keyIterator.hasNext()){
                String key = (String)(keyIterator.next());
                logger.debug("key : " + key);
                findJSONOutFields(key, jObject.get(key), index, fieldMap);
            }
        } else if(jsonObject instanceof JSONArray) {
            // For each JSONObject contained in the array, call this recursive method
            logger.debug("Instance of JSONArray found");
            JSONArray jsonArray = (JSONArray) jsonObject;
            int objectIndex = 0;
            for(Object object : jsonArray){
                findJSONOutFields(objectKey, object, objectIndex, fieldMap);
                objectIndex++;
            }
        } else {
            // Get the value and add a new CandidateField object in the map
            logger.debug("Other object found (" + jsonObject.getClass().getName() + ") = " + objectKey + ":" + jsonObject.toString());          
            String fieldName;
            if(index > -1){
                fieldName = index + "/" + objectKey;
            } else {
                fieldName = objectKey;
            }
            CandidateField field = new CandidateField(fieldName, jsonObject.toString());
            fieldMap.put(fieldName, field);
            return;
        }
    }
    
}
