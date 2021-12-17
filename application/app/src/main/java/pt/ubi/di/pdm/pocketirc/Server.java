package pt.ubi.di.pdm.pocketirc;

import android.text.Editable;

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
  Commands cmd;

  public Server(String ip, int port){
    assert (ip != null);
    this.ip = ip;
    this.port = port;
    this.channels = new ArrayList<String>();

    // Create socket
    try{
      this.socket = new Socket(ip, port);
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch (IOException e){
      System.out.println(e);
    }
    this.cmd = new Commands(this.out);
  }

  public void login(String nick, String pass, String user, String realname){
    this.nick = nick;
    this.pass = pass;
    this.user = user;
    this.realname = realname;
    
    // send login command
    this.cmd.nick(this.nick);
    this.cmd.pass(this.pass);
    this.cmd.user(this.user, this.user, this.user, this.realname);
  }
  public void join(String channel, String keys){
    // Check if channel starts with '#'
    // If not toast error
    //if(channels.charAt(0) == '#'){};
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
      System.out.println(e);
    }
  }

  public void send_message(String message){
    this.out.println(message);
  }

  public String receive_message() throws Exception{
    return this.in.readLine();
  }
}
