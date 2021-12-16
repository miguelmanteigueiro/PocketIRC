package pt.ubi.di.pdm.teste;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
  public static MessageRecyclerViewAdapter messageAdapter;
  ChannelsRecyclerViewAdapter channelsAdapter;
  ChannelsRecyclerViewAdapter privMessagesAdapter;
  public static RecyclerView messageRecyclerView;
  RecyclerView channelsRecyclerView;
  RecyclerView privMessagesRecyclerView;
  Toolbar toolbar;

  //Instantiate the drawers
  private DrawerLayout drawerLayout;
  private LinearLayout leftDrawer;
  private LinearLayout rightDrawer;

  //Create the private chats list and the chats list
  private ArrayList<String> chatsList;
  private ArrayList<String> privateChatsList;
  public static HashMap<String, ArrayList<MessageIRC>> channels_messageList;

  //Chat variables
  Commands cmd;
  String userName;
  public static String chatName;

  // create an arraylist of strings to store the users in a channel
  public static ArrayList<String> channelUserList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //Initiate the view
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatroom);

    //=======================================Drawers===============================================
    //Drawer Variables
    leftDrawer = (LinearLayout) findViewById(R.id.leftDrawer);
    rightDrawer = (LinearLayout) findViewById(R.id.rightDrawer);
    toolbar = (Toolbar) findViewById(R.id.chat_header);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

    Toolbar toolbar = (Toolbar) findViewById(R.id.chat_header);
    setSupportActionBar(toolbar);

    Button leftHamburger = (Button) findViewById(R.id.switch1);
    Button rightHamburger = (Button) findViewById(R.id.switch2);

    leftHamburger.setOnClickListener(v -> {
      drawerLayout.openDrawer(leftDrawer);
    });
    rightHamburger.setOnClickListener(v -> {
      drawerLayout.openDrawer(rightDrawer);
    });

    //========================================================================================


    //Initiate the widgets
    sendButton = (ImageButton)findViewById(R.id.chat_sendButton);
    sendMessage = (EditText)findViewById(R.id.chat_sendMessageBox);
    channelsRecyclerView = (RecyclerView)findViewById(R.id.drawer_channels_list);
    privMessagesRecyclerView = (RecyclerView) findViewById(R.id.drawer_messages_list);
    messageRecyclerView = (RecyclerView) findViewById(R.id.chat_messageContainer);

    // HashMap that handles the messages by channels
    channels_messageList = new HashMap<String, ArrayList<MessageIRC>>();

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

    //Setup the channels lists recycler views
    LinearLayoutManager linearLayoutManagerChannels = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
    LinearLayoutManager linearLayoutManagerPrivMessages = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

    //Channels list adapter
    channelsRecyclerView.setLayoutManager(linearLayoutManagerChannels);
    channelsAdapter = new ChannelsRecyclerViewAdapter(this, chatsList);
    channelsRecyclerView.setAdapter(channelsAdapter);

    //Private channels list adapter
    privMessagesRecyclerView.setLayoutManager(linearLayoutManagerPrivMessages);
    privMessagesAdapter = new ChannelsRecyclerViewAdapter(this, privateChatsList);
    privMessagesRecyclerView.setAdapter(privMessagesAdapter);

    //Add to the hashmap
    channels_messageList.put(chatName,new ArrayList<>());

    //Setup the message recycler view
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
    messageRecyclerView.setLayoutManager(linearLayoutManager);
    messageAdapter = new MessageRecyclerViewAdapter(this, channels_messageList.get(chatName));
    messageRecyclerView.setAdapter(messageAdapter);
    messageAdapter.setClickListener(this);

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
              // Send pong command when server send ping (to avoid timeouts)
              if(m.getMessage_type().equals("P")){
                cmd.pong();
                return;
              }

              // check if code = 353 (same as NAMES), so we can get the list of users in the channel
              if(m.getCode() == 353){
                channelUserList.addAll(Arrays.asList(m.getMsg().split(" ")));
              }

              String channel = m.getChannel();

              if(channel.equals("")){
                channel = chatName;
              }

              if(!channels_messageList.containsKey(channel)){
                channels_messageList.put(channel,new ArrayList<>());
              }

              // Check if is a user message
              if(m.getMessage_type().equals("UM") || m.getMessage_type().equals("NS")){
                // Add user message to privateChatsList
                if(!privateChatsList.contains(channel)){
                  privateChatsList.add(channel);
                  privMessagesAdapter = new ChannelsRecyclerViewAdapter(ChatRoom.this, privateChatsList);
                  privMessagesRecyclerView.setAdapter(privMessagesAdapter);
                }
              }

              // Add special message to the list to display User name
              if(m.getMessage_type().equals("C") || m.getMessage_type().equals("UM") || m.getMessage_type().equals("NS")){

                // mention user
                m.setMsg(mention_user(m));

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
                // Update list of users in the channel
                cmd.names(chatName);
                m.setUser(new String[]{"", m.getUser()[0], ""});
                // Quit appears just if the user is on the user's quit channel
                if(m.getMessage_type().equals("UQ")) {
                  if(channelUserList.contains(m.getUser()[0])){
                    m.setChannel(channel);
                  }
                  else{
                    return;
                  }
                }
                channels_messageList.get(channel).add(0, m);
              }

              //Updates the current message list
              if(channel.equals(chatName)){
                // Update message Adapter
                messageAdapter = new MessageRecyclerViewAdapter(getApplicationContext(), channels_messageList.get(channel));
                messageRecyclerView.setAdapter(messageAdapter);
                messageAdapter.setClickListener(ChatRoom.this);
              }

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
              // mention user
              m.setMsg(mention_user(m));
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

  public static String mention_user(MessageIRC m){
    String[] tempMessageSplit = m.getMsg().split(" ");
    for (String word : tempMessageSplit){
      if(channelUserList.contains(word)){
        tempMessageSplit[Arrays.asList(tempMessageSplit).indexOf(word)] = "<font color='#EE0000'>" + word + "</font>";
      }
    }

    StringBuilder s = new StringBuilder();
    for (String word : tempMessageSplit){
      s.append(word + " ");
    }

    return s.toString().trim();
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
  /*// Update message Adapter
    messageAdapter = new MessageRecyclerViewAdapter(getApplicationContext(), channels_messageList.get(channel));
    messageRecyclerView.setAdapter(messageAdapter);
    messageAdapter.setClickListener(ChatRoom.this);*/
}
