/**
 * FileServer
 * @title TM.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014��11��26��-����6:18:22
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import org.apache.log4j.Logger;

import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;

/**
 * @class TM
 * @author lzxz1234
 * @description ���������
 * @version v1.0
 */
public class TM {

    private static Logger log = Logger.getLogger(TM.class);
    private static ThreadLocal<Transaction> tl = new ThreadLocal<Transaction>();
    
    public static void begin(Environment env) {
        
        Transaction previor = tl.get();
        if(previor != null) log.error("[��ǰ�߳���δ�رյ�����]");
        
        Transaction trans = env.beginTransaction(null, null);
        tl.set(trans);
    }
    
    public static void commit() {
        
        Transaction previor = tl.get();
        if(previor == null) throw new IllegalStateException("[��ǰ�߳�δ��������]");
        
        previor.commit();
        tl.remove();
    }
    
    public static void roolback() {
        
        Transaction previor = tl.get();
        if(previor == null) throw new IllegalStateException("[��ǰ�߳�δ��������]");
        
        previor.abort();
        tl.remove();
    }
    
}
