package net.packets.lobby;

import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketCurLobbyInfo {

    @Test
  public void checkLobbyDoesNotExist() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketCurLobbyInfo p = new PacketCurLobbyInfo(1, 1);
    Assert.assertEquals("ERRORS: Lobby doesn't exist", p.createErrorMessage());
  }

  @Test
  public void checkLobbyInfoCorrectServer() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    Lobby lobby = new Lobby("TestLobby", 1, "l");
    ServerPlayer player = new ServerPlayer("Peter", 1);
    lobby.addPlayer(player);
    ServerLogic.getLobbyList().addLobby(lobby);
    PacketCurLobbyInfo p = new PacketCurLobbyInfo(1, lobby.getLobbyId());
    Assert.assertEquals("OK║TestLobby║1║Peter║false║", p.getData());
  }

  @Test
  public void checkInfoNull() {
    PacketCurLobbyInfo p = new PacketCurLobbyInfo(null);
    Assert.assertEquals("ERRORS: Invalid Data.", p.createErrorMessage());
  }

  @Test
  public void checkInfoNotAscii() {
    PacketCurLobbyInfo p = new PacketCurLobbyInfo("ਠ");
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

  @Test
  public void checkInvalidNumberOfArguments() {
    PacketCurLobbyInfo p = new PacketCurLobbyInfo("OK║TestLobby║ErrorsPlayer");
    p.processData();
    Assert.assertEquals("ERRORS: Invalid number of arguments.", p.createErrorMessage());
  }

  @Test
  public void checkCatalogCorrect() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    Lobby lobby = new Lobby("TestLobby", 1, "l");
    ServerPlayer player = new ServerPlayer("Peter", 1);
    lobby.addPlayer(player);
    ServerLogic.getLobbyList().addLobby(lobby);
    PacketCurLobbyInfo p = new PacketCurLobbyInfo("OK║TestLobby║1║TestPlayer║true");
    p.processData();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }

    @Test
    public void checkCatalogIncorrect() {
        ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
        Lobby lobby = new Lobby("TestLobby", 1, "l");
        ServerPlayer player = new ServerPlayer("Peter", 1);
        lobby.addPlayer(player);
        ServerLogic.getLobbyList().addLobby(lobby);
        PacketCurLobbyInfo p = new PacketCurLobbyInfo("OK║TestLobby║abs║TestPlayer║true");
        p.processData();
        Assert.assertEquals("ERRORS: Number incorrect.", p.createErrorMessage());
    }
}
