package com.example.root.new_hello;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.root.new_hello.main_screen.database;
import static com.example.root.new_hello.main_screen.message_to_be_sent;

public class do_chat extends AppCompatActivity {
    static Button send_button = null;
    static EditText tob_sent_msg = null;
    static String roll_num;
    private ListView chat_list = null;
    private personal_chat_adapter pca = null;
    private String sender = "";
    private SharedPreferences sharedPreferences = null;
    private ArrayList<String> message_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_chat);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            roll_num = extras.getString("roll_number");
            sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
            sender = sharedPreferences.getString("roll_number", "none");
            send_button = findViewById(R.id.send_button);
            tob_sent_msg = findViewById(R.id.new_msg);
            message_list = database.get(roll_num);
            if (message_list != null) {
                pca = new personal_chat_adapter(this, 0, message_list);
            } else {
                Toast.makeText(this, "cry baby", Toast.LENGTH_SHORT).show();
            }


            chat_list = (ListView) findViewById(R.id.personal_chat);
            if (chat_list != null)
            chat_list.setAdapter(pca);
            else {
                Toast.makeText(this, "chat_lst is null", Toast.LENGTH_SHORT).show();
            }
            send_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get text and send and add to current chat and update view or the view gets automatically updated.
                    String msg = tob_sent_msg.getText().toString();
                    if (msg.length() > 0) {
                        send_and_save(roll_num, msg);
                        pca.notifyDataSetChanged();
                        tob_sent_msg.setText("");
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void send_and_save(String receiver, String msg) {
        if (msg.length() > 292) {
            //maximum length of message that could be sent is 300 for now
            msg = msg.substring(0, 293);
        }
        message_to_be_sent.set("qz3001," + receiver + "," + sender + "," + msg + ",jk");
        while (message_to_be_sent.get() != "") ;
        //made sure that message is sent.
        database.get(receiver).add(msg);
    }

    private class personal_chat_adapter extends ArrayAdapter {
        public personal_chat_adapter(Context context, int resource, ArrayList<String> active_chats) {
            super(context, resource, active_chats);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_send, parent, false);
            }

            final String message_itr = (String) getItem(position);
            TextView name_a = (TextView) convertView.findViewById(R.id.send_bubble);
            name_a.setText(message_itr);
            return convertView;
        }

    }
}
