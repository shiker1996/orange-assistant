package tech.shiker.orangetech.panel;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// 自定义聊天气泡组件
public class ChatBubble extends JPanel {
    private static final int MAX_WIDTH = 600;
    private static final int PADDING = 12;
    private static final int AVATAR_SIZE = 32;
    private static final int POINT_WIDTH = 10;  // 尖角宽度
    private static final int POINT_HEIGHT = 14; // 尖角高度
    private static final int POINT_OFFSET = 8;  // 尖角偏移量（从顶部开始）

    private final JLabel textLabel;
    private final JLabel avatarLabel;
    private final JLabel timeLabel;

    public ChatBubble(String text, boolean isUser) {
        super(new GridBagLayout());
        setOpaque(false);

        // 创建文本标签 - 机器人文本使用深色

        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        System.out.println(renderer.render(parser.parse(text)));
        textLabel = new JLabel("<html><div style='width:300px; " +
                               "word-wrap: break-word; " +
                               "overflow-wrap: break-word; " +
                               "white-space: pre-wrap;'>" +
                               renderer.render(parser.parse(text)) + "</div></html>");
        textLabel.setFont(UIManager.getFont("Label.font"));
        textLabel.setForeground(JBColor.white);
        textLabel.setBorder(JBUI.Borders.empty(PADDING));

        // 创建头像
        avatarLabel = new JLabel();
        avatarLabel.setIcon(isUser ?
                IconLoader.getIcon("/META-INF/icon/user.svg", getClass()) :
                IconLoader.getIcon("/META-INF/icon/bot.svg", getClass()));
        avatarLabel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));

        // 创建时间戳
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(JBColor.GRAY);

        // 设置布局
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(2, 4);

        // 主内容区域
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 带尖角的气泡面板
        JPanel bubbleContent = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制气泡背景
                Color fillColor = isUser ?
                        new JBColor(new Color(0, 123, 255), new Color(0, 123, 255)) :
                        Gray._240;
                g2.setColor(fillColor);

                int width = getWidth() - 1;
                int height = getHeight() - 1;

                // 绘制圆角矩形主体
                if (isUser) {
                    // 用户气泡（右侧带尖角）
                    g2.fillRoundRect(0, 0, width - POINT_WIDTH, height, 16, 16);

                    // 绘制气泡尖角（右侧偏上）
                    int pointY = Math.min(POINT_OFFSET, height - POINT_HEIGHT);
                    int[] xPoints = {width - POINT_WIDTH, width, width - POINT_WIDTH};
                    int[] yPoints = {pointY, pointY + POINT_HEIGHT/2, pointY + POINT_HEIGHT};
                    g2.fillPolygon(xPoints, yPoints, 3);
                } else {
                    // 机器人气泡（左侧带尖角）
                    g2.fillRoundRect(POINT_WIDTH, 0, width - POINT_WIDTH, height, 16, 16);

                    // 绘制气泡尖角（左侧偏上）
                    int pointY = Math.min(POINT_OFFSET, height - POINT_HEIGHT);
                    int[] xPoints = {POINT_WIDTH, 0, POINT_WIDTH};
                    int[] yPoints = {pointY, pointY + POINT_HEIGHT/2, pointY + POINT_HEIGHT};
                    g2.fillPolygon(xPoints, yPoints, 3);
                }

                // 绘制气泡边框
                g2.setColor(JBColor.border());
                g2.setStroke(new BasicStroke(1.2f));

                // 绘制圆角矩形边框
                if (isUser) {
                    g2.drawRoundRect(0, 0, width - POINT_WIDTH, height, 16, 16);

                    // 绘制尖角边框
                    int pointY = Math.min(POINT_OFFSET, height - POINT_HEIGHT);
                    g2.drawLine(width - POINT_WIDTH, pointY, width, pointY + POINT_HEIGHT/2);
                    g2.drawLine(width, pointY + POINT_HEIGHT/2, width - POINT_WIDTH, pointY + POINT_HEIGHT);
                } else {
                    g2.drawRoundRect(POINT_WIDTH, 0, width - POINT_WIDTH, height, 16, 16);

                    // 绘制尖角边框
                    int pointY = Math.min(POINT_OFFSET, height - POINT_HEIGHT);
                    g2.drawLine(POINT_WIDTH, pointY, 0, pointY + POINT_HEIGHT/2);
                    g2.drawLine(0, pointY + POINT_HEIGHT/2, POINT_WIDTH, pointY + POINT_HEIGHT);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension textSize = textLabel.getPreferredSize();
                return new Dimension(
                        Math.min(textSize.width, MAX_WIDTH) + POINT_WIDTH, // 为尖角留出空间
                        textSize.height
                );
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(MAX_WIDTH + POINT_WIDTH, Short.MAX_VALUE);
            }
        };
        bubbleContent.setOpaque(false);
        bubbleContent.add(textLabel, BorderLayout.CENTER);

        // 头像和气泡的组合面板
        JPanel contentRow = new JPanel(new GridBagLayout());
        contentRow.setOpaque(false);
        GridBagConstraints rowGbc = new GridBagConstraints();
        rowGbc.insets = JBUI.insetsRight(4);
        rowGbc.gridy = 0;

        if (isUser) {
            // 用户消息：气泡在左，头像在右（上方对齐）
            rowGbc.anchor = GridBagConstraints.FIRST_LINE_END;
            rowGbc.weightx = 1.0;
            contentRow.add(bubbleContent, rowGbc);

            rowGbc.anchor = GridBagConstraints.FIRST_LINE_START;
            rowGbc.weightx = 0.0;
            rowGbc.insets = JBUI.emptyInsets();
            contentRow.add(avatarLabel, rowGbc);
        } else {
            // 机器人消息：头像在左，气泡在右（上方对齐）
            rowGbc.anchor = GridBagConstraints.FIRST_LINE_END;
            rowGbc.weightx = 0.0;
            rowGbc.insets = JBUI.emptyInsets();
            contentRow.add(avatarLabel, rowGbc);

            rowGbc.anchor = GridBagConstraints.FIRST_LINE_START;
            rowGbc.weightx = 1.0;
            rowGbc.insets = JBUI.insetsRight(4);
            contentRow.add(bubbleContent, rowGbc);
        }

        // 添加时间标签和内容行
        gbc.gridy = 0;
        gbc.anchor = isUser ? GridBagConstraints.LINE_END : GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        add(timeLabel, gbc);

        gbc.gridy = 1;
        gbc.anchor = isUser ? GridBagConstraints.LINE_END : GridBagConstraints.LINE_START;
        add(contentRow, gbc);
    }
}