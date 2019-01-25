package com.utils.comm;

/**
 * Http通讯结果
 *
 * @author jianshengd
 * @date 2018/2/19
 */
public interface IHttpOkListener {
    /**
     * 成功
     *
     * @param response 返回内容
     *                 根据实际情况，决定使用String还是byte[]
     */
    void onSucc(byte[] response);

    /**
     * 失败
     *
     * @param responeCode 网络返回码
     * @param message     错误提示
     */
    void onFailure(int responeCode, String message);
}
