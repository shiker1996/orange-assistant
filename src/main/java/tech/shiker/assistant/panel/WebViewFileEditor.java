package tech.shiker.assistant.panel;

import com.intellij.openapi.fileEditor.FileEditor;
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
            public void onLoadStart(CefBrowser cefBrowser, CefFrame frame, CefRequest.TransitionType transitionType) {
                if (frame.isMain()) {
                    //debug
//                    browser.openDevtools();
                    String js = """
                            if (document.readyState === "loading") {
                            	document.addEventListener("DOMContentLoaded", () => {
                            		localStorage.setItem("data-mode", "dark");
                            		// 如果有主题初始化函数，也执行它
                            		if (typeof commonContext !== "undefined" && typeof commonContext.initCommentTheme === "function") {
                            			commonContext.initCommentTheme();
                            		} else {
                            			// 应用 halo-comment 组件的 dark class
                            			const comments = document.getElementsByTagName("halo-comment");
                            			const curMode = document.documentElement.getAttribute("data-mode");
                            			for (let i = 0; i < comments.length; i++) {
                            				const shadowDom = comments[i].shadowRoot?.getElementById("halo-comment");
                            				if (shadowDom) {
                            					shadowDom.classList[curMode === "light" ? "remove" : "add"]("dark");
                            				}
                            			}
                            		}
                            		const el = document.querySelector(".joe_post");
                            		if (el) {
                            			document.body.innerHTML = el.outerHTML;
                            			document.body.style.margin = "0";
                            		}
                            	});
                            } else {
                            	const el = document.querySelector(".joe_post");
                            	if (el) {
                            		document.body.innerHTML = el.outerHTML;
                            		document.body.style.margin = "0";
                            	}
                            }
                            """;
                    cefBrowser.executeJavaScript(js, cefBrowser.getURL(), 0);
                }
            }
        }, browser.getCefBrowser());
    }

    @Override
    public @NotNull JComponent getComponent() {
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
