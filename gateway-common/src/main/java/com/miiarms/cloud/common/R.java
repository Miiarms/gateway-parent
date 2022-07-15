package com.miiarms.cloud.common;

import com.miiarms.cloud.enums.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共返回值工具
 * @author miiarms
 * @version 1.0
 * @date 2022/6/10 9:32
 */
@Data
public class R implements Serializable {
    private static final long serialVersionUID = 3671419529678451795L;

    private int code; // 200是正常，非200表示异常
    private String msg;
    private Object data;

    public static R ok() {
        return ok(ResponseCode.SUCCESS.getCode(), "操作成功", null);
    }

    public static R ok(Object data) {
        return ok(ResponseCode.SUCCESS.getCode(), "操作成功", data);
    }

    public static R ok(int code, String msg, Object data) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static R fail(String msg) {
        return fail(ResponseCode.INTERNAL_ERROR.getCode(), msg, null);
    }

    public static R fail(String msg, Object data) {
        return fail(ResponseCode.INTERNAL_ERROR.getCode(), msg, data);
    }

    public static R fail(int code, String msg, Object data) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }



}
