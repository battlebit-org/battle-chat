package org.acme;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.infinispan.client.Remote;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        String user = "Iarwain";
        String userLocation = ""+Math.random()+","+Math.random()+"";
        cache.put(user, userLocation);
        return "User: "+user+", Location: "+userLocation;
    }
    
    @Inject GreetingResource(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("location")
     RemoteCache<String, String> cache;
 
     RemoteCacheManager remoteCacheManager;
}