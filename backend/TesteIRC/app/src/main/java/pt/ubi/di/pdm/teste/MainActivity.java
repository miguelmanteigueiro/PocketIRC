package pt.ubi.di.pdm.teste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
  TextView tv;
  EditText et;
  Button bt;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tv = (TextView) findViewById(R.id.textView);
    et = (EditText) findViewById(R.id.editText);
    bt = (Button) findViewById(R.id.button);

    bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(MainActivity.this, ChatRoom.class);
        startActivity(i);
      }
    });

    if (0 == 1) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);

      Server server = new Server("irc.libera.chat", 6667);
      server.login("userHeni", "userheni123", "userHeni", ":garfadaDeSopa");
      server.join("#heni", "");

      // create a blocking queue
      BlockingQueue<String> queue = new LinkedBlockingQueue<>();

      // create a producer and consumer
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

      Runnable consumer = new Runnable() {
        public void run() {
          while (true) {
            final String[] message = {null};
            try {
              message[0] = queue.take();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            //String finalMessage = message;
            tv.post(new Runnable() {
              @Override
              public void run() {
                /* Parse the message */
                message[0] = Parser.parse_message(message[0]);
                /* ----------------- */
                tv.setText(message[0]);
              }
            });
          }
        }
      };

      new Thread(producer).start();
      new Thread(consumer).start();

      // Send Massage to the Server (EditText)
      bt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          String msg = et.getText().toString();
          // Check if msg is a command
          if (Commands.checkIfCommand(msg)) {
          }
          server.send_message(msg);
        }
      });

    }
  }
}