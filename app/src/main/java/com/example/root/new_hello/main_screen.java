package com.example.root.new_hello;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class main_screen extends AppCompatActivity {

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private static ViewPager mViewPager;
    private static assignment assignment_obj = null;
    private static chat chat_obj = null;
    private static home_tab home_tab_obj = null;
    private static TabLayout tabLayout = null;
    private static String roll_number = "";
    private static SharedPreferences sharedpref = null;
    private int time_delay = 1000;
    private static String ip = "";
    private static int port = 0;
    private static Socket s = null;
    private static boolean checking_for_messages = false;
    private static boolean receiving_messages = true;
    static Check_for_message cfm;
    static Receive_message rm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        if (savedInstanceState == null) {
            assignment_obj = new assignment();
            chat_obj = new chat();
            chat_obj.setActivityContext(this);
            home_tab_obj = new home_tab();
            sharedpref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
            roll_number = sharedpref.getString("roll_number", "02914802714");
            Bundle extras = getIntent().getExtras();
            ip = extras.getString("ip");
            port = extras.getInt("port");
            setTitle(roll_number);
            try {
                s = initialize_socket(s);
            } catch (Exception e) {
                show_toast("Unable To Connect to Server");
            }
            checking_for_messages = false;
            receiving_messages = false;
            cfm = new Check_for_message(s, ip, port);
            rm = new Receive_message(s, ip, port, chat_obj);
        }

        if (!checking_for_messages) {
            start_check();
            checking_for_messages = true;
        }
        if (!receiving_messages) {
            start_receive();
            receiving_messages = true;
        }
        setup_ui();


    }

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(nonRoot);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        return;
    }

    private void setup_ui() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public assignment getAssignment() {
            return assignment_obj;
        }

        public chat getChat() {
            return chat_obj;
        }

        public home_tab getHome_tab() {
            return home_tab_obj;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return getAssignment();
                case 1:
                    return getHome_tab();
                case 2:
                    return getChat();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public class Check_for_message implements Runnable {
        private final Socket s;
        private final String ip;
        private final int port;
        OutputStream dout = null;

        Check_for_message(Socket s, String ip, int port) {
            this.s = s;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {

            try {
                dout = s.getOutputStream();
                while (true) {
                    String command = "qz1004," + roll_number + ",jk";
                    byte[] buffer = command.getBytes();
                    dout.write(buffer);
                    TimeUnit.SECONDS.sleep(2);

                }
            } catch (Exception e) {
                checking_for_messages = false;
            }
        }
    }

    public class Receive_message implements Runnable {
        private final Socket s;
        private final String ip;
        private final int port;
        InputStream din = null;
        byte[] buffer;
        private chat co = null;
        private View v;

        Receive_message(Socket s, String ip, int port, chat co) {
            this.s = s;
            this.ip = ip;
            this.port = port;
            buffer = new byte[1024];
            this.co = co;
        }

        @Override
        public void run() {
            try {
                din = s.getInputStream();
                co.update_text("hello");
                String message = null;
                while (true) {
                    byte[] buffer = new byte[4];
                    int read_status = din.read(buffer);
                    if (read_status != -1) {
                        ByteBuffer wrapped = ByteBuffer.wrap(buffer); // big-endian by default
                        int num = wrapped.getInt();
                        if (num > 1024) {
                            Log.e("my", "main_screen len > 1024");
                        }
                        buffer = new byte[num];
                        read_status = din.read(buffer);
                        message = new String(buffer);
                        try {
                            final String finalMessage = message;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    co.update_text(finalMessage);
                                }
                            });

                        } catch (Exception e) {
                            Log.e("Fragment Error", e.getMessage());
                            Log.e("Error write fragment", "sd");
                        }
                    }

                }

            } catch (Exception e) {
                receiving_messages = false;
                Log.e("Died in exception", "If unable to det_exception");
                Log.e("Died due to exception", e.getMessage());
            }
        }
    }

    private Socket initialize_socket(Socket s) throws IOException {
        if (s == null) {
            show_toast("Connected");
            return new Socket(ip, port);
        }
        return null;
    }

    private void show_toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void start_check() {
        new Thread(cfm).start();
    }

    public void start_receive() {
        new Thread(rm).start();
    }
}
