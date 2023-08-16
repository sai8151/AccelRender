package com.android.gyro;

import android.widget.ProgressBar;

import fi.iki.elonen.NanoHTTPD;

public class MyHttpServer extends NanoHTTPD {
String msg;
String data;
float x,y,z;
Response res;

    public MyHttpServer(String ipAddress, int port) {
        super(port);
    }

    public Response serve(IHTTPSession session) {

        //msg= "Hello, this is your Android HTTP server!\n"+data;
        res= newFixedLengthResponse(Response.Status.OK, "text/plain", data);

        return res;

    }

    public void update(float x,float y,float z) {
        this.x=x;
        this.y=y;
        this.z=z;
        data="\nX="+ Float.parseFloat(Float.toString(x))+"\nY="+Float.parseFloat(Float.toString(y))+"\nZ="+Float.parseFloat(Float.toString(z));
    }
}
