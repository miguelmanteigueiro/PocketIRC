package pt.ubi.di.pdm.teste;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser{
  
  public static String[] tokenize(String data){
    String[] arr = data.split(":");
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
    if(!aux.contains("!")){
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
    int code = -1;
    boolean is_user = true;

    if(isServerMessage(arr)){
    	server = getServer(arr);
    	is_user = false;
    }
    else{
      user[0] = strippedNickname(getNickname(arr));
      user[1] = getUser(arr);
      user[2] = getHostname(arr);
      action = getAction(arr);
      if(action.equals("NOTICE") || action.equals("PRIVMSG")){
        recipient = getRecipient(arr);
      }
    }
    code = getIRCCode(arr);
    channel = getChannel(arr);
    msg = getMessage(arr);
    if(action.equals("PART")){
      msg = channel;
    }
    if(action.equals("JOIN")){
      channel = getMessage(arr);
    }

    MessageIRC mesg = new MessageIRC();
    mesg.setServer(server);
    mesg.setUser(user);
    mesg.setAction(action);
    mesg.setRecipient(recipient);
    mesg.setCode(code);
    mesg.setChannel(channel);
    mesg.setMsg(msg);
    mesg.setIs_user(is_user);
    System.out.println(mesg.toString());
    return mesg;
  }

  public static MessageIRC parse_message(String message){
    message = replaceIPV6(message);
    String[] arr = tokenize(message);
    return parser(arr);
  }
}
