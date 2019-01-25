package com.utils.comm;

import com.nlutils.util.BytesUtils;
import com.nlutils.util.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * HTTP通讯工具
 * POST或GE
 *
 * @author jianshengd
 * @date 2018/2/19
 */
public class HttpOkHelper {
    public final static int IO_ERR = -1000;
    private String mUrl;
    private Call mCall;
    private OkHttpClient mOkHttpClient;

    public HttpOkHelper(String url, int timeout) {
        this(url, timeout, null);
    }

    /**
     * @param url     服务器地址
     * @param timeout 超时时间
     * @param cerIs   证书信息输入流，为空表示无证书
     */
    public HttpOkHelper(String url, int timeout, InputStream cerIs) {
        this.mUrl = url;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置连接超时时间
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        builder.cache(null);
        if (cerIs != null) {
            //证书处理
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslsession) {
                    return true;
                }
            });
            TrustManager[] trustManagers = SslFactoryHelper.generateTrustManagers(cerIs);
            SSLSocketFactory sslSocketFactory = SslFactoryHelper.getSslSocketFactory(null, trustManagers);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0]);
        } else {

            if (url.startsWith("https:")) {
                try {
                    builder.sslSocketFactory(SSLContext.getDefault().getSocketFactory(), (X509TrustManager) SslFactoryHelper.generateDefaultTrustManagers());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        builder.addNetworkInterceptor(logInterceptor);
        this.mOkHttpClient = builder.build();
    }

    /**
     * post,表单类型
     *
     * @param map      key-value
     * @param listener 结果回调
     */
    public void httpPostForm(Map<String, String> map, IHttpOkListener listener) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        // 发送的内容
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            bodyBuilder.add(key, map.get(key));
        }
        FormBody formBody = bodyBuilder.build();

        Request.Builder requetBuilder = new Request.Builder().url(mUrl);
        requetBuilder.post(formBody);
        Request request = requetBuilder.build();
        mCall = mOkHttpClient.newCall(request);
        asyCallExe(listener);
    }

    /**
     * post,JSON类型
     *
     * @param json     JSON数据
     * @param listener 结果回调
     */
    public void httpPostJson(String json, IHttpOkListener listener) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);

        Request.Builder requetBuilder = new Request.Builder().url(mUrl);
        requetBuilder.post(requestBody);
        Request request = requetBuilder.build();
        mCall = mOkHttpClient.newCall(request);
        asyCallExe(listener);
    }

    /**
     * post,字节数据
     *
     * @param data     字节数据
     * @param listener 结果回调
     */
    public void httpPostByte(byte[] data, IHttpOkListener listener) {
        MediaType mediaType = MediaType.parse("application/octet-stream; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, data);

        Request.Builder requetBuilder = new Request.Builder().url(mUrl);
        requetBuilder.post(requestBody);

        Request request = requetBuilder.build();
        mCall = mOkHttpClient.newCall(request);
        asyCallExe(listener);
    }

    /**
     * 发送银联8583报文格式字节数据，银联要求Content-Type:x-ISO-TPDU/x-auth
     *
     * @param data     发送数据
     * @param listener 结果回调
     */
    public void httpPostIsoByte(byte[] data, IHttpOkListener listener) {
        MediaType mediaType = MediaType.parse("x-ISO-TPDU/x-auth");
        RequestBody requestBody = RequestBody.create(mediaType, data);

        Request.Builder requetBuilder = new Request.Builder().url(mUrl);
        requetBuilder.post(requestBody);

        Request request = requetBuilder.build();
        mCall = mOkHttpClient.newCall(request);
        asyCallExe(listener);
    }

    /**
     * post,文件
     *
     * @param file     文件
     * @param listener 结果回调
     */
    public void httpPostFile(File file, IHttpOkListener listener) {
        //参考:http://www.w3school.com.cn/media/media_mimeref.asp  [Mime类型列表]
        String fileType = "image/png";
        MultipartBody multBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(fileType), file)).build();

        Request.Builder requetBuilder = new Request.Builder().url(mUrl);
        requetBuilder.post(multBody);
        Request request = requetBuilder.build();
        mCall = mOkHttpClient.newCall(request);
        asyCallExe(listener);
    }

    /**
     * get方式通讯
     *
     * @param sendData 参数
     * @param listener 结果回调
     */
    public void httpGet(String sendData, IHttpOkListener listener) {
        //域名+参数
        String getUrl = mUrl + sendData;
        Request.Builder builder = new Request.Builder().url(getUrl);
        //builder.header(name, value);	//header添加参数,例如Cookie，User-Agent
        Request request = builder.build();
        mCall = mOkHttpClient.newCall(request);
        asyCallExe(listener);
    }

    /**
     * 同步发送方式,阻塞
     *
     * @param listener 结果回调
     */
    @SuppressWarnings("unused")
    private void synCallExe(IHttpOkListener listener) {
        try {
            Response response = mCall.execute();
            if (response == null) {
                LoggerUtils.e("Respone is null");
                listener.onFailure(IO_ERR, "Respone is null");
            } else if (response.code() != HttpURLConnection.HTTP_OK) {
                LoggerUtils.e("response err:" + response.code() + " " + response.message());
                listener.onFailure(response.code(), response.message());
            } else {    //成功
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    listener.onFailure(IO_ERR, "Respone Body is null");
                    return;
                }
                byte[] body = responseBody.bytes();
                LoggerUtils.i("response:" + BytesUtils.bcdToString(body));
                listener.onSucc(body);
            }
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(IO_ERR, "IOException:" + e.getMessage());
        }
    }

    /**
     * 异步发送方式，不阻塞
     *
     * @param listener 结果回调
     */
    private void asyCallExe(final IHttpOkListener listener) {
        mCall.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != HttpURLConnection.HTTP_OK) {
                    LoggerUtils.e("response err:" + response.code() + " " + response.message());
                    listener.onFailure(response.code(), response.message());
                } else {    //成功
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        listener.onFailure(IO_ERR, "Respone Body is null");
                        return;
                    }
                    byte[] body = responseBody.bytes();
                    LoggerUtils.i("response:" + BytesUtils.bcdToString(body));
                    listener.onSucc(body);
                }
            }

            @Override
            public void onFailure(Call call, IOException ioexception) {
                ioexception.printStackTrace();
                listener.onFailure(IO_ERR, "IOException:" + ioexception.getMessage());
            }
        });
    }

    /**
     * 终止通讯
     */
    public void abort() {
        if (mCall != null) {
            mCall.cancel();
        }
    }


}
