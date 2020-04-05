package diy.esp8266.controller;

import android.content.Context;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

class Calculator extends Thread {

    int leftX = 0, leftY = 0, rightX = 0, rightY = 0;
    private int leftFront, leftBack, rightFront, rightBack;

    private static final double minPercent = 0.5;
    private static final int coolDown = 150;

    private Socket socket;
    private Context context;

    private OutputStream out;
    private PrintWriter output;

    private boolean sendData = false;

    Calculator(Context context)
    {
        this.context = context;
        start();
    }

    @Override
    public void run() {
        super.run();
        try {
            socket = new Socket("192.168.4.1", 100);
            out = socket.getOutputStream();
            output = new PrintWriter(out);
        } catch (Exception e) {
            Toast.makeText(context, "Could not connect to ESP8266! Please restart the App", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        while (true) {
            if (sendData) {
                output.write("<" + leftFront + "|" + rightFront + "|" + leftBack + "|" + rightBack + ">");
                output.flush();
                System.out.println("Sent to Server: <" + leftFront + "|" + rightFront + "|" + leftBack + "|" + rightBack + ">");
                sendData = false;
                try { Thread.sleep(coolDown); } catch (InterruptedException ignored) {}
            }
        }
    }

    private void update()
    {
        System.out.println("Update to: leftX: " + leftX + " leftY: " + leftY + " rightX: " + rightX + " rightY: " + rightY);
        int base = Math.abs((leftY - JoystickFAB.RADIUS) * 100 / (2 * JoystickFAB.RADIUS)); //leftY
        System.out.println("Base: " + base);
        leftFront = leftBack = rightFront = rightBack = base;
        //leftX not jet used

        //use right
        if (rightX < 0) {
            leftFront -= base * Math.abs(((double)rightX / JoystickFAB.RADIUS) * (minPercent / 2));
            leftBack -= base * Math.abs(((double)rightX / JoystickFAB.RADIUS) * (minPercent / 2));
        }

        if (rightX > 0) {
            rightFront -= base * Math.abs(((double)rightX / JoystickFAB.RADIUS) * (minPercent / 2));
            rightBack -= base * Math.abs(((double)rightX / JoystickFAB.RADIUS) * (minPercent / 2));
        }

        if (rightY < 0) {
            leftFront -= base * Math.abs(((double)rightY / JoystickFAB.RADIUS) * (minPercent / 2));
            rightFront -= base * Math.abs(((double)rightY / JoystickFAB.RADIUS) * (minPercent / 2));
        }

        if (rightY > 0) {
            leftBack-= base * Math.abs(((double)rightY / JoystickFAB.RADIUS) * (minPercent / 2));
            rightBack -= base * Math.abs(((double)rightY / JoystickFAB.RADIUS) * (minPercent / 2));
        }

        sendData = true;
    }

    void setLeft(int leftX, int leftY)
    {
        this.leftX = leftX;
        this.leftY = leftY;
        update();
    }

    void setRight(int rightX, int rightY)
    {
        this.rightX = rightX;
        this.rightY = rightY;
        update();
    }
}
