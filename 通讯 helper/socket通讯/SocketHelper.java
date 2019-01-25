package com.utils.comm;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.nlutils.util.BytesUtils;
import com.nlutils.util.LoggerUtils;
import com.utils.thread.ThreadPool;

/**
 * Socket通讯
 *
 * @author cxy
 * @modifydate 2018/3/26
 * @modifier jianshengd
 * @date 2014/8
 */
public class SocketHelper {
    private static SocketHelper sSocketHelper;
    private static final long CONNECT_TIME = 60 * 1000;
    /**
     * 表示通讯开始时间,
     * 长链接模式时才更新，用来计算下次通讯是否断开原链接
     */
    private long mStartTime = 0;
    private Socket mSocket;
    private DataInputStream mInput;
    private DataOutputStream mOutput;
    private String mIp;
    private int mPort;
    private int mTimeOut;
    /**
     * ssl 证书输入流,为空表示不使用
     */
    private InputStream mCerIs;
    /**
     * 是否长链接
     */
    private boolean mIsLongConnect;

    /**
     * 取消通讯
     */
    private boolean mIsCancel;
    /**
     * 报文 头 两个字节表示报文总长度
     */
    private final static int SOCKET_LENGTH_LEN = 2;

    private SocketHelper(String ip, int port, int timeout, InputStream cerIs, boolean longConnect) {
        this.mIp = ip;
        this.mPort = port;
        this.mTimeOut = timeout;
        this.mCerIs = cerIs;
        this.mIsLongConnect = longConnect;
    }


    public static SocketHelper getSocketHelper(String ip, int port, int timeout, InputStream cerIs, boolean longConnect) {
        if (sSocketHelper == null || sSocketHelper.mIp == null || !sSocketHelper.mIp.equals(ip) || sSocketHelper.mPort != port || sSocketHelper.mTimeOut != timeout || System.currentTimeMillis() >= (sSocketHelper.mStartTime + CONNECT_TIME)) {
            //长链接：超过长链接时间范围，释放socket连接
            if (sSocketHelper != null && System.currentTimeMillis() >= (sSocketHelper.mStartTime + CONNECT_TIME)) {
                LoggerUtils.i("Long connection timeout!" + ((System.currentTimeMillis() - (sSocketHelper.mStartTime + CONNECT_TIME)) / 1000) + "s");
                sSocketHelper.release(false);
            }
            sSocketHelper = new SocketHelper(ip, port, timeout, cerIs, longConnect);
        }
        return sSocketHelper;
    }


    /**
     * 通讯开始
     */
    public void commSendRecv(final byte[] data, final SocketListener listener) {
        ThreadPool.newThread(new Runnable() {
            @Override
            public void run() {
                //通讯被取消
                if (mIsCancel) {
                    return;
                }
                //如果是长链接的，需要在调用以下，进行预连接
                if (mIsLongConnect) {
                    connect();
                }
                //每次连接
                if (!connect()) {
                    LoggerUtils.e("Connect server fail!");
                    listener.connectFail();
                    return;
                }
                //通讯被取消
                if (mIsCancel) {
                    return;
                }
                boolean isSsl = mCerIs != null;
                //发送数据
                boolean result = send(data, isSsl);
                if (!result) {
                    listener.sendFail();
                    return;
                }
                listener.sendFinish();
                //通讯被取消
                if (mIsCancel) {
                    return;
                }
                //接收数据
                byte[] respone = receive(isSsl);
                //通讯完毕，释放通讯socket
                closeComm();
                if (respone == null) {
                    listener.recvFail();
                } else {
                    listener.succ(respone);
                }
            }
        }).start();
    }

    /**
     * 发送数据
     *
     * @param data  数据
     * @param isSsl 是否使用ssl
     */
    private boolean send(byte[] data, boolean isSsl) {
        LoggerUtils.i("ready to send：" + BytesUtils.bcdToString(data));
        //使用SSL
        if (isSsl) {
            /*
             * SSL开启情况属于HTTP+ssl通讯方式
             * 报文结构 = http头+报文内容
             * 所以要添加http头
             */
            LoggerUtils.d("ready to send SLL data!");
            byte[] dataLen = new byte[2];
            dataLen[0] = data[0];
            dataLen[1] = data[1];
            int msgLen = BytesUtils.getShort(dataLen) + 2;
            byte[] httpHead1 = "POST /unp/webtrans/WPOS HTTP/1.1\r\n".getBytes();
            byte[] httpHead2 = ("HOST:" + mIp + ":" + mPort + "\r\n").getBytes();
            byte[] httpHead3 = ("User-Agent:" + "Donjin Http 0.1" + "\r\n").getBytes();
            byte[] httpHead4 = ("Cache-Control:" + "no-cache" + "\r\n").getBytes();
            byte[] httpHead5 = ("Content-Type:" + "x-ISO-TPDU/x-auth" + "\r\n").getBytes();
            byte[] httpHead6 = ("Accept:" + "*/*" + "\r\n").getBytes();
            byte[] httpHead7 = ("Content-Length:" + msgLen + "\r\n\r\n").getBytes();
            //httpHead 加入data中
            byte[] httpData = mergeByteArray(httpHead1, httpHead2, httpHead3, httpHead4, httpHead5, httpHead6, httpHead7, data);
            LoggerUtils.i("ready to send ssl data：" + BytesUtils.bcdToString(httpData));
            data = httpData;
        } else {
            LoggerUtils.d("ready to send data!");
        }
        initLongConnectTimer();
        try {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            ostream.write(data);
            byte[] sendData = ostream.toByteArray();
            ostream.close();
            mOutput.write(sendData);
            mOutput.flush();
            LoggerUtils.i("Send  data length=" + sendData.length);
            return true;
        } catch (IOException ioe) {
            LoggerUtils.e("send data fail");
            ioe.printStackTrace();
            release();
            return false;
        }
    }

    /**
     * 接收数据
     *
     * @param isSsl 是否使用ssl
     * @return 返回已经收到的数据
     */
    @SuppressWarnings("deprecation")
    private byte[] receive(boolean isSsl) {
        initLongConnectTimer();
        if (isSsl) {
            /*
             * SSL开启情况属于HTTP+ssl通讯方式
             * 报文结构 = http头+报文内容
             * 所以要去掉http头
             */
            LoggerUtils.e("ready to receive SLL data!");
            try {
                String httpHead1 = mInput.readLine();
                String httpHead2 = mInput.readLine();
                String httpHead3 = mInput.readLine();
                String httpHead4 = mInput.readLine();
                String httpHead5 = mInput.readLine();
                String httpHead6 = mInput.readLine();
                String httpHead7 = mInput.readLine();
                mInput.readLine();
                LoggerUtils.i("httpHead1:" + httpHead1);
                LoggerUtils.i("httpHead2:" + httpHead2);
                LoggerUtils.i("httpHead3:" + httpHead3);
                LoggerUtils.i("httpHead4:" + httpHead4);
                LoggerUtils.i("httpHead5:" + httpHead5);
                LoggerUtils.i("httpHead6:" + httpHead6);
                LoggerUtils.i("httpHead7:" + httpHead7);
            } catch (Exception e) {
                LoggerUtils.e("receive data error!");
                e.printStackTrace();
                release();
                return null;
            }
        } else {
            LoggerUtils.e("ready to receive data!");
        }

        try {
            /*
             *报文内容 = len[2个字节]+ 报文体[len个字节]
             */
            //1.从输入流中读取头两个字节socketLen -> msgLen
            byte[] socketLen = new byte[2];
            int retLen = mInput.read(socketLen);
            if (retLen != SOCKET_LENGTH_LEN) {
                LoggerUtils.e("error: The length of received data is:" + retLen);
                return null;
            }
            int msgLen = BytesUtils.getShort(socketLen);
            //2.将报文体长度len存入msg中,并判断输入流中填满msgLen长度内容
            byte[] msg = new byte[msgLen + 2];
            System.arraycopy(socketLen, 0, msg, 0, 2);
            while (true) {
                //相隔一段时间后 缓冲区内的可读数据没变的话说明客户端已经写入完成 退出循环 一次性将剩余数据取出
                if (mInput.available() == msgLen) {
                    break;
                }
                //try代码省略 sleep时间可以按需要调整
                Thread.sleep(50);
            }
            //3.从输入流中读取报文体
            byte[] buffer = new byte[mInput.available()];
            retLen = mInput.read(buffer);
            if (retLen != msgLen) {
                LoggerUtils.e("Received data length error! [" + retLen + "," + msgLen + "]");
                return null;
            }
            //4.将报文体存入msg中,返回msg
            System.arraycopy(buffer, 0, msg, 2, buffer.length);
            LoggerUtils.i("Received data length：" + msg.length);
            LoggerUtils.i("Received iso8583 data：" + BytesUtils.bcdToString(msg));
            return msg;
        } catch (Exception e) {
            LoggerUtils.e("Receive data error!");
            e.printStackTrace();
            release();
            return null;
        }
    }

    /**
     * 关闭连接（只有短连接时才会生效）
     */
    public void closeComm() {
        mIsCancel = true;
        if (!mIsLongConnect) {
            release();
        }
    }

    private Socket createSocket() {
        //创建Socket实例
        Socket socket = null;
        if (mCerIs != null) {
            TrustManager[] trustManagers = SslFactoryHelper.generateTrustManagers(mCerIs);
            SSLSocketFactory factory = SslFactoryHelper.getSslSocketFactory(null, trustManagers);
            try {
                socket = Objects.requireNonNull(factory).createSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            socket = new Socket();
        }
        return socket;
    }

    /**
     * 连接服务器
     *
     * @return 返回连接结果
     * <li>true - 连接成功</li>
     * <li>false - 连接失败</li>
     */
    private boolean connect() {
        if (!isConnected()) {
            return connectSocketHost();
        }
        return true;
    }

    /**
     * 判断socket是否已经连接
     *
     * @return 是否连接
     */
    private boolean isConnected() {
        if (mSocket == null || !mSocket.isConnected() || mInput == null || mOutput == null) {
            return false;
        }
        return true;
    }

    /**
     * 连接服务器，真实创建连接实体
     *
     * @return 连接成功
     */
    private boolean connectSocketHost() {
        LoggerUtils.i("Ready connect server:" + mIp + ":" + mPort);
        try {
            //获取SocketAddress对象
            mSocket = createSocket();
            //服务器地址
            SocketAddress mAddress = new InetSocketAddress(mIp, mPort);
            //设置连接超时并连接服务端
            mSocket.connect(mAddress, mTimeOut * 1000);
            mSocket.setKeepAlive(true);
            if (mSocket.isConnected()) {
                //连接成功，获取socket的输入输出流
                mInput = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
                mOutput = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
                LoggerUtils.i("Connected server succ!");
                initLongConnectTimer();
                return true;
            } else {
                throw new Exception("Connect Server error! Check server IP and Port! - 2");
            }
        } catch (Exception e) {
            LoggerUtils.e("Connect Server error! Check server IP and Port! - 1");
            e.printStackTrace();
            mSocket = null;
            mInput = null;
            mOutput = null;
        }
        return false;
    }

    /**
     * 长链接情况下使用，重置通讯起始时间
     * 下次根据通讯时间差判断是否要重新建立连接
     */
    private void initLongConnectTimer() {
        if (mIsLongConnect) {
            mStartTime = System.currentTimeMillis();
        }
    }

    /**
     * 强制释放所有资源
     *
     * @return true 成功，false 失败
     * @author jianshengd
     */
    private boolean release() {
        return release(true);
    }

    /**
     * 强制释放所有资源
     *
     * @param isReConnect 是否重连。长链接情况下有效
     * @return true 成功，false 失败
     */
    private boolean release(boolean isReConnect) {
        LoggerUtils.d("release socket");
        try {
            if (mInput != null) {
                mInput.close();
                mInput = null;
            }
            if (mOutput != null) {
                mOutput.close();
                mOutput = null;
            }
            if (mSocket != null && mSocket.isConnected()) {
                mSocket.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isReConnect && mIsLongConnect) {
                connectSocketHost();
            }
        }
        return false;
    }


    /**
     * byte数组拼接
     *
     * @param args 字节数组
     * @return 组合结果
     */
    private byte[] mergeByteArray(byte[]... args) {
        int arrayLen = 0;
        int copiedLen = 0;
        for (byte[] array : args) {
            arrayLen += array.length;
        }
        byte[] exchangeData = new byte[arrayLen];
        for (byte[] arrayCopy : args) {
            System.arraycopy(arrayCopy, 0, exchangeData, copiedLen, arrayCopy.length);
            copiedLen += arrayCopy.length;
        }

        return exchangeData;
    }
}
