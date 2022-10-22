package org.acme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

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

@ServerEndpoint("/char/{username}")         
@ApplicationScoped
    public class CharSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject CharSocket(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
     }
 
     @Inject @Remote("character")
     RemoteCache<String, String> cache;

     @Inject @Remote("character-active")
     RemoteCache<String, String> cacheActive;

     RemoteCacheManager remoteCacheManager;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        String joinedProfile = cache.get(username) ;
        broadcast(username + "," + "add" + "," + joinedProfile);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast(username + "," + "remove" + "," + "");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        broadcast(">> " + username + ": left on error " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        String[] values = {"Seba","Admin"};
        boolean contains = Arrays.stream(values).anyMatch(username::equals);
        
        if(message.startsWith("/") && contains){
            
            
            String[] attributes = null;
            String newProfile = null;
            String changeUser= null;
            String changeProfile = null;
            if(message.startsWith("/change exp")){
                // EX: /change exp add 1000 to Sergio
                List<String> commandAndParams = Arrays.asList(Pattern.compile("\\s").split(message));
                changeUser = commandAndParams.get(5);
                changeProfile = cache.get(changeUser) ;

                attributes = Pattern.compile("\\|").split(changeProfile);
                Integer userExpValue = Integer.valueOf(attributes[1].split("\\:")[1]);
                Integer newExp = userExpValue+Integer.valueOf(commandAndParams.get(3));
                
                attributes[1] = "exp:"+newExp ;
                
                newProfile = Arrays.toString(attributes);
                newProfile = newProfile.replaceAll("\\[", "");
                newProfile = newProfile.replaceAll("\\]", "");
                newProfile = newProfile.replaceAll("\\s", "");
                newProfile = newProfile.replaceAll(",", "|");
                cache.replace(changeUser,newProfile);

                System.out.println("Exp added to "+changeUser);
            }
            broadcast(changeUser + "," + "update" + "," + newProfile);
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    
                }
            });
        });
    }

}