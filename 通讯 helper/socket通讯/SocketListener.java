package com.utils.comm;

/**
 * Socket通讯监听
 *
 * @author jianshengd
 * @date 2018/2/19
 */
public interface SocketListener {
    /**
     * 成功
     *
     * @param response 返回数据
     */
    void succ(byte[] response);

    /**
     * 连接失败
     */
    void connectFail();

    /**
     * 发送失败
     */
    void sendFail();

    /**
     * 发送成功
     */
    void sendFinish();

    /**
     * 接受失败
     */
    void recvFail();
}
