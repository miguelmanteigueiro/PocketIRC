package pt.ubi.di.pdm.pocketirc;

//imports
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

/**
 * Shows the register screen of the application and manages registration
 */
public class RegisterActivity extends Activity{
  //== attributes ==
  //declare xml attributes
  EditText username,password,confirmPassword,email;
  //server
  Server server;

  //== methods ==
  /**
   * Shows the register activity and handles user registration
   * @param savedInstanceState the previous saved instance
   */
  @Override
  protected void onCreate(Bundle savedInstanceState){
    //create activity
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    //get xml ids
    username=findViewById(R.id.username);
    password=findViewById(R.id.password);
    confirmPassword=findViewById(R.id.confirmPassword);
    email=findViewById(R.id.email);
    //reset fields
    username.setText("");
    password.setText("");
    confirmPassword.setText("");
    email.setText("");
    //server
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    server = new Server("irc.libera.chat",6667);
  }
  /**
   * Check if the parameters are correct and register the user
   * @param v the current view
   */
  @SuppressLint("UseCompatLoadingForColorStateLists")
  public void register(View v){
    //transform username and passwords into strings
    String usernameString=String.valueOf(username.getText());
    String passwordString=String.valueOf(password.getText());
    String confirmPasswordString=String.valueOf(confirmPassword.getText());
    String emailString=String.valueOf(email.getText());
    //reset tint to default state
    username.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    password.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    confirmPassword.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    email.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    //check parameters
    boolean canRegister=true;
    if(!checkString(usernameString)){
      canRegister=false;
      username.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      username.setText("");
      username.setHint("Nick must not be empty!");
    }
    if(!checkString(passwordString)){
      canRegister=false;
      password.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      password.setText("");
      password.setHint("Password must not be empty!");
    }
    if(!passwordString.equals(confirmPasswordString)){
      canRegister=false;
      password.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      password.setText("");
      password.setHint("Passwords don't match!");
      confirmPassword.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      confirmPassword.setText("");
      confirmPassword.setHint("Passwords don't match!");
    }
    if(!((!TextUtils.isEmpty(emailString)&&Patterns.EMAIL_ADDRESS.matcher(emailString).matches()))){
      canRegister=false;
      email.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonwrong));
      email.setText("");
      email.setHint("Invalid email!");
    }
    //register
    if(canRegister){
      server.login(usernameString,"",usernameString,":"+usernameString);
      server.send_message("nickserv register "+passwordString+" "+emailString+" \r\n");

      //Intent confirmRegistrationIntent=new Intent(this,ConfirmRegisterActivity.class);
      //confirmRegistrationIntent.putExtra("Email",emailString);
      //startActivity(confirmRegistrationIntent);
    }
    else{
      //show error dialog
      AlertDialog.Builder builder=new AlertDialog.Builder(this);
      builder.setTitle("Registration Error").setMessage("There was an error in the registration. Please check the given errors!").setPositiveButton("I'm sorry :(",(dialog,which)->dialog.dismiss());
      Dialog dialog=builder.create();
      dialog.show();
    }
  }
  /**
   * Goes to the login page
   * @param v the current view
   */
  public void login(View v){
    Intent loginIntent=new Intent(this,LoginActivity.class);
    startActivity(loginIntent);
  }
  /**
   * Shows a helping dialog, so that users understand each field's purpose
   * @param v the current view
   */
  public void help(View v){
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle("IRC Help").setMessage(Html.fromHtml("After filling all the fields, you'll receive an email message from liberae with a command that looks something like '/msg NickServ VERIFY REGISTER nick code'. Copy and paste the command in the field that will appear to complete the registration<br><br>===<br><br><b>Nick</b>: the nickname you want to be called by<br><br><b>Password</b>: The password you need to login with. Keep this a secret<br><br><b>Confirm password</b>: Rewrite your password again, so that you're certain you didn't misspell it<br><br><b>Email</b>: The email you want to be contacted by")).setPositiveButton("Gotcha",(dialog,which)->dialog.dismiss());
    Dialog dialog=builder.create();
    dialog.show();
  }
  /**
   * Goes to the command activity
   * @param v the current view
   */
  public void goCommand(View v){
    Intent confirmRegistrationIntent=new Intent(this,ConfirmRegisterActivity.class);
    confirmRegistrationIntent.putExtra("Email","");
    startActivity(confirmRegistrationIntent);
  }
  /**
   * Checks if the string is not null
   * @param str the string to check
   * @return true if the string is not null and false otherwise
   */
  private boolean checkString(String str){
    return str.length()>0;
  }
}