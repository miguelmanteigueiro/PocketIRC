package pt.ubi.di.pdm.pocketirc;

/* Commands:
- away      FEITO GUI TODO
- back      FEITO GUI TODO
- clear     TODO NO ANDROID GUI
- ignore    NOT NOW TODO
- invite    FEITO DONE
- kick      FEITO DONE
- msg       FEITO GUI DONE
- nick      FEITO GUI TODO
- notice    FEITO DONE
- part      FEITO GUI DONE
- topic     FEITO GUI TODO
- whois     FEITO GUI TODO
*/

//imports
import java.io.PrintWriter;

/**
 * IRChat commands used in the application.
 * Based on: https://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands
 */
public class Commands{
  //== attributes ==
  private final PrintWriter out;
  private final String suffix=" \r\n";

  //== constructors ==

  /**
   * Constructs a new Commands object.
   * @param out the out
   */
  public Commands(PrintWriter out){
    this.out=out;
  }

  //== methods ==
  /**
   * Provides the server with a message to automatically send in reply to a
   * PRIVMSG directed at the user, but not to a channel they are on.
   * If {@code message} is omitted, the away status is removed.
   * @param message the message to send
   */
  public void away(String message){
    if(message.equals("")){
      message="brb";
    }
    this.out.println("AWAY "+message+suffix);
  }
  /**
   * Goes back
   */
  public void back(){
    this.out.println("AWAY " + suffix); 
  }
  /**
   * Clears the screen
   */
  public void clear(){
    // clears the screen 
  }
  /**
   * Invites {@code nickname} to the channel {@code channel}.
   * @param nickname the user to invite
   * @param channel the channel to invite the user to. It does not have to exist, but
   *                if it does, only members of the channel are allowed to invite other clients.
   */
  public void invite(String nickname, String channel){
    this.out.println("INVITE " + nickname + " " + channel + suffix);
  }
  /**
   * Forcibly removes {@code client} from {@code channel} with the given {@code message}
   * @param channel the channel to remove the client from
   * @param client the client to remove from the channel
   * @param message the message shown, when the client is kicked for the channel
   */
  public void kick(String channel, String client, String message){
    this.out.println("KICK " + channel + " " + client + " :" + message + suffix); 
  }
  /**
   * Sends {@code message} to {@code target}, which is usually a user or channel
   * @param target the target to send the message to
   * @param message the message to send to a target
   */
  public void msg(String target, String message){
    this.out.println("PRIVMSG " + target + " :" + message + suffix);
  }
  /**
   * Changes the client's nickname to the given one
   * @param nickname the new nickname
   */
  public void nick(String nickname){
    this.out.println("NICK " + nickname + suffix);
  }
  /**
   * Sets a connection password
   * @param password the connection password
   */
  public void pass(String password){
    this.out.println("PASS "+password+suffix);
  }
  /**
   * Specifies the username, hostname, real name and initial user modes of the connecting client
   * @param username the given username
   * @param hostname the given hostname
   * @param servername the given server name
   * @param realname the given real name (may contain spaces and must, thus, be prefixed with :)
   */
  public void user(String username, String hostname, String servername, String realname){
    this.out.println("USER " + username + " " + hostname + " " + servername + " " + realname + suffix);
  }
  /**
   * Send a message that does not accept automatic replies
   * @param msgtarget the target to message
   * @param message the message to send
   */
  public void notice(String msgtarget, String message){
    this.out.println("NOTICE " + msgtarget + " " + message + suffix);
  }
  /**
   * Causes a user to leave the given {@code channel} with the given {@code message}
   * @param channel the channel to leave
   * @param message the message to show when the user leaves the channel
   */
  public void part(String channel, String message){
    this.out.println("PART " + channel + " " + message + suffix);
  }
  /**
   * Sets the channel topic of a channel
   * @param channel the channel to change the topic
   * @param topic the new topic
   */
  public void topic(String channel, String topic){
    this.out.println("TOPIC " + channel + " " + topic + suffix);
  }
  /**
   * Reply to the ping command
   */
  public void pong(){
    this.out.println("PONG" + suffix);
  }
  /**
   * Shows information about the nickname
   * @param nickname the given nickname to show information
   */
  public void whois(String nickname){
    this.out.println("WHOIS " + nickname + suffix);
  }
  /**
   * Makes the client join the channels in the comma-separated list {@code channels},
   * specifying the passwords, if needed, in the comma-separated list {@code keys}
   * @param channels the given channels
   * @param keys the given passwords
   */
  public void join(String channels, String keys){
    this.out.println("JOIN " + channels + " " + keys + suffix);
  }
  /**
   * Shows a list of who is on the comma-separated list of {@code channels} by channel name.
   * If {@code channels} is omitted, all users are shown, grouped by channel name with all users
   * who are not on a channel being shown as part of channel "*"
   * @param channels the channels to show the users from
   */
  public void names(String channels){
    this.out.println("NAMES " + channels + suffix);
  }
  /**
   * Replaces the command with the given one
   * @param s the command to replace
   * @return the replaced command
   */
  public static String replaceCommand(String s){
    if(s.startsWith("/notice")){
      return s.replace("/notice","NOTICE");
    }
    if(s.startsWith("/invite")){
      return s.replace("/invite","INVITE");
    }
    if(s.startsWith("/kick")){
      return s.replace("/kick","KICK");
    }
    if(s.startsWith("/nick")){
      return s.replace("/nick","NICK");
    }
    return s;
  }
  /**
   * Checks if the given string begins with /
   * @param s the given string
   * @return true if the given string begins with / and false otherwise
   */
  public static boolean checkIfCommand(String s){
    return s.charAt(0)=='/';
  }
}