package com.miiarms.cloud.exception;


import com.miiarms.cloud.enums.ResponseCode;

/**
 *
 * 网关最基础的异常定义类
 * @author Miiarms
 * @date 2022/5/19 0:10
 */
public class MiifastBaseException extends RuntimeException {

    private static final long serialVersionUID = -5658789202563433456L;
    
    public MiifastBaseException() {
    }

    protected ResponseCode code;

    public MiifastBaseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public MiifastBaseException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

    public MiifastBaseException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public MiifastBaseException(String message, Throwable cause,
    		boolean enableSuppression, boolean writableStackTrace, ResponseCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
    
    public ResponseCode getCode() {
        return code;
    }

}
