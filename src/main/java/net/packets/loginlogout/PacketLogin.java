package net.packets.loginlogout;

import net.ServerLogic;
import net.packets.Packet;
import net.playerhandling.ServerPlayer;

public class PacketLogin extends Packet {

  private String username;

  /**
   * Constructor when the server receives a login attempt from the client.
   *
   * @param clientId clientId of the player that tries to connect
   * @param data Data received by server with the login attempt. Should contain the username.
   */
  public PacketLogin(int clientId, String data) {
    super(PacketTypes.LOGIN);
    setData(data);
    setClientId(clientId);
    username = getData().trim();
    validate();
  }

  /**
   * Constructor to create a package from the client side to be sent to the server.
   *
   * @param username The username the player wants to give himself
   */
  public PacketLogin(String username) {
    super(PacketTypes.LOGIN);
    setData(username);
    try {
      this.username = username.trim();
    } catch (NullPointerException e) {
      addError("There is no username.");
    }
    validate();
  }

  /**
   * Implementation of the abstract validate method to validate the input data/username Validate
   * method calls the checkUsername method which checks whether a username is null, too long, too
   * short or not extended ASCII. If an error is detected, the errors will be added to the errorList
   */
  public void validate() {
    checkUsername(username);
  }

  /**
   * implementation of the abstract processData method to process the given data This method gets
   * only called by the Server. Either creates an error message if the validate method has
   * discovered any errors or creates a new player and adds the player to the serverPlayerList. The
   * serverPlayerList checks whether the player already logged in, or if the username is already
   * taken. The status of adding the player to the ServerPlayerList will then be returned in a
   * String. Either this String or the error message will then be given to a PacketLoginStatus and
   * sent to the client.
   */
  public void processData() {
    String status;
    if (hasErrors()) {
      status = createErrorMessage();
    } else {
      ServerPlayer player = new ServerPlayer(username, getClientId());
      status = ServerLogic.getPlayerList().addPlayer(player);
    }
    try {
      PacketLoginStatus p = new PacketLoginStatus(getClientId(), status);
      p.sendToClient(getClientId());
      // Send update for clientid to player
      new PacketUpdateClientId(getClientId()).sendToClient(getClientId());
    } catch (NullPointerException e) {
      addError("Not connected to the server.");
    }
  }
}
