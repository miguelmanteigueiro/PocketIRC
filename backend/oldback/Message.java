public class Message {
  String server; // Server name
  String channel; // Channel name
  String recipient; // Who receives the message
  String[] user; //
  String type;
  String msg;
  String action;
  int code;

  public Message(){
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
  
  @Override
  public String toString() {
    return "Message{" +
            "server='" + server + '\'' +
            ", channel='" + channel + '\'' +
            ", recipient='" + recipient + '\'' +
            ", user=" + java.util.Arrays.toString(user) +
            ", type='" + type + '\'' +
            ", msg='" + msg + '\'' +
            ", action='" + action + '\'' +
            ", code=" + code +
            '}';
  }
}
