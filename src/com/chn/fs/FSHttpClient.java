/**
 * FileServer
 * @title FSHttpClient.java
 * @package com.chn.fs
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014��11��27��-����2:41:27
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs;

import java.io.WriteAbortedException;
import java.net.ConnectException;
import java.net.SocketException;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.netty4.client.Netty4ClientFactory;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;

import com.chn.fs.message.ExistRequest;
import com.chn.fs.message.WriteRequest;
import com.chn.fs.util.MD;

/**
 * @class FSHttpClient
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FSHttpClient implements ConstantCode {

    private static final int DEFAULT_CALL_TIMEOUT       = 30000;
    private static final int DEFAULT_CLIENT_NUMS        = 100;
    private static final int DEFAULT_CONNECT_TIMEOUT    = 10000;
    
    private String serverIp;
    private int rpcPort;
    private int httpPort;
    private int callTimeout = DEFAULT_CALL_TIMEOUT;
    private int clientNums = DEFAULT_CLIENT_NUMS;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    
    private Client client;
    
    
    /**
     * ���췽����
     * 
     * @param   serverIp        ������IP
     * @param   serverPort      �������˿�
     */
    public FSHttpClient(String serverIp, int rpcPort, int httpPort) {
        
        this.serverIp = serverIp;
        this.rpcPort = rpcPort;
        this.httpPort = httpPort;
    }
    
    public String add(byte[] bytes) throws Exception {
        
        byte[] digest = MD.digest("SHA-256", bytes);
        
        ExistRequest existReq = new ExistRequest(digest, 1);
        String existResp = (String) this.call(existReq);
        if (RESPONSE_CODE_FALSE.equals(existResp)) {
            WriteRequest writeReq = new WriteRequest(digest, bytes, 1);
            String writeResp = (String) this.call(writeReq);
            if (RESPONSE_CODE_FALSE.equals(writeResp)) {
                throw new WriteAbortedException("д���ļ�ʧ�ܣ�", new Exception());
            } 
        }
        return "http://"+serverIp+":"+httpPort+"/download.jsp?id="+MD.getHexString(digest);
    }
    
    /*
     * ����RPC��
     * 
     * @param   request                 �������
     * 
     * @return  ��Ӧ����
     * 
     * @throws  ConnectException        ��RPC���Ӵ���ʱ�׳�
     * @throws  SocketException         ��RPC���ó���ͨѶ����ʱ�׳�
     * @throws  InterruptedException    ��RPC���ñ��ж�ʱ�׳�
     */
    private Object call(Object request) throws ConnectException, SocketException, 
        InterruptedException {
        
        if (request == null) throw new IllegalArgumentException("RPC�����������Ϊnull��");
        
        if (!this.isConnected()) throw new ConnectException("RPC���Ӵ���");
        
        Object resp = null;
        boolean retry = false;
        do {
            try {
                resp = this.client.invokeSync(request, this.callTimeout, Codecs.JAVA_CODEC, 
                                              SimpleProcessorProtocol.TYPE);
                retry = false;
            } catch (Exception e) {
                String errMsg = e.getMessage();
                if ((errMsg != null) && errMsg.startsWith("receive response timeout")) {
                    retry = true;
                } else if ((errMsg != null) && errMsg.equals("Get response error")) {
                    throw new InterruptedException("RPC���ñ��жϣ�");
                } else {
                    this.closeClient();
                    throw new SocketException("RPC����ͨѶ����");
                }
            }
        } while (retry);
        
        return resp;
    }
    
    /*
     * �ж��Ƿ����ӵ�RPC�����������û�������������ӡ�
     * 
     * @return  �Ƿ����ӵ�RPC��������
     */
    private boolean isConnected() {
         
        if (this.client == null) {
            try {
                this.client = Netty4ClientFactory.getInstance().get(this.serverIp, 
                        this.rpcPort, this.connectTimeout, this.clientNums);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("����ʧ��", e);
            }
        } else {
            return true;
        }
    }
    
    /*
     * �رտͻ������ӡ�
     */
    private void closeClient() {
        
        Netty4ClientFactory.getInstance().removeClient(this.serverIp + ":" + this.rpcPort, this.client);
        this.client = null;
    }
    
}
