package pt.ubi.di.pdm.pocketirc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class messageNotificationService extends Service{
  private String msg="";
  private boolean canSend;
  private long numNotifications;
  private String whoSent="";
  private int iID=100;
  //== methods ==

  public static String createNotificationChannel(Context context) {

    // NotificationChannels are required for Notifications on O (API 26) and above.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      // The id of the channel.
      String channelId = "Channel_id";

      // The user-visible name of the channel.
      CharSequence channelName = "Application_name";
      // The user-visible description of the channel.
      String channelDescription = "Application_name Alert";
      int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
      boolean channelEnableVibrate = true;
      //            int channelLockscreenVisibility = Notification.;

      // Initializes NotificationChannel.
      NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
      notificationChannel.setDescription(channelDescription);
      notificationChannel.enableVibration(channelEnableVibrate);
      //            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

      // Adds NotificationChannel to system. Attempting to create an existing notification
      // channel with its original values performs no operation, so it's safe to perform the
      // below sequence.
      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      assert notificationManager != null;
      notificationManager.createNotificationChannel(notificationChannel);

      return channelId;
    } else {
      // Returns null for pre-O (26) devices.
      return null;
    }
  }



  /**
   * Binds stuff
   * @param intent the given intent
   * @return the apple version of a Binder
   */
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  /**
   * Creates the thread that will handle the sending of notifications
   * @param intent the given intent
   * @param flags the given flags
   * @param startId the thread id
   * @return the command
   */
  public int onStartCommand(Intent intent,int flags,final int startId){
    msg=intent.getStringExtra("message");
    whoSent=intent.getStringExtra("whoSent");
    numNotifications=Long.parseLong(intent.getStringExtra("numNotifications"));
    if(intent.getStringExtra("canSend").equals("true")){
      canSend=true;
    }
    Thread thread=new Thread(new NotificationThread(startId));
    thread.start();
    return super.onStartCommand(intent,flags,startId);
  }
  /**
   * Thread for notification sending
   */
  final class NotificationThread implements Runnable{
    //== attributes ==
    int serviceId;

    //== methods ==
    /**
     * Thread constructor
     * @param serviceId the id of the thread
     */
    NotificationThread(int serviceId){
      this.serviceId=serviceId;
    }
    /**
     * The thread function
     */
    @Override
    public void run(){
      while(true){
        sendNotification();
        try{
          Thread.sleep(5000);
        }
        catch(InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    /**
     * Sends a notification if the current day is one day before the appointment day or
     * if the the current day is the same as the appointment day and the current hour
     * is one hour before the appointment hour
     */
    public void sendNotification(){
      if(canSend){
        String channel_id = createNotificationChannel(messageNotificationService.this);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageNotificationService.this, channel_id).setSmallIcon(R.drawable.logo).setContentTitle("You have "+numNotifications+" new notifications!").setContentText("("+whoSent+"): "+msg);
        NotificationManager mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(iID,notificationBuilder.build());
      }
      canSend=false;
    }
  }
}
