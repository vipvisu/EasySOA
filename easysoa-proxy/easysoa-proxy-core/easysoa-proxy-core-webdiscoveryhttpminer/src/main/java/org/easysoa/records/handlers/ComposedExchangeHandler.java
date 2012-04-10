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
package org.easysoa.records.handlers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public class ComposedExchangeHandler implements ExchangeHandler {

    // TODO : add configuration in composite file
    
    //@Reference
    private List<ExchangeHandler> exchangeHandlers;

    /**
     * Handle the exchange. Call the handle method for all handlers from the exchange handler list
     * @param request HTTP Servlet request
     * @param response HTTP servlet Response
     * @throws Exception 
     */
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) throws Exception{
        if(exchangeHandlers != null){
            for(ExchangeHandler handler : exchangeHandlers){
                handler.handleExchange(request, response);
            }
        }
    }

}
