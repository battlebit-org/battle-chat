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
            cache.put(username,"-level=1-job=unknown");
        }
        return username+","+cache.get(username);
    }
    
    @Path("/all")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllProfile(String requesString) {
        String users = cacheActive.get("users");
        System.out.println("users: "+users);
        users = users.replaceAll("\\[", "");
        users = users.replaceAll("\\]", "");
        users = users.trim();
        System.out.println("users: "+users);
        //Set<String> usersList = new HashSet<String>(Arrays.asList(users.split(",")));
        Set<String> usersList = new HashSet<String>(new ArrayList<>(Arrays.asList(users.trim().split(","))));
        for (String string : usersList) {
            System.out.println("users member: "+ string);
        }
        System.out.println("usersList: "+usersList.toString());
        Map<String, String> activeUsersList = cache.getAll(usersList);
        System.out.println("activeUsersList size: "+activeUsersList.size());
        System.out.println("activeUsersList : "+activeUsersList);
        return activeUsersList.toString();
    }

    @Inject ProfileResource(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("character")
     RemoteCache<String, String> cache;
     @Inject @Remote("character-active")
     RemoteCache<String, String> cacheActive;
     
     RemoteCacheManager remoteCacheManager;
}