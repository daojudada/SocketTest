package com.test.tcptest;


import com.test.other.Entity;
import com.test.other.LogUtils;
import com.test.other.SessionUtils;

import android.content.Context;



/**
 * 网络层接口，还未抽象
 * @author GuoJun
 *
 */
public class WifiNet implements NetInterface{
    public enum SocketMode{TCP,UDP}
    private Context mContext;
    private static final String TAG = "WifiNet";
    private boolean isServer;
    private UDPMsgThread mUDPListener;
    private TcpClient mClient;
    private TcpServer mServer;
    
    private String serverIp = null;
    
    
    public WifiNet(Context context) {
    	mContext = context;
    }
    

    @Override
    public void addClientListener(MSGListener listener){
    	mClient.addMsgListener(listener);
    }
    

    @Override
    public void addServerListener(MSGListener listener){
    	mServer.addMsgListener(listener);
    }
    
    public void addUdpListener(MSGListener listener){
    	mUDPListener.addMsgListener(listener);
    }
    
    

    @Override
    public void removeClientListener(MSGListener listener){
    	mClient.addMsgListener(listener);
    }
    

    @Override
    public void removeServerListener(MSGListener listener){
    	mServer.addMsgListener(listener);
    }
    
    public void removeUdpListener(MSGListener listener){
    	mUDPListener.addMsgListener(listener);
    }
    
	public void createUDP(){
        mUDPListener = UDPMsgThread.getInstance(mContext);
		mUDPListener.connectUDPSocket();
		mUDPListener.start();
	}

    @Override
    public void createClient(){
    	isServer = false;
    	mClient = TcpClient.getInstance(mContext);
    }

    @Override
    public boolean connectServer(){
		return mClient.connect(serverIp);
    }
    
    
    @Override
    public void startServer(){
    	mServer.start();
    }
    
    @Override
    public void startClient(){
    	mClient.start();
    }
    
    @Override
    public void createServer(){
    	isServer = true;
    	mServer= TcpServer.getInstance(mContext);
    }

    @Override
    public boolean findServer(){
    	if(!isServer){
    		mUDPListener.notifiBroad();
    		long beginTime = System.currentTimeMillis();
    		while(true){
    			long diffTime = System.currentTimeMillis() - beginTime;
        		serverIp = mUDPListener.getServerIp();
    			if(diffTime > 3000 && serverIp == null)
    				return false;
    			if(serverIp != null){
    				LogUtils.i(TAG, "找到服务器"+serverIp);
    				return true;
    			}
    		}
    	}
    	else {
    		LogUtils.i(TAG, "没有找到服务器");
    		return false;
    	}
    }

    @Override
    public TcpClient getClient(){
    	return mClient;
    }

    @Override
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

    @Override
    public void sendToAllClient(int commandNo, Object addData, SocketMode sm) {
    	if(isServer){
	    	MSGProtocol ipmsg = packageMsg(commandNo,addData);
	    	if(ipmsg!=null){
            	if(sm == SocketMode.TCP)
            		mServer.sendToAllClient(ipmsg);
		    }
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
    
    /*
     * 
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
    
    

    @Override
    public void sendToServer(int commandNo, Object addData, SocketMode sm) {
    	if(!isServer){
	    	MSGProtocol ipmsg = packageMsg(commandNo,addData);
	    	if(ipmsg!=null){
            	if(sm == SocketMode.TCP)
            		mClient.sendData(ipmsg);
        		else
        			mUDPListener.sendUDPdata(ipmsg, serverIp);
	    	}
    	}
    }
    

  

    @Override
 	public void stopNet(){
 		mUDPListener.stop();
    	if(isServer){
    		mServer.stop();
    	}
    	else{
    		mClient.stop();
    	}
    }


}