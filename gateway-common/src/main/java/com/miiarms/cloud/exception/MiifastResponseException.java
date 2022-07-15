
package com.miiarms.cloud.exception;


import com.miiarms.cloud.enums.ResponseCode;

/**
 *
 * 所有的响应异常基础定义
 * @author Miiarms
 * @date 2022/5/19 0:11
 */
public class MiifastResponseException extends MiifastBaseException {

    private static final long serialVersionUID = -5658789202509039759L;

    public MiifastResponseException() {
        this(ResponseCode.INTERNAL_ERROR);
    }

    public MiifastResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public MiifastResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }

}
