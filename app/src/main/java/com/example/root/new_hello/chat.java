package com.example.root.new_hello;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.root.new_hello.R.layout.chat_send;

/**
 * Created by me on 11/1/17.
 */

public class chat extends Fragment {
    @Nullable
    private static View my_view;
    private static Button add_chat = null;
    private static EditText new_chat_box;
    private static ListView list_view = null;
    private static ArrayList<String> active_chats;
    private static HashMap<String, ArrayList<String>> database;
    private chat_updater updater_chat;
    private static FloatingActionButton add_chat_toggle = null;
    static Dialog dialog;
    Context mContext;

    public void setActivityContext(Context context) {
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            my_view = inflater.inflate(R.layout.chat, container, false);
            set_up_ui(inflater, container);
        }
        return my_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void update_text(String s) {
        if (my_view != null) {
            //add message to db and update it on layout;

            String sender = s.substring(0, 11);
            String message = s.substring(12);
            add_message_db(sender, message);
        }
    }

    void set_up_ui(LayoutInflater inflater, @Nullable ViewGroup container) {

        add_chat_toggle = my_view.findViewById(R.id.new_chat);
        database = new HashMap<String, ArrayList<String>>();
        list_view = my_view.findViewById(R.id.chat_box_view);
        active_chats = new ArrayList<String>();
        updater_chat = new chat_updater(mContext, 0, active_chats);
        list_view.setAdapter(updater_chat);
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.new_chat_dialog);
        dialog.setTitle("New Chat");
        new_chat_box = (EditText) dialog.findViewById(R.id.new_chat_box);
        add_chat = (Button) dialog.findViewById(R.id.add_chat_btn);

        add_chat_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate its a rollnumber brfore adding to database
                //active_chats.add((String) add_chat_text_view.getText());
                add_message_db(new_chat_box.getText().toString(), "");
                dialog.hide();
            }
        });

    }

    private class chat_updater extends ArrayAdapter {
        public chat_updater(Context context, int resource, ArrayList<String> active_chats) {
            super(context, resource, active_chats);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.active_chat_label, parent, false);
            }

            final String r_no = (String) getItem(position);
            TextView name_a = (TextView) convertView.findViewById(R.id.name_acl);
            name_a.setText(r_no);
            name_a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ms = new Intent(mContext, chat.class);
                    ms.putExtra("roll_number", r_no);
                    startActivity(ms);//yahan pe error aa raha hai
                }
            });


            return convertView;
        }

    }

    //save data base to file and load databse to file on resume.
    void add_message_db(String sender, String message) {
        int position = -1;
        position = active_chats.indexOf(sender);
        if (position == -1) {
            //create active chat and then add message to corresponding chat.
            active_chats.add(sender);
            database.put(sender, new ArrayList<String>());
            updater_chat.notifyDataSetChanged();
            if (message != "")
                database.get(sender).add(message);
            Toast.makeText(my_view.getContext(), "new active chat", Toast.LENGTH_SHORT).show();
        } else {
            // add message to cooresponding chat in hashmap.
            if (message != "")
                database.get(sender).add(message);
            Toast.makeText(my_view.getContext(), "old active chat", Toast.LENGTH_SHORT).show();
        }
    }
}
