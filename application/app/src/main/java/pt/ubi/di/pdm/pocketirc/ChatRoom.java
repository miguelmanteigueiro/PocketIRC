package pt.ubi.di.pdm.pocketirc;

//imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the chat room of the IRC app
 */
public class ChatRoom extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener,UsersRecyclerViewAdapter.ItemClickListenerUser{
  //== attributes ==
  //declare xml attributes
  ImageView sendButton;
  ImageView status_image;
  TextView drawer_username;
  EditText sendMessage;
  String passwordHash;
  ChannelsRecyclerViewAdapter channelsAdapter;
  ChannelsRecyclerViewAdapter privMessagesAdapter;
  UsersRecyclerViewAdapter userAdapter;
  RecyclerView channelsRecyclerView;
  RecyclerView privMessagesRecyclerView;
  RecyclerView userRecyclerView;
  TextView loadingText;
  public static MessageRecyclerViewAdapter messageAdapter;
  public static RecyclerView messageRecyclerView;
  @SuppressLint("StaticFieldLeak")
  static Toolbar toolbar;
  TextView userListTitle;
  ArrayList<String>ignore_list;
  //drawers
  static DrawerLayout drawerLayout;
  @SuppressLint("StaticFieldLeak")
  static LinearLayout leftDrawer;
  private LinearLayout rightDrawer;
  //numberOfNotifications
  long numberOfNotifications=0;
  //private chats list and the chats list
  private ArrayList<String>chatsList;
  private ArrayList<String>privateChatsList;
  public static HashMap<String,ArrayList<MessageIRC>>channels_messageList;
  //hash map status
  public static HashMap<String,Integer>channel_status;
  //user status
  boolean status_val=true;
  //chat variables
  static Commands cmd;
  String userName;
  public static String chatName;
  //users
  public static ArrayList<String>channelUserList=new ArrayList<>();
  //server
  Server server;
  //canSendNotification
  private boolean canSendNotification;
  //== methods ==

  /**
   * Show the chatroom and handle chat
   * @param savedInstanceState the previous saved instance
   */
  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //create activity
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chatroom);
    //get xml ids
    leftDrawer=findViewById(R.id.leftDrawer);
    rightDrawer=findViewById(R.id.rightDrawer);
    toolbar=findViewById(R.id.chat_header);
    setSupportActionBar(toolbar);
    drawerLayout=findViewById(R.id.drawerLayout);
    Toolbar toolbar=findViewById(R.id.chat_header);
    Button leftHamburger=findViewById(R.id.switch1);
    Button rightHamburger=findViewById(R.id.switch2);
    sendButton=findViewById(R.id.chat_sendButton);
    sendMessage=findViewById(R.id.chat_sendMessageBox);
    channelsRecyclerView=findViewById(R.id.drawer_channels_list);
    privMessagesRecyclerView=findViewById(R.id.drawer_messages_list);
    userRecyclerView=findViewById(R.id.drawer_user_list);
    messageRecyclerView=findViewById(R.id.chat_messageContainer);
    ImageView addChannelButton=findViewById(R.id.drawer_addChannel);
    userListTitle=findViewById(R.id.drawer_userTitle);
    status_image=findViewById(R.id.status_image);
    drawer_username=findViewById(R.id.drawer_username);
    LinearLayout loading=findViewById(R.id.loadingPanel);
    loadingText = findViewById(R.id.loading_text);

    //intents
    Intent fromLogin=getIntent();
    userName=fromLogin.getStringExtra("username");
    chatName=fromLogin.getStringExtra("channel");
    passwordHash=fromLogin.getStringExtra("password");

    //Loading screen treatment
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      public void run() {
        try{
          loadingText.setText("Loading chatroom...");
        }
        catch (Exception e){
          e.printStackTrace();
        }
      }
    }, 3500);

    //listeners
    leftHamburger.setOnClickListener(v->drawerLayout.openDrawer(leftDrawer));
    rightHamburger.setOnClickListener(v->{
      drawerLayout.openDrawer(rightDrawer);
      cmd.names(chatName);
      userAdapter=new UsersRecyclerViewAdapter(this,channelUserList);
      if(chatName.charAt(0)!='#'){
        userAdapter=new UsersRecyclerViewAdapter(this,new ArrayList<>(Arrays.asList(userName,chatName)));
        userListTitle.setText("Active users: 2");
      }
      else{
        userAdapter.setClickListener(this);
      }
      userRecyclerView.setAdapter(userAdapter);
    });
    addChannelButton.setOnClickListener(v ->addChannelMethod());
    //status image
    status_image.setImageResource(R.drawable.onlinestatus);
    //handles messages by channels
    channels_messageList=new HashMap<>();
    channels_messageList.put(chatName,new ArrayList<>());
    channels_messageList.put("NickServ",new ArrayList<>());
    //handles channels status
    channel_status=new HashMap<>();
    channel_status.put(chatName,0);
    channel_status.put("NickServ",0);
    //ignore list
    ignore_list = new ArrayList<>();
    //first message
    MessageIRC firstMessage = new MessageIRC();
    firstMessage.setRecipient(userName);
    firstMessage.setMsg("");
    firstMessage.setChannel(chatName);
    firstMessage.setMessage_type("C");
    firstMessage.setAction("PRIVMSG");
    firstMessage.setUser(new String[]{"","",""});
    Objects.requireNonNull(channels_messageList.get(chatName)).add(firstMessage);
    Objects.requireNonNull(channels_messageList.get("NickServ")).add(firstMessage);
    //server
    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    server=new Server("irc.libera.chat", 6667);
    server.login(userName, passwordHash, userName, ":"+userName);
    server.join(chatName, "");
    cmd=new Commands(server.out);
    toolbar.setTitle(chatName);
    drawer_username.setText(userName);
    //chat lists
    privateChatsList = new ArrayList<>();
    chatsList = new ArrayList<>();
    chatsList.add(chatName);
    //add nickserv
    privateChatsList.add("NickServ");
    //setup the channels lists recycler views
    LinearLayoutManager linearLayoutManagerChannels=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
    LinearLayoutManager linearLayoutManagerPrivMessages=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
    LinearLayoutManager linearLayoutManagerUser=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
    //channels list adapter
    channelsRecyclerView.setLayoutManager(linearLayoutManagerChannels);
    channelsAdapter=new ChannelsRecyclerViewAdapter(this,chatsList);
    channelsRecyclerView.setAdapter(channelsAdapter);
    //private channels list adapter
    privMessagesRecyclerView.setLayoutManager(linearLayoutManagerPrivMessages);
    privMessagesAdapter=new ChannelsRecyclerViewAdapter(this,privateChatsList);
    privMessagesRecyclerView.setAdapter(privMessagesAdapter);
    //user adapter
    userRecyclerView.setLayoutManager(linearLayoutManagerUser);
    //add to the hashmap
    channels_messageList.put(chatName,new ArrayList<>());
    //setup the message recycler view
    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
    messageRecyclerView.setLayoutManager(linearLayoutManager);
    messageAdapter=new MessageRecyclerViewAdapter(this,channels_messageList.get(chatName));
    messageRecyclerView.setAdapter(messageAdapter);
    messageAdapter.setClickListener(this);
    //create a blocking queue
    BlockingQueue<String> queue=new LinkedBlockingQueue<>();
    //Send message
    Runnable producer=()->{
      try{
        while(true){
          String data=server.receive_message();
          if(data!=null){
            queue.put(data);
          }
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
    };
    //Receive message
    Runnable consumer=()->{
      while(true){
        String message="";
        try{
          message=queue.take();
        }
        catch(InterruptedException e){
          e.printStackTrace();
        }
        String finalMessage=message;
        runOnUiThread(()->{
          //message received from chat
          MessageIRC m=Parser.parse_message(finalMessage);
          //send pong command when server send ping (to avoid timeouts)
          if(m.getMessage_type().equals("P")){
            cmd.pong();
            return;
          }
          //check if user is in ignore list
          if(m.getMessage_type().equals("C")||m.getMessage_type().equals("UM")){
            if(ignore_list.contains(m.getUser()[0])){
              return;
            }
          }
          //check if action is NICK
          if(m.getMessage_type().equals("N")){
            String old_username=m.getUser()[0];
            String new_username=m.getMsg();
            //update list
            channelUserList.remove(old_username);
            //update hashmap
            ArrayList<MessageIRC>temp=channels_messageList.get(old_username);
            channels_messageList.remove(old_username);
            channels_messageList.put(new_username,temp);
            //update username if the command is from app's user
            if(old_username.equals(userName)){
              userName=new_username;
              drawer_username.setText(userName);
            }
            return;
          }
          //forward to another channel
          if(m.getCode()==470){
            changeChannel(m.getChannel());
            //show toast to tell the user he was forwarded
            Toast.makeText(ChatRoom.this,"You have been forwarded to " + chatName, Toast.LENGTH_SHORT).show();
          }
          //manage kicks
          if(m.getAction().equals("KICK")&&m.getMsg().equals(userName)){
            chatsList.remove(m.getChannel());
            channels_messageList.remove(m.getChannel());
            channel_status.remove(m.getChannel());
            if(m.getChannel().equals(chatName)){
              chatName = "NickServ";
              toolbar.setTitle(chatName);
              messageAdapter = new MessageRecyclerViewAdapter(getApplicationContext(),channels_messageList.get(chatName));
              messageRecyclerView.setAdapter(messageAdapter);
              messageAdapter.setClickListener(ChatRoom.this);
              AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
              builder.setTitle("Server status");
              builder.setMessage("You've been kicked from "+ m.getChannel() + "\n\nMoving to Nickserv");
              builder.setPositiveButton("Ok",(dialog, which)->{
                //do nothing but close the dialog
                dialog.dismiss();
              });
              AlertDialog alert=builder.create();
              alert.show();
            }
            else{
              Toast.makeText(ChatRoom.this,"You've been kicked from " + m.getChannel(), Toast.LENGTH_SHORT).show();
            }
            channelsAdapter=new ChannelsRecyclerViewAdapter(ChatRoom.this,chatsList);
            channelsRecyclerView.setAdapter(channelsAdapter);
          }
          //names (update user list)
          if(m.getCode()==353){
            channelUserList.addAll(Arrays.asList(m.getMsg().split(" ")));
            Set<String>set=new HashSet<>(channelUserList);
            channelUserList.clear();
            channelUserList.addAll(set);
            userListTitle.setText("Active users: "+ channelUserList.size());
          }
          //check if code is 433
          if(m.getCode()==433){
            String s=generate_name(userName);
            drawer_username.setText(s);
            cmd.nick(s);
            userName=s;
            cmd.join(chatName,"");
            Toast.makeText(ChatRoom.this,"Username already taken! Using " + s, Toast.LENGTH_SHORT).show();
            return;
          }
          // if channel requires user to be logged in, redirect to #libera
          if(m.getCode()==477){
            changeChannel("#libera");
            cmd.join(chatName, "");
            //show toast to tell the user he was forwarded
            Toast.makeText(ChatRoom.this, "You need to be logged in to join this channel! Forwarding to #libera", Toast.LENGTH_SHORT).show();
          }
          String channel=m.getChannel();
          if(channel.equals("")){
            channel=chatName;
          }
          if(!channels_messageList.containsKey(channel)){
            channels_messageList.put(channel,new ArrayList<>());
          }
          //check if is a user message
          if(m.getMessage_type().equals("UM")||m.getMessage_type().equals("NS")){
            //add user message to privateChatsList
            if(!privateChatsList.contains(channel)){
              privateChatsList.add(channel);
              //update status
              channel_status.put(channel,1);
              privMessagesAdapter = new ChannelsRecyclerViewAdapter(ChatRoom.this, privateChatsList);
              privMessagesRecyclerView.setAdapter(privMessagesAdapter);
            }
          }
          //add special message to the list to display User name
          if(m.getMessage_type().equals("C")||m.getMessage_type().equals("UM")||m.getMessage_type().equals("NS")){
            if(canSendNotification){
              Intent notificationIntent=new Intent(this,messageNotificationService.class);
              notificationIntent.putExtra("message",m.getMsg());
              notificationIntent.putExtra("canSend","true");
              if(numberOfNotifications==99){
                notificationIntent.putExtra("numNotifications",numberOfNotifications+"+");
              }
              else{
                notificationIntent.putExtra("numNotifications",String.valueOf(++numberOfNotifications));
              }
              notificationIntent.putExtra("whoSent",m.getUser()[0]);
              startService(notificationIntent);
            }
            //mention user
            m.setMsg(mention_user(m));
            //checks if the previous user is different from the current
            if(Objects.requireNonNull(channels_messageList.get(channel)).size() == 0 ||
              !Objects.requireNonNull(channels_messageList.get(channel)).get(0).getUser()[0].equals(m.getUser()[0])){
              Calendar cal=Calendar.getInstance();
              //this add is the username that appears on the screen
              String hour=String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
              int auxMin=cal.get(Calendar.MINUTE);
              String minute=(auxMin < 10)? "0"+auxMin:String.valueOf(auxMin);
              m.setHour(hour+":"+minute);
              //checks if hashmap contains channel
              //if so append the message to the existent message list
              //if don't create a new list and then append the message to it
              Objects.requireNonNull(channels_messageList.get(channel)).add(0,m);
            }
            // Add message to the list
            Objects.requireNonNull(channels_messageList.get(channel)).add(0,m);
            if(!channel.equals(chatName)){
              channel_status.put(channel,1);
              //update list
              if(channel.charAt(0) == '#'){
                channelsAdapter = new ChannelsRecyclerViewAdapter(ChatRoom.this, chatsList);
                channelsRecyclerView.setAdapter(channelsAdapter);
              }
              else{
                privMessagesAdapter = new ChannelsRecyclerViewAdapter(ChatRoom.this, privateChatsList);
                privMessagesRecyclerView.setAdapter(privMessagesAdapter);
              }
            }
          }
          //check JOIN, PART and QUIT message
          if(Arrays.asList(new String[]{"UJ", "UP", "UQ"}).contains(m.getMessage_type())){
            //update list of users in the channel
            cmd.names(chatName);
            m.setUser(new String[]{"", m.getUser()[0], ""});
            //quit appears just if the user is on the user's quit channel
            if(m.getMessage_type().equals("UQ")){
              if(channelUserList.contains(m.getUser()[1])){
                m.setChannel(chatName);
              }
              else{
                return;
              }
            }
            Objects.requireNonNull(channels_messageList.get(channel)).add(0, m);
            //take out the loading bar
            loading.setVisibility(View.GONE);
          }
          //updates the current message list
          if(channel.equals(chatName)){
            //update message Adapter
            messageAdapter=new MessageRecyclerViewAdapter(getApplicationContext(),channels_messageList.get(channel));
            messageRecyclerView.setAdapter(messageAdapter);
            messageAdapter.setClickListener(ChatRoom.this);
          }
        });
      }
    };
    new Thread(producer).start();
    new Thread(consumer).start();
    //send button
    sendButton.setOnClickListener(
      oView->{
        String aux = sendMessage.getText().toString();
        if(!isBlank(aux)&&!aux.isEmpty()){
          //updates message list
          MessageIRC m = new MessageIRC();
          aux = aux.trim();
          m.setMsg(aux);
          //update message time
          Calendar now = Calendar.getInstance();
          String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
          int auxMin = now.get(Calendar.MINUTE);
          String minute = (auxMin < 10)? "0"+auxMin: String.valueOf(auxMin);
          m.setHour(hour+":"+minute);
          //update message user
          m.setUser(new String[]{userName, userName, userName});
          m.setMessage_type("C");
          m.setAction("PRIVMSG");
          m.setChannel(chatName);
          m.setRecipient(chatName);
          if(!Objects.requireNonNull(channels_messageList.get(chatName)).isEmpty()){
            //checks if the previous message is the user's or there are none
            if(!Objects.requireNonNull(channels_messageList.get(chatName)).get(0).getAction().equals("PRIVMSG")||!Objects.requireNonNull(channels_messageList.get(chatName)).get(0).getUser()[0].equals(m.getUser()[0])){
              Objects.requireNonNull(channels_messageList.get(chatName)).add(0,m);
            }
            //adds the message to the list
            Objects.requireNonNull(channels_messageList.get(chatName)).add(0, m);
            //send message to server
            String temp_message = m.getMsg();
            if(Commands.checkIfCommand(temp_message)){
              //check if is ignore or unignore command
              if(temp_message.contains("/ignore")||temp_message.contains("/unignore")){
                //get user to ignore
                String[] temp_message_splited=temp_message.split(" ");
                if(temp_message_splited.length>1){
                  if(temp_message.contains("/ignore")){
                    ignore_list.add(temp_message_splited[1]);
                  }
                  else{
                    if(temp_message_splited[1].equals("all")){
                      ignore_list.clear();
                    }
                    else{
                      ignore_list.remove(temp_message_splited[1]);
                    }
                  }
                }
              }
              //parse the command message
              temp_message = Commands.replaceCommand(temp_message);
              server.send_message(temp_message);
            }
            else{
              cmd.msg(chatName, m.getMsg());
              //check user mention
              m.setMsg(mention_user(m));
            }
            //update recyclerView
            messageAdapter = new MessageRecyclerViewAdapter(this, channels_messageList.get(chatName));
            messageRecyclerView.setAdapter(messageAdapter);
            messageAdapter.setClickListener(this);
          }
          else{
            Toast.makeText(ChatRoom.this, "Chat is loading", Toast.LENGTH_SHORT).show();
          }
          //empty chat text
          sendMessage.setText("");
        }
      }
    );
  }
  /**
   * Can send notifications now
   */
  @Override
  protected void onStop(){
    super.onStop();
    canSendNotification=true;
  }
  /**
   * Cannot send notifications now
   */
  @Override
  protected void onResume(){
    super.onResume();
    canSendNotification=false;
    numberOfNotifications=0;
  }
  /**
   * Mentions a user
   * @param m the command
   * @return the message mentioned
   */
  public static String mention_user(MessageIRC m){
    String msg=m.getMsg();
    StringBuilder newMsg=new StringBuilder();
    for(String msgSubstring:msg.split(" ")){
      if(msgSubstring.length()==0){
        break;
      }
      if(channelUserList.contains(msgSubstring)||(channelUserList.contains(msgSubstring.substring(0,msgSubstring.length()-1))&&msgSubstring.charAt(msgSubstring.length()-1)==':')){
        newMsg.append("<font color=\"red\">").append(msgSubstring).append(" ").append("</font>");
      }
      else{
        newMsg.append(msgSubstring).append(" ");
      }
    }
    return newMsg.toString();
  }
  /**
   * Verifies if a string is made entirely of blank spaces
   * @param cs the given string
   * @return true if the string is made entirely of blank spaces and false otherwise
   */
  public static boolean isBlank(final CharSequence cs) {
    int strLen;
    if(cs==null||(strLen=cs.length())==0){
      return true;
    }
    for(int i=0;i<strLen;i++){
      if(!Character.isWhitespace(cs.charAt(i))){
        return false;
      }
    }
    return true;
  }
  @Override
  public void onItemClick(View view, int position) {
  }
  /**
   * Shows a user properties menu
   * @param view the current view
   * @param position the clicked user
   */
  @Override
  public void onItemClickUser(View view, int position) {
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle(channelUserList.get(position));
    builder.setPositiveButton("Message user",(dialog,which)->{
      //do nothing but close the dialog
      chatName=channelUserList.get(position);

      if(chatName.charAt(0) == '@'){
        chatName = chatName.substring(1);
      }
      drawerLayout.closeDrawer(rightDrawer);
      if(!privateChatsList.contains(chatName)){
        privateChatsList.add(chatName);
      }
      toolbar.setTitle(chatName);
      if(!channels_messageList.containsKey(chatName)){
        channels_messageList.put(chatName,new ArrayList<>());
      }

      channel_status.put(chatName,0);

      MessageIRC m=new MessageIRC();
      m.setRecipient(userName);
      m.setMsg("");
      m.setChannel(chatName);
      m.setMessage_type("C");
      m.setAction("PRIVMSG");
      m.setUser(new String[]{"","",""});
      Objects.requireNonNull(channels_messageList.get(chatName)).add(m);
      //update recyclerView
      messageAdapter=new MessageRecyclerViewAdapter(this,channels_messageList.get(chatName));
      messageRecyclerView.setAdapter(messageAdapter);
      messageAdapter.setClickListener(this);
      dialog.dismiss();
    });
    builder.setNegativeButton("Go back",(dialog,which)->dialog.dismiss());
    AlertDialog alert = builder.create();
    alert.show();
  }
  // change status away or online

  /**
   * Changes the status from away to online or vice versa
   * @param view the current view
   */
  public void change_status(View view){
    if(status_val){
      status_val=false;
      status_image.setImageResource(R.drawable.offlinestatus);
      //send server away
      cmd.away("");
    }
    else{
      status_val=true;
      status_image.setImageResource(R.drawable.onlinestatus);
      //send server back
      cmd.back();
    }
  }

  /**
   * Logs out of the server, going to the login screen
   */
  @Override
  public void onBackPressed() {
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle("Log-out");
    builder.setMessage("Are you sure?");
    builder.setPositiveButton("YES",(dialog, which)->{
      // Do nothing but close the dialog
      //LoginActivity.passwordCheck.setChecked(true); //to uncheck
      finish();
      System.exit(0);
      dialog.dismiss();
    });
    builder.setNegativeButton("NO",(dialog,which)->dialog.dismiss());
    AlertDialog alert = builder.create();
    alert.show();
  }
  /**
   * Generates a random name
   * @param name the given name
   * @return a random name based on the given one
   */
  private String generate_name(String name){
    return name +(int)(Math.random()*100);
  }

  /**
   * Shows the add channel menu
   */
  @SuppressLint("SetTextI18n")
  private void addChannelMethod(){
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    TextView title=new TextView(this);
    title.setText("Add channel/user");
    title.setBackgroundColor(ContextCompat.getColor(this,R.color.toolBar));
    title.setPadding(10, 10, 10, 10);
    title.setGravity(Gravity.CENTER);
    title.setTextColor(Color.WHITE);
    title.setTextSize(20);
    builder.setCustomTitle(title);
    EditText user_input=new EditText(this);
    LinearLayout layout=new LinearLayout(this);
    user_input.setInputType(InputType.TYPE_CLASS_TEXT);
    user_input.setHint("Channel/Username");
    layout.addView(user_input);
    layout.setGravity(Gravity.CENTER);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(0,20,0,20);
    builder.setView(layout);
    // Dialog box "OK" button
    builder.setPositiveButton("OK",(dialog,i)->{
      String aux=user_input.getText().toString();
      if(aux.isEmpty() || isBlank(aux)) {
        Toast.makeText(ChatRoom.this,"Chat is loading", Toast.LENGTH_SHORT).show();
      }
      else {
        if(aux.charAt(0)=='#') {
          if(!chatsList.contains(aux)) {
            chatsList.add(aux);
          }
          channelsAdapter=new ChannelsRecyclerViewAdapter(ChatRoom.this, chatsList);
          channelsRecyclerView.setAdapter(channelsAdapter);
          cmd.join(aux,"");
        }
        else{
          if(!privateChatsList.contains(aux)){
            privateChatsList.add(aux);
          }
          privMessagesAdapter=new ChannelsRecyclerViewAdapter(ChatRoom.this, privateChatsList);
          privMessagesRecyclerView.setAdapter(privMessagesAdapter);
          MessageIRC m=new MessageIRC();
          m.setRecipient(userName);
          m.setMsg("");
          m.setChannel(aux);
          m.setMessage_type("UM");
          m.setAction("PRIVMSG");
          m.setUser(new String[]{"","",""});
          if(!channels_messageList.containsKey(aux)){
            channels_messageList.put(aux,new ArrayList<>());
          }
          Objects.requireNonNull(channels_messageList.get(aux)).add(m);
        }
        toolbar.setTitle(aux);
        chatName=aux;
        channelUserList.clear();
        if(!channels_messageList.containsKey(chatName)){
          channels_messageList.put(chatName,new ArrayList<>());
        }
        messageAdapter=new MessageRecyclerViewAdapter(ChatRoom.this, channels_messageList.get(chatName));
        messageRecyclerView.setAdapter(messageAdapter);
        messageAdapter.setClickListener(ChatRoom.this);
        //updates status list
        channel_status.put(chatName,0);
        drawerLayout.closeDrawer(leftDrawer);
      }
    });
    builder.setNegativeButton("CANCEL",(dialogInterface,i)->dialogInterface.dismiss());
    builder.show();
  }

  public void changeChannel(String channelName){
    //clean channel user list
    channelUserList.clear();
    //remove added chatname
    chatsList.remove(chatName);
    channel_status.remove(chatName);
    //update new chatname
    chatName = channelName;
    chatsList.add(chatName);
    channel_status.put(chatName,0);
    //update adapter
    channelsAdapter=new ChannelsRecyclerViewAdapter(ChatRoom.this,chatsList);
    channelsRecyclerView.setAdapter(channelsAdapter);
    //update toolbar
    toolbar.setTitle(chatName);
  }
}