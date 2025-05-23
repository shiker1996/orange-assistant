package tech.shiker.orangetech;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

public class WebViewHelper {

    public static void openWebView(Project project, String url, String title) {
        WebViewVirtualFile virtualFile = new WebViewVirtualFile(title, url);
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
        System.out.println("Opening virtual file: " + virtualFile.getName() + " with URL: " + virtualFile.getUserData(WebViewVirtualFile.URL_KEY));
    }
}
