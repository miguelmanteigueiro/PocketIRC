import java.util.*;

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
    int code;
    try{
      code = Integer.parseInt(arr[0].split(" ")[1]);
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
    //return arr[0].split(" ")[2];
    if(arr.length > 1){
      return Arrays.copyOfRange(arr, 1, arr.length);
    }
    return "";
  }

  public static void parser(String[] arr){
		String server = "";
		String channel = "";
		String recipient = "";
		String[] user = new String[3];
		String type = "";
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
      code = getIRCCode(arr);
      channel = getChannel(arr);
      message = getMessage(arr);
      if(action.equals("PART")){
        message = channel;
      }
      if(action.equals("JOIN")){
        channel = getMessage(arr);
      }
    }

		System.out.println(server);
		System.out.println(Arrays.toString(user));
		System.out.println(action);
		System.out.println(recipient);
		System.out.println(code);
		System.out.println(channel);
  }

  public static void main(String[] args){
    //String s = ":strontium.libera.chat 376 userHeni :End of /MOTD command." ;
    String s = ":NickServ!NickServ@services.libera.chat NOTICE userHeni :This nickname is registered. Please choose a different nickname, or identify via /msg NickServ IDENTIFY userHeni <password>";
    String[] arr = tokenize(s);
    System.out.println(Arrays.toString(arr));
		parser(arr);
  }







  /*
  public static void parser(String data){
    String[] arr = data.split(":");   
    String[] header = arr[1].split(" ");
    String message = arr[2];
    System.out.println(message);
  }

  */
}
