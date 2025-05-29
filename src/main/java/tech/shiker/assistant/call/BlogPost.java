package tech.shiker.assistant.call;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlogPost {
    public String title;
    public String summary;
    public String fullPath;

    @Override
    public String toString() {
        return title;
    }
}
