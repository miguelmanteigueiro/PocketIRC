import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser_v2{
  
  public static String[] tokenize(String data){
    String[] arr;
    if(data.contains("PRIVMSG")){
      arr = data.split(":", 3);
      System.out.println(Arrays.toString(arr));
    }
    else{
      arr = data.split(":");
    }
    return Arrays.copyOfRange(arr, 1, arr.length);  
  }

  public static String replaceIPV6(String data){
    // Checks if IPV6 is present in the data, if so then replace it with 'ipv6'
    Pattern pattern = Pattern.compile("(?:[0-9a-f]{1,4}:){7}[0-9a-f]{1,4}");
    // define a matcher
    Matcher matcher = pattern.matcher(data);
    if(matcher.find()){
      data = data.replace(matcher.group(), "ipv6");
      return data;
    }
    return data;
  }

  public static boolean isServerMessage(String[] arr){
    String aux = arr[0].split(" ")[0];
    if(!aux.contains("!") || aux.contains("NickServ")){
      return true;
    }
    return false;
  }

  public static String getServer(String[] arr){
		return arr[0].split(" ")[0];
	}

  public static String getNickname(String[] arr){
    return arr[0].split("!")[0];
  }

  public static String getUser(String[] arr){
    return arr[0].split("!")[1].split("@")[0];
  }

  public static String getHostname(String[] arr){
    return arr[0].split("!")[1].split("@")[1].split(" ")[0];
  }

  public static String strippedNickname(String s){
    if(s.length() > 1 && "@!&~".contains(String.valueOf(s.charAt(0)))){
      return s.substring(1);
    }
    return s;
  }

  public static String getAction(String[] arr){
    return arr[0].split(" ")[1];
  }

  public static String getRecipient(String[] arr){
    return arr[0].split(" ")[2];
  }

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
    catch(NumberFormatException e){}
    return -1;
  }

  public static String getChannel(String[] arr){
    for(String s : arr[0].split(" ")){
      if(s.charAt(0) == '#'){
        return s; 
      }
    }
    return "";
  }

  public static String getMessage(String[] arr){

    if(arr.length > 1){
      return arr[arr.length-1];
    }
    return "";
  }

  public static MessageIRC parser(String[] arr){
    String server = "";
    String channel = "";
    String recipient = "";
    String[] user = new String[3];
    String msg = "";
    String action = "";
    String message_type = "";
    int code = -1;

    // Check if it is a server message
    if(isServerMessage(arr)){
    	server = getServer(arr);
    	// Checks if message is from NickServ or just Server
    	if(arr[0].split(" ")[0].contains("NickServ")){
    	  message_type = "NS";
      }
    	else{
        System.out.println(Arrays.toString(arr));
        message_type = "S";
      }
    }
    else{
      // Get user names
      user[0] = strippedNickname(getNickname(arr));
      user[1] = getUser(arr);
      user[2] = getHostname(arr);

      // Get action
      action = getAction(arr);

      // Checks if action is NOTICE or PRIVMSG
      if(action.equals("NOTICE") || action.equals("PRIVMSG")){
        recipient = getRecipient(arr);
        // If recipient doesn't start with '#' so it is a normal user message
        // If not it is a channel message
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
        channel = getChannel(arr);
      }
      if(action.equals("PART")){
        message_type = "UP";
        channel = getChannel(arr);
      }
      if(action.equals("QUIT")){
        message_type = "UQ";
        channel = getChannel(arr);
      }
    }
    // Get code
    code = getIRCCode(arr);
    // Get msg content
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

  public static MessageIRC parse_message(String message){
    // Check if message is a Ping
    if(message.split(" ")[0].equals("PING")){
      MessageIRC m = new MessageIRC();
      m.setMessage_type("P");
      return m;
    }
    message = replaceIPV6(message);
    String[] arr = tokenize(message);

    return parser(arr);
  }

  public static void main(String[] args){
   	try {
      File myObj = new File("sample");
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        System.out.println(parse_message(data).toString());
        System.out.println();
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }  
  }
}
