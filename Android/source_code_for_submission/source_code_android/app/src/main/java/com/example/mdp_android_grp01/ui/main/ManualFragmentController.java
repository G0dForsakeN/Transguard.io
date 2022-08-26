package com.example.mdp_android_grp01.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mdp_android_grp01.MainActivity;
import com.example.mdp_android_grp01.R;

import static android.content.Context.SENSOR_SERVICE;

public class ManualFragmentController extends Fragment implements SensorEventListener {
    // Init
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "ControlFragment";
    private ViewModel1 viewModel1;

    // Declaration Variable
    // Shared Preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Control Button
    ImageButton exploreBtn;
    ImageButton fastestBtn;
    private static long explore_Timer;
    private static long fastest_Timer;
    ToggleButton exploreButton;
    ToggleButton fastestButton;
    TextView exploretb;
    TextView fastesttv;
    TextView robotstats;
    static Button calibrateButton;
    private static GridMapView gridMapView;

    private Sensor sensor;
    private SensorManager sensorManager;


    // Timer
    static Handler timerHandler = new Handler();

    Runnable timerRunnableExplore = new Runnable() {
        @Override
        public void run() {
            long mili = System.currentTimeMillis() - explore_Timer;
            int sec = (int) (mili / 1000);
            int min = sec / 60;
            sec = sec % 60;

            exploretb.setText(String.format("%02d:%02d", min, sec));

            timerHandler.postDelayed(this, 500);
        }
    };

    Runnable timerRunnableFastest = new Runnable() {
        @Override
        public void run() {
            long mili = System.currentTimeMillis() - fastest_Timer;
            int sec = (int) (mili / 1000);
            int min = sec / 60;
            sec = sec % 60;

            fastesttv.setText(String.format("%02d:%02d", min, sec));

            timerHandler.postDelayed(this, 500);
        }
    };

    // Fragment Constructor
    public static ManualFragmentController newInstance(int index) {
        ManualFragmentController fragment = new ManualFragmentController();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel1 = ViewModelProviders.of(this).get(ViewModel1.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        viewModel1.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // inflate
        View root = inflater.inflate(R.layout.activity_control, container, false);

        // get shared preferences
        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);


        // variable initialization
        exploretb = root.findViewById(R.id.exploreTimeTextView);
        fastesttv = root.findViewById(R.id.fastestTimeTextView);
        exploreButton = root.findViewById(R.id.exploreToggleBtn);
        fastestButton = root.findViewById(R.id.fastestToggleBtn);
        exploreBtn = root.findViewById(R.id.exploreResetImageBtn);
        fastestBtn = root.findViewById(R.id.fastestResetImageBtn);
        calibrateButton = root.findViewById(R.id.calibrateButton);

        robotstats = MainActivity.getRoboStatusTextView();
        fastest_Timer = 0;
        explore_Timer = 0;

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gridMapView = MainActivity.getGridMapDescriptor();

        // Button Listener


        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked exploreToggleBtn");
                ToggleButton exploreToggleBtn = (ToggleButton) v;
                if (exploreToggleBtn.getText().equals("EXPLORE")) {
                    showToast("Exploration timer stop!");
                    robotstats.setText(getResources().getString(R.string.end_exp));
                    timerHandler.removeCallbacks(timerRunnableExplore);
                }
                else if (exploreToggleBtn.getText().equals("STOP")) {
                    showToast("Exploration timer startConnectionService!");
                    MainActivity.sendMessageToBlueTooth("E");
                    robotstats.setText(getResources().getString(R.string.start_exp));
                    explore_Timer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableExplore, 0);
                }
                else {
                    showToast("Else statement: " + exploreToggleBtn.getText());
                }
                showLog("Exiting exploreToggleBtn");
            }
        });

        fastestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked fastestToggleBtn");
                ToggleButton fastestToggleBtn = (ToggleButton) v;
                if (fastestToggleBtn.getText().equals("FASTEST")) {
                    showToast("Fastest timer stop!");
                    robotstats.setText(getResources().getString(R.string.end_fast));
                    timerHandler.removeCallbacks(timerRunnableFastest);
                }
                else if (fastestToggleBtn.getText().equals("STOP")) {
                    showToast("Fastest timer startConnectionService!");
                    MainActivity.sendMessageToBlueTooth("F");
                    robotstats.setText(getResources().getString(R.string.start_fast));
                    fastest_Timer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableFastest, 0);
                }
                else
                    showToast(fastestToggleBtn.getText().toString());
                showLog("Exiting fastestToggleBtn");            }
        });

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Reseting exploration time...");
                exploretb.setText(getResources().getString(R.string.time00));
                robotstats.setText(getResources().getString(R.string.unavail));
                if(exploreButton.isChecked())
                    exploreButton.toggle();
                timerHandler.removeCallbacks(timerRunnableExplore);
            }
        });

        fastestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showToast("Reseting fastest time...");
                fastesttv.setText(getResources().getString(R.string.time00));
                robotstats.setText(getResources().getString(R.string.unavail));
                if (fastestButton.isChecked())
                    fastestButton.toggle();
                timerHandler.removeCallbacks(timerRunnableFastest);
                showLog("Exiting fastestResetImageBtn");            }
        });



        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 MainActivity.sendMessageToBlueTooth("C");
                MapTabFragmentController.isUpdateRequestManual = true;
                showLog("Calibrate ");
            }
        });

        return root;
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    Handler sensorHandler = new Handler();
    boolean sensorFlag= false;

    private final Runnable sensorDelay = new Runnable() {
        @Override
        public void run() {
            sensorFlag = true;
            sensorHandler.postDelayed(this,1000);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if(sensorFlag) {
            if (y < -2) {
                gridMapView.moveRobot("forward");
                MainActivity.updateText();
                MainActivity.sendMessageToBlueTooth("f");
            } else if (y > 2) {
                gridMapView.moveRobot("back");
                MainActivity.updateText();
                MainActivity.sendMessageToBlueTooth("b");
            } else if (x > 2) {
                gridMapView.moveRobot("left");
                MainActivity.updateText();
                MainActivity.sendMessageToBlueTooth("l");
            } else if (x < -2) {
                gridMapView.moveRobot("right");
                MainActivity.updateText();
                MainActivity.sendMessageToBlueTooth("r");
            }
        }
        sensorFlag = false;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            sensorManager.unregisterListener(ManualFragmentController.this);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }



    public static Button getCalibrateButton() {
        return calibrateButton;
    }
}
