package diy.esp8266.controller;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import static diy.esp8266.controller.Globals.DEF_IP;
import static diy.esp8266.controller.Globals.DEF_PORT;
import static diy.esp8266.controller.Globals.IS_DEBUG;
import static diy.esp8266.controller.Globals.PREFS_IP;
import static diy.esp8266.controller.Globals.PREFS_PORT;

class Connection
{
    private static int leftX = 0, leftY = 0, rightX = 0, rightY = 0;

    private static final int coolDown = 200;

    private static Socket socket;
    private static DataOutputStream output;

    private static boolean connected = false;
    private static boolean updated = false;

    private static boolean wasStarted = false;
    private static boolean isReceiving = false;


    static void start(final SharedPreferences globals)
    {
        if (!wasStarted) {
            wasStarted = true;
            connect(globals);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        boolean dontTimeOut = i * coolDown > 1000;//send every second (not exact)
                        if (updated || dontTimeOut) {
                            send("<#" + format(leftX) + "|" + format(leftY) + "|" + format(rightX) + "|" + format(rightY) + ">", globals);
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

    private static void connect(final SharedPreferences globals)
    {
        if (!connected) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress address = InetAddress.getByName(globals.getString(PREFS_IP, DEF_IP));
                        if (address.isReachable(100)) {
                            System.out.println("Tried to connect");
                            if (output != null) output.close();
                            if (socket != null) socket.close();
                            socket = new Socket(globals.getString(PREFS_IP, DEF_IP), globals.getInt(PREFS_PORT, DEF_PORT));
                            output = new DataOutputStream(socket.getOutputStream());
                            connected = true;
                            start(globals);
                        } else {
                            System.out.println("Address not reachable");
                            if (output != null) output.close();
                            if (socket != null) socket.close();
                        }
                    } catch (Exception e) {
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        e.printStackTrace();
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        connected = false;
                    }
                }
            });
            thread.start();
        }
    }

    private static void send(final String data, final SharedPreferences globals)
    {
        if (connected) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        output.writeChars(data);
                        output.flush();
                        System.out.println(data);
                    } catch (Exception ex) {
                        connected = false;
                        connect(globals);
                        ex.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            connect(globals);
        }
    }

    static void sendCalibrate(SharedPreferences globals)
    {
        send("<*cal>", globals);
    }

    @SuppressLint("DefaultLocale")
    private static String format(int i)
    {
        if (i < 0) {
            return String.format("%04d", i);
        } else {
            return "+" + String.format("%03d", i);
        }
    }

    static void receiveData(final SharedPreferences globals) {
        if (!isReceiving) {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("START RECEIVE");
                    isReceiving = true;
                    try {
                        while (globals.getBoolean(IS_DEBUG, false)) {
                            //System.out.println("Connected: " + connected + " socket closed: " + socket.isClosed());
                            if (connected && !socket.isClosed()) {
                                //System.out.println("In receive loop");
                                InputStream input = socket.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                                int length = input.available();
                                if (length > 0) {
                                    while(!reader.ready()) Thread.sleep(1);
                                    //System.out.println("Data available");
                                    char[] arr = new char[length];
                                    reader.read(arr, 0, length);
                                    String str = String.valueOf(arr);
                                    ControllerActivity.addToDebug(str);
                                    System.out.println("Received: " + str);
                                }
                            }
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isReceiving = false;
                        receiveData(globals);
                    }
                    isReceiving = false;
                    System.out.println("ENDED RECEIVE");
                }
            });
            thread.start();
        }
    }
}

