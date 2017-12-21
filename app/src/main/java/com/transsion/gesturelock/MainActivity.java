package com.transsion.gesturelock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GestureLockView gestureLockView = findViewById(R.id.gesture_view);
        gestureLockView.setGridCount(4);
        gestureLockView.setColor(0xff895164, 0xff999999);
        gestureLockView.setOnGestureListener(new GestureLockView.OnGestureListener() {
            @Override
            public void onGestureAccept(int[] gestureCode) {
                Log.i("TTT", "gesture result: " + Arrays.toString(gestureCode));
            }

            @Override
            public void onGestureNotAccept() {
                Log.i("TTT", "gesture not accept");
            }
        });
    }
}
