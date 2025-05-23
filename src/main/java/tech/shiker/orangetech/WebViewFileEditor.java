package tech.shiker.orangetech;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class WebViewFileEditor extends UserDataHolderBase implements FileEditor {
    private final JBCefBrowser browser;

    public WebViewFileEditor(String url) {
        this.browser = new JBCefBrowser(url);
        browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                if (frame.isMain()) {
                    String js = """
                                if (document.readyState === 'loading') {
                                    document.addEventListener('DOMContentLoaded', () => {
                                        const button = document.querySelector('.joe_action_item.mode');
                                        if (button) {
                                            const icon = button.querySelector('.mode-light');
                                            if (icon && !icon.classList.contains('active')) {
                                              button.click();
                                            }
                                        }
                                        const el = document.querySelector('.joe_post');
                                        if (el) {
                                            document.body.innerHTML = el.outerHTML;
                                            document.body.style.margin = '0';
                                        }
                                    });
                                } else {
                                    const el = document.querySelector('.joe_post');
                                    if (el) {
                                        document.body.innerHTML = el.outerHTML;
                                        document.body.style.margin = '0';
                                    }
                                }
                            """;
                    browser.executeJavaScript(js, browser.getURL(), 0);
                }
            }
        }, browser.getCefBrowser());
    }

    @Override
    public @NotNull JComponent getComponent() {
        System.out.println("getComponent called, component parent: " + browser.getComponent().getParent());
        return browser.getComponent();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return browser.getComponent();
    }

    @Override
    public @NotNull String getName() {
        return "WebView";
    }

    @Override
    public void dispose() {
        System.out.println("Disposing browser");
        browser.dispose();
    }

    // 其他方法可以根据需要实现或返回默认值
    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
