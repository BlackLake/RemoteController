package com.example.blacklake.remotecontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;

public class Controller extends AppCompatActivity {

    Button btnUP,btnDown,btnLeft,btnRight,btnLed,btnBeep,btnImperial,btnLowVel,btnHighVel;
    RadioButton radioLow,radioHigh;
    TextView txtDistance;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        setContentView(R.layout.activity_controller);

        btnUP = (Button)findViewById(R.id.btnUp);
        btnDown = (Button)findViewById(R.id.btnDown);
        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnRight = (Button)findViewById(R.id.btnRight);
        btnLed = (Button)findViewById(R.id.btnLed);
        btnBeep = (Button)findViewById(R.id.btnBeep);
        btnImperial = (Button)findViewById(R.id.btnImperial);
        btnLowVel = (Button)findViewById(R.id.btnLowVel);
        btnHighVel = (Button)findViewById(R.id.btnHighVel);
        radioLow = (RadioButton)findViewById(R.id.radioLow);
        radioHigh = (RadioButton)findViewById(R.id.radioHigh);

        radioLow.setEnabled(false);
        radioHigh.setEnabled(false);

        radioLow.setChecked(false);
        radioHigh.setChecked(true);


        new ConnectBT().execute(); //Call the class to connect

        btnUP.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("w");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnDown.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("s");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnLeft.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("a");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnRight.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("d");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnLed.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("l");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnBeep.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("b");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnImperial.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("i");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnLowVel.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("1");
                    radioLow.setChecked(true);
                    radioHigh.setChecked(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
        btnHighVel.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Send("2");
                    radioLow.setChecked(false);
                    radioHigh.setChecked(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Send("h");
                }
                return false;
            }
        });
    }

    private void Send(String cmd)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(cmd.getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
    private String Receive()
    {
        if (btSocket!=null)
        {

        }
        return null;
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Controller.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }
    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
