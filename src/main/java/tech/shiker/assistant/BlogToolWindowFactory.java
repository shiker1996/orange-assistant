package tech.shiker.assistant;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import tech.shiker.assistant.component.AiComponent;
import tech.shiker.assistant.component.PostComponent;

public class BlogToolWindowFactory implements ToolWindowFactory {
    private static final PostComponent postComponent = new PostComponent();
    private static final AiComponent aiComponent = new AiComponent();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        postComponent.postList(project, toolWindow);
        aiComponent.addAIChatTab(toolWindow);
    }
}
