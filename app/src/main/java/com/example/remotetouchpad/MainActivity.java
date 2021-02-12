package com.example.remotetouchpad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Touchpad touchpad;
    private Dispatch dispatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        touchpad = new Touchpad(width);
        try {
            dispatch = new Dispatch(touchpad);
        } catch (UnknownHostException e) {
            Toast.makeText(getApplicationContext(), "Erro ao inicializar", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        final EditText address = findViewById(R.id.address);
        final EditText port = findViewById(R.id.port);
        final ToggleButton startServer = findViewById(R.id.startButton);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startServer.isChecked()){
                    try {
                        dispatch.setPort(Integer.valueOf(port.getText().toString()));
                        dispatch.setAddress(address.getText().toString());
                        dispatch.setOn(true);
                        address.setInputType(InputType.TYPE_NULL);
                        port.setInputType(InputType.TYPE_NULL);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                } else {
                    dispatch.setOn(false);
                    address.setInputType(InputType.TYPE_CLASS_TEXT);
                    port.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });


        View screen = findViewById(R.id.screen);
        screen.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();

                if (event.getPointerCount() == 1 && System.currentTimeMillis() - touchpad.getTimerWheel() > 600) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchpad.setInitialPosition(x, y);
                            touchpad.setTimerClick();
                            new Thread(dispatch).start();

                            break;
                        case MotionEvent.ACTION_MOVE:
                            touchpad.setActualPosition(x, y);
                            new Thread(dispatch).start();

                            break;
                        case MotionEvent.ACTION_UP:
                            touchpad.verifyClick();
                            new Thread(dispatch).start();
                            break;
                    }
                } else if (event.getPointerCount() == 2) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchpad.setWheelInitialPosition(y);
                            touchpad.reset();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touchpad.wheelTimerReset();
                            touchpad.setWheelActualPosition(y);
                            touchpad.setWheelInitialPosition(y);
                            new Thread(dispatch).start();

                            break;
                        case MotionEvent.ACTION_UP:
                            touchpad.reset();
                            new Thread(dispatch).start();

                            break;
                    }
                }

                return true;
            }
        });

    }
}
