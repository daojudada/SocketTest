package com.test.other;

import com.alibaba.fastjson.JSON;

/**
 * JSON解码编码类
 * @author GuoJun
 *
 */
public class JsonUtils {
 
    /**
     * 将javaBean转换成json对象
     * 
     * @param paramObject
     *            需要解析的对象
     */
    public static String createJsonString(Object paramObject) {
        String str = JSON.toJSONString(paramObject);
        return str;
    }

    /**
     * 对单个javaBean进行解析
     * 
     * @param <T>
     * @param paramJson 需要解析的json字符串
     * @param paramCls 需要转换成的类
     * @return
     */
    public static <T> T getObject(String paramJson, Class<T> paramCls) {
        T t = null;
        try {
            t = JSON.parseObject(paramJson, paramCls);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

}
