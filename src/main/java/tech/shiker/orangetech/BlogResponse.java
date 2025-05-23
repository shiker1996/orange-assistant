package tech.shiker.orangetech;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlogResponse {
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
