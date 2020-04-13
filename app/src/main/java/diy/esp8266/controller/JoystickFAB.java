package diy.esp8266.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class JoystickFAB extends FloatingActionButton {

    public static final int RADIUS = 150; //is also in fragment_controlleroller.xml
    public static final int SIZE = 60; //is also in fragment_controller.xmlr.xml
    float posX;
    float posY;

    String position;

    public JoystickFAB(Context context)
    {
        super(context);
        setSpecificThings();
    }

    public JoystickFAB(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setSpecificThings();
    }

    public JoystickFAB(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setSpecificThings();
    }

    public void setDefaultPosition(float x, float y, String position)
    {
        this.position = position;
        setX(x - ((float)getWidth() / 2));
        posX = x;
        setY(y - ((float) getHeight() / 2));
        posY = y;
        Connection.setRight(0, 0);
    }

    private void setSpecificThings()
    {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view.isClickable()) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (Math.sqrt(Math.pow(event.getRawX() - posX, 2) + Math.pow(event.getRawY() - posY, 2)) < RADIUS) {
                                view.setX(event.getRawX() - ((float) view.getWidth() / 2));
                                view.setY(event.getRawY() - ((float) view.getHeight() / 2));
                                if (position.equals("left")) {
                                    Connection.setLeft(Math.round((event.getRawX() - posX) * 100 / RADIUS), Math.round((event.getRawY() - posY)  * 100 / RADIUS));
                                } else {
                                    Connection.setRight(Math.round((event.getRawX() - posX)  * 100 / RADIUS), Math.round((event.getRawY() - posY) * 100 / RADIUS));
                                }
                            } else {
                                float temp = (float) (RADIUS / Math.sqrt(Math.pow(event.getRawX() - posX, 2) + Math.pow(event.getRawY() - posY, 2)));
                                view.setX(((event.getRawX() - posX) * temp) + posX - ((float) view.getWidth() / 2));
                                view.setY(((event.getRawY() - posY) * temp) + posY - ((float) view.getHeight() / 2));
                                if (position.equals("left")) {
                                    Connection.setLeft(Math.round(((event.getRawX() - posX) * temp) * 100 / RADIUS), Math.round(((event.getRawY() - posY) * temp)) * 100 / RADIUS);
                                } else {
                                    Connection.setRight(Math.round(((event.getRawX() - posX) * temp) * 100 / RADIUS), Math.round(((event.getRawY() - posY) * temp)) * 100 / RADIUS);
                                }
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            view.setX(posX - ((float) getWidth() / 2));
                            view.setY(posY - ((float) getHeight() / 2));
                            if (position.equals("left")) {
                                Connection.setLeft(0, 0);
                            } else {
                                Connection.setRight(0, 0);
                            }

                            view.performClick();
                            break;

                        default:
                            return false;
                    }
                }
                return true;
            }
        });
    }
}
