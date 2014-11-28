/**
 * FileServer
 * @title FSClient.java
 * @package com.chn.fs
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014��11��27��-����11:02:53
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

import com.chn.fs.message.DeleteRequest;
import com.chn.fs.message.ExistRequest;
import com.chn.fs.message.ReadRequest;
import com.chn.fs.message.WriteRequest;
import com.chn.fs.util.MD;

/**
 * @class FSClient
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FSClient implements ConstantCode {

    private static final int DEFAULT_CALL_TIMEOUT       = 30000;
    private static final int DEFAULT_CLIENT_NUMS        = 100;
    private static final int DEFAULT_CONNECT_TIMEOUT    = 10000;
    
    private String serverIp;
    private int serverPort;
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
    public FSClient(String serverIp, int serverPort) {
        
        this(serverIp, serverPort, DEFAULT_CALL_TIMEOUT, DEFAULT_CLIENT_NUMS, DEFAULT_CONNECT_TIMEOUT);
    }
    
    /**
     * ���췽����
     * 
     * @param   serverIp        ������IP
     * @param   serverPort      �������˿�
     * @param   callTimeout     ���ó�ʱʱ�䣬��λ������
     */
    public FSClient(String serverIp, int serverPort, int callTimeout) {
        
        this(serverIp, serverPort, callTimeout, DEFAULT_CLIENT_NUMS, DEFAULT_CONNECT_TIMEOUT);
    }
    
    /**
     * ���췽����
     * 
     * @param   serverIp        ������IP
     * @param   serverPort      �������˿�
     * @param   callTimeout     ���ó�ʱʱ�䣬��λ������
     * @param   clientNums      �ͻ������������������Ƽ�1
     */
    public FSClient(String serverIp, int serverPort, int callTimeout, int clientNums) {
        
        this(serverIp, serverPort, callTimeout, clientNums, DEFAULT_CONNECT_TIMEOUT);
    }
    
    /**
     * ���췽����
     * 
     * @param   serverIp        ������IP
     * @param   serverPort      �������˿�
     * @param   callTimeout     ���ó�ʱʱ�䣬��λ������
     * @param   clientNums      �ͻ������������������Ƽ�1
     * @param   connectTimeout  ���ӳ�ʱʱ�䣬��λ������
     */
    public FSClient(String serverIp, int serverPort, int callTimeout, int clientNums, int connectTimeout) {
        
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.callTimeout = callTimeout;
        this.clientNums = clientNums;
        this.connectTimeout = connectTimeout;
    }
    
    /**
     * д���ļ���
     * 
     * @param   bytes                   �ļ������ֽ�����
     * @param   amount                  �ļ�����
     * 
     * @return  �ļ�ժҪ
     * 
     * @throws  ConnectException        ��RPC���Ӵ���ʱ�׳�
     * @throws  SocketException         ��RPC���ó���ͨѶ����ʱ�׳�
     * @throws  WriteAbortedException   ��RPC����д�����ʱ�׳�
     * @throws  InterruptedException    ��RPC���ñ��ж�ʱ�׳�
     */
    public byte[] write(byte[] bytes, int amount) throws ConnectException, 
        SocketException, WriteAbortedException, InterruptedException {
        
        byte[] digest = MD.digest("SHA-256", bytes);
        
        ExistRequest existReq = new ExistRequest(digest, amount);
        String existResp = (String) this.call(existReq);
        if (RESPONSE_CODE_FALSE.equals(existResp)) {
            WriteRequest writeReq = new WriteRequest(digest, bytes, amount);
            String writeResp = (String) this.call(writeReq);
            if (RESPONSE_CODE_FALSE.equals(writeResp)) {
                throw new WriteAbortedException("д���ļ�ʧ�ܣ�", new Exception());
            } 
        }
        
        return digest;
    }
    
    /**
     * ��ȡ�ļ���
     * 
     * @param   digest                  �ļ�ժҪ
     * 
     * @return  �ļ�����
     * 
     * @throws  ConnectException        ��RPC���Ӵ���ʱ�׳�
     * @throws  SocketException         ��RPC���ó���ͨѶ����ʱ�׳�
     * @throws  InterruptedException    ��RPC���ñ��ж�ʱ�׳�
     */
    public byte[] read(byte[] digest) throws ConnectException, SocketException, 
        InterruptedException {
        
        return (byte[]) this.call(new ReadRequest(digest));
    }
    
    /**
     * ɾ��
     * @param   digest                  �ļ�ժҪ
     * 
     * @return  �ļ�����
     * 
     * @throws  ConnectException        ��RPC���Ӵ���ʱ�׳�
     * @throws  SocketException         ��RPC���ó���ͨѶ����ʱ�׳�
     * @throws  InterruptedException    ��RPC���ñ��ж�ʱ�׳�
     */
    public boolean delete(byte[] digest) throws ConnectException, SocketException, 
        InterruptedException {
        
        String result = (String) this.call(new DeleteRequest(digest));
        
        if (RESPONSE_CODE_FALSE.equals(result)) {
            return false;
        } else if (RESPONSE_CODE_TRUE.equals(result)) {
            return true;
        }
        
        return true;
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
                        this.serverPort, this.connectTimeout, this.clientNums);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("���ӷ�����ʧ�ܣ���", e);
            }
        } else {
            return true;
        }
    }
    
    /*
     * �رտͻ������ӡ�
     */
    private void closeClient() {
        
        Netty4ClientFactory.getInstance().removeClient(this.serverIp + ":" + this.serverPort, this.client);
        this.client = null;
    }
    
}
