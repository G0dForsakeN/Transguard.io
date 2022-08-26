package com.example.mdp_android_grp01.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.Nullable;

import com.example.mdp_android_grp01.MainActivity;
import com.example.mdp_android_grp01.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

public class GridMapView extends View {

    public GridMapView(Context c) {
        super(c);
        initMap();
    }

    SharedPreferences sharedPreferences;

    private Paint black = new Paint();
    private Paint obstacle = new Paint();
    private Paint robot = new Paint();
    private Paint end = new Paint();
    private Paint start = new Paint();
    private Paint waypoint = new Paint();
    private Paint unExplored = new Paint();
    private Paint explored = new Paint();


    private static JSONObject receivedJsonObject = new JSONObject();
    private static JSONObject bpMapInfo;
    private static String robotDirection = "None";
    private static int[] startPoint = new int[]{-1, -1};
    private static int[] currentPoint = new int[]{-1, -1};
    private static int[] previousPoint = new int[]{-1, -1};
    private static int[] wayCoordinates = new int[]{-1, -1};
    private static ArrayList<int[]> obstacleCoordinates = new ArrayList<>();
    private static ArrayList<String[]> directionOfObstacleCoordinates = new ArrayList<>();
    private static Bitmap obstacleDirectionBitmap;
    private static boolean isAutoUpdateSet = false;
    private static boolean isRobotDrawable = false;
    private static boolean isWayPointSet = false;
    private static boolean isStartCoordinatesSet = false;
    private static boolean isObstacleSet = false;
    public static boolean isObstacleDirectionCoordinatesSet = false;
    private static boolean isCellUnSet = false;
    private static boolean isExploring = false;
    private static boolean isPositionValid = false;
    public static String obstacleDirection = "";
    public static boolean isAddObstacle = false;

    private static final String TAG = "GridMap";
    private static final int Column = 20;
    private static final int Row = 20;
    private static float sizeOfCell;
    private static Cell[][] cellsDetail;

    private boolean isMapDrawn = false;
    public static String MDF_Exploration_Details;
    public static String MDF_Obstacle_Details;


    public GridMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMap();
        black.setStyle(Paint.Style.FILL_AND_STROKE);
        obstacle.setColor(Color.BLACK);
        robot.setColor(Color.GREEN);
        //end.setColor(Color.RED);
        start.setColor(Color.CYAN);
        waypoint.setColor(Color.YELLOW);
        unExplored.setColor(Color.LTGRAY);
        explored.setColor(Color.WHITE);
        sharedPreferences = getContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
    }

    private void initMap() {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isMapDrawn == false) {
            this.createGridCell();
           // this.setEndCoordinates(19, 19);
            isMapDrawn = true;
        }

        displayCellInGrid(canvas);
        displayHorizontalLines(canvas);
        displayVerticalLines(canvas);
        displayGridNumber(canvas);
        if (getCanDrawRobot())
            displayRobotOnGrid(canvas, currentPoint);
        drawObstacleWithDirection(canvas, directionOfObstacleCoordinates);

        showLog("Exiting onDraw");
    }

    private void displayCellInGrid(Canvas canvas) {
        showLog("Entering displayCellInGrid");

        for (int x = 1; x <= Column; x++)
            for (int y = 0; y < Row; y++)
                if (!cellsDetail[x][y].type.equals("image") && cellsDetail[x][y].getId() == -1) {
                    canvas.drawRect(cellsDetail[x][y].startX, cellsDetail[x][y].startY, cellsDetail[x][y].endX, cellsDetail[x][y].endY, cellsDetail[x][y].paint);
                } else {
                    Paint textPaint = new Paint();
                    textPaint.setTextSize(30);
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawRect(cellsDetail[x][y].startX, cellsDetail[x][y].startY, cellsDetail[x][y].endX, cellsDetail[x][y].endY, cellsDetail[x][y].paint);
                    canvas.drawText(String.valueOf(cellsDetail[x][y].getId()), (cellsDetail[x][y].startX + cellsDetail[x][y].endX) / 2, cellsDetail[x][y].endY + (cellsDetail[x][y].startY - cellsDetail[x][y].endY) / 4, textPaint);
                }

        showLog("Exiting displayCellInGrid");
    }

    public void displayNumberInCell(int x, int y, int id) {
        cellsDetail[x + 1][19 - y].setType("image");
        cellsDetail[x + 1][19 - y].setId(id);
        this.invalidate();
    }

    private void displayHorizontalLines(Canvas canvas) {
        for (int y = 0; y <= Row; y++)
            canvas.drawLine(cellsDetail[1][y].startX, cellsDetail[1][y].startY - (sizeOfCell /30), cellsDetail[20][y].endX, cellsDetail[20][y].startY - (sizeOfCell / 30), black);
    }

    private void displayVerticalLines(Canvas canvas) {
        for (int x = 0; x <= Column; x++)
            canvas.drawLine(cellsDetail[x][0].startX - (sizeOfCell / 30) + sizeOfCell, cellsDetail[x][0].startY - (sizeOfCell / 30), cellsDetail[x][0].startX - (sizeOfCell / 30) + sizeOfCell, cellsDetail[x][19].endY + (sizeOfCell / 30), black);
    }

    private void displayGridNumber(Canvas canvas) {
        showLog("Entering displayGridNumber");
        for (int x = 1; x <= Column; x++) {
            if (x > 9)
                canvas.drawText(Integer.toString(x-1), cellsDetail[x][20].startX + (sizeOfCell / 5), cellsDetail[x][20].startY + (sizeOfCell / 3), black);
            else
                canvas.drawText(Integer.toString(x-1), cellsDetail[x][20].startX + (sizeOfCell / 3), cellsDetail[x][20].startY + (sizeOfCell / 3), black);
        }
        for (int y = 0; y < Row; y++) {
            if ((20 - y) > 10)
                canvas.drawText(Integer.toString(19 - y), cellsDetail[0][y].startX + (sizeOfCell / 3), cellsDetail[0][y].startY + (sizeOfCell / 1.5f), black);
            else
                canvas.drawText(Integer.toString(19 - y), cellsDetail[0][y].startX + (sizeOfCell / 1.5f), cellsDetail[0][y].startY + (sizeOfCell / 1.5f), black);
        }
        showLog("Exiting displayGridNumber");
    }

    private void displayRobotOnGrid(Canvas canvas, int[] curCoord) {
        showLog("Entering displayRobotOnGrid");
        int rowCoordinates = this.convertRow(curCoord[1]);
        /*
        for (int y = rowCoordinates; y <= rowCoordinates + 1; y++)
            canvas.drawLine(cellsDetail[curCoord[0] - 1][y].startX, cellsDetail[curCoord[0] - 1][y].startY - (sizeOfCell / 30), cellsDetail[curCoord[0] + 1][y].endX, cellsDetail[curCoord[0] + 1][y].startY - (sizeOfCell / 30), robot);
        */
        int y = rowCoordinates;
        while(y<=rowCoordinates+1)
        {
            canvas.drawLine(cellsDetail[curCoord[0] - 1][y].startX, cellsDetail[curCoord[0] - 1][y].startY - (sizeOfCell / 30), cellsDetail[curCoord[0] + 1][y].endX, cellsDetail[curCoord[0] + 1][y].startY - (sizeOfCell / 30), robot);
            y++;
        }

        /*
        for (int x = curCoord[0] - 1; x < curCoord[0] + 1; x++)
            canvas.drawLine(cellsDetail[x][rowCoordinates - 1].startX - (sizeOfCell / 30) + sizeOfCell, cellsDetail[x][rowCoordinates - 1].startY, cellsDetail[x][rowCoordinates + 1].startX - (sizeOfCell / 30) + sizeOfCell, cellsDetail[x][rowCoordinates + 1].endY, robot);
        */

        int x = curCoord[0] - 1;

        while (x < curCoord[0] + 1)
        {
            canvas.drawLine(cellsDetail[x][rowCoordinates - 1].startX - (sizeOfCell / 30) + sizeOfCell, cellsDetail[x][rowCoordinates - 1].startY, cellsDetail[x][rowCoordinates + 1].startX - (sizeOfCell / 30) + sizeOfCell, cellsDetail[x][rowCoordinates + 1].endY, robot);
            x++;
        }

        switch (this.getRobotDirection()) {
            case "up":
                canvas.drawLine(cellsDetail[curCoord[0] - 1][rowCoordinates + 1].startX, cellsDetail[curCoord[0] - 1][rowCoordinates + 1].endY, (cellsDetail[curCoord[0]][rowCoordinates - 1].startX + cellsDetail[curCoord[0]][rowCoordinates - 1].endX) / 2, cellsDetail[curCoord[0]][rowCoordinates - 1].startY, black);
                canvas.drawLine((cellsDetail[curCoord[0]][rowCoordinates - 1].startX + cellsDetail[curCoord[0]][rowCoordinates - 1].endX) / 2, cellsDetail[curCoord[0]][rowCoordinates - 1].startY, cellsDetail[curCoord[0] + 1][rowCoordinates + 1].endX, cellsDetail[curCoord[0] + 1][rowCoordinates + 1].endY, black);
                break;
            case "down":
                canvas.drawLine(cellsDetail[curCoord[0] - 1][rowCoordinates - 1].startX, cellsDetail[curCoord[0] - 1][rowCoordinates - 1].startY, (cellsDetail[curCoord[0]][rowCoordinates + 1].startX + cellsDetail[curCoord[0]][rowCoordinates + 1].endX) / 2, cellsDetail[curCoord[0]][rowCoordinates + 1].endY, black);
                canvas.drawLine((cellsDetail[curCoord[0]][rowCoordinates + 1].startX + cellsDetail[curCoord[0]][rowCoordinates + 1].endX) / 2, cellsDetail[curCoord[0]][rowCoordinates + 1].endY, cellsDetail[curCoord[0] + 1][rowCoordinates - 1].endX, cellsDetail[curCoord[0] + 1][rowCoordinates - 1].startY, black);
                break;
            case "right":
                canvas.drawLine(cellsDetail[curCoord[0] - 1][rowCoordinates - 1].startX, cellsDetail[curCoord[0] - 1][rowCoordinates - 1].startY, cellsDetail[curCoord[0] + 1][rowCoordinates].endX, cellsDetail[curCoord[0] + 1][rowCoordinates - 1].endY + (cellsDetail[curCoord[0] + 1][rowCoordinates].endY - cellsDetail[curCoord[0] + 1][rowCoordinates - 1].endY) / 2, black);
                canvas.drawLine(cellsDetail[curCoord[0] + 1][rowCoordinates].endX, cellsDetail[curCoord[0] + 1][rowCoordinates - 1].endY + (cellsDetail[curCoord[0] + 1][rowCoordinates].endY - cellsDetail[curCoord[0] + 1][rowCoordinates - 1].endY) / 2, cellsDetail[curCoord[0] - 1][rowCoordinates + 1].startX, cellsDetail[curCoord[0] - 1][rowCoordinates + 1].endY, black);
                break;
            case "left":
                canvas.drawLine(cellsDetail[curCoord[0] + 1][rowCoordinates - 1].endX, cellsDetail[curCoord[0] + 1][rowCoordinates - 1].startY, cellsDetail[curCoord[0] - 1][rowCoordinates].startX, cellsDetail[curCoord[0] - 1][rowCoordinates - 1].endY + (cellsDetail[curCoord[0] - 1][rowCoordinates].endY - cellsDetail[curCoord[0] - 1][rowCoordinates - 1].endY) / 2, black);
                canvas.drawLine(cellsDetail[curCoord[0] - 1][rowCoordinates].startX, cellsDetail[curCoord[0] - 1][rowCoordinates - 1].endY + (cellsDetail[curCoord[0] - 1][rowCoordinates].endY - cellsDetail[curCoord[0] - 1][rowCoordinates - 1].endY) / 2, cellsDetail[curCoord[0] + 1][rowCoordinates + 1].endX, cellsDetail[curCoord[0] + 1][rowCoordinates + 1].endY, black);
                break;
            default:
                Toast.makeText(this.getContext(), "Error with drawing robot (unknown direction)", Toast.LENGTH_LONG).show();
                break;
        }
        showLog("Exiting displayRobotOnGrid");
    }


    public String getRobotDirection() {
        return robotDirection;
    }

    public void setAutomatedUpdate(boolean autoUpdate) throws JSONException {
        showLog(String.valueOf(bpMapInfo));
        if (!autoUpdate)
            bpMapInfo = this.getJsonObjectReceived();
        else {
            setReceivedJsonObject(bpMapInfo);
            bpMapInfo = null;
            this.updateMap();
        }
        GridMapView.isAutoUpdateSet = autoUpdate;
    }

    public JSONObject getJsonObjectReceived() {
        return receivedJsonObject;
    }

    public void setReceivedJsonObject(JSONObject receivedJsonObject) {
        showLog("Entered setReceivedJsonObject");
        GridMapView.receivedJsonObject = receivedJsonObject;
        bpMapInfo = receivedJsonObject;
    }

    public boolean getAutomatedUpdate() {
        return isAutoUpdateSet;
    }

    private void setValidPosition(boolean status) {
        isPositionValid = status;
    }

    public boolean getValidPosition() {
        return isPositionValid;
    }

    public void setUnSetCellStatus(boolean status) {
        isCellUnSet = status;
    }

    public boolean getUnSetCellStatus() {
        return isCellUnSet;
    }

    public void setSetObstacleStatus(boolean status) {
        isObstacleSet = status;
    }

    public boolean getSetObstacleStatus() {
        return isObstacleSet;
    }

    public void setStartCoordStatus(boolean status) {
        isStartCoordinatesSet = status;
    }

    private boolean getStartCoordStatus() {
        return isStartCoordinatesSet;
    }

    public void setWayPointStatus(boolean status) {
        isWayPointSet = status;
    }

    public boolean getCanDrawRobot() {
        return isRobotDrawable;
    }

    private void createGridCell() {
        showLog("Entering cellCreate");
        cellsDetail = new Cell[Column + 1][Row + 1];
        this.calculateDimension();
        sizeOfCell = this.getCellSize();

        for (int x = 0; x <= Column; x++)
            for (int y = 0; y <= Row; y++)
                cellsDetail[x][y] = new Cell(x * sizeOfCell + (sizeOfCell / 30), y * sizeOfCell + (sizeOfCell / 30), (x + 1) * sizeOfCell, (y + 1) * sizeOfCell, unExplored, "unexplored");
        showLog("Exiting createGridCell");
    }

    public void setEndCoordinates(int col, int row) {
        showLog("Entering setEndCoordinates");
        row = this.convertRow(row);
        for (int x = col - 1; x <= col + 1; x++)
            for (int y = row - 1; y <= row + 1; y++)
                cellsDetail[x][y].setType("end");
        showLog("Exiting setEndCoordinates");
    }

    public void setStartCoordinates(int col, int row) {
        showLog("Entering setStartCoordinates");
        startPoint[0] = col;
        startPoint[1] = row;
        String direction = getRobotDirection();
        if (direction.equals("None")) {
            direction = "up";
        }
        if (this.getStartCoordStatus())
            this.setCurrentCoordinates(col, row, direction);
        showLog("Exiting setStartCoordinates");
    }

    private int[] getStartCoordinates() {
        return startPoint;
    }

    public void setCurrentCoordinates(int col, int row, String direction) {
        showLog("Entering setCurrentCoordinates");
        currentPoint[0] = col;
        currentPoint[1] = row;
        this.setDirectionOfRobot(direction);
        this.updateAxisOfRobot(col, row, direction);

        row = this.convertRow(row);
        for (int x = col - 1; x <= col + 1; x++)
            for (int y = row - 1; y <= row + 1; y++)
                cellsDetail[x][y].setType("robot");
        showLog("Exiting setCurrentCoordinates");
    }

    public int[] getCurrentCoordinates() {
        return currentPoint;
    }

    private void calculateDimension() {
        float width = getWidth();
        this.setCellSize(width / (Column +1));
    }

    private int convertRow(int row) {
        return (20 - row);
    }

    private void setCellSize(float cellSize) {
        GridMapView.sizeOfCell = cellSize;
    }

    private float getCellSize() {
        return sizeOfCell;
    }

    private void setPreviousRobotCoordinates(int oldCol, int oldRow) {
        showLog("Entering setPreviousRobotCoordinates");
        previousPoint[0] = oldCol;
        previousPoint[1] = oldRow;
        oldRow = this.convertRow(oldRow);
        for (int x = oldCol - 1; x <= oldCol + 1; x++)
            for (int y = oldRow - 1; y <= oldRow + 1; y++)
                cellsDetail[x][y].setType("explored");
        showLog("Exiting setPreviousRobotCoordinates");
    }

    private int[] getPreviousPoint() {
        return previousPoint;
    }


    public void setDirectionOfRobot(String direction) {
        sharedPreferences = getContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        robotDirection = direction;
        editor.putString("direction", direction);
        editor.apply();
        this.invalidate();
    }

    private void updateAxisOfRobot(int col, int row, String direction) {
        TextView xAxisTextView = ((Activity) this.getContext()).findViewById(R.id.xAxisTextView);
        TextView yAxisTextView = ((Activity) this.getContext()).findViewById(R.id.yAxisTextView);
        TextView directionAxisTextView = ((Activity) this.getContext()).findViewById(R.id.directionAxisTextView);

        xAxisTextView.setText(String.valueOf(col - 1));
        yAxisTextView.setText(String.valueOf(row - 1));
        directionAxisTextView.setText(direction);
    }

    private void setCoordinatesOfWaypoint(int col, int row) throws JSONException {
        showLog("Entering setCoordinatesOfWaypoint");
        wayCoordinates[0] = col;
        wayCoordinates[1] = row;

        row = this.convertRow(row);
        cellsDetail[col][row].setType("waypoint");
        // waypoint comm
        MainActivity.sendMessageToBlueTooth("waypoint", wayCoordinates[0] - 1, wayCoordinates[1] - 1);
        showLog("Exiting setCoordinatesOfWaypoint");
    }

    private int[] getCoordinatesOfWaypoint() {
        return wayCoordinates;
    }

    private void setCoordinatesOfObstacle(int col, int row) {
        showLog("Entering setCoordinatesOfObstacle");
        int[] coordinatesOfObstacle = new int[]{col, row};
        GridMapView.obstacleCoordinates.add(coordinatesOfObstacle);
        row = this.convertRow(row);
        cellsDetail[col][row].setType("obstacle");
        showLog("Exiting setCoordinatesOfObstacle");
    }

    private ArrayList<int[]> getCoordinatesOfObstacle() {
        return obstacleCoordinates;
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }


    private class Cell {
        float startX, startY, endX, endY;
        Paint paint;
        String type;
        int id = -1;

        private Cell(float startX, float startY, float endX, float endY, Paint paint, String type) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.paint = paint;
            this.type = type;
        }

        public void setType(String type) {
            this.type = type;
            switch (type) {
                case "obstacle":
                    this.paint = obstacle;
                    break;
                case "robot":
                    this.paint = robot;
                    break;
                case "end":
                    this.paint = end;
                    break;
                case "startConnectionService":
                    this.paint = start;
                    break;
                case "waypoint":
                    this.paint = waypoint;
                    break;
                case "unexplored":
                    this.paint = unExplored;
                    break;
                case "explored":
                    this.paint = explored;
                    break;
                case "image":
                    this.paint = obstacle;
                default:
                    break;
            }
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        showLog("Entering onTouchEvent");
        if (event.getAction() == MotionEvent.ACTION_DOWN && !this.getAutomatedUpdate()) {
            int column = (int) (event.getX() / sizeOfCell);
            //MainActivity.sendMessageToBlueTooth("Y"+Integer.toString(column));
            int row = this.convertRow((int) (event.getY() / sizeOfCell));
            //MainActivity.sendMessageToBlueTooth("X"+Integer.toString(row));
            ToggleButton startbtn = ((Activity) this.getContext()).findViewById(R.id.startpointbtn);
            ToggleButton waypointbtn = ((Activity) this.getContext()).findViewById(R.id.waypointbtn);

            if (isStartCoordinatesSet) {
                if (isRobotDrawable) {
                    int[] startCoord = this.getStartCoordinates();
                    if (startCoord[0] >= 2 && startCoord[1] >= 2) {
                        startCoord[1] = this.convertRow(startCoord[1]);
                        for (int x = startCoord[0] - 1; x <= startCoord[0] + 1; x++)
                            for (int y = startCoord[1] - 1; y <= startCoord[1] + 1; y++)
                                cellsDetail[x][y].setType("unexplored");
                    }
                } else
                    isRobotDrawable = true;
                this.setStartCoordinates(column, row);
                isStartCoordinatesSet = false;
                String direction = getRobotDirection();
                if (direction.equals("None")) {
                    direction = "up";
                }
                try {
                    int directionInt = 0;
                    switch (direction) {
                        case "left":
                            directionInt = 3;
                            break;
                        case "right":
                            directionInt = 1;
                            break;
                        case "down":
                            directionInt = 2;
                            break;
                    }
                    MainActivity.sendMessageToBlueTooth("starting " + "(" + (column - 1) + "," + (row - 1) + "," + directionInt + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateAxisOfRobot(column, row, direction);
                if (startbtn.isChecked())
                    startbtn.toggle();
                this.invalidate();
                return true;
            }
            if (isObstacleDirectionCoordinatesSet) {
                this.setCoordinatesOfObstacle(column, row);
                setObstacleDirectionCoordinate(column,row,obstacleDirection);
                this.isObstacleDirectionCoordinatesSet = false;
                isAddObstacle = true;
                this.invalidate();
                return true;
            }
            if (isWayPointSet) {
                int[] waypointCoord = this.getCoordinatesOfWaypoint();
                if (waypointCoord[0] >= 1 && waypointCoord[1] >= 1)
                    cellsDetail[waypointCoord[0]][this.convertRow(waypointCoord[1])].setType("unexplored");
                isWayPointSet = false;
                try {
                    this.setCoordinatesOfWaypoint(column, row);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (waypointbtn.isChecked())
                    waypointbtn.toggle();
                this.invalidate();
                return true;
            }
            if (isObstacleSet) {
                //this.setCoordinatesOfObstacle(column, row);
                this.isObstacleDirectionCoordinatesSet = false;
                this.invalidate();
                return true;
            }
            if (isExploring) {
                cellsDetail[column][20 - row].setType("explored");
                this.invalidate();
                return true;
            }
            if (isCellUnSet) {
                ArrayList<int[]> obstacleCoord = this.getCoordinatesOfObstacle();
                cellsDetail[column][20 - row].setType("unexplored");
                for (int i = 0; i < obstacleCoord.size(); i++)
                    if (obstacleCoord.get(i)[0] == column && obstacleCoord.get(i)[1] == row)
                    {
                        obstacleCoord.remove(i);
                        directionOfObstacleCoordinates.remove(i);
                    }

                this.invalidate();
                return true;
            }

        }
        showLog("Exiting onTouchEvent");
        return false;
    }

    public void toggleCheckedBtn(String buttonName) {
        ToggleButton startButton = ((Activity) this.getContext()).findViewById(R.id.startpointbtn);
        ToggleButton waypointButton = ((Activity) this.getContext()).findViewById(R.id.waypointbtn);
        ToggleButton addObstacleButton = ((Activity) this.getContext()).findViewById(R.id.addobstaclebtn);

        ImageButton clearButton = ((Activity) this.getContext()).findViewById(R.id.clearbtn);

        if (!buttonName.equals("startButton"))
            if (startButton.isChecked()) {
                this.setStartCoordStatus(false);
                startButton.toggle();
            }
        if (!buttonName.equals("wayPointButton"))
            if (waypointButton.isChecked()) {
                this.setWayPointStatus(false);
                waypointButton.toggle();
            }

        if (!buttonName.equals("addObstacleButton"))
        {
            if (addObstacleButton.isChecked()) {
                this.setSetObstacleStatus(false);
                addObstacleButton.toggle();
            }
        }

        if (!buttonName.equals("clearButton"))
            if (clearButton.isEnabled())
                this.setUnSetCellStatus(false);
    }


    public void resetMap() {
        showLog("Entering resetMap");
        TextView robotStatusTextView = ((Activity) this.getContext()).findViewById(R.id.robotStatusTextView);
        Switch manualAutoToggleBtn = ((Activity) this.getContext()).findViewById(R.id.manualAutoBtn);
        Switch phoneTiltSwitch = ((Activity) this.getContext()).findViewById(R.id.phoneTiltSwitch);

        updateAxisOfRobot(1, 1, "None");
        robotStatusTextView.setText(getResources().getString(R.string.unavail));
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (manualAutoToggleBtn.isChecked()) {
            manualAutoToggleBtn.toggle();
            manualAutoToggleBtn.setText(getResources().getString(R.string.manual));

        }
        this.toggleCheckedBtn("None");

        if (phoneTiltSwitch.isChecked()) {
            phoneTiltSwitch.toggle();
            phoneTiltSwitch.setText(getResources().getString(R.string.tilt_off));
        }

        receivedJsonObject = null;
        bpMapInfo = null;
        startPoint = new int[]{-1, -1};
        currentPoint = new int[]{-1, -1};
        previousPoint = new int[]{-1, -1};
        robotDirection = "None";
        isAutoUpdateSet = false;
        obstacleCoordinates = new ArrayList<>();
        wayCoordinates = new int[]{-1, -1};
        isMapDrawn = false;
        isRobotDrawable = false;
        isPositionValid = false;
        directionOfObstacleCoordinates = new ArrayList<>();
        Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_error);

        showLog("Exiting resetMap");
        this.invalidate();
    }

    public void updateMap() throws JSONException {
        showLog("Entering updateMap");
        JSONObject mapInformation = this.getJsonObjectReceived();
        showLog("updateMap --- mapInformation: " + mapInformation);
        JSONArray infoJsonArray;
        JSONObject infoJsonObject;
        String hex_Explored;
        String explore_string;
        BigInteger hexBigIntegerExplored, hexBigIntegerObstacle;
        String hexStringExplored, hexStringObstacle, exploredString, obstacleString;

        String message;

        if (mapInformation == null)
            return;

        for (int i = 0; i < mapInformation.names().length(); i++) {
            message = "updateMapInformation Default message";
            switch (mapInformation.names().getString(i)) {
                case "map":
                    infoJsonArray = mapInformation.getJSONArray("map");
                    infoJsonObject = infoJsonArray.getJSONObject(0);

                    hexStringExplored = infoJsonObject.getString("explored");
                    hexBigIntegerExplored = new BigInteger(hexStringExplored, 16);
                    exploredString = hexBigIntegerExplored.toString(2);
                    showLog("updateMapInformation.exploredString: " + exploredString);

                    int x, y;
                    for (int j = 0; j < exploredString.length() - 4; j++) {
                        y = 19 - (j / 19);
                        x = 1 + j - ((19 - y) * 19);
                        if ((String.valueOf(exploredString.charAt(j + 2))).equals("1") && !cellsDetail[x][y].type.equals("robot"))
                            cellsDetail[x][y].setType("explored");
                        else if ((String.valueOf(exploredString.charAt(j + 2))).equals("0") && !cellsDetail[x][y].type.equals("robot"))
                            cellsDetail[x][y].setType("unexplored");
                    }

                    int length = infoJsonObject.getInt("length");

                    hexStringObstacle = infoJsonObject.getString("obstacle");
                    showLog("updateMapInformation hexStringObstacle: " + hexStringObstacle);
                    hexBigIntegerObstacle = new BigInteger(hexStringObstacle, 16);
                    showLog("updateMapInformation hexBigIntegerObstacle: " + hexBigIntegerObstacle);
                    obstacleString = hexBigIntegerObstacle.toString(2);
                    while (obstacleString.length() < length) {
                        obstacleString = "0" + obstacleString;
                    }
                    showLog("updateMapInformation obstacleString: " + obstacleString);
                    setMDF_Exploration_Details(hexStringExplored);
                    setMDF_Obstacle_Details(hexStringObstacle);

                    int k = 0;
                    for (int row = Row - 1; row >= 0; row--)
                        for (int col = 1; col <= Column; col++)
                            if ((cellsDetail[col][row].type.equals("explored") || (cellsDetail[col][row].type.equals("robot"))) && k < obstacleString.length()) {
                                if ((String.valueOf(obstacleString.charAt(k))).equals("1"))
                                    this.setCoordinatesOfObstacle(col, 20 - row);
                                k++;
                            }

                    int[] waypointCoord = this.getCoordinatesOfWaypoint();
                    if (waypointCoord[0] >= 1 && waypointCoord[1] >= 1)
                        cellsDetail[waypointCoord[0]][20 - waypointCoord[1]].setType("waypoint");
                    break;
                case "robotPosition":
                    if (isRobotDrawable)
                        this.setPreviousRobotCoordinates(currentPoint[0], currentPoint[1]);
                    infoJsonArray = mapInformation.getJSONArray("robotPosition");
//                    infoJsonObject = infoJsonArray.getJSONObject(0);

                    for (int row = Row - 1; row >= 0; row--)
                        for (int col = 1; col <= Column; col++)
                            cellsDetail[col][row].setType("unexplored");

                    String direction;
                    if (infoJsonArray.getInt(2) == 90) {
                        direction = "right";
                    } else if (infoJsonArray.getInt(2) == 180) {
                        direction = "down";
                    } else if (infoJsonArray.getInt(2) == 270) {
                        direction = "left";
                    } else {
                        direction = "up";
                    }
                    this.setStartCoordinates(infoJsonArray.getInt(0), infoJsonArray.getInt(1));
                    this.setCurrentCoordinates(infoJsonArray.getInt(0) + 2, convertRow(infoJsonArray.getInt(1)) - 1, direction);
                    isRobotDrawable = true;
                    break;
                case "waypoint":
                    infoJsonArray = mapInformation.getJSONArray("waypoint");
                    infoJsonObject = infoJsonArray.getJSONObject(0);
                    this.setCoordinatesOfWaypoint(infoJsonObject.getInt("x"), infoJsonObject.getInt("y"));
                    isWayPointSet = true;
                    break;
                case "obstacle":
                    infoJsonArray = mapInformation.getJSONArray("obstacle");
                    for (int j = 0; j < infoJsonArray.length(); j++) {
                        infoJsonObject = infoJsonArray.getJSONObject(j);
                        this.setCoordinatesOfObstacle(infoJsonObject.getInt("x"), infoJsonObject.getInt("y"));
                    }
                    message = "No. of Obstacle: " + String.valueOf(infoJsonArray.length());
                    break;

                case "move":
                    infoJsonArray = mapInformation.getJSONArray("move");
                    infoJsonObject = infoJsonArray.getJSONObject(0);
                    if (isRobotDrawable)
                        moveRobot(infoJsonObject.getString("direction"));
                    message = "moveDirection: " + infoJsonObject.getString("direction");
                    break;
                case "status":
                    String msg = mapInformation.getString("status");
                    printRobotStatus(msg);
                    message = "status: " + msg;
                    break;
                default:
                    message = "Unintended default for JSONObject";
                    break;
            }
            if (!message.equals("updateMapInformation Default message"))
                MainActivity.receiveMessage(message);
        }
        showLog("Exiting updateMapInformation");
        this.invalidate();
    }

    public void moveRobot(String direction) {
        showLog("Entering moveRobot");
        setValidPosition(false);
        int[] curCoord = this.getCurrentCoordinates();
        ArrayList<int[]> obstacleCoord = this.getCoordinatesOfObstacle();
        this.setPreviousRobotCoordinates(curCoord[0], curCoord[1]);
        int[] oldCoord = this.getPreviousPoint();
        String robotDirection = getRobotDirection();
        String backupDirection = robotDirection;

        switch (robotDirection) {
            case "up":
                switch (direction) {
                    case "forward":
                        if (curCoord[1] != 19) {
                            curCoord[1] += 1;
                            isPositionValid = true;
                        }
                        break;
                    case "right":
                        robotDirection = "right";
                        break;
                    case "back":
                        if (curCoord[1] != 2) {
                            curCoord[1] -= 1;
                            isPositionValid = true;
                        }
                        break;
                    case "left":
                        robotDirection = "left";
                        break;
                    default:
                        robotDirection = "error up";
                        break;
                }
                break;
            case "right":
                switch (direction) {
                    case "forward":
                        if (curCoord[0] != 19) {
                            curCoord[0] += 1;
                            isPositionValid = true;
                        }
                        break;
                    case "right":
                        robotDirection = "down";
                        break;
                    case "back":
                        if (curCoord[0] != 2) {
                            curCoord[0] -= 1;
                            isPositionValid = true;
                        }
                        break;
                    case "left":
                        robotDirection = "up";
                        break;
                    default:
                        robotDirection = "error right";
                }
                break;
            case "down":
                switch (direction) {
                    case "forward":
                        if (curCoord[1] != 2) {
                            curCoord[1] -= 1;
                            isPositionValid = true;
                        }
                        break;
                    case "right":
                        robotDirection = "left";
                        break;
                    case "back":
                        if (curCoord[1] != 19) {
                            curCoord[1] += 1;
                            isPositionValid = true;
                        }
                        break;
                    case "left":
                        robotDirection = "right";
                        break;
                    default:
                        robotDirection = "error down";
                }
                break;
            case "left":
                switch (direction) {
                    case "forward":
                        if (curCoord[0] != 2) {
                            curCoord[0] -= 1;
                            isPositionValid = true;
                        }
                        break;
                    case "right":
                        robotDirection = "up";
                        break;
                    case "back":
                        if (curCoord[0] != 19) {
                            curCoord[0] += 1;
                            isPositionValid = true;
                        }
                        break;
                    case "left":
                        robotDirection = "down";
                        break;
                    default:
                        robotDirection = "error left";
                }
                break;
            default:
                robotDirection = "error moveCurCoord";
                break;
        }
        if (getValidPosition())
            for (int x = curCoord[0] - 1; x <= curCoord[0] + 1; x++) {
                for (int y = curCoord[1] - 1; y <= curCoord[1] + 1; y++) {
                    for (int i = 0; i < obstacleCoord.size(); i++) {
                        if (obstacleCoord.get(i)[0] != x || obstacleCoord.get(i)[1] != y)
                            setValidPosition(true);
                        else {
                            setValidPosition(false);
                            break;
                        }
                    }
                    if (!getValidPosition())
                        break;
                }
                if (!getValidPosition())
                    break;
            }
        if (getValidPosition())
            this.setCurrentCoordinates(curCoord[0], curCoord[1], robotDirection);
        else {
            if (direction.equals("forward") || direction.equals("back"))
                robotDirection = backupDirection;
            this.setCurrentCoordinates(oldCoord[0], oldCoord[1], robotDirection);
        }
        this.invalidate();
        showLog("Exiting moveRobot");
    }

    public void printRobotStatus(String message) {
        TextView robotStatusTextView = ((Activity) this.getContext()).findViewById(R.id.robotStatusTextView);
        robotStatusTextView.setText(message);
    }

    public static void setMDF_Exploration_Details(String msg) {
        MDF_Exploration_Details = msg;
    }

    public static void setMDF_Obstacle_Details(String msg) {
        MDF_Obstacle_Details = msg;
    }

    public static String getMDFstring() {
        return MDF_Exploration_Details;
    }

    public static String getMDF_Obstacle_Details() {
        return MDF_Obstacle_Details;
    }

    private ArrayList<String[]> getObstacleDirectionCoord(){
        return directionOfObstacleCoordinates;
    }
    private void setObstacleDirectionCoordinate(int col, int row,String obstacleDirection) {
        showLog("Entering setDirectionCoord");
        String[] directionCoord = new String[3];
        directionCoord[0] = String.valueOf(col);
        directionCoord[1] = String.valueOf(row);
        directionCoord[2] = obstacleDirection;
        this.getObstacleDirectionCoord().add(directionCoord);


        row = this.convertRow(row);
        cellsDetail[col][row].setType("obstacleDirection");
        showLog("Exiting setDirectionCoord");
    }

    private void drawObstacleWithDirection(Canvas canvas,ArrayList<String[]> obstacleDirectionCoord){
        showLog("Entering drawObstacleWithDirection");
        RectF rect;
        int arrayIndex=0;

        for(int i =0; i<obstacleDirectionCoord.size(); i++){
            arrayIndex=i;
            int col= Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
            int row= convertRow(Integer.parseInt(obstacleDirectionCoord.get(i)[1]));
            rect = new RectF(col * sizeOfCell, row * sizeOfCell, (col+1) * sizeOfCell, (row+1) * sizeOfCell);

            switch(obstacleDirectionCoord.get(i)[2]){
                case "0":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.north_obstacle);
                    break;
                case "1":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.east_obstacle);

                    break;
                case "2":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.south_obstacle);

                    break;
                case "3":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.west_obstacle);
                    break;
                default:
                    break;

            }

            canvas.drawBitmap(obstacleDirectionBitmap,null,rect,null);
            showLog("Exiting drawObstacleWithDirection");
        }
        if(!obstacleDirectionCoord.isEmpty() && isAddObstacle == true){
            MainActivity.sendMessageToBlueTooth("ALG|obstacle"+ "(" +(Integer.parseInt(obstacleDirectionCoord.get(arrayIndex)[0])-1) + "," +(Integer.parseInt(obstacleDirectionCoord.get(arrayIndex)[1]) - 1)   + "," + Integer.parseInt(obstacleDirectionCoord.get(arrayIndex)[2] )+ ")");}
        isAddObstacle = false;
    }
}
