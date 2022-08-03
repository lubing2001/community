package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串（激活码、上传文件的随机名字）
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
        // 后面的 replace 方法是去掉随机字符串中的 “-”
    }

    // MD5加密（存的时候对密码进行加密,防止泄露）
    // MD5加密特点：1. 只能加密，不能解密   2. 每次加密结果都一样
    // 为了避免由于用户设的密码过于简单导致很容易被破解，
    // 我们在处理密码的时候会在密码后面加上一个随机的字符串然后用MD5加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            // 空串（nulll 或者 空格 或者 空串）直接返回 null
            return null;
        }
        // 不为空串则进行加密（方法参数是byte）
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 返回JSON字符串
    // 参数：编码、提示信息、Map（Map里面封装业务数据）
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        /*
        封装成json对象，然后把json对象转换成字符串就得到了一个json格式的字符串
         */
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    // 重载
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }
    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

}
