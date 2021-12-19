package pt.ubi.di.pdm.pocketirc;

//imports
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses several commands
 */
public class Parser{
  //== methods ==
  /**
   * Tokenizes PRIVMSG
   * @param data the PRIVMSG command
   * @return the tokenized PRIVMSG
   */
  public static String[] tokenize(String data){
    String[] arr;
    if(data.contains("PRIVMSG")){
      arr = data.split(":", 3);
      System.out.println(Arrays.toString(arr));
    }
    else{
      arr=data.split(":");
    }
    return Arrays.copyOfRange(arr, 1, arr.length);
  }
  /**
   * Replaces ipv6 ips with the word "ipv6" in the given data
   * @param data the given data
   * @return the given data with the ipv6 ips replaced by "ipv6"
   */
  public static String replaceIPV6(String data){
    String bak=data;
    data=data.replace("@", " ");
    String[] arr=data.split(" ");
    //matches an ipv6 ip
    Pattern pattern=Pattern.compile("^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))$");
    Matcher matcher;
    for(String word:arr){
      matcher=pattern.matcher(word);
      if(matcher.find()){
        bak=bak.replace(word,"ipv6");
      }
    }
    return bak;
  }
  /**
   * Gets the server from {@code arr}
   * @param arr the command
   * @return the server
   */
  public static String getServer(String[] arr){
    return arr[0].split(" ")[0];
  }
  /**
   * Gets the nickname from {@code arr}
   * @param arr the command
   * @return the nickname
   */
  public static String getNickname(String[] arr){
    return arr[0].split("!")[0];
  }
  /**
   * Gets the user from {@code arr}
   * @param arr the command
   * @return the user
   */
  public static String getUser(String[] arr){
    return arr[0].split("!")[1].split("@")[0];
  }
  /**
   * Gets the hostname from the {@code arr}
   * @param arr the command
   * @return the hostname
   */
  public static String getHostname(String[] arr){
    return arr[0].split("!")[1].split("@")[1].split(" ")[0];
  }
  /**
   * Strips the nickname
   * @param s the nickname to strip
   * @return the stripped nickname
   */
  public static String strippedNickname(String s){
    if(s.length()>1&&"@!&~".contains(String.valueOf(s.charAt(0)))){
      return s.substring(1);
    }
    return s;
  }
  /**
   * Gets the action from the {@code arr}
   * @param arr the command
   * @return the action
   */
  public static String getAction(String[] arr){
    return arr[0].split(" ")[1];
  }
  /**
   * Gets the recipient from the {@code arr}
   * @param arr the command
   * @return the recipient
   */
  public static String getRecipient(String[] arr){
    return arr[0].split(" ")[2];
  }
  /**
   * Gets the IRC code from {@code arr}
   * @param arr the command
   * @return the IRC code
   */
  public static int getIRCCode(String[] arr){
    try{
      int code;
      String[] aux = arr[0].split(" ");
      if(aux.length > 1){
        code = Integer.parseInt(arr[0].split(" ")[1]);
        return code;
      }
      else{
        return -1;
      }
    }
    catch(NumberFormatException e){
      e.printStackTrace();
    }
    return -1;
  }
  /**
   * Gets the channel from {@code arr}
   * @param arr the command
   * @return the channel
   */
  public static String getChannel(String[] arr){
    for(String s : arr[0].split(" ")){
      if(s.charAt(0) == '#'){
        return s;
      }
    }
    return "";
  }
  /**
   * Gets the message from the {@code arr}
   * @param arr the command
   * @return the message
   */
  public static String getMessage(String[] arr){
    if(arr.length > 1){
      return arr[arr.length-1];
    }
    return "";
  }

  /**
   * Checks if the message is a server message
   * @param arr the message command
   * @return true if the message is a server message and false otherwise
   */
  public static boolean isServerMessage(String[] arr){
    String aux = arr[0].split(" ")[0];
    return !aux.contains("!")||aux.contains("NickServ");
  }
  /**
   * Parses a message from {@code arr}
   * @param arr the command
   * @return the parsed message
   */
  public static MessageIRC parser(String[] arr){
    String server = "";
    String channel = "";
    String recipient = "";
    String[] user = new String[3];
    String msg;
    String action = "";
    String message_type = "";
    int code;
    //Check if it is a server message
    if(isServerMessage(arr)){
    	server = getServer(arr);
    	//Checks if message is from NickServ or just Server
    	if(arr[0].split(" ")[0].contains("NickServ")){
    	  message_type = "NS";
    	  channel = "NickServ";
        user[0] = "NickServ";
        user[1] = "NickServ";
        user[2] = "NickServ";
        action = "NOTICE";
      }
    	else{
        message_type = "S";
      }
    }
    else{
      //Get user names
      user[0] = strippedNickname(getNickname(arr));
      user[1] = getUser(arr);
      user[2] = getHostname(arr);
      //Get the channel
      channel = getChannel(arr);
      //Get action
      action = getAction(arr);
      //check if is a NICK action
      if(action.equals("NICK")){
        message_type = "N";
      }
      //Checks if action is NOTICE or PRIVMSG
      if(action.equals("NOTICE") || action.equals("PRIVMSG")){
        recipient = getRecipient(arr);
        //If recipient doesn't start with '#' so it is a normal user message
        //If not it is a channel message
        if(recipient.charAt(0) != '#'){
          channel = user[0];
          message_type = "UM";
        }
        else{
          channel = getChannel(arr);
          message_type = "C";
        }
      }
      // Check if message action is a JOIN, PART or QUIT
      if(action.equals("JOIN")){
        message_type = "UJ";
      }
      if(action.equals("PART")){
        message_type = "UP";
      }
      if(action.equals("QUIT")){
        message_type = "UQ";
      }
    }
    //Get code
    code = getIRCCode(arr);
    //forward channel message
    if(code == 470){
      //get new channel
      channel = arr[0].split(" ")[4];
    }
    //Get msg content
    msg = getMessage(arr);
    MessageIRC mesg = new MessageIRC();
    mesg.setServer(server);
    mesg.setUser(user);
    mesg.setAction(action);
    mesg.setRecipient(recipient);
    mesg.setCode(code);
    mesg.setChannel(channel);
    mesg.setMsg(msg);
    mesg.setMessage_type(message_type);
    return mesg;
  }
  /**
   * Parses a message from a command
   * @param message the command
   * @return the parsed message
   */
  public static MessageIRC parse_message(String message){
    //Check if message is a Ping
    if(message.split(" ")[0].equals("PING")){
      MessageIRC m = new MessageIRC();
      m.setMessage_type("P");
      return m;
    }
    message = replaceIPV6(message);
    String[] arr = tokenize(message);
    return parser(arr);
  }
}
