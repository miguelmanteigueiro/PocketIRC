import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server{
  int port;
  String ip;

  String nick;
  String user;
  String pass;
  String realname;
  List<String> channels;

  Socket socket;
  BufferedReader in;
  PrintWriter out;

  Thread receive;
  Thread send;

  public Server(String ip, int port) throws Exception{
    assert (ip != null);
    this.ip = ip;
    this.port = port;
    this.channels = new ArrayList<String>();
    
    this.socket = new Socket(ip, port);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(socket.getOutputStream(), true);
  }

  public void login(String nick, String pass, String user, String realname) throws Exception{
    this.nick = nick;
    this.pass = pass;
    this.user = user;
    this.realname = realname;
    
    // send login command
    this.out.println("NICK " + this.nick + " \r\n");
    this.out.println("PASS " + this.pass + " \r\n");
    this.out.println("USER " + this.user + " " + this.user + " " + this.user + " " + this.realname + " \r\n");

  }

  public void join(String channel) throws Exception{
    assert (channel.charAt(0) == '#');
    this.channels.add(channel);
    
    this.out.println("JOIN " + channel + " \r\n");
  }
  
  public void receive_data() throws Exception{
    while(true){
      String data = this.in.readLine();
      if(data != null){
        // print in text view
        System.out.println(data);
      }
    }
  }

  public void send_data() throws Exception{
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    while(true){
      String data = stdIn.readLine();
      this.out.println(data);
    }
  }

  public void send_data2(String s) throws Exception{
    //BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    while(true){
      this.out.println(s);
    }
  }
}
