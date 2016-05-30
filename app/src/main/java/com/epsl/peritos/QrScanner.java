package com.epsl.peritos;

import android.app.Activity;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QrScanner extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        String resultado = rawResult.getText();
        String [] tratamiento;
        tratamiento = resultado.split("[-]");
        System.out.println(rawResult.getText());
        for (int i=0; i < tratamiento.length ; i++){
            System.out.println(tratamiento[i]);


        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }


}

