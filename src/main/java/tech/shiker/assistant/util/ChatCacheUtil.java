package tech.shiker.assistant.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.shiker.assistant.call.ChatMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatCacheUtil {
    private static final Path CACHE_FILE = Paths.get(System.getProperty("user.home"), ".ai_chat_cache.json");
    private static final int MAX_RECORDS = 100;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<ChatMessage> loadMessages() {
        if (!Files.exists(CACHE_FILE)) return new ArrayList<>();
        try {
            ChatMessage[] arr = mapper.readValue(Files.readAllBytes(CACHE_FILE), ChatMessage[].class);
            return new ArrayList<>(Arrays.asList(arr)); // ✅ 返回可变列表
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveMessage(ChatMessage message) {
        List<ChatMessage> history = loadMessages();
        history.add(message);
        if (history.size() > MAX_RECORDS) {
            history = history.subList(history.size() - MAX_RECORDS, history.size());
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(CACHE_FILE.toFile(), history);
        } catch (IOException e) {
            // Optional: log error
        }
    }
}
