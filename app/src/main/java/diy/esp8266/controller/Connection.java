package diy.esp8266.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class Connection
{
    private static final String HOSTIP = "192.168.4.1";
    private static final int PORT = 100;

    static int leftX = 0, leftY = 0, rightX = 0, rightY = 0;

    private static final int coolDown = 150;

    private static Socket socket;
    private static OutputStream out;
    private static PrintWriter output;

    private static boolean connencted = false;
    private static boolean updated = false;

    static boolean wasStarted = false;

    Connection()
    {
        connect();
        start();
    }

    static void start()
    {
        if (!wasStarted) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        checkConnection();
                        if (updated) {
                            send("<" + leftX + "|" + leftY + "|" + rightX + "|" + rightY + ">");
                            updated = false;
                        }
                        try {Thread.sleep(coolDown);} catch (InterruptedException ignored) {}
                    }
                }
            });
            thread.start();
            wasStarted = true;
        }
    }

    static void checkConnection()
    {
        try {
            InetAddress address = InetAddress.getByName(HOSTIP);
            connencted = address.isReachable(100);
        } catch (Exception ex) {
            connencted = false;
        }
    }

    static void setLeft(int leftX, int leftY)
    {
        Connection.leftX = leftX;
        Connection.leftY = leftY;
        updated = true;
    }

    static void setRight(int rightX, int rightY)
    {
        Connection.rightX = rightX;
        Connection.rightY = rightY;
        updated = true;
    }


    public static void connect()
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

    static void send(final String data)
    {
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

    static void sendCalibrate()
    {
        send("<*cal>");
    }
}
