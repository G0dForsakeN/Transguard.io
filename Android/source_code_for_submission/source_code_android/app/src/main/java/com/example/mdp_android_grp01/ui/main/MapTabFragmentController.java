package com.example.mdp_android_grp01.ui.main;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.ClipData;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mdp_android_grp01.MainActivity;
import com.example.mdp_android_grp01.R;

import org.json.JSONException;

public class MapTabFragmentController extends Fragment {

    private static final String ARG_SECTION_NUMBER = "sectionNumber";
    private static final String TAG = "MapFragment";

    private ViewModel1 viewModel1;

    Button resetButton;
//    Button updateButton;
//    ImageButton changeDirectionButton;
    ImageButton setNorthObstacleDirectionButton;
    ImageButton setSouthObstacleDirectionButton;
    ImageButton setWestObstacleDirectionButton;
    ImageButton setEastObstacleDirectionButton;
    ToggleButton obstacleButton;
    ImageButton clearButton;
    ToggleButton startPointButton;
//    ToggleButton wayPointButton;
//    Switch switchManualOrAuto;
    ToggleButton toggleDirection;
    GridMapView gridMapViewDescriptor;
    private static boolean updateAuto = false;
    public static boolean isUpdateRequestManual = false;
    public static MapTabFragmentController newInstance(int index) {
        MapTabFragmentController fragment = new MapTabFragmentController();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public boolean isViewInBounds(View view, int x, int y){
        Rect outRect = new Rect();
        int[] location = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0],location[1]);
        return outRect.contains(x, y);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_map, container, false);
        View main = inflater.inflate(R.layout.activity_main, container, false);
        gridMapViewDescriptor = MainActivity.getGridMapDescriptor();
        final MapDirectionFragmentView mapDirectionFragmentView = new MapDirectionFragmentView();

        resetButton = root.findViewById(R.id.resetMapBtn);
        startPointButton = root.findViewById(R.id.startpointbtn);
//        wayPointButton = root.findViewById(R.id.waypointbtn);
//        changeDirectionButton = root.findViewById(R.id.directionBTN);

        obstacleButton = root.findViewById(R.id.addobstaclebtn);
        clearButton = root.findViewById(R.id.clearbtn);
//        switchManualOrAuto = root.findViewById(R.id.manualAutoBtn);
        toggleDirection = root.findViewById(R.id.ChangeDirectionBtn);
//        updateButton = root.findViewById(R.id.updateButton);
        setEastObstacleDirectionButton = root.findViewById(R.id.eastObstacleBTN);
        setEastObstacleDirectionButton.setBackgroundResource(R.drawable.eastobstaclebtn);
        setNorthObstacleDirectionButton = root.findViewById(R.id.northObstacleBTN);
        setNorthObstacleDirectionButton.setBackgroundResource(R.drawable.northobstaclebtn);
        setWestObstacleDirectionButton = root.findViewById(R.id.westObstacleBTN);
        setWestObstacleDirectionButton.setBackgroundResource(R.drawable.westobstaclebtn);
        setSouthObstacleDirectionButton = root.findViewById(R.id.southObstacleBTN);
        setSouthObstacleDirectionButton.setBackgroundResource(R.drawable.southobstaclebtn);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Reset map");
                gridMapViewDescriptor.resetMap();
//                updateButton.setTextColor(Color.WHITE);
                setEastObstacleDirectionButton.setVisibility(View.GONE);
                setNorthObstacleDirectionButton.setVisibility(View.GONE);
                setSouthObstacleDirectionButton.setVisibility(View.GONE);
                setWestObstacleDirectionButton.setVisibility(View.GONE);

            }
        });

        startPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked startbtn");
                if (startPointButton.getText().equals("STARTING POINT"))
                    showToast("Cancelled selecting starting point");
                else if (startPointButton.getText().equals("CANCEL") && !gridMapViewDescriptor.getAutomatedUpdate()) {
                    showToast("Please select starting point");
                    gridMapViewDescriptor.setStartCoordStatus(true);
                    gridMapViewDescriptor.toggleCheckedBtn("startButton");
                }
//                else
//                    showToast("Please select manual mode");
                showLog("Exiting startbtn");
            }
        });

//        wayPointButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLog("Clicked wayPointButton");
//                if (wayPointButton.getText().equals("WAYPOINT"))
//                    showToast("Cancelled selecting waypoint");
//                else if (wayPointButton.getText().equals("CANCEL")) {
//                    showToast("Please select waypoint");
//                    gridMapViewDescriptor.setWayPointStatus(true);
//                    gridMapViewDescriptor.toggleCheckedBtn("wayPointButton");
//                }
//                else
//                    showToast("Please select manual mode");
//                showLog("Exiting wayPointButton");
//            }
//        });

//        changeDirectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLog("Clicked directionChangeImageBtn");
//                mapDirectionFragmentView.show(getActivity().getFragmentManager(), "Direction Fragment");
//                showLog("Exiting directionChangeImageBtn");
//            }
//        });


        obstacleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gridMapViewDescriptor.getSetObstacleStatus()) {
                    gridMapViewDescriptor.setSetObstacleStatus(true);
                    gridMapViewDescriptor.toggleCheckedBtn("addObstacleButton");
                    setEastObstacleDirectionButton.setVisibility(View.VISIBLE);
                    setNorthObstacleDirectionButton.setVisibility(View.VISIBLE);
                    setSouthObstacleDirectionButton.setVisibility(View.VISIBLE);
                    setWestObstacleDirectionButton.setVisibility(View.VISIBLE);
                }
                else if (gridMapViewDescriptor.getSetObstacleStatus()) {
                    gridMapViewDescriptor.setSetObstacleStatus(false);

                    setEastObstacleDirectionButton.setVisibility(View.GONE);
                    setNorthObstacleDirectionButton.setVisibility(View.GONE);
                    setSouthObstacleDirectionButton.setVisibility(View.GONE);
                    setWestObstacleDirectionButton.setVisibility(View.GONE);
                }
            }
        });

        obstacleButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!gridMapViewDescriptor.getUnSetCellStatus()) {
                    gridMapViewDescriptor.setUnSetCellStatus(true);
                    gridMapViewDescriptor.toggleCheckedBtn("clearButton");
                    showToast("Please remove cells");
                }
                else if (gridMapViewDescriptor.getUnSetCellStatus())

                    gridMapViewDescriptor.setUnSetCellStatus(false);
            }
        });

//        switchManualOrAuto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (switchManualOrAuto.getText().equals("MANUAL")) {
//                    try {
//                        gridMapViewDescriptor.setAutomatedUpdate(true);
//                        updateAuto = true;
//                        gridMapViewDescriptor.toggleCheckedBtn("None");
//                        updateButton.setClickable(false);
//                        updateButton.setTextColor(Color.GRAY);
//                        ManualFragmentController.getCalibrateButton().setClickable(false);
//                        ManualFragmentController.getCalibrateButton().setTextColor(Color.GRAY);
//                        switchManualOrAuto.setText(getResources().getString(R.string.auto));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    showToast("AUTO mode");
//                }
//                else if (switchManualOrAuto.getText().equals("AUTO")) {
//                    try {
//                        gridMapViewDescriptor.setAutomatedUpdate(false);
//                        updateAuto = false;
//                        gridMapViewDescriptor.toggleCheckedBtn("None");
//                        updateButton.setClickable(true);
//                        ManualFragmentController.getCalibrateButton().setClickable(true);
//                        updateButton.setTextColor(Color.WHITE);
//                        ManualFragmentController.getCalibrateButton().setTextColor(Color.BLACK);
//                        switchManualOrAuto.setText(getResources().getString(R.string.manual));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    showToast("MANUAL mode");
//                }
//            }
//        });

        toggleDirection.setBackground(getResources().getDrawable(R.drawable.change_dir_on));
        toggleDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleDirection.getText().equals("y")) {
                        toggleDirection.setBackground(getResources().getDrawable(R.drawable.change_dir_off));
                        gridMapViewDescriptor.setChangeDirection(true);
                        toggleDirection.setText(getResources().getString(R.string.off1));
                    showToast("CHANGING DIRECTION");
                }
                else if (toggleDirection.getText().equals("n")) {
                        toggleDirection.setBackground(getResources().getDrawable(R.drawable.change_dir_on));
                        gridMapViewDescriptor.setChangeDirection(false);
                        toggleDirection.setText(getResources().getString(R.string.toggleDirection));
                    showToast("OFF");
                }
            }
        });


        setNorthObstacleDirectionButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                showLog("Clicked setNorthObstacleDirectionButton");
                GridMapView.isObstacleDirectionCoordinatesSet = true;
                // GridMap.isAddObstacle = true;
                GridMapView.obstacleDirection = "0";
                int x =  (int) e.getRawX();
                int y = (int) e.getRawY();
                if(e.getAction() == MotionEvent.ACTION_DOWN){
                    ClipData.Item item = new ClipData.Item((CharSequence) "North");

                    ClipData dragData = new ClipData(
                            (CharSequence) "North",
                            new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                            item
                    );
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(setNorthObstacleDirectionButton);
                    v.startDrag(dragData, shadow, null, 0);
                    if(isViewInBounds(gridMapViewDescriptor, x, y)) {
                        showLog("dragging setNorthObstacleDirectionButton");
//                        gridMapViewDescriptor.dispatchTouchEvent(e);
                    }
                }
                showLog("Exiting setNorthObstacleDirectionButton");
                return true;
            }
        });

        setNorthObstacleDirectionButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        ((ImageView) v).setColorFilter(Color.LTGRAY);
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:

                        ((ImageView) v).setColorFilter(Color.GRAY);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        showLog("dragging..");
                        if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())) {
                            showLog("inside gridmapview");
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        ((ImageView) v).setColorFilter(Color.LTGRAY);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())){
                            showLog("inside gridmapview");
                            long downTime = SystemClock.uptimeMillis();
                            long eventTime = SystemClock.uptimeMillis();
                            int action = MotionEvent.ACTION_DOWN;
                            MotionEvent e  = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, event.getX(), event.getY(),0);
                            gridMapViewDescriptor.dispatchTouchEvent(e);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;

                    default:
                        break;
                }
                return false;
            }
        });

//        setSouthObstacleDirectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLog("Clicked setSouthObstacleDirectionButton");
//                GridMapView.isObstacleDirectionCoordinatesSet = true;
//                // GridMap.isAddObstacle = true;
//                GridMapView.obstacleDirection = "2";
//                showLog("Exiting setSouthObstacleDirectionButton");
//            }
//        });
        setSouthObstacleDirectionButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                showLog("Clicked setSouthObstacleDirectionButton");
                GridMapView.isObstacleDirectionCoordinatesSet = true;
                // GridMap.isAddObstacle = true;
                GridMapView.obstacleDirection = "3";
                int x =  (int) e.getRawX();
                int y = (int) e.getRawY();
                if(e.getAction() == MotionEvent.ACTION_DOWN){
                    ClipData.Item item = new ClipData.Item((CharSequence) "South");

                    ClipData dragData = new ClipData(
                            (CharSequence) "South",
                            new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                            item
                    );
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(setSouthObstacleDirectionButton);
                    v.startDrag(dragData, shadow, null, 0);
                    if(isViewInBounds(gridMapViewDescriptor, x, y)) {
                        showLog("dragging setSouthObstacleDirectionButton");
//                        gridMapViewDescriptor.dispatchTouchEvent(e);
                    }
                }
                showLog("Exiting setSouthObstacleDirectionButton");
                return true;
            }
        });

        setSouthObstacleDirectionButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        ((ImageView) v).setColorFilter(Color.LTGRAY);
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:

                        ((ImageView) v).setColorFilter(Color.GRAY);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        showLog("dragging..");
                        if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())) {
                            showLog("inside gridmapview");
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        ((ImageView) v).setColorFilter(Color.LTGRAY);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())){
                            showLog("inside gridmapview");
                            long downTime = SystemClock.uptimeMillis();
                            long eventTime = SystemClock.uptimeMillis();
                            int action = MotionEvent.ACTION_DOWN;
                            MotionEvent e  = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, event.getX(), event.getY(),0);
                            gridMapViewDescriptor.dispatchTouchEvent(e);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;

                    default:
                        break;
                }
                return false;
            }
        });

//        setWestObstacleDirectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLog("Clicked setWestObstacleDirectionButton");
//                GridMapView.isObstacleDirectionCoordinatesSet = true;
//               // GridMap.isAddObstacle = true;
//                GridMapView.obstacleDirection = "3";
//                showLog("Exiting setWestObstacleDirectionButton");
//            }
//        });

        setWestObstacleDirectionButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                showLog("Clicked setWestObstacleDirectionButton");
                GridMapView.isObstacleDirectionCoordinatesSet = true;
                // GridMap.isAddObstacle = true;
                GridMapView.obstacleDirection = "2";
                int x =  (int) e.getRawX();
                int y = (int) e.getRawY();
                if(e.getAction() == MotionEvent.ACTION_DOWN){
                    ClipData.Item item = new ClipData.Item((CharSequence) "West");

                    ClipData dragData = new ClipData(
                            (CharSequence) "West",
                            new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                            item
                    );
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(setWestObstacleDirectionButton);
                    v.startDrag(dragData, shadow, null, 0);
                    if(isViewInBounds(gridMapViewDescriptor, x, y)) {
                        showLog("dragging setWestObstacleDirectionButton");
//                        gridMapViewDescriptor.dispatchTouchEvent(e);
                    }
                }
                showLog("Exiting setWestObstacleDirectionButton");
                return true;
            }
        });

        setWestObstacleDirectionButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        ((ImageView) v).setColorFilter(Color.LTGRAY);
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:

                        ((ImageView) v).setColorFilter(Color.GRAY);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        showLog("dragging..");
                        if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())) {
                            showLog("inside gridmapview");
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        ((ImageView) v).setColorFilter(Color.LTGRAY);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())){
                            showLog("inside gridmapview");
                            long downTime = SystemClock.uptimeMillis();
                            long eventTime = SystemClock.uptimeMillis();
                            int action = MotionEvent.ACTION_DOWN;
                            MotionEvent e  = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, event.getX(), event.getY(),0);
                            gridMapViewDescriptor.dispatchTouchEvent(e);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;

                    default:
                        break;
                }
                return false;
            }
        });

//        setEastObstacleDirectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLog("Clicked setEastObstacleDirectionButton");
//                GridMapView.isObstacleDirectionCoordinatesSet = true;
//              //  GridMap.isAddObstacle = true;
//                GridMapView.obstacleDirection = "1";
//                showLog("Exiting setEastObstacleDirectionButton");
//            }
//        });

        setEastObstacleDirectionButton.setOnTouchListener(new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            showLog("Clicked setEastObstacleDirectionButton");
            GridMapView.isObstacleDirectionCoordinatesSet = true;
            // GridMap.isAddObstacle = true;
            GridMapView.obstacleDirection = "1";
            int x =  (int) e.getRawX();
            int y = (int) e.getRawY();
            if(e.getAction() == MotionEvent.ACTION_DOWN){
                ClipData.Item item = new ClipData.Item((CharSequence) "East");

                ClipData dragData = new ClipData(
                        (CharSequence) "East",
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item
                );
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(setEastObstacleDirectionButton);
                v.startDrag(dragData, shadow, null, 0);
                if(isViewInBounds(gridMapViewDescriptor, x, y)) {
                    showLog("dragging setEastObstacleDirectionButton");
//                    gridMapViewDescriptor.dispatchTouchEvent(e);
                }
            }
            showLog("Exiting setEastObstacleDirectionButton");
            return true;
        }
    });

        setEastObstacleDirectionButton.setOnDragListener(new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    ((ImageView) v).setColorFilter(Color.LTGRAY);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:

                    ((ImageView) v).setColorFilter(Color.GRAY);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    showLog("dragging..");
                    if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())) {
                        showLog("inside gridmapview");
                    }
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    ((ImageView) v).setColorFilter(Color.LTGRAY);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DROP:
                    if(isViewInBounds(gridMapViewDescriptor,(int) event.getX(),(int) event.getY())){
                        showLog("inside gridmapview");
                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis();
                        int action = MotionEvent.ACTION_DOWN;
                        MotionEvent e  = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, event.getX(), event.getY(),0);
                        gridMapViewDescriptor.dispatchTouchEvent(e);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    return true;

                default:
                    break;
            }
            return false;
        }
    });


        return root;
    }
    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}