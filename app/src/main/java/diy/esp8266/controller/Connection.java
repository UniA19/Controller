package diy.esp8266.controller;

import android.content.Context;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

class Connection extends Thread
{
    private static final String HOSTIP = "192.168.4.1";
    private static final int PORT = 100;

    int leftX = 0, leftY = 0, rightX = 0, rightY = 0;

    private static final double minPercent = 0.5;
    private static final int coolDown = 150;

    private Context context;

    private Socket socket;
    private OutputStream out;
    private PrintWriter output;

    private boolean connencted = false;
    private boolean updated = false;

    Connection(Context context)
    {
        this.context = context;
        connect();
        start();
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            if (updated) {
                send("<" + leftX + "|" + leftY + "|" + rightX + "|" + rightY + ">");
                updated = false;
            }
            try {sleep(coolDown);} catch (InterruptedException ignored) {}
        }
    }

    void setLeft(int leftX, int leftY)
    {
        this.leftX = leftX;
        this.leftY = leftY;
        updated = true;
    }

    void setRight(int rightX, int rightY)
    {
        this.rightX = rightX;
        this.rightY = rightY;
        updated = true;
    }

    public void connect()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (output != null) output.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                    socket = new Socket(HOSTIP, PORT);
                    out = socket.getOutputStream();
                    output = new PrintWriter(out);
                    connencted = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    connencted = false;
                }
            }
        });
        thread.start();
    }

    void send(final String data) {
        if (connencted) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    output.write(data);
                    output.flush();
                    System.out.println(data);
                }
            });
            thread.start();
        }
    }
}
