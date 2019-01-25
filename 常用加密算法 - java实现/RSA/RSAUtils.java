package com.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author zhanghongbin on 2018/8/13 0013
 */
public class RSAUtils {


    private static final String CHARSET = "UTF-8";
    private static final String RSA_ALGORITHM = "RSA";

    /**
     * 生成公钥、私钥
     */
    private static Map<String, String> createKeys(int keySize) {        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>(2);
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }

    /**
     * 得到公钥
     */
    private static RSAPublicKey getPublicKey(byte[] publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String data, byte[] publicKey) {
        try {
            RSAPublicKey key = getPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64String(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), key.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String data, String privateKey) {
        try {
            RSAPrivateKey key = getPrivateKey(privateKey);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), key.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     */
    private static String privateEncrypt(String data, String privateKey) {
        try {
            RSAPrivateKey key = getPrivateKey(privateKey);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64String(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), key.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     */
   public static String  publicDecrypt(byte[] bytes, byte[] publicKey) {
        try {
            RSAPublicKey key = getPublicKey(publicKey);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, bytes, key.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + bytes + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    public static void main(String[] args) {
        //int size = 2048;
        //Map<String, String> keyMap = RSAUtils.createKeys(size);
        //String publicKey = keyMap.get("publicKey");
        //String privateKey = keyMap.get("privateKey");
        //
        //logger.info("公钥：\r\n{}", publicKey);
        //logger.info("私钥：\r\n{}", privateKey);
    }

    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmjUvuLwqw4OuHZp7sXf715DI7pMlP39k8itYl76Sr5je/WoYYIXWkFC/Bt3xQZtNRHJr+/ulL4miegnLEAsNeYBy1E3ZeFiZwT6/xfa7cQuA7+JG2PhKiE0wiG/Yzfr+tHRrGdYlz2jQcTdopK7Gp1OF3uRky4pDd0Wjn4XxgdUSran6eJRLtJJYTZTEx0+3YPSNbp00KOKTDJ+NiL8htmD+chlKO7AwnitW4/yp01E7nsX1jtM6MKjrXlxK+CaLgaGKb72f3FIowXVhCDJ4tfBRX1jej1bjLFUzMaYRGNL9ivRz487VNtQGrOPOlmXypNIxqfGUMcjJA07C0zvzdwIDAQAB";

    private static final String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCaNS+4vCrDg64dmnuxd/vXkMjukyU/f2TyK1iXvpKvmN79ahhghdaQUL8G3fFBm01Ecmv7+6UviaJ6CcsQCw15gHLUTdl4WJnBPr/F9rtxC4Dv4kbY+EqITTCIb9jN+v60dGsZ1iXPaNBxN2ikrsanU4Xe5GTLikN3RaOfhfGB1RKtqfp4lEu0klhNlMTHT7dg9I1unTQo4pMMn42IvyG2YP5yGUo7sDCeK1bj/KnTUTuexfWO0zowqOteXEr4JouBoYpvvZ/cUijBdWEIMni18FFfWN6PVuMsVTMxphEY0v2K9HPjztU21Aas486WZfKk0jGp8ZQxyMkDTsLTO/N3AgMBAAECggEAVQZ0XJBrp8eoLixqfSyh0HbFQz4gsIUmfJxHyctLB8TrVqCz9ix1ZLcQzNF832ao0M+iGJrktnRWAKH7s8nUUSTwq3yNJtz7UeCuTpG/QhJMsnd6BtHIDqU42FGLIe99lrQGBYXc7SJmo7YgXYT8hW/5BS+7H7sJfe8Iy8FHqwSb9+pHcvmPGpKTAyzVBKSsPicGWhCAFq8hnJdpIl9cgfbjLDAXvICttV44Ayz/os56GQrT0KvfiaOl9KU9B2avmvYBfmOexLx++luvR1VlpfxijcZoSFazbCgLrMygVmzK855GSdcjOvvoAnNKVR6yM8fMcFMEcdPAAlqe20V1mQKBgQDWrz4U3jtUAhYWbH0cP3JzinGIUVrBXZeQNr6iKqQzi0QP5qaz1jWvAxSXvJ8N4nh19KZp8zM/XMqZ63YeHETLbf17y3zmt/FdfyHKMZ5GhtiYjt/k0O/TncC3OHJJBA9Rn76wwF6UaR2Qqd42s8I83b/fxaG2tAJJxLIHdAN4RQKBgQC34nbPxD0Gi6SilccZPhEaePcwUoIPODPAG6zkeAKGYnY11DCYclr1EcM3nAbaaBfPosqPZtVGfKY38UAmuBtebQWvopR/MCEmAYw/M8dC9Rr9vR4sLGBGhRVac1LRZ20GYOAnPgJ01ONvB++3v6mziRiRIKtHWYPq6/zhwbxuiwKBgAZNdsEeFRfOR9+4p6OgqTeDJPofiU6L9NsjAE26er+YoxLkbphw/Xp52KzruK+rqmWsHQaXQTXm/3utUfZoVgyy2fI8cxpC5C8egFtzQXZIlytDZodNKv1HcFTwr0o9eEWpLuv5ec1ZmKialA9/D4DB8dQb9Tdr6XVI9QELAt8RAoGBAJGBt0hy3WNXjsBEytZzoa8T924aa+ZFL9YdUDM9r6nNMgMoVAfdvZHdK1/OrDGZWvYnuCe/VJbBPfpYgc3hs26F0JEJkn4OhLyBTwwYGd4m++O7CawktU44Mwxu4yLmt4PrlpQW4nS2nP2YBx2coVl666CzfDUqZVO4x1ORLV0/AoGBAIzaB5ZG3WSlw5AbzChDUA8ALKlkoexleL+GI0h9PvqvTR8py8izp9+VtM8Wd97KVjN8FWCQOvT4vfmmbW9IAy9fp75/AH5jcQbY6eO2pH/MIpEHqO11b7000ImGNF3DRN/g1b2yDAHt/xcbEvV9c+gWRxMRK92qODj1CcAu7vcP";

}
