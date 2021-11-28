import java.io.*;
import java.net.*;

// Just a prototype

public class Prototype {

  public static void receive_data(BufferedReader in) throws Exception {
    while(true){
      String data = in.readLine();
      if(data != null){
        System.out.println(data);
      }
    }
  }

  public static void send_data(PrintWriter out) throws Exception {
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    while(true){
      String data = stdIn.readLine();
      out.println(data);
    }
  }

  public static void main(String[] args) throws Exception {
    String nick = "userHeni";
    String user = "userHeni";
    String channel = "#heni";

    // Create socket
    Socket socket = new Socket("irc.libera.chat", 6667);

    // receive data from the server
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    // send data to the server
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

    // log in

    out.println("NICK userHeni \r\nPASS userheni123 \r\nUSER userHeni userHeni userHeni :garfadadesopa \r\n");
    //out.println("USER " + user + " " + user + " " + user + " " + " :sopa \r\n");
    out.println("JOIN " + channel + "\r\n");

    // create threads to receive and send data
    Thread receive = new Thread(() -> {
      try {
        receive_data(in);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Thread send = new Thread(() -> {
      try {
        send_data(out);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    // start the threads
    receive.start();
    send.start();

    // join the threads
    receive.join();
    send.join();

  }

}
