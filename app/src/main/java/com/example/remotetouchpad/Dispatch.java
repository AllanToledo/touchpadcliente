package com.example.remotetouchpad;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Dispatch implements Runnable {

    private Touchpad touchpad;
    private InetAddress address;
    private int port;
    private Boolean isOn = false;

    public Dispatch(String host, int port, Touchpad touchpad) throws UnknownHostException {
        this.touchpad = touchpad;
        this.port = port;
        if (host != null)
            this.address = InetAddress.getByName(host);
    }

    public Dispatch(int port, Touchpad touchpad) throws UnknownHostException {
        this(null, port, touchpad);
    }

    public Dispatch(Touchpad touchpad) throws UnknownHostException {
        this(null, 0, touchpad);
    }

    public void setOn(Boolean on) {
        isOn = on;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String host) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
    }

    @Override
    public void run() {
        if (!isOn)
            return;

        byte[] buf = touchpad.getPositions();
        try (DatagramSocket socket = new DatagramSocket(port)) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 3000);
            socket.send(packet);
            //Log.v("Touchpad", "Pacote enviado port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
