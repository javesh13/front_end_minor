package com.example.root.new_hello;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import static com.example.root.new_hello.main_screen.active_chats;
import static com.example.root.new_hello.main_screen.database;

/**
 * Created by me on 11/1/17.
 */

public class chat extends Fragment {
    @Nullable
    private static View my_view;
    private static Button add_chat = null;
    private static EditText new_chat_box;
    private static ListView list_view = null;
    private chat_updater updater_chat;
    private static FloatingActionButton add_chat_toggle = null;
    static Dialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
            my_view = inflater.inflate(R.layout.chat, container, false);
            set_up_ui(inflater, container);

        return my_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void update_text() {
        if (my_view != null) {
            updater_chat.notifyDataSetChanged();
        }
    }


    void set_up_ui(LayoutInflater inflater, @Nullable ViewGroup container) {

        add_chat_toggle = my_view.findViewById(R.id.new_chat);
        list_view = my_view.findViewById(R.id.chat_box_view);
        updater_chat = new chat_updater(getContext(), 0, active_chats);
        list_view.setAdapter(updater_chat);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.new_chat_dialog);
        dialog.setTitle("New Chat");
        new_chat_box = (EditText) dialog.findViewById(R.id.new_chat_box);
        add_chat = (Button) dialog.findViewById(R.id.add_chat_btn);

        main_screen.setOnMessageUpdateListener(new main_screen.onMessageUpdateListener() {
            @Override
            public void onMessageGenerated(String message) {
                update_text();
            }
        });

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
                String sender = new_chat_box.getText().toString();
                active_chats.add(sender);
                database.put(sender, new ArrayList<String>());
                update_text();
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
                    Intent ms = new Intent(getContext(), do_chat.class);
                    ms.putExtra("roll_number", r_no);
                    startActivity(ms);

                }
            });


            return convertView;
        }

    }

    //save data base to file and load databse to file on resume.

}
