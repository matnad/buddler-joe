package net.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import net.ClientLogic;
import net.ServerLogic;
import net.playerhandling.Player;

/**
 * Abstract Packet class which all Packets implement and build upon. The enum represents all
 * possible packages which can be implemented by the server/client.
 */
public abstract class Packet {

  private List<String> errors = new ArrayList<>();
  private PacketTypes packetType;
  private int clientId;
  private String data;

  protected Packet(PacketTypes packetType) {
    this.packetType = packetType;
  }

  /**
   * Method to lookup packages from the enum of possible packages Goes through the whole enum and
   * checks whether the packetCode equals the searched for code. If none has been found, the looked
   * up code gets returned as invalid
   *
   * @param code The code of the package to be looked up
   * @return The PacketType corresponding to the code searched for or in case that the code didn't
   *     exist it gets returned invalid
   */
  public static PacketTypes lookupPacket(String code) {
    for (PacketTypes p : PacketTypes.values()) {
      if (p.getPacketCode().equals(code)) {
        return p;
      }
    }
    return PacketTypes.INVALID;
  }

  /**
   * Abstract method to validate the data on the package Used to validate the data as well as the
   * package to either create an error message or to not do anything at all if all is in order.
   * Different in every subclass because every packet has different standards and validation
   * processes to be done.
   */
  public abstract void validate();

  /**
   * Abstract method to process the data on the package Used to do all the necessary work with the
   * respective packet. Here all the necessary methods get called and all the work gets done. Vital
   * for every subclass to implement because every packet has a different workload to process.
   */
  public abstract void processData();

  /**
   * Communication method to send data to a client. The destination address is determined by their
   * clientId.
   *
   * @param receiver The clientId of the receiving client
   */
  public void sendToClient(int receiver) {
    ServerLogic.sendPacketToClient(receiver, this);
  }

  /**
   * Communication method to send data to all clients in a lobby Loops over all player in the lobby
   * and sends them the package by calling the sendToClient method for each player in the lobby.
   *
   * @param lobbyId The lobbyId of the lobby to which the packet should be sent to.
   */
  public void sendToLobby(int lobbyId) {
    ServerLogic.sendPacketToLobby(lobbyId, this);
  }

  /**
   * Communication method to send data to all clients on the server Loops over all players on the
   * server and calls the sendToClient for every player on the server.
   */
  public void sendToAllClients() {
    // TODO: When we use this, move it to ServerLogic
    HashMap<Integer, Player> players = ServerLogic.getPlayerList().getPlayers();
    for (Player p : players.values()) {
      sendToClient(p.getClientId());
    }
  }

  /**
   * Communication Method to send data to all clients currently not in a lobby. Calls the
   * sendToClient Method for each player on the server that is currently not in a Lobby.
   */
  public void sendToClientsNotInALobby() {
    ServerLogic.sendToClientsNotInALobby(this);
  }

  /** Communication method to send data from a client to the server. */
  public void sendToServer() {
    ClientLogic.sendToServer(this);
  }

  /**
   * Getter method to return the current PacketType The PacketType is determined by the enum.
   *
   * @return The packetType of the current instance of the packet
   */
  private PacketTypes getPacketType() {
    return packetType;
  }

  /**
   * Setter to set the current packetType to a different one.
   *
   * @param packetType The PacketType to which the packet should be changed to
   */
  public void setPacketType(PacketTypes packetType) {
    this.packetType = packetType;
  }

  protected int getClientId() {
    return clientId;
  }

  protected void setClientId(int clientId) {
    this.clientId = clientId;
  }

  protected String getData() {
    return data;
  }

  protected void setData(String data) {
    this.data = data;
  }

  /**
   * Returns a list of all errors of a instance of this class.
   *
   * @return The list of errors currently on the instance of the class
   */
  private List<String> getErrors() {
    return errors;
  }

  /**
   * Boolean method to check whether an instance of this class has errors or not.
   *
   * @return either true or false, depending whether the instance of this class has errors or not.
   *     True if it has errors, false if not.
   */
  public boolean hasErrors() {
    return errors.size() > 0;
  }

  /**
   * Method to add an error to the errorList. Is used to combine multiple errors to give the client
   * or server as detailed an errormessage as possible.
   *
   * @param error The error to be added to the errorList
   */
  protected void addError(String error) {
    errors.add(error);
  }

  /**
   * Boolean method to check whether a String is part of extended Ascii or not Goes through the
   * provided String and checks each character whether it is part of extended Ascii or not.
   *
   * @param s The String which should be checked
   * @return Boolean value whether the String is extended Ascii or not. True if it is extended
   *     Ascii, false if not
   */
  protected boolean isExtendedAscii(String s) {
    char[] charArray = s.toCharArray();
    for (char c : charArray) {
      if (c > 255) {
        addError("Invalid characters, only extended ASCII.");
        return false;
      }
    }
    return true;
  }

  /**
   * Boolean method to check whether a String is an int and thus if it is possible to convert it to
   * an int Checks whitch a NumberFormatException whether the conversion to int is possible or not
   * and also whether there is a String at all with a NullPointerException.
   *
   * @param s The String which should be checked whether it contains an integer or not
   * @return A boolelan value which is either true if it contains an Integer or false if not
   */
  protected boolean isInt(String s) {
    boolean h = true;
    try {
      int i = Integer.parseInt(s);
    } catch (NumberFormatException | NullPointerException nfe) {
      h = false;
    }
    return h;
  }

  /**
   * Method to create an error message out of all errors collected throughout the validation
   * process. Joins together all the errors present in the errorList by getting the errors with the
   * getErrors() method and going through the list and adding the errors to a StringJoiner
   *
   * @return the created error message
   */
  protected String createErrorMessage() {
    StringJoiner statusJ = new StringJoiner(" ", "ERRORS: ", "");
    for (String error : getErrors()) {
      statusJ.add(error);
    }
    return statusJ.toString();
  }

  /**
   * Method to check a username if it is valid or not. Checks whether there is a username at all, if
   * it is not too long, not too short and if it is extended Ascii Used whenever a username should
   * be validated.
   *
   * @param username The username to be checked by this method
   */
  protected void checkUsername(String username) {
    if (username == null) {
      addError("No username found.");
      return;
    }
    if (username.length() > 30) {
      addError("Username to long. Maximum is 30 Characters.");
    } else if (username.length() < 4) {
      addError("Username to short. Minimum is 4 Characters.");
    }
    isExtendedAscii(username);
  }

  /**
   * This method checks if the client how send this Packet is logged in or not. This method should
   * only be called from the server, since it will always return false on the client side.
   *
   * @return true if logged in else false.
   */
  protected boolean isLoggedIn() {
    try {
      if (!ServerLogic.getPlayerList().getPlayers().containsKey(getClientId())) {
        addError("Not loggedin yet.");
        return false;
      } else {
        return true;
      }
    } catch (NullPointerException e) {
      return false;
    }
  }

  /**
   * This method checks if the client how send this Packet is currently in a Lobby. This method
   * should only be called on the serverside, since it will always return false on the clientside.
   *
   * @return true if in a Lobby else false.
   */
  protected boolean isInALobby() {
    try {
      int lobbyId = ServerLogic.getPlayerList().getPlayers().get(getClientId()).getCurLobbyId();
      // addError("Already in a lobby");
      return lobbyId != 0;
    } catch (NullPointerException e) {
      return false;
    }
  }

  /**
   * ToString method to compile all the information contained in this packet. Used to send the data
   * to either to the server or client.
   *
   * @return string to be sent over TCP socket
   */
  public String toString() {
    return getPacketType().getPacketCode() + " " + getData();
  }

  public enum PacketTypes {
    INVALID("INVAL"),
    LOGIN("PLOGI"),
    LOGIN_STATUS("PLOGS"),
    UPDATE_CLIENT_ID("UPCID"),
    DISCONNECT("DISCP"),
    GET_NAME("GETNM"),
    SEND_NAME("SENDN"),
    SET_NAME("SETNM"),
    SET_NAME_STATUS("STNMS"),
    GET_LOBBIES("LOBGE"),
    LEAVE_LOBBY("LOBLE"),
    JOIN_LOBBY("LOBJO"),
    CREATE_LOBBY("LOBCR"),
    PING("UPING"),
    PONG("PONGU"),
    CREATE_LOBBY_STATUS("LOBCS"),
    JOIN_LOBBY_STATUS("LOBJS"),
    LOBBY_OVERVIEW("LOBOV"),
    CUR_LOBBY_INFO("LOBCI"),
    GET_LOBBY_INFO("LOBGI"),
    LEAVE_LOBBY_STATUS("LOBLS"),
    CHAT_MESSAGE_TO_SERVER("CHATS"),
    CHAT_MESSAGE_TO_CLIENT("CHATC"),
    CHAT_MESSAGE_STATUS("CHATN");

    private final String packetCode;

    /**
     * Constructor to assign the packet type to the subclass.
     *
     * @param packetCode to Assign the packet ID so that the subclass is clearly identified
     */
    PacketTypes(String packetCode) {
      this.packetCode = packetCode;
    }

    public String getPacketCode() {
      return packetCode;
    }
  }
}
