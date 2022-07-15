package com.miiarms.cloud.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Json工具类
 * @author miiarms
 * @version 1.0
 * @date 2021/11/18 15:13
 */
@Slf4j
public class JacksonHelper {

     private static final ObjectMapper OBJECT_MAPPER;
    private static final JsonFactory jasonFactory ;
     static {
         OBJECT_MAPPER = new ObjectMapper();
         jasonFactory = OBJECT_MAPPER.getFactory();
         OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
         OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 注册一个时间序列化及反序列化的处理模块，用于解决jdk8中localDateTime等的序列化问题
         OBJECT_MAPPER.registerModule(new JavaTimeModule());
         // 忽略空Bean转json的错误
         OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
     }


    /**
     * 转换为 JSON 字符串
     * @param obj 源对象
     */
    public static <T> String toJson(T obj) {

        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("parse json error", e);
        }
        return null;
    }

    /**
     * json字符串 => 对象
     * @param json 源json串
     * @param clazz 对象类
     * @param <T> 泛型
     */
    public static <T> T parse(String json, Class<T> clazz) {
        return parse(json, clazz, null);
    }

    /**
     * json字符串 => 对象
     *
     * @param json 源json串
     * @param type 对象类型
     * @param <T> 泛型
     */
    public static <T> T parse(String json, TypeReference<T> type) {

        return parse(json, null, type);
    }

    public static <T> List<T> parseList(String json) {
        return parse(json, null, new TypeReference<List<T>>(){ });
    }


    /**
     * json => 对象处理方法
     * <br>
     * 参数clazz和type必须一个为null，另一个不为null
     * <br>
     * 此方法不对外暴露，访问权限为private
     *
     * @param json 源json串
     * @param clazz 对象类
     * @param type 对象类型
     * @param <T> 泛型
     */
    private static <T> T parse(String json, Class<T> clazz, TypeReference<T> type) {

        if (StringUtils.isEmpty(json)) {
            return null;
        }
        T obj = null;
        try {
            if (clazz != null) {
                obj = OBJECT_MAPPER.readValue(json, clazz);
            } else {
                obj = OBJECT_MAPPER.readValue(json, type);
            }
        } catch (IOException e) {
            log.error("parse json error", e);
        }
        return obj;
    }

    public static String parseOneField(String str, String fieldName) {
        try {
            JsonParser jsonParser = jasonFactory.createParser(str);
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                // get the current token
                String fieldname = jsonParser.getCurrentName();
                if (fieldName.equals(fieldname)) {
                    // move to next token
                    jsonParser.nextToken();
                    return jsonParser.getText();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("object format to json error:", e);
        }
        return null;
    }
}
