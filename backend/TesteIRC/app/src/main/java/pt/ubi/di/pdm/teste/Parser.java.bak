import java.util.*;
import java.io.*;

import javax.sound.sampled.SourceDataLine;

public class Parser{
  
  public static String[] tokenize(String data){
    String[] arr = data.split(":");   
    return Arrays.copyOfRange(arr, 1, arr.length);  
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
      code = Integer.parseInt(arr[0].split(" ")[1]);
      return code;
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
    
    System.out.println(Arrays.toString(arr));
    if(arr.length > 1){
      return arr[arr.length-1];
    }
    return "";
  }

  public static Message parser(String[] arr){
		String server = "";
		String channel = "";
		String recipient = "";
		String[] user = new String[3];
		String msg = "";
    String action = "";
    int code = -1;

    if(isServerMessage(arr)){
    	server = getServer(arr);
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

    Message mesg = new Message();
    mesg.setServer(server);
    mesg.setUser(user);
    mesg.setAction(action);
    mesg.setRecipient(recipient);
    mesg.setCode(code);
    mesg.setChannel(channel);
    mesg.setMsg(msg);

    //System.out.println(mesg.toString());
    return mesg;
  }

  public static void main(String[] args){

   	try {
      File myObj = new File("sample");
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        String[] arr = tokenize(data);
        System.out.println(parser(arr).toString());
        System.out.println();
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }  
  }
}
