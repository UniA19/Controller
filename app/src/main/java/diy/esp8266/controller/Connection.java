package diy.esp8266.controller;

import android.util.Log;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class Connection
{
    private static final String HOSTIP = "192.168.4.1";
    private static final int PORT = 100;

    private static int leftX = 0, leftY = 0, rightX = 0, rightY = 0;

    private static final int coolDown = 150;

    private static Socket socket;
    private static OutputStream out;
    private static PrintWriter output;

    private static boolean connencted = false;
    private static boolean updated = false;

    private static boolean wasStarted = false;

    static void start()
    {
        if (!wasStarted) {
            wasStarted = true;
            connect();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        checkConnection();
                        boolean dontTimeOut = i * coolDown > 1000;//send every second (not exact)
                        if (updated || dontTimeOut) {
                            send("<" + format(leftX) + "|" + format(leftY) + "|" + format(rightX) + "|" + format(rightY) + ">");
                            updated = false;
                            i = 0;
                        }
                        try {Thread.sleep(coolDown);} catch (InterruptedException ignored) {}
                        ++i;
                    }
                }
            });
            thread.start();
        }
    }

    private static void checkConnection()
    {
        try {
            InetAddress address = InetAddress.getByName(HOSTIP);
            connencted = connencted && socket.isConnected() && address.isReachable(100);
        } catch (Exception ex) {
            connencted = false;
        }
        System.out.println("Connected: " + connencted);
    }

    static void setLeft(int leftX, int leftY)
    {
        Connection.leftX = leftX;
        Connection.leftY = leftY;
        updated = true;
        printToLog();
    }

    static void setRight(int rightX, int rightY)
    {
        Connection.rightX = rightX;
        Connection.rightY = rightY;
        updated = true;
        printToLog();
    }

    static void printToLog ()
    {
        Log.d("Input:", "<" + format(leftX) + "|" + format(leftY) + "|" + format(rightX) + "|" + format(rightY) + ">");
    }
    private static void connect()
    {
        checkConnection();
        if (!connencted) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress address = InetAddress.getByName(HOSTIP);
                        if (address.isReachable(100)) {
                            if (output != null) output.close();
                            if (out != null) out.close();
                            if (socket != null) socket.close();
                            socket = new Socket(HOSTIP, PORT);
                            out = socket.getOutputStream();
                            output = new PrintWriter(out);
                            connencted = true;
                            checkConnection();
                        }
                    } catch (Exception e) {
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        e.printStackTrace();
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        connencted = false;
                    }
                }
            });
            thread.start();
        }
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
        } else {
            connect();
        }
    }

    static void sendCalibrate()
    {
        send("<*cal>");
    }

    static String format(int i)
    {
        if (i < 0) {
            return String.format("%04d", i);
        } else {
            return "+" + String.format("%03d", i);
        }
    }
}
