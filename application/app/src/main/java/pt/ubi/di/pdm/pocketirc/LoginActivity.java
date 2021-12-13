package pt.ubi.di.pdm.pocketirc;

//imports
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

/**
 * Shows the login screen of the application and manages login authentication
 */
public class LoginActivity extends Activity{
  //== attributes ==
  //show password fields or not
  boolean requiresPassword=false;
  //declare xml attributes
  TextView passwordLabel;
  EditText username,password,channel;
  CheckBox passwordCheck;
  Space space;
  Button loginButton;

  //== methods ==
  /**
   * Shows the login activity and handles user authentication
   * @param savedInstanceState the previous saved instance
   */
  @Override
  protected void onCreate(Bundle savedInstanceState){
    //create activity
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    //get xml ids
    username=findViewById(R.id.username);
    password=findViewById(R.id.password);
    passwordCheck=findViewById(R.id.passwordCheckbox);
    passwordLabel=findViewById(R.id.passwordLabel);
    space=findViewById(R.id.space);
    loginButton=findViewById(R.id.login);
    channel=findViewById(R.id.channel);
    //reset fields
    username.setText("");
    password.setText("");
    channel.setText("");
    //automatic log in if user is saved in shared preference
    //if(SaveSharedPreference.getUserName(LoginActivity.this).length()!=0){
    //  //go into the home activity if the user is logged in
    //  goHome(SaveSharedPreference.getUserName(LoginActivity.this));
    //}
  }
  /**
   * Check if the credentials given by the user match some on the database
   * and, if so, login the user; otherwise, show an error dialog
   * @param v the current view
   */
  @SuppressLint("UseCompatLoadingForColorStateLists")
  public void login(View v){
    //transform username and passwords into strings
    String usernameString=String.valueOf(username.getText());
    String passwordString=String.valueOf(password.getText());
    String channelString=String.valueOf(channel.getText());
    //check parameters
    boolean canLogIn=true;
    if(!checkString(usernameString)){
      canLogIn=false;
      username.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      username.setText("");
      username.setHint("Nick must not be empty!");
    }
    if(requiresPassword){
      if(!checkString(passwordString)){
        canLogIn=false;
        password.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
        password.setText("");
        password.setHint("Password must not be empty!");
      }
    }
    channelString=formatChannel(channelString);
    //reset tint to default state
    username.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    password.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    //log in
    if(canLogIn){
      if(requiresPassword){
        //loginNonGuest(usernameString,passwordString,channelString);
      }
      else{
        //loginGuest(usernameString,channelString);
      }
    }
    else{
      //show error dialog
      AlertDialog.Builder builder=new AlertDialog.Builder(this);
      builder.setTitle("Login Error").setMessage("There was an error logging in. Please check the given errors!").setPositiveButton("I'm sorry :(",(dialog,which)->dialog.dismiss());
      Dialog dialog=builder.create();
      dialog.show();
    }
  }
  /**
   * Goes to the register page
   * @param v the current view
   */
  public void register(View v){
    //Intent registerIntent=new Intent(this,RegisterActivity.class);
    //startActivity(registerIntent);
  }
  /**
   * Shows a helping dialog, so that users understand each field's purpose
   * @param v the current view
   */
  public void help(View v){
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle("IRC Help").setMessage(Html.fromHtml("<b>Nick</b>: the nickname you want to be called by<br><br><b>Password</b>: if the password isn't specified, you will login as a guest, which means you won't be able to connect to some channels and people may steal your nickname in the future. Create an account before logging in with a password<br><br><b>Channel</b>: the channel you want to connect to")).setPositiveButton("Gotcha",(dialog,which)->dialog.dismiss());
    Dialog dialog=builder.create();
    dialog.show();
  }
  /**
   * Updates the password requirement. If the password checkbox is checked and it's clicked, it'll not be required anymore and vice versa.
   * @param v the current view
   */
  public void passwordCheck(View v){
    requiresPassword=!requiresPassword;
    updatedPasswordFields(requiresPassword);
  }
  /**
   * Shows the password fields if the password is required or hides them otherwise.
   * Also updates the login button's text to 'start as guest' if the password is not required
   * @param requiresPassword true if the password is required and false otherwise
   */
  @SuppressLint("SetTextI18n")
  private void updatedPasswordFields(boolean requiresPassword){
    if(requiresPassword){
      passwordLabel.setVisibility(View.VISIBLE);
      password.setVisibility(View.VISIBLE);
      LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        16.0f
      );
      space.setLayoutParams(param);
      loginButton.setText("Start");
    }
    else{
      passwordLabel.setVisibility(View.GONE);
      password.setVisibility(View.GONE);
      LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        22.0f
      );
      space.setLayoutParams(param);
      loginButton.setText("Start as guest");
    }
  }
  /**
   * Checks if the string is not null
   * @param str the string to check
   * @return true if the string is not null and false otherwise
   */
  private boolean checkString(String str){
    return str.length()>0;
  }
  /**
   * Formats the channel, so that no repeated # appear at the beggining.
   * Also, if the channel is empty, the default is #libera
   * @param channel the channel string
   * @return the formatted channel string
   */
  private String formatChannel(String channel){
    StringBuilder formattedChannel=new StringBuilder("#");
    boolean hasFoundChar=false;
    for(Character c:channel.toCharArray()){
      if(!hasFoundChar&&c!='#'){
        hasFoundChar=true;
      }
      if(hasFoundChar){
        formattedChannel.append(c);
      }
    }
    return formattedChannel.toString().equals("#")?"#libera":formattedChannel.toString();
  }
}