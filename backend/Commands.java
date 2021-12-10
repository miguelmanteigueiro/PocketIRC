// Commands:
/*
- away
- back
- clear
- cycle
- ignore
- invite
- kick
- msg

- nick
- notice
- part
- query
- topic
- unignore
- whois
- whowas
*/

class Commands{
  
  PrintWriter out;
  private String suffix = " \r\n";

  public Commands(PrintWriter out){
    this.out = out;
  }
 
  public static away(String s){
    this.out.println("AWAY " + s + suffix);
  }
}
