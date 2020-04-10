package diy.esp8266.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ControllerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controller, container, false);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.post(new Runnable() {
            @Override
            public void run() {
                Connection connection = new Connection(getContext());
                int padding = 250 + JoystickFAB.RADIUS;
                JoystickFAB left = view.findViewById(R.id.joystick_left);
                ImageView backLeft = view.findViewById(R.id.background_left);
                JoystickFAB right = view.findViewById(R.id.joystick_right);
                ImageView backRight = view.findViewById(R.id.background_right);

                left.setDefaultPosition(padding, (float) view.getHeight() / 2, "left", connection);
                backLeft.setX(padding - (float) backLeft.getWidth() / 2);
                backLeft.setY((float) (view.getHeight() - backLeft.getHeight()) / 2);

                right.setDefaultPosition(view.getWidth() - padding, (float) view.getHeight() / 2, "right", connection);
                backRight.setX(view.getWidth() - padding - (float) backLeft.getWidth() / 2);
                backRight.setY((float) (view.getHeight() - backLeft.getHeight()) / 2);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
