package pt.ubi.di.pdm.teste;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        et = (EditText) findViewById(R.id.editText);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Server server = new Server("irc.libera.chat", 6667);
        server.login("userHeni", "userheni123", "userHeni", ":garfadaDeSopa");
        server.join("#heni");
        System.out.println("here");
        startTimerThread(server);
    }

    private void startTimerThread(Server s) {
        Thread th = new Thread(new Runnable() {
            private long startTime = System.currentTimeMillis();
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(s.receive_data());
                        }
                    });
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
        //lol
    }

    private void startCounter(Server s) {
        tv.setText("00XUPA");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = s.receive_data();
                        tv.setText(data);
                        System.out.println(data);
                    }
                });
            }
        }});
        thread.start();
    }
}