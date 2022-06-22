package org.acme;

import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.infinispan.client.Remote;

@Path("/profile")
public class ProfileResource {

    @Path("/{username}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getOneProfile(@PathParam("username") String username) {
        if(cache.get(username) == null){
            cache.put(username,"-level=1-job=unknown");
        }
        return ">> "+username+":"+cache.get(username);
    }
    
    @Path("/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllProfile() {
        cache.getAll(Set<? extends String>);
        return "";
    }

    @Inject ProfileResource(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("character")
     RemoteCache<String, String> cache;
 
     RemoteCacheManager remoteCacheManager;
}