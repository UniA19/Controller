package diy.esp8266.controller;

import android.view.InputDevice;
import android.view.MotionEvent;

public class GamepadInputDaemon {

    static void processJoystickInput(MotionEvent event,
                                      int historyPos, boolean debug) {

        InputDevice inputDevice = event.getDevice();

        float lx = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);

        float ly = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);

        float rx = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Z, historyPos);

        float ry = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_RZ, historyPos);

        Connection.setLeft(convert(lx), convert(ly));
        Connection.setRight(convert(rx), convert(ry));

        if (debug) {
            Connection.printToLog();
        }
    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    static int convert(float input) {
        float i = 100 * input;
        return  (int) i;
    }

}
