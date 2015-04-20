package com.test.tcptest;


/**
 * 消息处理接口
 * @author GuoJun
 *
 */

public interface MSGListener {
    public void processMessage(MSGProtocol pMsg);
}