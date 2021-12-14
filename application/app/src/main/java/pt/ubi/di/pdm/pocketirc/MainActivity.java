package pt.ubi.di.pdm.pocketirc;

//imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Shows the splash screen of the application
 */
public class MainActivity extends Activity{
  //== attributes ==
  Handler handler;

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
   * Sleeps the app for 3 seconds, so that the logo can be seen. After that, sends the user
   * to the login screen
   */
  @Override
  protected void onResume(){
    super.onResume();
    handler=new Handler();
    handler.postDelayed(this::startApp,3000);
  }
  /**
   * Shows a dialog to confirm user leaving the app
   * Making him able to use the application (69 gottem)
   */
  @Override
  public void onBackPressed(){
    handler.removeCallbacksAndMessages(null);
    new AlertDialog.Builder(this)
      .setTitle("Exit?")
      .setMessage("Are you sure you want to exit the application?")
      .setPositiveButton(android.R.string.yes,(dialog,which)->{
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
      })
      .setNegativeButton(android.R.string.no,(dialog,which)->handler.postDelayed(this::startApp,3000))
      .setIcon(android.R.drawable.ic_lock_power_off)
      .show();
  }
  /**
   * Goes to the login page
   */
  public void startApp(){
    Intent start=new Intent(this,LoginActivity.class);
    startActivity(start);
  }
}