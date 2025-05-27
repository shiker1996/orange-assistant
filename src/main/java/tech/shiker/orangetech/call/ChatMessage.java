package tech.shiker.orangetech.call;

public class ChatMessage {
    public String text;
    public boolean isUser;

    public ChatMessage() {
    }

    public ChatMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }
}
