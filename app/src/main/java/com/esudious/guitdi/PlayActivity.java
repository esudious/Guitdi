package com.esudious.guitdi;

import android.app.Activity;
import android.graphics.Point;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.graphics.Color;
import android.media.midi.MidiManager;

import java.io.IOException;
import java.util.ArrayList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

/**
 * Created by Jeremy on 11/8/2015.
 */
public class PlayActivity extends Activity implements SensorEventListener, Button.OnClickListener, View.OnTouchListener {

    MidiManager midiManager;
    private SensorManager mSensorManager;
    private Sensor tilt;
    int screenWidth, screenHeight;

    Button buttonR0C0, buttonR1C0, buttonR2C0, buttonR3C0, //all the buttons
            buttonR0C1, buttonR1C1, buttonR2C1, buttonR3C1,
            buttonR0C2, buttonR1C2, buttonR2C2, buttonR3C2,
            buttonR4C0, buttonR4C1, buttonR4C2;
    int[] noteColors = new int[75]; //range of notes
    int basePosition, portNumber, lowestNote;
    double sensorDelay = 100000; //sensor's accelerometer delay in microseconds 10,000 is 1/100 a second
    double oneSecond = 1000000;  //1 second in microseconds
    double sensorDelayInSeconds;
    private ArrayList<Button> buttonList = new ArrayList();

    double xAccel, yAccel, zAccel, sumOfAccel;
    double vector, speed;

    MidiInputPort inputPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight= size.y;


        buttonR0C0 = (Button) findViewById(R.id.Butn00);
        buttonR1C0 = (Button) findViewById(R.id.Butn10);
        buttonR2C0 = (Button) findViewById(R.id.Butn20);
        buttonR3C0 = (Button) findViewById(R.id.Butn30);
        buttonR4C0 = (Button) findViewById(R.id.Butn40);
        buttonR0C1 = (Button) findViewById(R.id.Butn01);
        buttonR1C1 = (Button) findViewById(R.id.Butn11);
        buttonR2C1 = (Button) findViewById(R.id.Butn21);
        buttonR3C1 = (Button) findViewById(R.id.Butn31);
        buttonR4C1 = (Button) findViewById(R.id.Butn41);
        buttonR0C2 = (Button) findViewById(R.id.Butn02);
        buttonR1C2 = (Button) findViewById(R.id.Butn12);
        buttonR2C2 = (Button) findViewById(R.id.Butn22);
        buttonR3C2 = (Button) findViewById(R.id.Butn32);
        buttonR4C2 = (Button) findViewById(R.id.Butn42);

        basePosition = 0;
        speed = 0;
        sumOfAccel = 0;
        lowestNote = 24;
        sensorDelayInSeconds = sensorDelay/oneSecond;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tilt = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //MIDI STUFF
        portNumber = getIntent().getIntExtra("portNumber", 0);
        midiManager  = (MidiManager) getSystemService(Context.MIDI_SERVICE);
        MidiDeviceInfo[] info = midiManager.getDevices();
        Handler handler = new Handler(Looper.getMainLooper());


        midiManager.openDevice(info[portNumber], new MidiManager.OnDeviceOpenedListener() {
                    @Override
                    public void onDeviceOpened(MidiDevice device) {
                        if (device == null) {
                            Log.e("Midi Error", "could not open device ");
                        } else {
                            inputPort = device.openInputPort(0);
                        }
                    }

                }, handler
        );

        MidiManager.DeviceCallback callback = new MidiManager.DeviceCallback();

        midiManager.registerDeviceCallback(callback, handler);

        fillButtonList();
        fillNoteColors();
        changeButtonSize();
        changeButtonColors(basePosition);
    }

    private void changeButtonSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;


        for(Button b:buttonList){
            b.getLayoutParams().width=(width/3);
            b.getLayoutParams().height=(height/5);
        }
    }

    public void sendNote(int note){
        byte[] buffer = new byte[32];
        int numBytes = 0;
        int channel = 3; // MIDI channels 1-16 are encoded as 0-15.
        buffer[numBytes++] = (byte)(0x90 + (channel - 1)); // note on 0x90
        buffer[numBytes++] = (byte)(note+basePosition); // 60 pitch is middle C
        buffer[numBytes++] = (byte)127; // max velocity = 127
        int offset = 0;
        try {
            inputPort.send(buffer, offset, numBytes);
        } catch (IOException ex){
            //do nothing
        }

    }

    //stops a pressed note thats playing
    private void stopNote(int note) {
        byte[] buffer = new byte[32];
        int numBytes = 0;
        int channel = 3; // MIDI channels 1-16 are encoded as 0-15.
        buffer[numBytes++] = (byte)(0x90 + (channel - 1)); // note on 0x90
        buffer[numBytes++] = (byte)(note+basePosition); // 60 pitch is middle C
        buffer[numBytes++] = (byte)0; // max velocity = 127, 0 stops note
        int offset = 0;
        try {
            inputPort.send(buffer, offset, numBytes);
        } catch (IOException ex){
            //do nothing
        }
    }

    private void stopAllNotes(){
        for(int i = 0; i<100;i++){
            byte[] buffer = new byte[32];
            int numBytes = 0;
            int channel = 3; // MIDI channels 1-16 are encoded as 0-15.
            buffer[numBytes++] = (byte)(0x90 + (channel - 1)); // note on 0x90
            buffer[numBytes++] = (byte)(i); // 60 pitch is middle C
            buffer[numBytes++] = (byte)0; // max velocity = 127, 0 stops note
            int offset = 0;
            try {
                inputPort.send(buffer, offset, numBytes);
            } catch (IOException ex){
                //do nothing
            }
        }
    }


    public void fillButtonList(){
        buttonList.add(buttonR0C0);
        buttonList.add(buttonR1C0);
        buttonList.add(buttonR2C0);
        buttonList.add(buttonR3C0);
        buttonList.add(buttonR4C0);
        buttonList.add(buttonR0C1);
        buttonList.add(buttonR1C1);
        buttonList.add(buttonR2C1);
        buttonList.add(buttonR3C1);
        buttonList.add(buttonR4C1);
        buttonList.add(buttonR0C2);
        buttonList.add(buttonR1C2);
        buttonList.add(buttonR2C2);
        buttonList.add(buttonR3C2);
        buttonList.add(buttonR4C2);

        for (Button button : buttonList){
            button.setId(buttonList.indexOf(button) + 1000);

            button.setOnTouchListener(this);
        }
    }

    private void fillNoteColors(){  //fill the NoteColors array with the whole rainbow
        int fractionOfColors = (256/(noteColors.length/4));
        int fifthOfNoteColors = noteColors.length/5;

        for(int i = 0; i<fifthOfNoteColors; i++){ //first 1/4 of colors  Red to Yellow
            noteColors[i]= Color.rgb(255, i*fractionOfColors, 0);
        }
        for(int i = 0; i<fifthOfNoteColors; i++){ //second fourth Yellow to Green
            noteColors[i+fifthOfNoteColors]= Color.rgb(255 - i*fractionOfColors, 255, 0);
        }
        for(int i = 0; i<fifthOfNoteColors; i++){ // Green to Teal
            noteColors[i+2*fifthOfNoteColors]= Color.rgb(0, 255, i*fractionOfColors);
        }
        for(int i = 0; i<fifthOfNoteColors; i++){ // Teal to Blue
            noteColors[i+3*fifthOfNoteColors]= Color.rgb(0, 255-i*fractionOfColors, 255);
        }
        for(int i = 0; i<fifthOfNoteColors; i++){ // Blue to Purple
            noteColors[i+4*fifthOfNoteColors]= Color.rgb(i*fractionOfColors, 0, 255);
        }
        /*for(int i = 0; i<fifthOfNoteColors; i++){ // Purple to Red
            noteColors[i+5*fifthOfNoteColors]= Color.rgb(255, 0, 255-i*fractionOfColors);
        }*/  //colors once looped back to red but removed

        /*for(int i = 0; i < noteColors.length; i++){  //LOG all the colors
            Log.e(noteColors[i] + "", noteColors[i]+" "+i);
        }*/
    }

    private void changeButtonColors(int baseNote){

        if (baseNote<0) baseNote = 0; //prevent going under errors
        if (baseNote>= noteColors.length-buttonList.size())
            baseNote = noteColors.length-buttonList.size(); //prevent going over errors
        int i = 0;
        for(Button button: buttonList){
            button.setBackgroundColor(noteColors[baseNote + i]);
            button.setText( getNoteLetter((baseNote + i) % 12));
            i++;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xAccel = event.values[0];
        yAccel = event.values[1];
        zAccel = event.values[2];
        vector = Math.sqrt(xAccel * xAccel + yAccel * yAccel + zAccel * zAccel);



        /*if (yAccel > 2.5 && yAccel <= 4   && basePosition > 0) changeButtonColors(--basePosition);
        if (yAccel < -2.5 && yAccel >= -4 && basePosition < noteColors.length-12) changeButtonColors(++basePosition);

        if (yAccel >  4 && basePosition > 0) {  //double speed
            changeButtonColors(--basePosition);
            if (basePosition> 0){
                changeButtonColors(--basePosition);
            }
        }
        if (yAccel < -4 && basePosition < noteColors.length-buttonList.size()){  //double speed
            changeButtonColors(++basePosition);
            if (basePosition< noteColors.length){
                changeButtonColors(++basePosition);
            }
        }*///test tilt to change notes
        

        double absY = Math.abs(yAccel);
        //center spot
        if (absY <= 3.0){
            basePosition = (noteColors.length*2)/5;
        }
        if (yAccel > 3 && yAccel <= 6){
            basePosition = (noteColors.length*3)/5;
        }
        if (yAccel > 6){
            basePosition = (noteColors.length*4)/5;
        }
        if (yAccel < -3 && yAccel >= -6){
            basePosition = (noteColors.length)  /5;
        }
        if (yAccel < -6){
            basePosition = 0;
        }
        changeButtonColors(basePosition);
        //trackPosition(xAccel, yAccel, zAccel, vector);

        //Log.d("Sensor Changed", String.format("x = %8.6f,  y = %8.6f,  z = %8.6f, v= %8.6f",
        //        xAccel, yAccel, zAccel, vector));
        //Log.d("basePosition", String.valueOf(basePosition));
    }

    // find how much the device has moved, how fast it is moving
    // unused
    /*
    private void trackPosition(double x, double y, double z, double v){
        sumOfAccel = sumOfAccel + x + y + z;
        speed = speed + (sensorDelayInSeconds*y);
        //Log.d("Equation", String.valueOf(y));
        Log.d("velocity", String.format("y = %8.6f, velocity= %8.6f, delay = %8.6f, sum = %8.6f"
                , y, speed, sensorDelayInSeconds, sumOfAccel));

    }*/

    //get the Letter note from the number
    private String getNoteLetter(int num){
        String letter;

        switch (num){
            case 0: letter = "C";
                break;
            case 1: letter = "C#";
                break;
            case 2: letter = "D";
                break;
            case 3: letter = "D#";
                break;
            case 4: letter = "E";
                break;
            case 5: letter = "F";
                break;
            case 6: letter = "F#";
                break;
            case 7: letter = "G";
                break;
            case 8: letter = "G#";
                break;
            case 9: letter = "A";
                break;
            case 10: letter = "A#";
                break;
            case 11: letter = "B";
                break;
            default: letter = "";
        }

        return letter;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, tilt,
                (int) sensorDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        stopAllNotes();

    }

    @Override
    public void onClick(View v) {
        int buttonNumber;
        buttonNumber = v.getId()-1000;
        sendNote(buttonNumber + 24); // if 60 is middle C then 24 is 3 octaves down
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int buttonNumber;
        buttonNumber = v.getId()-1000;
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            sendNote(buttonNumber+lowestNote);
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            stopNote(buttonNumber+lowestNote);
        }
        return false;
    }

}
