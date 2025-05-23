package tech.shiker.orangetech;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.ui.Messages;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Collections;
import java.util.ResourceBundle;

public class BlogService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final ResourceBundle bundle = ResourceBundle.getBundle("META-INF.OrangeTechBundle");

    public BlogResponse.BlogData getBlogData(Integer page, String keyword) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://shiker.tech/api/content/posts?page=" + page + "&size=10&keyword=" + keyword + "&sort=topPriority%2CcreateTime%2Cdesc")
                    .addHeader("api-authorization", "joe2.0")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    String json = response.body().string();
                    System.err.println(json);
                    BlogResponse blogResponse = objectMapper.readValue(json, BlogResponse.class);
                    return blogResponse.data;
                }
            }
        } catch (Exception e) {
            Messages.showMessageDialog("主站异常，请反馈插件管理员：" + e.getMessage(), bundle.getString(BlogConstants.TITLE), Messages.getInformationIcon());
        }
        return null;
    }

}
