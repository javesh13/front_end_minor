package com.example.root.new_hello;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button cnt_serv = (Button) findViewById(R.id.connect_to_server);
        cnt_serv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip_val;
                int port;
                Socket s = null;

                TextView ip_textbox = (TextView) findViewById(R.id.ip);
                TextView port_textbox = (TextView) findViewById(R.id.port);
                ip_val = ip_textbox.getText().toString();
                port = Integer.valueOf(port_textbox.getText().toString());


                try {
                    s = new Socket(ip_val, port);
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    String command = "qz1002xxxxxxxxxxxjk";//random check
                    dout.writeBytes(command);
                    dout.flush();
                    dout.close();
                    s.close();
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    Intent register = new Intent(MainActivity.this, registration.class);
                    register.putExtra("ip", ip_val);
                    register.putExtra("port", port);
                    startActivity(register);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    //ip.setText("Unable to connect");
                }

            }
        });

    }
}
