package com.amal.bingo;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Fade;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity8 extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    private static final boolean AUTO_HIDE = true;
    ListView listView;
    TextView text;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();;
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    static final int MESSAGE_READ = 1;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;
    public static int peerCount;
    public static int turn;
    public static List<String> ignoreTurn;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peerList.getDeviceList())
                {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView = (ListView) findViewById(R.id.listview);
                listView.setAdapter(adapter);
            }

            if(peers.size() == 0)
            {
                Toast.makeText(getApplicationContext(), "No device(s) found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            text = (TextView) findViewById(R.id.info1);
            TextView game = (TextView) findViewById(R.id.gamename);
            Button next = (Button) findViewById(R.id.nextBtn2);
            Button send = (Button) findViewById(R.id.send);
            next.setVisibility(View.VISIBLE);
            next.setEnabled(true);
            game.setVisibility(View.VISIBLE);
            game.setEnabled(true);
            send.setVisibility(View.VISIBLE);
            send.setEnabled(true);
            Toast toast = Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            toast = Toast.makeText(getApplicationContext(), "Try sending messages to verify strength of connection", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
            {
                text.setText("HOST");
                serverClass = new ServerClass();
                serverClass.start();
            }
            else if(wifiP2pInfo.groupFormed)
            {
                text.setText("CLIENT");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt)
        {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null)
            {
                try {
                    bytes = inputStream.read(buffer);

                    if(bytes > 0)
                    {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress)
        {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    int temp = 0;
                    String tempMsg = new String(readBuff, 0, msg.arg1);

                    try{
                        temp = Integer.parseInt(tempMsg);
                    } catch (Exception e){ e.printStackTrace(); }

                    try {
                        if (temp < 26 && temp > 0) {
                            Intent intent = new Intent("data1");
                            intent.putExtra("idMess", tempMsg);
                            LocalBroadcastManager.getInstance(FullscreenActivity8.this).sendBroadcast(intent);

                        } else if (tempMsg.equals("start")) {
                            peerCount = peers.size();
                            Intent intent = new Intent(FullscreenActivity8.this, FullscreenActivity3.class);
                            startActivity(intent);

                        } else if (tempMsg.equals("begin")) {
                            Intent intent = new Intent("data3");
                            intent.putExtra("begin1", "begin");
                            LocalBroadcastManager.getInstance(FullscreenActivity8.this).sendBroadcast(intent);

                        } else if (tempMsg.equals("ready")) {
                            Intent intent = new Intent("data3");
                            intent.putExtra("begin1", "ready");
                            LocalBroadcastManager.getInstance(FullscreenActivity8.this).sendBroadcast(intent);

                        } else if (tempMsg.equals("not ready")) {
                            Toast.makeText(getApplicationContext(), "Some of the players are still arranging", Toast.LENGTH_SHORT).show();

                        } else if (tempMsg.equals("peer")) {
                            FullscreenActivity3.count++;

                        } else if (tempMsg.contains("bingo")) {
                            String[] strings = tempMsg.split(" ");
                            ignoreTurn.add(strings[1]);
                            Toast.makeText(getApplicationContext(), strings[2]+" hit bingo!", Toast.LENGTH_SHORT).show();
                            if(!ignoreTurn.contains(turn+""))
                                FullscreenActivity4.bingoNum++;

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    break;
            }
            return true;
        }
    });

    private BroadcastReceiver tempReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ThreadX thread = new ThreadX(intent.getStringExtra("idMsg"));
            thread.start();
        }
    };

    private BroadcastReceiver tempReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ThreadX thread = new ThreadX(intent.getStringExtra("begin"));
            thread.start();
        }
    };

    private BroadcastReceiver tempReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ThreadX thread = new ThreadX(intent.getStringExtra("ready"));
            thread.start();
        }
    };

    private BroadcastReceiver tempReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ThreadX thread = new ThreadX(intent.getStringExtra("peer"));
            thread.start();
        }
    };

    private BroadcastReceiver tempReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ThreadX thread = new ThreadX(intent.getStringExtra("bingo"));
            thread.start();
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());

        setContentView(R.layout.activity_fullscreen8);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver,new IntentFilter("data"));
        LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver2,new IntentFilter("data2"));
        LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver3,new IntentFilter("data4"));
        LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver4,new IntentFilter("peer"));
        LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver5,new IntentFilter("bingo"));
        ignoreTurn = new ArrayList<>();
        Button scan = (Button) findViewById(R.id.nextBtn);
        TextView game = (TextView) findViewById(R.id.gamename);
        Button next = (Button) findViewById(R.id.nextBtn2);
        Button send = (Button) findViewById(R.id.send);
        text = (TextView) findViewById(R.id.info1);
        next.setVisibility(View.INVISIBLE);
        next.setEnabled(false);
        send.setVisibility(View.INVISIBLE);
        send.setEnabled(false);
        game.setVisibility(View.INVISIBLE);
        game.setEnabled(false);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        scan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "MissingPermission"})
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Discovery Started", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Discovery Starting Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peerCount = peers.size();
                ThreadX thread = new ThreadX("start");
                thread.start();
                Intent intent = new Intent(FullscreenActivity8.this, FullscreenActivity3.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connecting to "+device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(peers.size() != 0) {
                    ThreadX thread = new ThreadX(game.getText().toString());
                    thread.start();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    class ThreadX extends Thread{

        String msg;

        ThreadX(String msg){
            this.msg = msg;
        }

        @Override
        public void run() {
            if(sendReceive != null) {
                sendReceive.write(msg.getBytes());
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, 0);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}