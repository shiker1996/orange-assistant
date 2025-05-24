package tech.shiker.orangetech.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import tech.shiker.orangetech.panel.WebViewVirtualFile;

public class WebViewHelper {

    public static void openWebView(Project project, String url, String title) {
        WebViewVirtualFile virtualFile = new WebViewVirtualFile(title, url);
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }
}
