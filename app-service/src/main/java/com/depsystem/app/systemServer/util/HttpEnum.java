package com.depsystem.app.systemServer.util;

/** https status
 * @author adiao
 */

public enum HttpEnum {
    /**
     * 请求处理正常
     */
    OK_200(200, "access"),
    /**
     * 请求处理正常
     */
    CREATED_201(201, "create access"),
    /**
     * 请求处理异常，请稍后再试
     */
    ERROR_400(400, "非法请求"),
    /**
     * 访问内容不存在
     */
    NotFound_400(404, "访问内容不存在"),
    /**
     * 系统内部错误
     */
    ERROR_500(500, "系统内部错误"),
    /**
     * 参数校验失败
     */
    ERROR_600(600, "参数校验失败"),
    /**
     * 支付失败
     */
    ERROR_700(700,"支付失败");
    /**
     *
     */
    private final String desc;
    private final int code;
    //private final Object data;
    HttpEnum(int code, String desc) {
        this.desc = desc;
        this.code = code;
        //this.data = data;
    }
    public String desc() {
        return desc;
    }
    public int code() {
        return code;
    }
    //public Object data(){ return data; }

}
