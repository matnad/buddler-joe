package game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("Duplicates")
public class History {
    private static CopyOnWriteArrayList<String> finished = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<Integer, String> open = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, String> running = new ConcurrentHashMap<>();

    public static String  getStory(){
        String res = "Open Lobbies:║";
        for(Map.Entry<Integer, String> entry : open.entrySet()){
            String value =  entry.getValue();
            res = res + value + "║";
        }
        if(open.size() == 0){
            res = res + "none║";
        }
        res = res + "Lobbies Of Running Games:║";
        for(Map.Entry<Integer, String> entry : running.entrySet()){
            String value =  entry.getValue();
            res = res + value + "║";
        }
        if(running.size() == 0){
            res = res + "none║";
        }
        res = res + "Old Games:║";
        for(int i = 0; i < finished.size(); i++){
            res = res + finished.get(i)  + "║";
        }
        if(finished.size() == 0){
            res = res + "none║";
        }
        return res;
    }

    public static void openRemove(int lobbyId){
        open.remove(lobbyId);
    }

    public static void openAdd(int lobbyId, String lobbyName){
        open.put(lobbyId,lobbyName);
    }

    public static void runningRemove(int lobbyId){
        running.remove(lobbyId);
    }

    public static void runningAdd(int lobbyId, String lobbyName){
        running.put(lobbyId,lobbyName);
    }

    public static void archive(String data){
        finished.add(data);
    }
}
