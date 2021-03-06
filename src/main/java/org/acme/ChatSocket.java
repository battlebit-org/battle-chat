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

     @Inject @Remote("character-active")
     RemoteCache<String, String> cacheActive;
 
     RemoteCacheManager remoteCacheManager;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("Opening");
        
        sessions.put(username, session);
        Set<String> usersList = sessions.keySet();

        String profile = cache.get(username);
        if(profile==null){
            profile = "";
            cache.put(username,"level:1-job:unknown");
            
            System.out.println("Username and profile cached");
        }else{
            System.out.println("Username already exist in cache");
        }
        cacheActive.put("users", usersList.toString());
        broadcast(username+" has joined the channel");
        
        
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        Set<String> usersList = sessions.keySet();
        cacheActive.remove("users");
        cacheActive.put("users", usersList.toString());
        broadcast(username + ",message");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        broadcast(">>" + username + ": left on error " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        cache.put(username, message);
        if (message.equalsIgnoreCase("_ready_")) {
            broadcast(">> " + username + ": joined");
        } else {
            broadcast(">> " + username + ": " + message );
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