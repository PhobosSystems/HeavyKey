package com.phobos.system.heavykey;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

// saves the key to file to prep for beam or bluetooth transfer
public class SavePad extends AsyncTask<byte[], String, String> {

    public void setFileName(String fileName) {
        this.file = fileName;
    }

    private String file = "";

    @Override
    protected String doInBackground(byte[]... jpeg) {
        File pad =new File(Environment.getExternalStorageDirectory(), file);

        if (pad.exists()) {
            pad.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(pad.getPath());
            Log.e("SavePad", "sPad Success");
            fos.write(jpeg[0]);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("SavePad", "sPad Failure", e);
        }

        return(null);
    }

}