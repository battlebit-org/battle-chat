package org.acme;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.infinispan.client.Remote;

@Path("/profile/{username}")
public class ProfileResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getProfile(@PathParam("username") String username) {
        if(cache.get(username) == null){
            cache.put(username,"-level=1-job=unknown");
        }
        return ">>"+username+":"+cache.get(username);
    }
    
    @Inject ProfileResource(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("profile")
     RemoteCache<String, String> cache;
 
     RemoteCacheManager remoteCacheManager;
}