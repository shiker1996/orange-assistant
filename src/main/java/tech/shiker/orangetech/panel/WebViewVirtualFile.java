package tech.shiker.orangetech.panel;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.util.Key;
import com.intellij.testFramework.LightVirtualFile;

public class WebViewVirtualFile extends LightVirtualFile {
    public static final Key<String> URL_KEY = Key.create("WEBVIEW_URL");

    public WebViewVirtualFile(String name, String url) {
        super(name + ".post", PlainTextFileType.INSTANCE, "");
        putUserData(URL_KEY, url);
    }
}