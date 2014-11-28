/**
 * FileServer
 * @title KryoSerializer.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014��11��26��-����6:08:49
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @class KryoSerializer
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class KryoSerializer {
    
    private static ThreadLocal<Kryo> threadKryo = new ThreadLocal<Kryo>();
    
    
    /**
     * ʹ��Kryo���л���ʽ�����л�����
     * 
     * @param   obj                 Ҫ���л��Ķ���
     * 
     * @return  ���л����ֽ�����
     */
    public static byte[] serialize(Object obj) {
        
        byte[] result = null;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output out = null;
        try {
            out = new Output(baos);
            currentKryo().writeObject(out, obj);
            out.flush();
            result = baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(out);
        }
        
        return result;
    }
    
    /**
     * ʹ��Kryo���л���ʽ�������л�����
     * 
     * @param   bytes                       Ҫ�����л�������ֽ�����
     * @param   clazz                       Ҫ�����л������Class����
     * 
     * @return  �����л��Ķ���
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        
        T result = null;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input in = null;
        try {
            in = new Input(bais);
            result = currentKryo().readObject(in, clazz);
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        return result;
    }
    
    /*
     * ����뵱ǰ�̹߳�����Kryo����
     * ��ΪKryo�������̰߳�ȫ�ġ�
     */
    private static Kryo currentKryo() {
        
        Kryo kryo = threadKryo.get();
        if (kryo == null) {
            kryo = new Kryo();
            threadKryo.set(kryo);
        }
        
        return kryo;
    }
}