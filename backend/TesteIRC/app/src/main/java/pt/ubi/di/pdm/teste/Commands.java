package pt.ubi.di.pdm.teste;/* Commands:
- away      FEITO
- back      FEITO
- clear     TODO NO ANDROID
- cycle     FEITO
- ignore    NOT NOW
- invite    FEITO
- kick      FEITO
- msg       FEITO
- nick      FEITO
- notice    FEITO
- part      FEITO
- topic     FEITO
- unignore  NOT NOW
- whois     FEITO
*/

import java.io.PrintWriter;

/*
Commands based on: https://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands
 */
class Commands{
  
  private PrintWriter out;
  private String suffix = " \r\n";

  public Commands(PrintWriter out){
    this.out = out;
  }
 
  public void away(String message){
    this.out.println("AWAY " + message + suffix);
  }

  public void back(){
    this.out.println("AWAY " + suffix); 
  }

  public void clear(){
    // clears the screen 
  }
  
  public void cycle(String channel){
    // leave channel and join again
    this.out.println("PART " + channel + suffix);
    this.out.println("JOIN " + channel + suffix);
  }

  public void invite(String nickname, String channel){
    this.out.println("INVITE " + nickname + " " + channel + suffix);
  }

  public void kick(String channel, String client, String message){
    this.out.println("KICK " + channel + " " + client + " :" + message + suffix); 
  }

  public void msg(String target, String message){
    this.out.println("PRIVMSG " + target + " :" + message + suffix);
  }

  public void nick(String nickname){
    this.out.println("NICK " + nickname + suffix);
  }

  public void pass(String password){
    this.out.println("PASS " + password + suffix);
  }
  // USER <username> <hostname> <servername> <realname>
  public void user(String username, String hostname, String servername, String realname){
    this.out.println("USER " + username + " " + hostname + " " + servername + " " + realname + suffix);
  }

  // nao e' necessario verificar a validade do msgtarget
  public void notice(String msgtarget, String message){
    this.out.println("NOTICE " + msgtarget + " " + message + suffix);
  }

  public void part(String channel, String message){
    this.out.println("PART " + channel + " " + message + suffix);
  }

  // tratar caso em que channel nao comece com #
  // ver https://datatracker.ietf.org/doc/html/rfc2812#section-3.2.4
  public void topic(String channel, String topic){
    if(channel.charAt(0) == '#'){
      this.out.println("TOPIC " + channel + " " + topic + suffix);
    }
  }

  // send a pong
  public void pong(){
    this.out.println("PONG");
  }

  // public unignore(String nickname){
  //   this.out.println()
  // }

  public void whois(String nickname){
    this.out.println("WHOIS " + nickname + suffix);
  }

  // channels and keys separeted with commas
  public void join(String channels, String keys){
    this.out.println("JOIN " + channels + " " + keys + suffix);
  }

  // check if string begins with /
  public static boolean checkIfCommand(String s){
    return s.charAt(0) == '/';
  }

  public static String replaceCommand(String s){
    if(s.startsWith("/msg")){
      String replaced = s.replace("/msg", "PRIVMSG");
      return replaced;
    }
    if(s.startsWith("/me")){
      String replaced = s.replace("/me", "ACTION");
      return replaced;
    }
    if(s.startsWith("/back")){
      String replaced = s.replace("/back", "AWAY");
      return replaced;
    }
    if(s.startsWith("/away")){
      String replaced = s.replace("/away", "AWAY");
      return replaced;
    }
    if(s.startsWith("/part")){
      String replaced = s.replace("/part", "PART");
      return replaced;
    }
    if(s.startsWith("/join")){
      String replaced = s.replace("/join", "JOIN");
      return replaced;
    }
    if(s.startsWith("/whois")){
      String replaced = s.replace("/whois", "WHOIS");
      return replaced;
    }
    if(s.startsWith("/notice")){
      String replaced = s.replace("/notice", "NOTICE");
      return replaced;
    }
    if(s.startsWith("/invite")){
      String replaced = s.replace("/invite", "INVITE");
      return replaced;
    }
    if(s.startsWith("/kick")){
      String replaced = s.replace("/kick", "KICK");
      return replaced;
    }
    if(s.startsWith("/pass")){
      String replaced = s.replace("/pass", "PASS");
      return replaced;
    }
    if(s.startsWith("/nick")){
      String replaced = s.replace("/nick", "NICK");
      return replaced;
    }
    if(s.startsWith("/user")){
      String replaced = s.replace("/user", "USER");
      return replaced;
    }
    return s; //failsafe
  }
}
