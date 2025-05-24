package tech.shiker.orangetech.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESDecrypt {

    public static String encrypt(String src, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(src.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String base64EncryptedText, String key) throws Exception {


        // 转换密钥
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        // 获取 Cipher 实例（ECB 模式 + PKCS5Padding）
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // 解码 base64
        byte[] encryptedBytes = Base64.getDecoder().decode(base64EncryptedText);

        // 解密
        byte[] originalBytes = cipher.doFinal(encryptedBytes);

        return new String(originalBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        String encryptedText = "https://ai.shiker.tech/api/v1/prediction/0b06f19b-f2f4-4727-8cae-c9524776e43a"; // 这是加密后的 Base64 字符串
        String key = "shiker.tech"; // 16 字节密钥（128 位）

        String decrypted = encrypt(encryptedText, Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8)));
        System.out.println("result:" + decrypted);
        String decryptedText = decrypt(decrypted, Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8)));
        System.out.println("result:" + decryptedText);
    }

}
