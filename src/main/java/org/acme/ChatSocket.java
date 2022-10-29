package org.acme;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.infinispan.client.Remote;

@ServerEndpoint("/chat/{username}")         
@ApplicationScoped
    public class ChatSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();
    @Inject ChatSocket(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("character")
     RemoteCache<String, String> cache;
 
     RemoteCacheManager remoteCacheManager;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("Opening");
        sessions.put(username, session);
        System.out.println(username+" has joined the channel");
        
        
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast(username + ",message");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        broadcast(">>" + username + ": left on error " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {

        String finalUsername = message.split("[|_|]")[0];
        String profile = cache.get(finalUsername);
        String newProfile = null;
        if(profile==null){
            cache.put(finalUsername,"1");
            System.out.println("Username and profile cached");
            System.out.println("MESSAGE: "+ message);
            System.out.println("Username: "+finalUsername+", and profile: "+1);

        }else{
            System.out.println("Username already exist in cache");
            System.out.println("Username: "+finalUsername+", and profile: "+profile);
            newProfile = String.valueOf(Integer.valueOf(profile).intValue() + 1);
            System.out.println("Username: "+finalUsername+", and profile: "+newProfile);
            cache.replace(finalUsername, newProfile);

        }
        if (message.equalsIgnoreCase("_ready_")) {
            System.out.println(">> " + username + ": joined");
        } else {
            broadcast( message );
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

}