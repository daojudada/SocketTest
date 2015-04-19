package com.test.tcptest;

import javax.security.auth.callback.Callback;

import com.test.other.SessionUtils;
import com.test.tcptest.UDPMsgThread.OnNewMsgListener;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

public class NetActivity extends Activity implements OnNewMsgListener{

	Boolean isServer;
    private TelephonyManager mTelephonyManager;
    protected UDPMsgThread mUDPListener;
    
    private TextView localIp,otherIp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net);

		localIp = (TextView)findViewById(R.id.local_ip);
		otherIp = (TextView)findViewById(R.id.other_ip);
		
		
        
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mIMEI = mTelephonyManager.getDeviceId();
        WifiManager wifiManager = (WifiManager)this.getSystemService(android.content.Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        SessionUtils.setIMEI(mIMEI);
        SessionUtils.setLocalIPaddress((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."+ ((i >> 24) & 0xFF));
		isServer = this.getIntent().getExtras().getBoolean("isServer");
		
		localIp.setText(SessionUtils.getLocalIPaddress());
		if(isServer){
			
		}
		else{
		}
		
		
		
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
        	case MSGConst.BR_ENTRY: // 用户上线
        		String ServerIp = mUDPListener.getServerIp();
        		otherIp.setText(ServerIp);
        		break;
    	 	default:
    	 		break;
            }
		}
	};


	 
	
	
    
	@Override
	public void processMessage(Message pMsg) {
		handler.sendMessage(pMsg);
	}


}
