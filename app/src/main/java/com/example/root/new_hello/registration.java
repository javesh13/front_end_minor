package com.example.root.new_hello;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registration extends AppCompatActivity {

    private String roll_num = "random";
    private String group = "random";
    private String name = "random";
    private String email = "random";
    private String phone = "random";
    private String ip = "random";
    private int port = 0;
    private Button reg;
    private SharedPreferences sharedpref = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        reg = (Button) findViewById(R.id.register);
        sharedpref = getSharedPreferences("user_info", MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        ip = extras.getString("ip");
        port = extras.getInt("port");
        roll_num = sharedpref.getString("roll_num", "none");//delete this line after debugging
        boolean Prerigistered = sharedpref.getBoolean("preregistered", false);

        if (Prerigistered) {

            //String status = "Unable to connect";
            //status = register_info(ip, port);
            // show_toast(status);
            Intent ms = new Intent(registration.this, main_screen.class);
            ms.putExtra("ip", ip);
            ms.putExtra("port", port);
            startActivity(ms);


        }

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get info into variables
                if (!getInfo()) {
                    return;
                }
                String status = "Unable to Connect to server";
                try {
                    Log.e("my", "registering info");
                    status = register_info(ip, port);
                    if (status == "0000") {
                        show_toast("unbale to register try again");
                    }
                    show_toast(status);
                    if (!saveinfo()) {
                        return;
                    }
                    Intent ms = new Intent(registration.this, main_screen.class);
                    ms.putExtra("ip", ip);
                    ms.putExtra("port", port);
                    startActivity(ms);
                } catch (Exception e) {
                    show_toast(status);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        return;
    }

    private boolean getInfo() {
        TextView rn = (TextView) findViewById(R.id.roll_number);
        TextView gp = (TextView) findViewById(R.id.group);
        TextView nm = (TextView) findViewById(R.id.name);
        TextView eml = (TextView) findViewById(R.id.email);
        TextView ph = (TextView) findViewById(R.id.phone);
        Pattern pattern = Pattern.compile("[0-9]{11}+");

        roll_num = rn.getText().toString();
        Matcher matcher = pattern.matcher(roll_num);

        if (!matcher.find()) {
            Toast.makeText(this, "Wrong Roll Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        group = gp.getText().toString();
        pattern = Pattern.compile("[a-zA-Z][0-9]");
        matcher = pattern.matcher(group);

        if (!matcher.find()) {
            show_toast("!!Wrong Group!!");
            return false;
        }
        name = nm.getText().toString();
        pattern = Pattern.compile("[^0-9]");
        matcher = pattern.matcher(name);
        if (!matcher.find()) {
            show_toast("!!Weird Name!!");
            return false;
        }
        name = name.replaceAll(" ", "_");

        email = eml.getText().toString();
        pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        if (!matcher.find()) {
            show_toast("!!Check Mail!!");
            return false;
        }
        phone = ph.getText().toString();
        pattern = Pattern.compile("[0-9]{10}+", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(phone);
        if (!matcher.find()) {
            show_toast("!!Check Phone No!!");
            return false;
        }
        return true;
    }

    private boolean saveinfo() {
        if (sharedpref == null) { // line 45
            show_toast("Shared pref is NUll");
            return false;
        }
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString("roll_number", roll_num);
        editor.putString("group", group);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("ip", ip);
        editor.putInt("port", port);
        editor.putBoolean("preregistered", true);
        editor.apply();
        return true;
    }

    private String register_info(String ip, int port) throws IOException {
        Socket s = new Socket(ip, port);
        InputStream din = s.getInputStream();
        OutputStream dout = s.getOutputStream();
        String command = "qz" + "1001" + roll_num.trim() + "," + name.trim() + "," + email.trim() + "," + group.trim() + "," + phone.trim() + "," + "jk";
        byte[] buffer = command.getBytes();
        dout.write(buffer);
        dout.flush();

        byte[] b = new byte[4];
        din.read(b);
        int len = Integer.parseInt(new String(b));
        if (len > 1000) {
            Log.e("my", new String(b));
            show_toast("len > 1000");
            return "0000";
        }
        Log.e("my", Integer.toString(len));
        b = new byte[len];
        din.read(b);
//        show_toast("return status");
        String status = new String(b);
        dout.close();
        din.close();
        s.close();
        return status;
    }

    private void show_toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
