package com.test.tcptest;


import java.io.IOException;

import com.test.other.Entity;
import com.test.other.LogUtils;
import com.test.other.SessionUtils;
import com.test.tcptest.UDPMsgThread.OnNewMsgListener;

import android.content.Context;
import android.os.Message;



/**
 * 网络层接口，还未抽象
 * @author GuoJun
 *
 */
public class WifiNet implements OnNewMsgListener{
    public enum SocketMode{TCP,UDP}
    private Context mContext;
    private static final String TAG = "WifiNet";
    private MSGListener clientListener;
    private MSGListener serverListener;
    private boolean isServer;
    private UDPMsgThread mUDPListener;
    private TcpClient mClient;
    private TcpServer mServer;
    
    private String serverIp = null;
    
    
    public WifiNet(Context context, OnNewMsgListener listener, MSGListener clientListener, MSGListener serverListener) {
    	mContext = context;
        mUDPListener = UDPMsgThread.getInstance(context);
        mUDPListener.addMsgListener(listener);
        mUDPListener.addMsgListener(this);
        this.clientListener = clientListener;
        this.serverListener = serverListener;
    }
    
    
    public boolean connectServer(){
		return mClient.connect(serverIp);
    }
    

	@Override
	public void processMessage(Message pMsg) {
		
	}
	
    public void createClient(){
    	mClient = TcpClient.getInstance(mContext);
    	mClient.start();
    }
    
    public void createServer() throws IOException{
    	mServer= TcpServer.getInstance(mContext);
    	mServer.start();
    }
    
    public boolean findServer(){
		mUDPListener.connectUDPSocket();
		boolean isFind = mUDPListener.notifiBroad();
    	serverIp = mUDPListener.getServerIp();
    	return isFind;
    }
    
    public TcpClient getClient(){
    	return mClient;
    }
    
    public TcpServer getServer(){
    	return mServer;
    }
    
    /**
     * 打包数据包
     * 
     * @param commandNo
     *            消息命令
     * @param addData
     *            附加数据
     * @see MSGConst
     */
    private MSGProtocol packageMsg(int commandNo, Object addData) {
        MSGProtocol ipmsgProtocol = null;
        String imei = SessionUtils.getIMEI();

        if (addData == null) {
            ipmsgProtocol = new MSGProtocol(imei, commandNo);
        }
        else if (addData instanceof Entity) {
            ipmsgProtocol = new MSGProtocol(imei, commandNo, (Entity) addData);
        }
        else if (addData instanceof String) {
            ipmsgProtocol = new MSGProtocol(imei, commandNo, (String) addData);
        }
        return ipmsgProtocol;
    }
    
    public void sendToAllClient(int commandNo, Object addData, SocketMode sm) {
    	MSGProtocol ipmsg = packageMsg(commandNo,addData);
    	if(ipmsg!=null){

        	try {
            	if(sm == SocketMode.TCP)
            		mServer.sendToAllClient(ipmsg);
            }
            catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "sendToClient() 发送数据包失败");
            }
            LogUtils.i(TAG, "sendToAllClient() 发送数据包成功");
	    }
    }
    
    /*
    public void sendToAllExClient(int commandNo, Object addData, int connectionID, SocketMode sm) {
    	MSGProtocol ipmsg = packageMsg(commandNo,addData);
    	if(ipmsg!=null){

        	try {
            	if(sm == SocketMode.TCP)
            		mServer.sendToAllExceptTCP(connectionID, object);
        		else
            		mServer.sendToAllExceptUDP(connectionID, object);
            }
            catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "sendToClient() 发送数据包失败");
            }
            LogUtils.i(TAG, "sendToAllExClient() 发送数据包成功");
	    }
    }
     */
    /**
     * 发送ipmsg数据到指定客户端
     * @param commandNo 
     * @param addData 
     * @param connectionID
     * @param sm
     */
    /*
    public void sendToClient(int commandNo, Object addData,int connectionID, SocketMode sm) {
    	MSGProtocol ipmsg = packageMsg(commandNo,addData);
    	if(ipmsg!=null){
        	try {
            	if(sm == SocketMode.TCP)
            		mServer.sendToTCP(connectionID,object);
        		else
            		mServer.sendToUDP(connectionID,object);
            }
            catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "sendToClient() 发送数据包失败");
            }
            LogUtils.i(TAG, "sendToServer() 发送数据包成功");
    	}
    }
    */
    
    
    /**
     * 发送ipmsg数据到服务器
     * @param commandNo
     * @param addData
     * @param sm
     */
    public void sendToServer(int commandNo, Object addData, SocketMode sm) {
    	MSGProtocol ipmsg = packageMsg(commandNo,addData);
    	if(ipmsg!=null){
    		try {
            	if(sm == SocketMode.TCP)
            		mClient.sendData(ipmsg);
        		else
        			mUDPListener.sendUDPdata(ipmsg, serverIp);
                LogUtils.i(TAG, "sendToServer() 发送数据包成功");
            }
            catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "sendToServer() 发送数据包失败");
            }
    	}
    }
    

  
 		
 	public void stopNet(){
    	if(isServer){
    		mServer.stop();
    	}
    	else{
    		mClient.stop();
    	}
    }


}