package pt.ubi.di.pdm.pocketirc;

//imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Shows the splash screen of the application
 */
public class MainActivity extends Activity{
  //== methods ==
  /**
   * Displays the main activity
   * @param savedInstanceState the previous saved instance
   */
  @Override
  protected void onCreate(Bundle savedInstanceState){
    //create activity
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
  /**
   * Shows a dialog to confirm user leaving the app
   */
  @Override
  public void onBackPressed(){
    new AlertDialog.Builder(this)
      .setTitle("Exit?")
      .setMessage("Are you sure you want to exit the application?")
      .setNegativeButton(android.R.string.no,null)
      .setPositiveButton(android.R.string.yes,(arg0,arg1)->MainActivity.super.onBackPressed()).create().show();
  }
  /**
   * Goes to the login page
   * @param v the current view
   */
  public void startApp(View v){
    Intent start=new Intent(this,LoginActivity.class);
    startActivity(start);
  }
}