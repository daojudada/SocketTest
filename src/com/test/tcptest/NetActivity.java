package com.test.tcptest;

import com.test.other.SessionUtils;
import com.test.tcptest.WifiNet.SocketMode;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NetActivity extends Activity{

	Boolean isServer;
    private TelephonyManager mTelephonyManager;
    private UDPMsgThread mUDPListener;
    private WifiNet wifiNet;
    private TextView localIp,otherIp;
    private TextView recMsg;
    private Button sendMsg;
    int log = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net);

		localIp = (TextView)findViewById(R.id.local_ip);
		otherIp = (TextView)findViewById(R.id.other_ip);
		recMsg = (TextView)findViewById(R.id.rec_msg);
		sendMsg = (Button)findViewById(R.id.send_msg);
        sendMsg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(wifiNet.getIsServer()){
					wifiNet.sendToAllClient(MSGConst.SENDMSG, log + "" +System.currentTimeMillis(), SocketMode.TCP);
					log++;
				}
				else{
					wifiNet.sendToServer(MSGConst.SENDMSG, log + "" +System.currentTimeMillis(), SocketMode.TCP);
					log++;
				}
			}
        });
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mIMEI = mTelephonyManager.getDeviceId();
        WifiManager wifiManager = (WifiManager)this.getSystemService(android.content.Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        SessionUtils.setIMEI(mIMEI);
        SessionUtils.setLocalIPaddress((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."+ ((i >> 24) & 0xFF));
		isServer = this.getIntent().getExtras().getBoolean("isServer");
		
		localIp.setText(SessionUtils.getLocalIPaddress());
		
		wifiNet = new WifiNet(this);
		wifiNet.createUDP();
		wifiNet.addUdpListener(new MSGListener(){
			@Override
			public void processMessage(MSGProtocol pMsg) {
				int command = pMsg.getCommandNo();
		        android.os.Message msg = new android.os.Message();
		        Bundle b = new Bundle();
		        b.putString("Ip", pMsg.getAddStr());
		        msg.what = command;
		        msg.setData(b);
				handler.sendMessage(msg);
			}
			
		});
		if(wifiNet.findServer()){
			wifiNet.createClient();
			wifiNet.addClientListener(new MSGListener(){
				@Override
				public void processMessage(MSGProtocol pMsg) {
					int command = pMsg.getCommandNo();
			        android.os.Message msg = new android.os.Message();
			        msg.what = command;
					handler.sendMessage(msg);
				}
			});
			wifiNet.connectServer();
			wifiNet.startClient();
			wifiNet.sendToServer(MSGConst.SENDMSG, "first" + System.currentTimeMillis(), SocketMode.TCP);
		}
		else{
			wifiNet.createServer();
			wifiNet.startServer();
			wifiNet.addServerListener(new MSGListener(){
				@Override
				public void processMessage(MSGProtocol pMsg) {
					int command = pMsg.getCommandNo();
			        android.os.Message msg = new android.os.Message();
			        msg.what = command;
			        Bundle b = new Bundle();
			        b.putCharSequence("msg", pMsg.getAddStr());
			        msg.setData(b);
					handler.sendMessage(msg);
				}
			});
		}
		
	}
	
	/**
	 * 主线程处理
	 */
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
        	case MSGConst.BR_ENTRY: 
        		otherIp.setText(msg.getData().getString("Ip"));
        		break;
        	case MSGConst.REANSENTRY:
        		otherIp.setText(msg.getData().getString("Ip"));
        		break;
        	case MSGConst.SENDMSG:
        		String s = msg.getData().getString("msg");
        		recMsg.setText(s);
        		break;
    	 	default:
    	 		break;
            }
		}
	};


	 
	
	
    


}
