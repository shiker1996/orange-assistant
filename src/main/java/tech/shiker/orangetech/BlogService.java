package tech.shiker.orangetech;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Collections;

public class BlogService {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            System.err.println("博客请求失败: " + e.getMessage());
        }
        return null;
    }

}
