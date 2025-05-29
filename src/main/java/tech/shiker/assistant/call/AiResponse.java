package tech.shiker.assistant.call;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponse {
    public String text;
}

