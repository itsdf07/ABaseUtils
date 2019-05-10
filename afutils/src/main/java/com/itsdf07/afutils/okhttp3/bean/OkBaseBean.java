package com.itsdf07.afutils.okhttp3.bean;

/**
 * @Description ：网络应答报文基础类
 * @Author itsdf07
 * @Time 2018/09/13
 */
public class OkBaseBean {
    /**
     * code : 200
     * desc : 服务器成功处理请求
     */

    /**
     * 应答码
     */
    private String code;
    /**
     * 应答码描述
     */
    private String desc;
    /**
     * 数据是否有加密
     */
    private boolean isDecode;
    /**
     * 加密数据
     */
    private String encrptyData;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isDecode() {
        return isDecode;
    }

    public void setDecode(boolean decode) {
        isDecode = decode;
    }

    public String getEncrptyData() {
        return encrptyData;
    }

    public void setEncrptyData(String encrptyData) {
        this.encrptyData = encrptyData;
    }
}
