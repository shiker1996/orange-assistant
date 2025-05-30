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
        // 获取 Cipher 实例（GCM 模式 + NoPadding）
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        // 解码 base64
        byte[] encryptedBytes = Base64.getDecoder().decode(base64EncryptedText);
        // 提取 IV（假设 IV 是前 12 字节）
        byte[] iv = new byte[12];
        System.arraycopy(encryptedBytes, 0, iv, 0, 12);
        // 提取实际加密数据
        byte[] cipherText = new byte[encryptedBytes.length - 12];
        System.arraycopy(encryptedBytes, 12, cipherText, 0, cipherText.length);
        // 初始化 Cipher
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        // 解密
        byte[] originalBytes = cipher.doFinal(cipherText);
        return new String(originalBytes, StandardCharsets.UTF_8);
    }

}
