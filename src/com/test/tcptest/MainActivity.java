package com.test.tcptest;

import com.test.other.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	Button server,client;
	Boolean isServer;
	Intent intent;
	Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		server = (Button)findViewById(R.id.server);
		client = (Button)findViewById(R.id.client);
		LogUtils.setLogStatus(true);
		server.setOnClickListener(this);
		client.setOnClickListener(this);
		intent = new Intent(this, NetActivity.class);
		bundle = new Bundle();
	}



	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.server:
            	isServer = true;
        		bundle.putBoolean("isServer", isServer);
        		intent.putExtras(bundle);    
        		startActivity(intent);
                break;
            case R.id.client:
            	isServer = false;
        		bundle.putBoolean("isServer", isServer);
        		intent.putExtras(bundle);    
        		startActivity(intent);
                break;
        }
    }
	
}
