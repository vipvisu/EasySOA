package org.easysoa.proxy.handler.event.admin;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.osoa.sca.annotations.Remotable;


/**
 * 
 * @author fntangke
 *
 */


@Remotable 
public interface ISubscriptionWebService {
    
    @GET
    @Path("/subscriptions")
    @Produces({"application/xml", "application/json"})
    public Subscriptions getSubscriptions();
		
    @POST
    @Path("/subscriptions")
    @Produces({"application/xml", "application/json"})
    public Subscriptions udpateSubscriptions(Subscriptions subscriptions);

}