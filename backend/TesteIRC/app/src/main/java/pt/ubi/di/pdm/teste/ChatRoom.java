package pt.ubi.di.pdm.teste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.TypedArrayUtils;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatRoom extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener{

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
  Toolbar toolbar;

  //Instantiate the drawers
  private DrawerLayout drawerLayout;
  private NavigationView navView;
  private NavigationView navView2;

  //Create the private chats list and the chats list
  private ArrayList<String> chatsList;
  private ArrayList<String> privateChatsList;

  //Chat variables
  Commands cmd;
  String userName;
  String chatName;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //Initiate the view
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatroom);

    //=======================================Drawers===============================================
    //Drawer Variables
    navView = (NavigationView) findViewById(R.id.nav_view);
    navView2 = (NavigationView) findViewById(R.id.nav_view2);
    toolbar = (Toolbar) findViewById(R.id.chat_header);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

    Toolbar toolbar = (Toolbar) findViewById(R.id.chat_header);
    setSupportActionBar(toolbar);

    Button burguer1 = (Button) findViewById(R.id.switch1);
    Button burguer2 = (Button) findViewById(R.id.switch2);

    burguer1.setOnClickListener(v -> {
      drawerLayout.openDrawer(navView);
    });
    burguer2.setOnClickListener(v -> {
      drawerLayout.openDrawer(navView2);
    });

    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.channel) {
          Toast.makeText(ChatRoom.this, "Channels", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.privMassages) {
          Toast.makeText(ChatRoom.this, "Private Messages", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.random) {
          Toast.makeText(ChatRoom.this, "Random", Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    });
    navView2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.channel2) {
          Toast.makeText(ChatRoom.this, "Channels", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.privMassages2) {
          Toast.makeText(ChatRoom.this, "Private Messages", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.random2) {
          Toast.makeText(ChatRoom.this, "Random", Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    });

    //========================================================================================


    //Initiate the widgets
    sendButton = (ImageButton)findViewById(R.id.chat_sendButton);
    sendMessage = (EditText)findViewById(R.id.chat_sendMessageBox);
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

    userName = "userHeni";
    chatName = "#heni";

    toolbar.setTitle(chatName);

    Server server = new Server("irc.libera.chat", 6667);
    server.login(userName, "userheni123", "userHeni", ":garfadaDeSopa");
    server.join(chatName, "");

    cmd = new Commands(server.out);

    //Initialize the chat lists
    privateChatsList = new ArrayList<>();
    chatsList = new ArrayList<>();
    chatsList.add(chatName);

    //Add to the hashmap
    channels_messageList.put(chatName,new ArrayList<>());

    // create a blocking queue
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    // create a producer and consumer

    //Send message
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

    //Receive message
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

              if(channel.equals("")){
                channel = chatName;
              }

              // Check if is a user message
              if(m.getMessage_type().equals("UM")){
                // Add user message to privateChatsList
                if(!privateChatsList.contains(channel)){
                  privateChatsList.add(channel);
                }
              }

              // Add special message to the list to display User name
              if(m.getMessage_type().equals("C") || m.getMessage_type().equals("UM")){
                // Checks if the previous user is different from the current
                if(channels_messageList.get(channel).size() == 0 ||
                  !channels_messageList.get(channel).get(0).getUser()[0].equals(m.getUser()[0])){
                  Calendar cal = Calendar.getInstance();
                  //This add is the username that appears on the screen
                  String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                  int auxMin = cal.get(Calendar.MINUTE);
                  String minute = (auxMin < 10)? "0"+String.valueOf(auxMin) : String.valueOf(auxMin);
                  m.setHour(hour+":"+minute);

                  // checks if hashmap contains channel
                  // if so append the message to the existent message list
                  // if don't create a new list and then append the message to it
                  channels_messageList.get(channel).add(0, m);
                }
                // Add message to the list
                channels_messageList.get(channel).add(0, m);
              }

              // Check JOIN, PART and QUIT message
              if(Arrays.asList(new String[]{"UJ", "UP", "UQ"}).contains(m.getMessage_type())){
                m.setUser(new String[]{"", m.getUser()[0], ""}); //🩹
                // Quit appears on current channel no matter if user is on it
                if(m.getMessage_type().equals("UQ")) {
                  m.setChannel(channel);
                }
                channels_messageList.get(channel).add(0, m);
              }

              // Update message Adapter
              messageAdapter = new MessageRecyclerViewAdapter(getApplicationContext(), channels_messageList.get(channel));
              messageRecyclerView.setAdapter(messageAdapter);
              messageAdapter.setClickListener(ChatRoom.this);
            }
          });
        }
      }
    };

    new Thread(producer).start();
    new Thread(consumer).start();


    //Send button
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
          m.setUser(new String[]{userName, userName, userName});
          m.setMessage_type("C");
          m.setAction("PRIVMSG");
          m.setChannel(chatName);
          m.setRecipient(chatName);


          if(!channels_messageList.get(chatName).isEmpty()) {
            //Checks if the previous message is the user's or there are none
            //channels_messageList.get(chatName).size() == 0 ||
            if(!channels_messageList.get(chatName).get(0).getAction().equals("PRIVMSG") || !channels_messageList.get(chatName).get(0).getUser()[0].equals(m.getUser()[0])){
              channels_messageList.get(chatName).add(0,m);
            }

            //Adds the message to the list
            channels_messageList.get(chatName).add(0, m);

            //Send message to server
            String temp_message = m.getMsg();
            if(Commands.checkIfCommand(temp_message)){
              // Parse the command message
              temp_message = Commands.replaceCommand(temp_message);
              server.send_message(temp_message);
            }
            else{
              cmd.msg(chatName, m.getMsg());
            }

            //Update recyclerView
            messageAdapter = new MessageRecyclerViewAdapter(this, channels_messageList.get(chatName));
            messageRecyclerView.setAdapter(messageAdapter);
            messageAdapter.setClickListener(this);
          }
          else{
            Toast.makeText(ChatRoom.this, "Chat is loading", Toast.LENGTH_SHORT).show();
          }
          //Empty chat text
          sendMessage.setText("");
        }
      }
    );

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
