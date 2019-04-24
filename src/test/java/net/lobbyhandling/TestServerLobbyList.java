package net.lobbyhandling;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class TestServerLobbyList {

    @Test
    public void checkAddLobby() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test2", 2, "large");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbies.put(testLobby2.getLobbyId(),testLobby2);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby2);
        Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
    }

    @Test
    public void checkAddLobbyDouble() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby1);
        Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
    }

    @Test
    public void checkNotAddLobby() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test2", 2, "large");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby2);
        Assert.assertFalse(lobbies.equals(lobbyList.getLobbies()));
    }

    @Test
    public void checkAddLobbyDoubleName() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test1", 2, "large");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby2);
        Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
    }

    @Test
    public void checkRemoveLobbyExists() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test2", 2, "large");
        lobbies.put(testLobby2.getLobbyId(),testLobby2);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby2);
        lobbyList.removeLobby(testLobby1.getLobbyId());
        Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
    }

    @Test
    public void checkRemoveLobbyNotExists() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test2", 2, "large");
        Lobby testLobby3 = new Lobby("test3", 2, "large");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbies.put(testLobby2.getLobbyId(),testLobby2);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby2);
        lobbyList.removeLobby(testLobby3.getLobbyId());
        Assert.assertTrue(lobbies.equals(lobbyList.getLobbies()));
    }

    @Test
    public void checkGetName() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        lobbyList.addLobby(testLobby1);
        Assert.assertTrue(testLobby1.getLobbyName().equals(lobbyList.getName(testLobby1.getLobbyId())));
    }

    @Test
    public void checkGetLobby() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        lobbyList.addLobby(testLobby1);
        Assert.assertTrue(testLobby1.equals(lobbyList.getLobby(testLobby1.getLobbyId())));
    }

    @Test
    public void checkGetLobbyId() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbyList.addLobby(testLobby1);
        Assert.assertTrue(testLobby1.getLobbyId() == lobbyList.getLobbyId(testLobby1.getLobbyName()));
    }

    @Test
    public void checkGetTopTen() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test2", 2, "large");
        Lobby testLobby3 = new Lobby("test3", 3, "large");
        Lobby testLobby4 = new Lobby("test4", 4, "large");
        Lobby testLobby5 = new Lobby("test5", 5, "large");
        Lobby testLobby6 = new Lobby("test6", 26, "large");
        Lobby testLobby7 = new Lobby("test7", 7, "large");
        Lobby testLobby8 = new Lobby("test8", 8, "large");
        Lobby testLobby9 = new Lobby("test9", 29, "large");
        Lobby testLobby10 = new Lobby("test10", 20, "large");
        Lobby testLobby11 = new Lobby("test11", 24, "large");
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbies.put(testLobby2.getLobbyId(),testLobby2);
        lobbies.put(testLobby3.getLobbyId(),testLobby3);
        lobbies.put(testLobby4.getLobbyId(),testLobby4);
        lobbies.put(testLobby5.getLobbyId(),testLobby5);
        lobbies.put(testLobby6.getLobbyId(),testLobby6);
        lobbies.put(testLobby7.getLobbyId(),testLobby7);
        lobbies.put(testLobby8.getLobbyId(),testLobby8);
        lobbies.put(testLobby9.getLobbyId(),testLobby9);
        lobbies.put(testLobby10.getLobbyId(),testLobby10);
        lobbyList.addLobby(testLobby1);
        lobbyList.addLobby(testLobby2);
        lobbyList.addLobby(testLobby3);
        lobbyList.addLobby(testLobby4);
        lobbyList.addLobby(testLobby5);
        lobbyList.addLobby(testLobby6);
        lobbyList.addLobby(testLobby7);
        lobbyList.addLobby(testLobby8);
        lobbyList.addLobby(testLobby9);
        lobbyList.addLobby(testLobby10);
        lobbyList.addLobby(testLobby11);
        StringBuilder s = new StringBuilder();
        s.append("10");
        for(int i = 1; i <= 10; i++) {
            s.append(lobbies.get(i).toString());
        }
        System.out.println(lobbyList.getTopTen());
        System.out.println(s.toString());
        Assert.assertTrue(lobbyList.getTopTen().equals(s.toString()));
    }

    @Test
    public void checkGetLobbiesInGame() {
        ServerLobbyList lobbyList = new ServerLobbyList();
        ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
        Lobby testLobby1 = new Lobby("test1", 1, "mid");
        Lobby testLobby2 = new Lobby("test2", 1, "mid");
        testLobby1.setInGame(true);
        lobbies.put(testLobby1.getLobbyId(),testLobby1);
        lobbies.put(testLobby2.getLobbyId(),testLobby2);
        lobbyList.addLobby(testLobby1);
        StringBuilder s = new StringBuilder();
        s.append(testLobby1.toString()).append("║");
        Assert.assertTrue(s.toString().equals(lobbyList.getLobbiesInGame()));
    }

}
