package com.test.tcptest;

import com.test.other.Entity;
import com.test.other.LogUtils;
import com.test.other.SessionUtils;
import com.test.tcptest.WifiNet.SocketMode;


/**
 * 消息处理接口
 * @author GuoJun
 *
 */

public interface NetInterface {
	    
		/**
		 * 添加客户端消息处理监听回调
		 * @param listener
		 */
	    public void addClientListener(MSGListener listener);

		/**
		 * 添加服务器消息处理监听回调
		 * @param listener
		 */
	    public void addServerListener(MSGListener listener);

		/**
		 * 删除客户端消息处理监听回调
		 * @param listener
		 */
	    public void removeClientListener(MSGListener listener);

		/**
		 * 删除服务器消息处理监听回调
		 * @param listener
		 */
	    public void removeServerListener(MSGListener listener);

	    /**
	     * 连接服务器
	     * @return is connected successful
	     */
	    public void connectServer();
		   
	    /**
	     * 创建客户端
	     */
	    public void createClient();
	    
	    /**
	     * 创建服务器
	     */
	    public void createServer();
	    
	    /**
	     * 开启客户端线程
	     */
	    public void startClient();
	    
	    /**
	     * 开启服务器线程
	     */
	    public void startServer();
	    
	    /**
	     * 寻找服务器
	     * @return is find successful
	     */
	    public boolean findServer();
	    
	    /**
	     * 得到客户端实例
	     * @return client
	     */
	    public TcpClient getClient();
	    
	    /**
	     * 得到服务器实例
	     * @return server
	     */
	    public TcpServer getServer();
	    
	    
	    /**
	     * 发送ipmsg数据到所有客户端
	     * @param commandNo 
	     * @param addData 
	     * @param connectionID
	     * @param sm
	     */
	    public void sendToAllClient(int commandNo, Object addData, SocketMode sm);
	    
	    
	    //public void sendToAllExClient(int commandNo, Object addData, int connectionID, SocketMode sm) ;
	     
	    /**
	     * 发送ipmsg数据到指定客户端
	     * @param commandNo 
	     * @param addData 
	     * @param connectionID
	     * @param sm
	     */
	   	//public void sendToClient(int commandNo, Object addData,int connectionID, SocketMode sm) ;
	    
	    
	    
	    /**
	     * 发送ipmsg数据到服务器
	     * @param commandNo
	     * @param addData
	     * @param sm
	     */
	    public void sendToServer(int commandNo, Object addData, SocketMode sm) ;
	  
	 	/**
	 	 * 停止网络服务	
	 	 */
	 	public void stopNet();

}