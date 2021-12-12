/* Commands:
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

class Commands{
  
  PrintWriter out;
  private String suffix = " \r\n";

  public Commands(PrintWriter out){
    this.out = out;
  }
 
  public away(String message){
    this.out.println("AWAY " + message + suffix);
  }

  public back(){
    this.out.println("AWAY " + suffix); 
  }

  public clear(){
    // clears the screen 
  }
  
  public cycle(String channel){
    // leave channel and join again
    this.out.println("PART " + channel + suffix);
    this.out.println("JOIN " + channel + suffix);
  }

  public invite(String nickname, String channel){
    this.out.println("INVITE " + nickname + " " + channel + suffix);
  }

  public kick(String channel, String client, String message){
    this.out.println("KICK " + channel + " " + client + " :" + message + suffix); 
  }

  public msg(String target, String message){
    this.out.println("PRIVMSG " + target + " :" + message + suffix);
  }

  public nick(String nickname){
    this.out.println("NICK " + nickname + suffix);
  }

  // nao e' necessario verificar a validade do msgtarget
  public notice(String msgtarget, String message){
    this.out.println("NOTICE " + msgtarget + " " + message + suffix);
  }

  public part(String channel, String message){
    this.out.println("PART " + channel + " " + message + suffix);
  }

  // tratar caso em que channel nao comece com #
  // ver https://datatracker.ietf.org/doc/html/rfc2812#section-3.2.4
  public topic(String channel, String topic){
    if(channel.charAt(0) == "#"){
      this.out.println("TOPIC " + channel + " " + topic + suffix);
    }
  }
  // public unignore(String nickname){
  //   this.out.println()
  // }

  public whois(String nickname){
    this.out.println("WHOIS " + nickname + suffix);
  }
}
