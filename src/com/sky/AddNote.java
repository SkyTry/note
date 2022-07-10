package com.sky;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.EditorTextField;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

import static com.sky.Constants.DEFAULTS_PARAMS;

public class AddNote extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        //获取当前编辑器对象
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        //获取选择的数据模型
        SelectionModel selectionModel = editor.getSelectionModel();
        //获取当前选择的文本
        String selectedText = selectionModel.getSelectedText();
        //选择的内容
        DataCenter.SELECTED_TEXT = selectedText;
        //文件名称
        PsiFile file = e.getRequiredData(CommonDataKeys.PSI_FILE);
        String projectName = e.getProject().getName();
        DataCenter.FILE = file;
        VirtualFile virtualFile = file.getViewProvider().getVirtualFile();
        if (virtualFile.getPath().indexOf(projectName) == -1) {
            int length = e.getProject().getBasePath().lastIndexOf("/") + 1;
            DataCenter.CURRENT_FILE_NAME = virtualFile.getPath().substring(length);
        } else {
            DataCenter.CURRENT_FILE_NAME = virtualFile.getPath().substring(virtualFile.getPath().indexOf(projectName));
        }
        if (StringUtils.equals(virtualFile.getFileType().getName(), "JAVA")) {
            DataCenter.METHOD_PARAM = getParams(file, selectedText, selectionModel.getSelectionStart());
        } else {
            DataCenter.METHOD_PARAM = DEFAULTS_PARAMS;
        }
        AddNoteDialog dialog = new AddNoteDialog();
        dialog.showAndGet();
    }

    public static String getParams(PsiFile file, String methodName, int offset) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) file;
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        for (int i = 0; i < psiClasses.length; i++) {
            PsiClass psiClass = psiClasses[i];
            PsiMethod[] psiMethods = psiClass.getMethods();
            for (int j = 0; j < psiMethods.length; j++) {
                if (StringUtils.equals(psiMethods[j].getName(), methodName) && offset == psiMethods[j].getTextOffset()) {
                    return psiMethods[j].getParameterList().getText();
                }
            }
        }
        return DEFAULTS_PARAMS;
    }

    public class AddNoteDialog extends DialogWrapper {
        /**
         * 标题输入框
         */
        private EditorTextField etfTitle;
        /**
         * 内容输入框
         */
        private EditorTextField etfMark;


        public AddNoteDialog() {
            super(true);
            init();
            setTitle("添加笔记");
        }

        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            etfTitle = new EditorTextField("标题");
            etfMark = new EditorTextField("备注");
            etfMark.setPreferredSize(new Dimension(200, 100));
            panel.add(etfTitle, BorderLayout.NORTH);
            panel.add(etfMark, BorderLayout.CENTER);
            return panel;
        }

        @Override
        protected JComponent createSouthPanel() {
            JPanel panel = new JPanel(new FlowLayout());
            JButton btnAdd = new JButton("确认添加");
            //按钮点击事件处理
            btnAdd.addActionListener(e -> {
                //获取标题
                String title = etfTitle.getText();
                //获取内容
                String content = etfMark.getText();

                String[] row = new String[5];
                row[0] = title;
                row[1] = content;
                row[2] = DataCenter.CURRENT_FILE_NAME;
                row[3] = DataCenter.SELECTED_TEXT;
                row[4] = DataCenter.DATA_ID.toString();
                DataCenter.FILE_MAP.put(DataCenter.DATA_ID, DataCenter.FILE);
                DataCenter.PARAMS_MAP.put(DataCenter.DATA_ID, DataCenter.METHOD_PARAM);
                DataCenter.TABLE_MODEL.addRow(row);
                DataCenter.DATA_ID++;
                close(OK_EXIT_CODE);
            });
            panel.add(btnAdd);
            return panel;
        }

    }
}
