package org.acme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.infinispan.client.Remote;

@Path("/profile")
public class ProfileResource {

    private boolean add;

    @Path("/single/{username}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getOneProfile(@PathParam("username") String username) {
        if(cache.get(username) == null){
            cache.put(username,"1");
        }
        return cache.get(username);
    }
    
    @Path("/all")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllProfile(String requesString) {

        return "NOT IMPLEMENTED";
    }

    @Inject ProfileResource(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("character")
     RemoteCache<String, String> cache;
     
     RemoteCacheManager remoteCacheManager;
}