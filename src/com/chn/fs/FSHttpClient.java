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

import com.chn.fs.util.MD;

/**
 * @class FSHttpClient
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FSHttpClient implements ConstantCode {

    private FSClient client;
    private String serverIp;
    private int httpPort;
    
    /**
     * ���췽����
     * 
     * @param   serverIp        ������IP
     * @param   serverPort      �������˿�
     */
    public FSHttpClient(String serverIp, int rpcPort, int httpPort) {
        
        this.client = new FSClient(serverIp, rpcPort);
        this.serverIp = serverIp;
        this.httpPort = httpPort;
    }
    
    public String add(byte[] bytes) throws Exception {
        
        byte[] digest = client.write(bytes, 1);
        return "http://"+serverIp+":"+httpPort+"/download.jsp?id="+MD.getHexString(digest);
    }
    
}
