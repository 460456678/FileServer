/**
 * FileServer
 * @title MD.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014��11��26��-����6:13:50
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @class MD
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class MD {
    
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
    /**
     * ����ֽ������ժҪ��Ϣ��
     * 
     * @param   algorithm       ժҪ�㷨
     * @param   bytes           �ֽ�����
     * 
     * @return  �ֽ������ժҪ��Ϣ�����bytes��null���򷵻�null��
     * 
     * @throws  UnsupportedOperationException   ����㷨��null�����ַ������߲�֧�֣����׳���
     */
    public static byte[] digest(String algorithm, byte[] bytes) {
        
        if ((algorithm == null) || "".equals(algorithm)) 
            throw new UnsupportedOperationException("Algorithm is null!");
        
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            if (bytes == null) return null;
            md.update(bytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Algorithm[" + algorithm + "] is not supported!", e);
        }
    }
    
    /**
     * ����ַ�����ժҪ��Ϣ��
     * 
     * @param   algorithm       ժҪ�㷨
     * @param   string          �ַ���
     * 
     * @return  �ַ�����ժҪ��Ϣ�����bytes��null���򷵻�null��
     * 
     * @throws  UnsupportedOperationException   ����㷨��null�����ַ������߲�֧�֣����׳���
     */
    public static byte[] digest(String algorithm, String string) {
        
        return digest(algorithm, (string == null) ? null : string.getBytes());
    }
    
    /**
     * ����ֽ������ժҪ�ַ�����
     * 
     * @param   algorithm       ժҪ�㷨
     * @param   bytes           �ֽ�����
     * 
     * @return  �ֽ������ժҪ�ַ��������bytes��null���򷵻�null��
     * 
     * @throws  UnsupportedOperationException   ����㷨��null�����ַ������߲�֧�֣����׳���
     */
    public static String digestString(String algorithm, byte[] bytes) {
        
        return getHexString(digest(algorithm, bytes));
    }
    
    /**
     * ����ַ�����ժҪ�ַ�����
     * 
     * @param   algorithm       ժҪ�㷨
     * @param   bytes           �ֽ�����
     * 
     * @return  �ַ�����ժҪ�ַ��������bytes��null���򷵻�null��
     * 
     * @throws  UnsupportedOperationException   ����㷨��null�����ַ������߲�֧�֣����׳���
     */
    public static String digestString(String algorithm, String string) {
        
        return getHexString(digest(algorithm, string));
    }
    
    public static String getHexString(byte[] bytes) {
        
        if (bytes == null) return null;
        
        int len = bytes.length;
        StringBuilder result = new StringBuilder(len * 2);
        
        for (int i = 0; i < len; i++) {
            result.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            result.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        
        return result.toString();
    }
    
    public static byte[] decodeHexString(String hex) {
        
        if(hex == null) return new byte[0];
        byte[] result = new byte[hex.length() / 2];
        for(int i = 0; i < hex.length(); i = i + 2) {
            result[i / 2] = (byte)(decodeHex(hex.charAt(i)) << 4 | decodeHex(hex.charAt(i+1)));
        }
        return result;
    }
    
    private static int decodeHex(char c) {
        
        if(c >= '0' && c <= '9') return c - '0';
        if(c >= 'a' && c <= 'f') return c - 'a' + 10;
        throw new RuntimeException("δ֪���ţ�" + c);
    }
    
}
