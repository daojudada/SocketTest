package com.test.tcptest;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.other.Entity;
import com.test.other.LogUtils;
import com.test.other.SessionUtils;

import android.content.Context;

public class TcpClient implements Runnable {
    private static final String TAG = "TcpClient";
    private static TcpClient instance;
    private static ExecutorService executor;
    private static final int POOL_SIZE = 5; // 单个CPU线程池大小
    private static byte[] sendBuffer = new byte[Constant.READ_BUFFER_SIZE]; // 数据报内容
    private static byte[] receiveBuffer = new byte[Constant.READ_BUFFER_SIZE];// 数据报内容

    private OutputStream output = null;
    private InputStream input = null;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private Thread receiveThread;
    private Socket socket ;
    private List<MSGListener> mListenerList;
    private boolean isThreadRunning ; // 是否线程开始标志

    private TcpClient() {
        int cpuNums = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNums * POOL_SIZE); // 根据CPU数目初始化线程池
        mListenerList = new ArrayList<MSGListener>();
        LogUtils.i(TAG, "建立线程成功");

    }

    public Thread getThread() {
        return receiveThread ;
    }


    public void addMsgListener(MSGListener listener) {
        this.mListenerList.add(listener);
    }

    public void removeMsgListener(MSGListener listener) {
        this.mListenerList.remove(listener);
    }

    /**
     * <p>
     * 获取TcpService实例
     * <p>
     * 单例模式，返回唯一实例
     */
    public static TcpClient getInstance(Context context) {
        if (instance == null) {
            instance = new TcpClient();
        }
        return instance;
    }

    private TcpClient(Context context) {
        this();
        LogUtils.i(TAG, "TCP_Client初始化完毕");
    }

    /** 开始监听线程 **/
    public void start() {
        isThreadRunning  = true; // 使能发送标识
        if (receiveThread == null) {
            receiveThread = new Thread(this);
            receiveThread.start();
        }
        LogUtils.i(TAG, "发送线程开启");
    }

    /** 暂停监听线程 **/
    public void stop() {
        try {
            output.close();
			dataOutput.close();
	        socket.close();
	    	isThreadRunning  = false;
	        if (receiveThread != null)
	            receiveThread.interrupt();
	        receiveThread = null;
	        instance = null; // 置空,
	        LogUtils.i(TAG, "stopSocketThread() 线程停止成功");
		} catch (IOException e) {
	        LogUtils.i(TAG, "stopSocketThread() 线程停止失败");
			e.printStackTrace();
		}
    }
    
    /** 建立Socket连接 **/
    public boolean connect(String target_IP) {
        try {
            // 绑定端口
            if (socket == null){
        		socket = new Socket(InetAddress.getByName(target_IP), Constant.TCP_PORT);
            }
            output = socket.getOutputStream();
            input = socket.getInputStream();
            dataOutput = new DataOutputStream(output);
            dataInput = new DataInputStream(input);
            
            
            LogUtils.i(TAG, "connectSocket() 绑定端口成功");
            return true;
        }
	    catch (UnknownHostException e) {
	        LogUtils.e(TAG, "建立客户端socket失败");
	        isThreadRunning = false;
	        e.printStackTrace();
	        return false;
	    }
	    catch (IOException e) {
	        LogUtils.e(TAG, "建立客户端socket失败");
	        isThreadRunning = false;
	        e.printStackTrace();
	        return false;
	    }
    }
    
    
    
    @Override
    public void run() {
        while (isThreadRunning) {
        	try {
        		dataInput.read(receiveBuffer);
            }
            catch (IOException e) {
                isThreadRunning = false;
                if (socket != null) {
                    try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                    socket = null;
                }
                receiveThread = null;
                LogUtils.e(TAG, "数据包接收失败！线程停止");
                e.printStackTrace();
                break;
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

                for (MSGListener msgListener: mListenerList) {
                    msgListener.processMessage(msgRes);
                }
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

    public void sendData(int commandNo) {
        sendData(commandNo,  null);
    }

    public void sendData(int commandNo,  Object addData) {
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
        sendData(ipmsgProtocol);
    }

    public void sendData(final MSGProtocol msg) {
    	 executor.execute(new Runnable() {
             @Override
             public void run() {
                 try {
                     sendBuffer = msg.getProtocolJSON().getBytes("gbk");
                     dataOutput.write(sendBuffer);
                     dataOutput.flush();
                     LogUtils.d(TAG, "sendData() 发送服务器数据包成功");
                 }
                 catch (Exception e) {
                     e.printStackTrace();
                     LogUtils.e(TAG, "sendData() 发送服务器数据包失败");
                 }
             }
         });
    }
    
}
