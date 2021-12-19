package pt.ubi.di.pdm.pocketirc;

//imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a server. Handles server connection and messaging
 */
public class Server{
  //== attributes ==
  private final int port;
  private final String ip;
  private Socket socket;
  public BufferedReader in;
  public PrintWriter out;
  public final Commands cmd;
  public final List<String>channels;

  //== constructors ==
  /**
   * Constructs a new server object with the given ip and port, creating the socket for it
   * @param ip the given ip
   * @param port the given port
   */
  public Server(String ip, int port){
    this.ip=ip;
    this.port=port;
    this.channels=new ArrayList<>();
    //create socket
    try{
      this.socket=new Socket(ip,port);
      this.in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.out=new PrintWriter(socket.getOutputStream(),true);
    }
    catch(IOException e){
      e.printStackTrace();
    }
    this.cmd=new Commands(this.out);
  }

  //== methods ==
  /**
   * Authenticates a new user in the server
   * @param nick the nickname of the user
   * @param pass the password of the user
   * @param user the name of the user
   * @param realname the realname of the user
   */
  public void login(String nick, String pass, String user, String realname){
    this.cmd.nick(nick);
    this.cmd.pass(pass);
    this.cmd.user(user,user,user,realname);
  }
  /**
   * Joins to a new channel
   * @param channel the channel to join into
   * @param keys the needed keys
   */
  public void join(String channel, String keys){
    this.channels.add(channel);
    this.cmd.join(channel, keys);
  }
  public void create_connection(){
    try{
      this.socket = new Socket(ip, port);
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch (IOException e){
      e.printStackTrace();
    }
  }
  /**
   * Send a message to the server
   * @param message the message to send
   */
  public void send_message(String message){
    this.out.println(message);
  }
  /**
   * Receives a message from the server
   * @return the message from the server
   * @throws Exception a possible IO error
   */
  public String receive_message() throws Exception{
    return this.in.readLine();
  }
}