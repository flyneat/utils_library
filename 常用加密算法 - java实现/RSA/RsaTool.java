package com.utils;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import com.nlutils.util.BytesUtils;

import org.apache.commons.io.IOUtils;

/**
 * RSA 加密工具
 *
 * @author jianshengd
 * @date 2018/3/18
 */
public class RsaTool {
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * RSA公钥加密
     *
     * @param bPubkey 公钥
     * @param data    被加密数据
     * @return 公钥加密结果
     * @throws Exception 加密异常
     */
    public static byte[] rsaPubkey(byte[] bPubkey, byte[] data) throws Exception {
        PublicKey publicKey = getPublicKey(bPubkey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);

    }

    /**
     * RSA私钥解密
     *
     * @param bPriKey 私钥
     * @param data    被解密数据
     * @return 私钥解密结果
     * @throws Exception 解密异常
     */
    public static byte[] unRsaPrivate(byte[] bPriKey, byte[] data) throws Exception {
        PrivateKey privateKe = getPrivateKey(bPriKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKe);
        return cipher.doFinal(data);

    }

    /**
     * RSA私钥加密
     *
     * @param bPriKey 私钥
     * @param data    被加密数据
     * @return 私钥加密结果
     * @throws Exception 加密异常
     */
    public static byte[] rsaPrivate(byte[] bPriKey, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKey(bPriKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * RSA公钥解密
     *
     * @param bPubKey 公钥
     * @param data    被解密数据
     * @return 公钥解密结果
     * @throws Exception 解密异常
     */
    public static byte[] unRsaPubkey(byte[] bPubKey, byte[] data) throws Exception {
        PublicKey publicKey = getPublicKey(bPubKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsaSplitCodec(cipher,Cipher.ENCRYPT_MODE,data,data.length);
    }

    public static byte[] rsaSplitCodec(Cipher cipher,int opmode, byte[] data, int keySize) {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        try {
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        byte[] decryptedData = out.toByteArray();
        IOUtils.closeQuietly(out);
        return decryptedData;
    }

    /**
     * 根据字符串生成PublicKey对象
     *
     * @param bPubkey 公钥
     * @return 公钥对象
     * @throws Exception 异常
     */
    private static PublicKey getPublicKey(byte[] bPubkey) throws Exception {
        X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(bPubkey);
        // RSA对称加密算法
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 取公钥匙对象
        return keyFactory.generatePublic(bobPubKeySpec);
    }

    /**
     * 根据字符串生成PrivateKey对象
     *
     * @param bPriKey 私钥
     * @return 私钥对象
     * @throws Exception 过程异常
     */
    private static PrivateKey getPrivateKey(byte[] bPriKey) throws Exception {
        PKCS8EncodedKeySpec bobPriKeySpec = new PKCS8EncodedKeySpec(bPriKey);
        // RSA对称加密算法
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 取公钥匙对象
        return keyFactory.generatePrivate(bobPriKeySpec);
    }

    /**
     * 获取RSA秘钥对
     *
     * @return 公钥，私钥
     */
    public static String[] getKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            //获取公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            String strPubKey = BytesUtils.bcdToString(publicKey.getEncoded());
            //获取私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            String strPriKey = BytesUtils.bcdToString(privateKey.getEncoded());
            return new String[]{strPubKey, strPriKey};
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
