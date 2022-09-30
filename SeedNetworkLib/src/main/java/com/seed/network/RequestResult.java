package com.seed.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestResult<T> {

    /**
     * 请求成功
     */
    public static final int SCODE_SUCCESS = 1;

    /**
     * 请求错误
     */
    public static final int SCODE_ERROR = -1;


    @SerializedName("msg")
    private String msg;

    @SerializedName(value = "result")
    private T result;

    @SerializedName("statusCode")
    private int statusCode = SCODE_ERROR;


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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return statusCode == SCODE_SUCCESS;
    }


    @Override
    public String toString() {
        return "API{" +
                "msg='" + msg + '\'' +
                ", result=" + result +
                ", statusCode=" + statusCode +
                '}';
    }
}
