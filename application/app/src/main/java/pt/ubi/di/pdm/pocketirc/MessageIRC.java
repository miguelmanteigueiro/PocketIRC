package pt.ubi.di.pdm.pocketirc;

//imports
import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * Represents an IRC message
 */
public class MessageIRC{
  //== attributes ==
  public String server;
  public String channel;
  public String recipient;
  public String[] user;
  public String type;
  public String msg;
  public String action;
  public String hour;
  public int code;
  /**
   * The message type can be:
   *  -Server Message (S)
   *  -Channel Message (C)
   *  -User Message (Message, JOIN, QUIT, PART) (UM) (UJ) (UQ) (UP)
   *  -NickServ Message (Appears on the chat and it is a NOTICE) (NS)
   *  -Ping (P)
   */
  public String message_type;

  //== constructors ==
  /**
   * Constructs an empty IRC message
   */
  public MessageIRC(){
    server = "";
    channel = "";
    recipient = "";
    user = new String[3];
    type = "";
    msg = "";
    action = "";
    message_type = "";
    code=-1;
  }

  //== methods ==
  //Getters & Setters
  /**
   * Gets the action
   * @return the action
   */
  public String getAction(){
    return action;
  }
  /**
   * Gets the channel
   * @return the channel
   */
  public String getChannel(){
    return channel;
  }
  /**
   * Gets the code
   * @return the code
   */
  public int getCode(){
    return code;
  }
  /**
   * Gets the msg
   * @return the msg
   */
  public String getMsg(){
    return msg;
  }
  /**
   * Gets the recipient
   * @return the recipient
   */
  public String getRecipient(){
    return recipient;
  }
  /**
   * Gets the server
   * @return the server
   */
  public String getServer(){
    return server;
  }
  /**
   * Gets the type
   * @return the type
   */
  public String getType(){
    return type;
  }
  /**
   * Gets the user
   * @return the user
   */
  public String[] getUser(){
    return user;
  }
  /**
   * Gets the hour
   * @return the hour
   */
  public String getHour(){
    return hour;
  }
  /**
   * Gets the message type
   * @return the message type
   */
  public String getMessage_type(){
    return message_type;
  }
  /**
   * Sets the action to {@code action}
   * @param action the specified action
   */
  public void setAction(String action){
    this.action=action;
  }
  /**
   * Sets the channel to {@code channel}
   * @param channel the specified channel
   */
  public void setChannel(String channel){
    this.channel=channel;
  }
  /**
   * Sets the code to {@code code}
   * @param code the specified code
   */
  public void setCode(int code){
    this.code=code;
  }
  /**
   * Sets the msg to {@code msg}
   * @param msg the specified msg
   */
  public void setMsg(String msg){
    this.msg =msg;
  }
  /**
   * Sets the recipient to {@code recipient}
   * @param recipient the specified recipient
   */
  public void setRecipient(String recipient){
    this.recipient=recipient;
  }
  /**
   * Sets the server to {@code server}
   * @param server the specified server
   */
  public void setServer(String server){
    this.server=server;
  }
  /**
   * Sets the type to {@code type}
   * @param type the specified type
   */
  public void setType(String type){
    this.type=type;
  }
  /**
   * Sets the user to {@code user}
   * @param user the specified user
   */
  public void setUser(String[] user){
    this.user=user;
  }
  /**
   * Sets the hour to {@code hour}
   * @param hour the specified hour
   */
  public void setHour(String hour){
    this.hour=hour;
  }
  /**
   * Sets the message type to {@code message_type}
   * @param message_type the specified message type
   */
  public void setMessage_type(String message_type){
    this.message_type=message_type;
  }
  //to string
  /**
   * Gets the message as a string
   * @return the message as a string
   */
  @NonNull
  @Override
  public String toString(){
    return "MessageIRC{" +
      "server='" + server + '\'' +
      ", channel='" + channel + '\'' +
      ", recipient='" + recipient + '\'' +
      ", user=" + Arrays.toString(user) +
      ", type='" + type + '\'' +
      ", msg='" + msg + '\'' +
      ", action='" + action + '\'' +
      ", hour='" + hour + '\'' +
      ", message_type='" + message_type + '\'' +
      ", code=" + code +
      '}';
  }
}