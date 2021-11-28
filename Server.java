import java.io.*;
import java.net.*;

public class Server{
  int port;
  String ip;

  String nick;
  String user;
  String channel;

  Socket socket;
  BufferedReader in;
  PrintWriter out;

  public Server(String ip, int port){
    assert (ip != null)
    this.ip = ip;
    this.port = port;

    this.socket = new Socket(ip, port);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(socket.getOutputStream(), true);
  }

  public l
}
