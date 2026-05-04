package cn.wildfirechat.sdk.model;

import cn.wildfirechat.common.ErrorCode;

/**
 * IM结果类
 * <p>
 * 泛型类，用于封装所有API调用的返回结果。
 * 包含错误码、错误消息和返回数据。
 * </p>
 * @param <T> 返回数据的类型
 */
public class IMResult<T> {

    public int code;
    public String msg;
    public T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.fromCode(code);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
