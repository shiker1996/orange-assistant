package tech.shiker.orangetech;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

public class BlogToolWindowFactory implements ToolWindowFactory {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("META-INF.OrangeTechBundle");
    private int currentPage = 0;
    private String keyword = "";
    private JTextField searchField;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel listPanel;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(JBUI.Borders.empty());

        // 🔍 搜索区域（顶部）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(JBUI.Borders.empty(8, 8, 4, 8));

        // 搜索输入框
        searchField = new JTextField();
        searchField.setToolTipText("输入关键词");

        // 放大镜按钮
        JButton searchButton = new JButton(AllIcons.Actions.Find);
        searchButton.setToolTipText("点击搜索");
        searchButton.setFocusable(false);
        searchButton.setPreferredSize(new Dimension(28, 28));
        searchButton.addActionListener(e -> {
            keyword = searchField.getText();
            currentPage = 0;
            reloadData(project);
        });

        // 重置按钮
        JButton resetButton = new JButton(AllIcons.Actions.Refresh);
        resetButton.setFocusable(false);
        resetButton.setPreferredSize(new Dimension(28, 28));
        resetButton.addActionListener(e -> {
            searchField.setText("");
            keyword = "";
            currentPage = 0;
            reloadData(project);
        });

        // 搜索框 + 图标 + 重置组合
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(JBUI.Borders.customLine(JBColor.border(), 1));
        searchPanel.setBackground(JBColor.background());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel searchRowPanel = new JPanel(new BorderLayout());
        searchRowPanel.add(searchPanel, BorderLayout.CENTER);
        searchRowPanel.add(resetButton, BorderLayout.EAST);

        topPanel.add(searchRowPanel, BorderLayout.CENTER);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // 📄 内容区域
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(JBColor.WHITE);

        JScrollPane scrollPane = new JBScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // ⬅️➡️ 分页按钮区域
        // ⬅️🏠➡️ 分页和主页按钮区域（底部固定）
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(JBUI.Borders.empty(4, 8, 8, 8));

        // 创建主页按钮
        JButton homeButton = new JButton("🏠 主页");
        homeButton.addActionListener(e -> {
            currentPage = 0;
            searchField.setText("");
            keyword = "";
            reloadData(project);
        });

        // 创建上一页、下一页按钮
        prevButton = new JButton("⬅️ 上一页");
        nextButton = new JButton("下一页 ➡️");

        prevButton.addActionListener(e -> {
            currentPage--;
            reloadData(project);
        });
        nextButton.addActionListener(e -> {
            currentPage++;
            reloadData(project);
        });

        // 添加按钮到面板
        bottomPanel.add(homeButton);
        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);

        // 添加到底部
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);


        // 添加内容到工具窗口
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(contentPanel, bundle.getString(BlogConstants.TITLE), false);
        toolWindow.getContentManager().addContent(content);

        reloadData(project);
    }

    private void reloadData(Project project) {
        BlogService service = new BlogService();
        BlogResponse.BlogData result = service.getBlogData(currentPage, keyword);

        listPanel.removeAll();

        for (BlogPost post : result.content) {
            listPanel.add(createPostComponent(project, post));
        }

        prevButton.setVisible(result.hasPrevious);
        nextButton.setVisible(result.hasNext);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createPostComponent(Project project, BlogPost post) {
        JPanel postPanel = new JPanel(new BorderLayout());
        postPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        postPanel.setBackground(JBColor.WHITE);

        JLabel titleLabel = new JLabel("<html><a href='#'>" + post.title + "</a></html>");
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleLabel.setForeground(new JBColor(new Color(0x1a0dab), new Color(0xffffff)));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                WebViewHelper.openWebView(project, post.fullPath, post.title);
            }
        });

        JTextArea summaryArea = new JTextArea(post.summary);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setEditable(false);
        summaryArea.setBackground(JBColor.WHITE);
        summaryArea.setBorder(null);
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        postPanel.add(titleLabel, BorderLayout.NORTH);
        postPanel.add(summaryArea, BorderLayout.CENTER);

        return postPanel;
    }
}
