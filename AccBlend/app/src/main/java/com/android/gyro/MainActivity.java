package com.android.gyro;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends AppCompatActivity {
    TextView textX, textY, textZ;
    String msg;
    int expectedStatusCode = 200;

    SensorManager sensorManager;
    public float x, y, z;
    NanoHTTPD.Response resp;
    DatagramSocket socket;
    Sensor sensor;
    private MyHttpServer httpServer;
    private TextView urlTextView;
    boolean isConnected = false;
    ProgressBar pb;

    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlTextView = findViewById(R.id.urlTextView);
        pb=findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textZ = findViewById(R.id.textZ);
        String ipAddress = getIPAddress(true);
        String serverUrl = "http://" + ipAddress + ":8080";
        try {
            httpServer = new MyHttpServer(ipAddress, 8080);
            httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        urlTextView.setText(serverUrl);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the HTTP server when the activity is destroyed
        if (httpServer != null) {
            httpServer.stop();
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && (useIPv4 ? address.getAddress().length == 4 : address.getAddress().length == 16)) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(accListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(accListener);
        if (socket != null && !socket.isClosed()) {
            socket.close();
            isConnected = false;
        }
    }

    public SensorEventListener accListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            textX.setText("X : " + (float) x + " rad/s");
            textY.setText("Y : " + (float) y + " rad/s");
            textZ.setText("Z : " + (float) z + " rad/s");
            if (httpServer.res != null &&  httpServer.res.getStatus().getRequestStatus()==expectedStatusCode) {
                pb.setAlpha(1.0F);
                httpServer.res.setStatus(NanoHTTPD.Response.Status.lookup(expectedStatusCode));
                pb.setIndeterminate(true);
                httpServer.res.setStatus(NanoHTTPD.Response.Status.lookup(101));
            } else {
                pb.setIndeterminate(false);
                pb.setAlpha(0.0F);
            }
            httpServer.data="\n X="+ Float.parseFloat(Float.toString(x))+"\n Y="+Float.parseFloat(Float.toString(y))+"\n Z="+Float.parseFloat(Float.toString(z));
            new Thread(){
                @Override
                public void run() {
                    msg="\n"+x;
                    httpServer.update(x,y,z);
                    httpServer.msg="thread ";
                    if (httpServer.res != null &&  httpServer.res.getStatus().getRequestStatus()==expectedStatusCode) {
                        httpServer.res.setStatus(NanoHTTPD.Response.Status.lookup(101));
                        pb.setIndeterminate(true);
                        httpServer.res.setStatus(NanoHTTPD.Response.Status.lookup(101));

                    } else {
                        pb.setIndeterminate(false);
                    }
                }
            };
            }
    };
}
