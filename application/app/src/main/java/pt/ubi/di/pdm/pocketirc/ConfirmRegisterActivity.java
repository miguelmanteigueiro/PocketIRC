package pt.ubi.di.pdm.pocketirc;

//imports
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Shows the confirm register screen of the application and manages registration
 */
public class ConfirmRegisterActivity extends Activity{
  //== attributes ==
  //declare xml attributes
  TextView commandLabel;
  EditText command;
  //server
  Server server;
  //username
  String username;

  //== methods ==
  /**
   * Shows the confirm registration activity and handles user registration
   * @param savedInstanceState the previous saved instance
   */
  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState){
    //create activity
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirm_registration);
    //get xml ids
    commandLabel=findViewById(R.id.commandLabel);
    command=findViewById(R.id.command);
    //reset fields
    command.setText("");
    //get intent
    Intent i=getIntent();
    if(i.getStringExtra("username")==null){
      username="";
      for(int j=0;j<20;j++){
        int rand=(int)(Math.random()*75);
        username+=(char)(rand+48);
      }
    }
    else{
      username=i.getStringExtra("username");
    }
    if(i.getStringExtra("Email").equals("")){
      commandLabel.setText("Please type the command sent to your email");
    }
    else{
      commandLabel.setText("Please type the command sent to "+i.getStringExtra("Email"));
    }
    //server
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    server = new Server("irc.libera.chat",6667);
    System.out.println(username);
  }
  /**
   * Send the command to the server to complete user registration, sending
   * the user to the home activity
   * @param v the current view
   */
  @SuppressLint("UseCompatLoadingForColorStateLists")
  public void confirmRegistration(View v){
    //transform username and passwords into strings
    String commandString=String.valueOf(command.getText());
    String parsedCommand=parseCommand(commandString);
    //reset tint to default state
    command.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    //confirm registration
    if(parsedCommand.equals("LOLNOOB")){
      //show error dialog
      command.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      AlertDialog.Builder builder=new AlertDialog.Builder(this);
      builder.setTitle("Registration Error").setMessage("There was an error in the registration. The command is probably not well written!").setPositiveButton("I'm sorry :(",(dialog,which)->dialog.dismiss());
      Dialog dialog=builder.create();
      dialog.show();
    }
    else{
      //confirm registration
      server.login(username,"",username, ":"+username);
      server.send_message(parseCommand(commandString));
      //go to login activity
      Intent loginIntent=new Intent(this,LoginActivity.class);
      startActivity(loginIntent);
    }
  }
  /**
   * Parses the command to the correct output
   * @param command the command to parse
   * @return the parsed command
   */
  private String parseCommand(String command){
    try{
      return "nickserv verify register"+command.split(" ")[4]+command.split(" ")[5]+" \r\n";
    }
    catch(Exception err){
      return "LOLNOOB";
    }
  }
}