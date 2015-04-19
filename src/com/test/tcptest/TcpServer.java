package com.test.tcptest;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.other.Entity;
import com.test.other.LogUtils;
import com.test.other.SessionUtils;
import com.test.tcptest.UDPMsgThread.OnNewMsgListener;

import android.content.Context;

public class TcpServer implements Runnable {
    private static final String TAG = "TcpClient";
    private static TcpServer instance;
    private static ExecutorService executor;
    private static final int POOL_SIZE = 5; // 单个CPU线程池大小
    private static byte[] sendBuffer = new byte[Constant.READ_BUFFER_SIZE]; // 数据报内容
    private static byte[] receiveBuffer = new byte[Constant.READ_BUFFER_SIZE];// 数据报内容

    private List<Socket> clientList;
    private OutputStream output = null;
    private InputStream input = null;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private Thread receiveThread;
    private ServerSocket serverSocket ;
    private List<OnNewMsgListener> mListenerList;
    private boolean isThreadRunning ; // 是否线程开始标志

    private TcpServer() {
        int cpuNums = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNums * POOL_SIZE); // 根据CPU数目初始化线程池
        mListenerList = new ArrayList<OnNewMsgListener>();
        clientList = new ArrayList<Socket>();
        serverSocket = null;
        LogUtils.d(TAG, "建立线程成功");

    }

    public Thread getThread() {
        return receiveThread ;
    }


    public void addMsgListener(OnNewMsgListener listener) {
        this.mListenerList.add(listener);
    }

    public void removeMsgListener(OnNewMsgListener listener) {
        this.mListenerList.remove(listener);
    }

    /**
     * <p>
     * 获取TcpService实例
     * <p>
     * 单例模式，返回唯一实例
     */
    public static TcpServer getInstance(Context context) {
        if (instance == null) {
            instance = new TcpServer();
        }
        return instance;
    }

    private TcpServer(Context context) {
        this();
        LogUtils.d(TAG, "TCP_Client初始化完毕");
    }

    public void start() {
        LogUtils.d(TAG, "发送线程开启");
        isThreadRunning  = true; // 使能发送标识
        if (!receiveThread.isAlive())
        	 receiveThread.start();
    }

    /** 暂停监听线程 **/
    public void stop() {
    	isThreadRunning  = false;
        if (receiveThread != null)
            receiveThread.interrupt();
        receiveThread = null;
        instance = null; // 置空, 消除静�?�变量引�?
        LogUtils.i(TAG, "stopUDPSocketThread() 线程停止成功");
    }
    
    /** 建立Socket连接 **/
    public void connect(String target_IP) {
        try {
            // 绑定端口
            if (serverSocket == null)
            	serverSocket = new ServerSocket(Constant.TCP_PORT);

            
            LogUtils.i(TAG, "connectUDPSocket() 绑定端口成功");
            startSocketThread();
        }
	    catch (UnknownHostException e) {
	        LogUtils.d(TAG, "建立客户端socket失败");
	        isThreadRunning = false;
	        e.printStackTrace();
	    }
	    catch (IOException e) {
	        LogUtils.d(TAG, "建立客户端socket失败");
	        isThreadRunning = false;
	        e.printStackTrace();
	    }
    }
    
    /** �?始监听线�? **/
    private void startSocketThread() {
        if (receiveThread == null) {
        	receiveThread = new Thread(this);
            receiveThread.start();
        }
        isThreadRunning = true;
        LogUtils.i(TAG, "startUDPSocketThread() 线程启动成功");
    }
    
    
    @Override
    public void run(){
    	Socket socket = null;
    	while(isThreadRunning){  
    		try {            
				//监听客户端请求，启个线程处理                
				socket = serverSocket.accept();      
				clientList.add(socket);
				recieveData(socket);
			}catch (Exception e) {   
				e.printStackTrace();
				LogUtils.e(TAG, "sendUDPdata() 发送UDP数据包失败");
			}
    	}
        if (socket != null) {
            try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            socket = null;
        }
        receiveThread = null;

    }
    
    private void recieveData(final Socket socket){
    	executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
	                input = socket.getInputStream();
	                dataInput = new DataInputStream(input);
	                
	                try {
	            		dataInput.read(receiveBuffer);
	                }
	                catch (IOException e) {
	                    isThreadRunning = false;
	                    receiveThread = null;
	                    LogUtils.e(TAG, "数据包接收失败！线程停止");
	                    e.printStackTrace();
	                }
	                
	                String TCPListenResStr = "";
	                try {
	                    TCPListenResStr = new String(receiveBuffer,"gbk");
	                }
	                catch (UnsupportedEncodingException e) {
	                    LogUtils.e(TAG, "系统不支持GBK编码");
	                }

	                MSGProtocol msgRes = new MSGProtocol(TCPListenResStr);
	                int command = msgRes.getCommandNo();
	                if (!SessionUtils.isLocalUser(msgRes.getSenderIMEI())) 
	                {
	                	switch(command){
	                		default:
	                			break;
	                	}
	                	

	                    for (int i = 0; i < mListenerList.size(); i++) {
	                        android.os.Message pMsg = new android.os.Message();
	                        pMsg.what = command;
	                        mListenerList.get(i).processMessage(pMsg);
	                    }
	                }
	                
				}
				catch (Exception e) {
					e.printStackTrace();
					LogUtils.e(TAG, "sendUDPdata() 发送UDP数据包失败");
				}
			}
		});  
    }
    
    
   

    public void sendToAllClient(int commandNo) {
    	sendToAllClient(commandNo,  null);
    }

    public void sendToAllClient(int commandNo,  Object addData) {
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
        
        sendToAllClient(ipmsgProtocol);
    }

    public void sendToAllClient(MSGProtocol ipmsgProtocol) {
        for(Socket socket : clientList){
            sendData(ipmsgProtocol,socket);
        }
    }
    
    public void sendData(final MSGProtocol msg, final Socket socket) {
    	 executor.execute(new Runnable() {
             @Override
             public void run() {
                 try {
                	 output = socket.getOutputStream();
                	 dataOutput = new DataOutputStream(output);
                     sendBuffer = msg.getProtocolJSON().getBytes("gbk");
                     dataOutput.write(sendBuffer);
                     dataOutput.flush();
                     LogUtils.d(TAG, "Tcp msg发送完毕");
                     output.close();
                     dataOutput.close();
                     socket.close();
         		} catch (IOException e) {
         			LogUtils.i(TAG, "Tcp msg发送失败");
         			e.printStackTrace();
         		}
             }
         });
    }
}

