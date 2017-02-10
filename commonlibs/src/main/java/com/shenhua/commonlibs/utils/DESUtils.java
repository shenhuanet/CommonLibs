package com.shenhua.commonlibs.utils;

import com.sun.crypto.provider.SunJCE;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密类
 * Created by shenhua on 9/9/2016.
 */
public class DESUtils {

    private static String strDefaultKey = "shenhua";
    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;

    public static DESUtils getInstance() {
        return new DESUtils();
    }

    public DESUtils() {
        this(strDefaultKey);
    }

    public DESUtils(String strDefaultKey) {
        Security.addProvider(new SunJCE());
        Key key = getKey(strDefaultKey.getBytes());
        try {
            encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strIn) {
        try {
            return byteArr2HexStr(encrypt(strIn.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public byte[] encrypt(byte[] arrB) {
        try {
            return encryptCipher.doFinal(arrB);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String strIn) {
        try {
            return new String(decrypt(hexStr2ByteArr(strIn)));
        } catch (Exception e) {
            return "";
        }
    }

    public byte[] decrypt(byte[] arrB) {
        try {
            return decryptCipher.doFinal(arrB);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Key getKey(byte[] bytes) {
        byte[] arrB = new byte[8];
        for (int i = 0; i < bytes.length && i < arrB.length; i++) {
            arrB[i] = bytes[i];
        }
        return new SecretKeySpec(arrB, "DES");
    }

    private String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        StringBuilder sb = new StringBuilder(iLen * 2);
        for (byte anArrB : arrB) {
            int intTmp = anArrB;
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    private byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

}
