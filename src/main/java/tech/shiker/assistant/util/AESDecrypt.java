package tech.shiker.assistant.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESDecrypt {
    /**
     * 虽然解密了也没啥用，但还是加个密吧
     * @param base64EncryptedText
     * @param key
     * @return
     * @throws Exception
     */
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

}
