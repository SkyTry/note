package com.sky;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OpenFile extends MouseAdapter {

    private JTable contentTable;

    public OpenFile(JTable contentTable) {
        this.contentTable = contentTable;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && contentTable.getSelectedColumn() > 1) {
            Integer dataId = Integer.valueOf(DataCenter.TABLE_MODEL.getValueAt(contentTable.getSelectedRow(), 4).toString());
            PsiFile file = DataCenter.FILE_MAP.get(dataId);
            String keyWord = "";
            if (DataCenter.TABLE_MODEL.getValueAt(contentTable.getSelectedRow(), 3) != null) {
                keyWord = DataCenter.TABLE_MODEL.getValueAt(contentTable.getSelectedRow(), 3).toString();
            }
            String params = DataCenter.PARAMS_MAP.get(dataId);
            if (file != null) {
                new OpenFileDescriptor(file.getProject(), file.getViewProvider().getVirtualFile(), getOffset(file, keyWord, params)).navigate(true);
            }
        }
    }

    private int getOffset(PsiFile file, String keyWord, String params) {
        if (StringUtils.isEmpty(keyWord)) {
            return 0;
        }
        if (StringUtils.equals(file.getViewProvider().getVirtualFile().getFileType().getName(), "JAVA")) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiClass[] psiClasses = psiJavaFile.getClasses();
            for (int i = 0; i < psiClasses.length; i++) {
                PsiClass psiClass = psiClasses[i];
                PsiMethod[] psiMethods = psiClass.getMethods();
                for (int j = 0; j < psiMethods.length; j++) {
                    if (StringUtils.equals(psiMethods[j].getName(), keyWord) && StringUtils.equals(psiMethods[j].getParameterList().getText(), params)) {
                        return psiMethods[j].getTextOffset();
                    }
                }
            }
        }
        return file.getText().indexOf(keyWord);
    }
}
