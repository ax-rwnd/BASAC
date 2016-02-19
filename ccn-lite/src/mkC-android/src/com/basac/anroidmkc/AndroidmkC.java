package com.basac.androidmkc;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;



public class AndroidmkC extends Activity
{
	static {
		System.loadLibrary("crypto");
		System.loadLibrary("ssl");
		System.loadLibrary("androidmkc");
	}

	private native int generateContent(String content, String ipath, String opath);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
		alertDB.setTitle("Hello World!");
		alertDB.create().show();
	new AndroidmkC().generateContent("/ndn/hello-world", "/mnt/sdcard/infile.txt", "/mnt/sdcard/test.ndntlv");
	
    }
}
