package pt.ubi.di.pdm.teste;

import java.util.Arrays;

public class MessageIRC {
  String server;
  String channel;
  String recipient;
  String[] user;
  String type;
  String msg;
  String action;
  String hour;
  int code;
  boolean is_user;

  public MessageIRC(){
    String server = "";
    String channel = "";
    String recipient = "";
    String[] user = new String[3];
    String type = "";
    String msg = "";
    String action = "";
    int code = -1;
  }

  // Getters e setters
  public String getAction()  {
    return action;
  }
  public String getChannel() {
    return channel;
  }
  public int getCode() {
    return code;
  }
  public String getMsg() {
    return msg;
  }
  public String getRecipient() {
    return recipient;
  }
  public String getServer() {
    return server;
  }
  public String getType() {
    return type;
  }
  public String[] getUser() {
    return user;
  }
  public boolean getIs_user() { return is_user; }
  public String getHour() {
    return hour;
  }

  public void setAction(String action) {
    this.action = action;
  }
  public void setChannel(String channel) {
    this.channel = channel;
  }
  public void setCode(int code) {
    this.code = code;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }
  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }
  public void setServer(String server) {
    this.server = server;
  }
  public void setType(String type) {
    this.type = type;
  }
  public void setUser(String[] user) {
    this.user = user;
  }
  public void setIs_user(boolean is_user) { this.is_user = is_user; }
  public void setHour(String hour) {
    this.hour = hour;
  }

  @Override
  public String toString() {
    return "MessageIRC{" +
            "server='" + server + '\'' +
            ", channel='" + channel + '\'' +
            ", recipient='" + recipient + '\'' +
            ", user=" + Arrays.toString(user) +
            ", type='" + type + '\'' +
            ", msg='" + msg + '\'' +
            ", action='" + action + '\'' +
            ", hour='" + hour + '\'' +
            ", code=" + code +
            ", is_user=" + is_user +
            '}';
  }
}
