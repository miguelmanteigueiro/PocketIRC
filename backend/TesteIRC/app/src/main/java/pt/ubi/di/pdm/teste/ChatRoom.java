package pt.ubi.di.pdm.teste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatRoom extends Activity implements MessageRecyclerViewAdapter.ItemClickListener{

  //Instantiate the list
  ArrayList<MessageIRC> messageList = new ArrayList<MessageIRC>();
  ArrayList<MessageIRC> userList = new ArrayList<MessageIRC>();

  //Instantiate the widgets
  ImageButton sendButton;
  ImageButton userButton;
  EditText sendMessage;
  TextView roomName;
  MessageRecyclerViewAdapter messageAdapter;
  RecyclerView messageRecyclerView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //Initiate the view
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatroom);

    //Initiate the widgets
    sendButton = (ImageButton)findViewById(R.id.chat_sendButton);
    userButton = (ImageButton)findViewById(R.id.chat_userButton);
    sendMessage = (EditText)findViewById(R.id.chat_sendMessageBox);
    roomName = (TextView) findViewById(R.id.chat_roomName);
    messageRecyclerView = (RecyclerView) findViewById(R.id.chat_messageContainer);

    //Setup the message recycler view
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

    messageRecyclerView.setLayoutManager(linearLayoutManager);
    messageAdapter = new MessageRecyclerViewAdapter(this, messageList);
    messageRecyclerView.setAdapter(messageAdapter);
    messageAdapter.setClickListener(this);

    // HashMap that handles the messages by channels
    HashMap<String, ArrayList<MessageIRC>> channels_messageList = new HashMap<String, ArrayList<MessageIRC>>();


    /* Server connection and threads */
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    Server server = new Server("irc.libera.chat", 6667);
    server.login("userHeni", "userheni123", "userHeni", ":garfadaDeSopa");
    server.join("#heni", "");

    // create a blocking queue
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    // create a producer and consumer
    Runnable producer = new Runnable() {
      @Override
      public void run() {
        try {
          while (true) {
            String data = server.receive_message();
            if (data != null) {
              queue.put(data);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };

    Runnable consumer = new Runnable() {
      public void run() {
        while (true) {
          String message = "";
          try {
            message = queue.take();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          String finalMessage = message;
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //Message received from chat
              MessageIRC m = Parser.parse_message(finalMessage);
              String channel = m.getChannel();

              if(!channel.equals("")){
                if(!channels_messageList.containsKey(channel)){
                  channels_messageList.put(channel, new ArrayList<>());
                }
              }

              if(m.getIs_user()){
                //Checks if the previous message is the user's or there are none
                if(m.getAction().equals("PRIVMSG") && (channels_messageList.get(channel).size() == 0 || !channels_messageList.get(channel).get(0).getAction().equals("PRIVMSG") || !channels_messageList.get(channel).get(0).getUser()[0].equals(m.getUser()[0]))) {
                  Calendar cal = Calendar.getInstance();
                  //This add is the username that appears on the screen
                  m.setHour(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));

                  // checks if hashmap contains channel
                  // if so append the message to the existent message list
                  // if don't create a new list and then append the message to it
                  channels_messageList.get(channel).add(0, m);
                }

                System.out.println("MESSAGE: " + m.toString());
                channels_messageList.get(channel).add(0, m);

                messageAdapter = new MessageRecyclerViewAdapter(getApplicationContext(), channels_messageList.get(channel));
                messageRecyclerView.setAdapter(messageAdapter);
                messageAdapter.setClickListener(ChatRoom.this);

              }
              else{
                if(!channels_messageList.containsKey("NickServer")){
                  channels_messageList.put("NickServer", new ArrayList<>());
                }
                channels_messageList.get("NickServer").add(m);
              }
            }
          });
        }
      }
    };

    new Thread(producer).start();
    new Thread(consumer).start();


    //User a
    sendButton.setOnClickListener(
      oView -> {
        String aux = sendMessage.getText().toString();

        if(!isBlank(aux) && !aux.isEmpty())
        {
          //Updates message list
          MessageIRC m = new MessageIRC();
          aux = aux.trim();
          m.setMsg(aux);

          //Update message time
          Calendar now = Calendar.getInstance();
          String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
          int auxMin = now.get(Calendar.MINUTE);
          String minute = (auxMin < 10)? "0"+String.valueOf(auxMin) : String.valueOf(auxMin);
          m.setHour(hour+":"+minute);

          //Update message user
          m.setUser(new String[]{"Simao", "a", "a"});

          //Checks if the previous message is the user's or there are none
          if(messageList.size() == 0 || !messageList.get(0).getUser()[0].equals(m.getUser()[0])){
            messageList.add(0,m);
          }

          //Adds the message to the list
          messageList.add(0,m);

          //Update recyclerView
          messageAdapter = new MessageRecyclerViewAdapter(this, messageList);
          messageRecyclerView.setAdapter(messageAdapter);
          messageAdapter.setClickListener(this);
          System.out.println(m.getUser()[1]);

          //Empty chat text
          sendMessage.setText("");
        }
      }
    );

    //user b
    /*userButton.setOnClickListener(
      oView -> {
        String aux = sendMessage.getText().toString();

        if(!isBlank(aux) && !aux.isEmpty())
        {
          //Updates message list
          MessageIRC m = new MessageIRC();
          aux = aux.trim();
          m.setMsg(aux);
          m.setUser(new String[]{"Barrico", "b", "b"});

          Calendar now = Calendar.getInstance();
          String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
          int auxMin = now.get(Calendar.MINUTE);
          String minute = (auxMin < 10)? "0"+String.valueOf(auxMin) : String.valueOf(auxMin);
          m.setHour(hour+":"+minute);

          if(messageList.size() == 0 || !messageList.get(0).getUser()[0].equals(m.getUser()[0]))
          {
            messageList.add(0,m);
          }

          messageList.add(0,m);

          //Update recyclerView
          //messageAdapter = new MessageRecyclerViewAdapter(this, userList,1);
          messageAdapter = new MessageRecyclerViewAdapter(this, messageList);
          messageRecyclerView.setAdapter(messageAdapter);
          messageAdapter.setClickListener(this);

          //Empty chat text
          sendMessage.setText("");
        }
      }
    );*/


  }

  //Verifies if a string is made entirely of blank spaces
  public static boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  //If any Item of the list is clicked
  @Override
  public void onItemClick(View view, int position) {
    MessageIRC m = messageAdapter.getItem(position);
    System.out.println(m.getMsg());
  }


}