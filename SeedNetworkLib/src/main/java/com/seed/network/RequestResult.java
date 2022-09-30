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


    @SerializedName("errorMsg")
    private String errorMsg;

    @SerializedName(value = "data")
    private T data;

    @SerializedName("errorCode")
    private int errorCode = SCODE_ERROR;


    public String getMsg() {
        return errorMsg;
    }

    public void setMsg(String msg) {
        this.errorMsg = msg;
    }

    public T getResult() {
        return data;
    }

    public void setResult(T result) {
        this.data = result;
    }

    public int getStatusCode() {
        return errorCode;
    }

    public void setStatusCode(int statusCode) {
        this.errorCode = statusCode;
    }

    public boolean isSuccess() {
        return errorCode == SCODE_SUCCESS;
    }


    @Override
    public String toString() {
        return "API{" +
                "msg='" + errorMsg + '\'' +
                ", result=" + data +
                ", statusCode=" + errorCode +
                '}';
    }
}
