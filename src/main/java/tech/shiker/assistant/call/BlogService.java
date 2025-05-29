package tech.shiker.assistant.call;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.ui.Messages;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.shiker.assistant.util.AESDecrypt;
import tech.shiker.assistant.util.SignatureUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;

public class BlogService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final ResourceBundle bundle = ResourceBundle.getBundle("META-INF.OrangeTechBundle");

    public BlogResponse.BlogData getBlogData(Integer page, String keyword) {
        try {
            String decryptKey = Base64.getEncoder().encodeToString(BlogConstants.SITE.getBytes(StandardCharsets.UTF_8));
            OkHttpClient client = new OkHttpClient();
            String timestamp = String.valueOf(System.currentTimeMillis());
            Request request = new Request.Builder()
                    .url(AESDecrypt.decrypt(BlogConstants.BLOG_URL, decryptKey) + page + "&size=10&keyword=" + keyword + "&sort=topPriority%2CcreateTime%2Cdesc")
                    .addHeader("API-Authorization", AESDecrypt.decrypt(BlogConstants.NONCE, decryptKey))
                    .addHeader("API-Timestamp", timestamp)
                    .addHeader("API-signature", SignatureUtil.generateSignature(timestamp, AESDecrypt.decrypt(BlogConstants.NONCE, decryptKey), AESDecrypt.decrypt(BlogConstants.SITE_URL,decryptKey)))
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    String json = response.body().string();
                    BlogResponse blogResponse = objectMapper.readValue(json, BlogResponse.class);
                    if (blogResponse.status != 200){
                        Messages.showMessageDialog("主站异常，请反馈插件管理员：" + blogResponse.message, bundle.getString(BlogConstants.TITLE), Messages.getInformationIcon());
                    } else {
                        return blogResponse.data;
                    }
                }
            }
        } catch (Exception e) {
            Messages.showMessageDialog("主站异常，请反馈插件管理员：" + e.getMessage(), bundle.getString(BlogConstants.TITLE), Messages.getInformationIcon());
        }
        return new BlogResponse.BlogData();
    }

}
