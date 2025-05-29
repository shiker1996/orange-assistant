package tech.shiker.assistant.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.WrapLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.groovy.util.Maps;
import tech.shiker.assistant.call.AiResponse;
import tech.shiker.assistant.call.BlogConstants;
import tech.shiker.assistant.call.ChatMessage;
import tech.shiker.assistant.panel.ChatBubble;
import tech.shiker.assistant.util.AESDecrypt;
import tech.shiker.assistant.util.ChatCacheUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class AiComponent {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("META-INF.OrangeTechBundle");

    public void addAIChatTab(ToolWindow toolWindow) {
        String decryptKey = Base64.getEncoder().encodeToString(BlogConstants.SITE.getBytes(StandardCharsets.UTF_8));
        JPanel aiPanel = new JPanel(new BorderLayout());
        aiPanel.setBorder(JBUI.Borders.empty(10));

        // 聊天内容区域 - 使用 GridBagLayout 替代 BoxLayout
        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setBackground(JBColor.PanelBackground);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);

        JScrollPane scrollPane = new JBScrollPane(messagePanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 输入区域
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("发送");

        List<ChatMessage> history = ChatCacheUtil.loadMessages();
        for (ChatMessage msg : history) {
            ChatBubble bubble = new ChatBubble(msg.text, msg.isUser);
            gbc.gridy++;
            messagePanel.add(bubble, gbc);
        }

        // 发送按钮事件
        Runnable sendMessage = () -> {
            String userInput = inputField.getText().trim();
            if (userInput.isEmpty()) return;

            ChatBubble userBubble = new ChatBubble(userInput, true);
            gbc.gridy++;
            messagePanel.add(userBubble, gbc);
            ChatCacheUtil.saveMessage(new ChatMessage(userInput, true));
            inputField.setText("");
            scrollToBottom(scrollPane);

            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .build();

                    String json = "{\"question\": \"" + userInput.replace("\"", "\\\"") + "\"}";
                    RequestBody body = RequestBody.create(json, MediaType.get("application/json"));


                    Request request = new Request.Builder()
                            .url(AESDecrypt.decrypt(BlogConstants.AI_URL, decryptKey))
                            .post(body)
                            .addHeader("Content-Type", "application/json")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ApplicationManager.getApplication().invokeLater(() -> {
                                ChatBubble errorBubble = new ChatBubble("出错了：" + e.getMessage(), false);
                                gbc.gridy++;
                                messagePanel.add(errorBubble, gbc);
                                ChatCacheUtil.saveMessage(new ChatMessage("出错了：" + e.getMessage(), false));
                                messagePanel.revalidate();
                                scrollToBottom(scrollPane);
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful() && response.body() != null) {
                                ObjectMapper mapper = new ObjectMapper();
                                AiResponse aiResponse = mapper.readValue(response.body().string(), AiResponse.class);
                                ApplicationManager.getApplication().invokeLater(() -> {
                                    ChatBubble aiBubble = new ChatBubble(aiResponse.text, false);
                                    gbc.gridy++;
                                    messagePanel.add(aiBubble, gbc);
                                    ChatCacheUtil.saveMessage(new ChatMessage(aiResponse.text, false)); // 新增这一行
                                    messagePanel.revalidate();
                                    messagePanel.repaint();
                                    scrollToBottom(scrollPane);
                                });
                            }
                        }
                    });
                } catch (Exception ex) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ChatBubble errorBubble = new ChatBubble("出错了：" + ex.getMessage(), false);
                        gbc.gridy++;
                        messagePanel.add(errorBubble, gbc);
                        ChatCacheUtil.saveMessage(new ChatMessage("出错了：" + ex.getMessage(), false));
                        messagePanel.revalidate();
                        scrollToBottom(scrollPane);
                    });
                }
            });
        };

        // 按下回车键发送消息
        inputField.addActionListener(e -> sendMessage.run());
        // 按按钮发送消息
        sendButton.addActionListener(e -> sendMessage.run());

        // 提示标签
        JLabel tipLabel = new JLabel("<html><a href='https://shiker.tech/s/assistant'>想要获取详细解答，请访问 https://shiker.tech/s/assistant</a></html>");
        tipLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        tipLabel.setForeground(JBColor.GRAY);

        // 支持点击打开链接（可选）
        tipLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tipLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse("https://shiker.tech/s/assistant");
            }
        });

        // 输入+按钮区域
        JPanel inputBar = new JPanel(new BorderLayout(5, 5));
        inputBar.add(inputField, BorderLayout.CENTER);
        inputBar.add(sendButton, BorderLayout.EAST);

        // === 新增：快捷发送按钮区域 ===
        JPanel quickButtonsPanel = new JPanel(new GridLayout(1, 3, 5, 5)); // 1行N列，水平间距5px
        Map<String, String> buttons = Maps.of("🐉   成语接龙", "成语接龙", "🏮   猜谜语", "猜谜语", "🎓   模拟面试", "模拟面试");

        for (String button : buttons.keySet()) {
            JButton quickBtn = new JButton(button);
            quickBtn.addActionListener(e -> {
                inputField.setText(buttons.get(button));
                sendMessage.run();
            });
            quickBtn.setPreferredSize(new Dimension(120, 32));
            quickBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32)); // 自动拉伸
            quickButtonsPanel.add(quickBtn);
        }

        // 输入区域整体布局（按钮 + 输入栏 + 提示）
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(JBUI.Borders.emptyTop(4));
        inputPanel.add(quickButtonsPanel);        // 添加快捷按钮栏
        inputPanel.add(inputBar);                 // 输入框+发送按钮
        inputPanel.add(Box.createVerticalStrut(3));
        inputPanel.add(tipLabel);

        aiPanel.add(scrollPane, BorderLayout.CENTER);
        aiPanel.add(inputPanel, BorderLayout.SOUTH);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content aiContent = contentFactory.createContent(aiPanel, bundle.getString(BlogConstants.AI), false);
        toolWindow.getContentManager().addContent(aiContent);
    }

    // 辅助方法：滚动到ScrollPane底部
    private void scrollToBottom(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }
}
