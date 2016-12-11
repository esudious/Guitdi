package com.esudious.guitdi;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.media.midi.MidiDevice;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.bluetooth.le.BluetoothLeScanner;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    int portNumber;
    MidiManager midiManager;
    BluetoothLeScanner bluetoothScanner;
    ScanCallback scanCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner)findViewById(R.id.spinner);
        portNumber = 0;


        midiManager = (MidiManager) getSystemService(MIDI_SERVICE);

        MidiDeviceInfo[] info = midiManager.getDevices();


        ArrayAdapter<MidiDeviceInfo> devicesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, info);
        devicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(devicesAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        portNumber = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public void startClicked(View view){
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("portNumber", portNumber);
        startActivity(intent);
    }

    public void bluetoothScanClicked(View view){
        //find bluetooth device TODO
        /*
        bluetoothScanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

            }
        });
        */
    }

}
