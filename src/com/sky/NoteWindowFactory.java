package com.sky;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class NoteWindowFactory implements ToolWindowFactory, Condition<Project> {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        NoteWindow myToolWindow = new NoteWindow(toolWindow, project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), "常用文件", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public boolean value(Project project) {
        return false;
    }
}
