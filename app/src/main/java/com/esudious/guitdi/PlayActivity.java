package com.esudious.guitdi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.graphics.Color;
import android.media.midi.*;
import java.util.ArrayList;

/**
 * Created by R on 11/8/2015.
 */
public class PlayActivity extends Activity {

    Button buttonR0C0, buttonR1C0, buttonR2C0, buttonR3C0, //all the buttons
            buttonR0C1, buttonR1C1, buttonR2C1, buttonR3C1,
            buttonR0C2, buttonR1C2, buttonR2C2, buttonR3C2;
    int[] noteColors = new int[48];
    ArrayList<Button> buttonList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Button buttonR0C0 = (Button) findViewById(R.id.Butn00);
        Button buttonR1C0 = (Button) findViewById(R.id.Butn10);
        Button buttonR2C0 = (Button) findViewById(R.id.Butn20);
        Button buttonR3C0 = (Button) findViewById(R.id.Butn30);
        Button buttonR0C1 = (Button) findViewById(R.id.Butn01);
        Button buttonR1C1 = (Button) findViewById(R.id.Butn11);
        Button buttonR2C1 = (Button) findViewById(R.id.Butn21);
        Button buttonR3C1 = (Button) findViewById(R.id.Butn31);
        Button buttonR0C2 = (Button) findViewById(R.id.Butn02);
        Button buttonR1C2 = (Button) findViewById(R.id.Butn12);
        Button buttonR2C2 = (Button) findViewById(R.id.Butn22);
        Button buttonR3C2 = (Button) findViewById(R.id.Butn32);

        buttonList.add(buttonR0C0);
        buttonList.add(buttonR1C0);
        buttonList.add(buttonR2C0);
        buttonList.add(buttonR3C0);
        buttonList.add(buttonR0C1);
        buttonList.add(buttonR1C1);
        buttonList.add(buttonR2C1);
        buttonList.add(buttonR3C1);
        buttonList.add(buttonR0C2);
        buttonList.add(buttonR1C2);
        buttonList.add(buttonR2C2);
        buttonList.add(buttonR3C2);


        //fillButtonList();
        fillNoteColors();
        changeButtonColors(0);
    }

    public void fillButtonList(){
        buttonList.add(buttonR0C0);
        buttonList.add(buttonR1C0);
        buttonList.add(buttonR2C0);
        buttonList.add(buttonR3C0);
        buttonList.add(buttonR0C1);
        buttonList.add(buttonR1C1);
        buttonList.add(buttonR2C1);
        buttonList.add(buttonR3C1);
        buttonList.add(buttonR0C2);
        buttonList.add(buttonR1C2);
        buttonList.add(buttonR2C2);
        buttonList.add(buttonR3C2);
    }

    private void fillNoteColors(){
        /*
        for(int i = 0; i<noteColors.length/4; i++){ //first 1/4 of colors
            noteColors[i]= Color.rgb(255, i*21, 0);
        }
        for(int i = 1 + noteColors.length/4; i<noteColors.length/2; i++){ //second fourth
            noteColors[i]= Color.rgb(255-(i*21), 255, 0);
        }
        for(int i = 1 + noteColors.length/2; i<noteColors.length*(3/4); i++){
            noteColors[i]= Color.rgb(0, 255, i*21);
        }
        for(int i = 1 + noteColors.length*(3/4); i<noteColors.length; i++){
            noteColors[i]= Color.rgb(0, 255-(i*21), 255);
        }*/
        for(int i = 0; i<12; i++){ //first 1/4 of colors
            noteColors[i]= Color.rgb(255, i*21, 0);
        }
        for(int i = 12; i<noteColors.length/24; i++){ //second fourth
            noteColors[i]= Color.rgb(255-(i*21), 255, 0);
        }
        for(int i = 24; i<36; i++){
            noteColors[i]= Color.rgb(0, 255, i*21);
        }
        for(int i = 36; i<48; i++){
            noteColors[i]= Color.rgb(0, 255-(i*21), 255);
        }
        for(int i = 0; i < noteColors.length; i++){
            Log.e(noteColors[i] + "", noteColors[i]+"");
        }
    }

    private void changeButtonColors(int baseNote){
        int i = 0;
        for(Button button: buttonList){
            button.setBackgroundColor(noteColors[baseNote+i]);
            i++;
        }
    }
}
