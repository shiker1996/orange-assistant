package tech.shiker.assistant.call;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlogResponse {
    public String message;

    public Integer status;

    public BlogData data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BlogData {
        public List<BlogPost> content;

        public Integer pages;

        public Integer total;

        public Integer page;

        public boolean hasNext;

        public boolean hasPrevious;
    }
}
