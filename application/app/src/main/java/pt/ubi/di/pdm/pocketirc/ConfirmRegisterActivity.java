package pt.ubi.di.pdm.pocketirc;

//imports
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    if(i.getStringExtra("Email").equals("")){
      commandLabel.setText("Please type the command sent to your email");
    }
    else{
      commandLabel.setText("Please type the command sent to "+i.getStringExtra("Email"));
    }
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
    //reset tint to default state
    command.setBackgroundTintList(this.getResources().getColorStateList(R.color.buttonright));
    //confirm registration
    //confirm(commandString); //uncomment
    //go to home activity
    //Intent homeIntent=new Intent(this,HomeActivity.class); //uncomment
    //startActivity(homeIntent); //uncomment
  }
}