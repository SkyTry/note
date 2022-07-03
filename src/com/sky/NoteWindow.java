package com.sky;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.xml.crypto.Data;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoteWindow {
    private JButton hideButton;
    private JButton delButton;
    private JButton saveButton;
    private JButton importButton;
    private JButton resetButton;
    private JPanel myToolWindowContent;
    private JTable contentTable;
    private Project project;

    public NoteWindow(ToolWindow toolWindow, Project project) {
        init();
        hideButton.addActionListener(e -> toolWindow.hide(null));
        this.project = project;
    }

    private void init() {
        hideButton = new JButton("取消");
        contentTable = new JBTable();
        contentTable.setModel(DataCenter.TABLE_MODEL);
        contentTable.setEnabled(true);
        contentTable.addMouseListener(new OpenFile(contentTable));
        // 隐藏dataId
        contentTable.removeColumn(contentTable.getColumnModel().getColumn(4));

        myToolWindowContent = new JPanel();
        myToolWindowContent.setLayout(new BorderLayout());
        JScrollPane jScrollPane = new JBScrollPane(contentTable);
        myToolWindowContent.add(jScrollPane, BorderLayout.CENTER);

        delButton = new JButton("删除");
        delButton.addActionListener(e -> delRow());
        saveButton = new JButton("导出");
        saveButton.addActionListener(e -> out());
        importButton = new JButton("导入");
        importButton.addActionListener(e -> in());
        resetButton = new JButton("清空");
        resetButton.addActionListener(e -> clear());
        JPanel myToolWindowContent2 = new JPanel();
        myToolWindowContent2.add(delButton);
        myToolWindowContent2.add(saveButton);
        myToolWindowContent2.add(importButton);
        myToolWindowContent2.add(resetButton);
        myToolWindowContent2.add(hideButton);
        myToolWindowContent.add(myToolWindowContent2, BorderLayout.SOUTH);

    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    public void delRow() {
        //获取要删除的行,没有选择是-1
        int row = contentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(contentTable, "请选择要删除的行!");
        } else {
            DataCenter.TABLE_MODEL.removeRow(row);
        }
    }

    public void clear() {
        while (DataCenter.TABLE_MODEL.getRowCount() != 0) {
            DataCenter.TABLE_MODEL.removeRow(0);
        }
        DataCenter.FILE_MAP.clear();
        DataCenter.PARAMS_MAP.clear();
        DataCenter.DATA_ID = 0;
    }

    public void out() {
        // 选择导出文件
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), project, null);
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            List<String> list = new ArrayList<>();
            for (int row = 0; row < DataCenter.TABLE_MODEL.getRowCount(); row++) {
                List<String> rows = new ArrayList<>();
                for (int column = 0; column < DataCenter.TABLE_MODEL.getColumnCount(); column++) {
                    String str = "";
                    if (DataCenter.TABLE_MODEL.getValueAt(row, column) != null) {
                        str = DataCenter.TABLE_MODEL.getValueAt(row, column).toString();
                    }
                    if (column == 4) {
                        Integer dataId = Integer.valueOf(DataCenter.TABLE_MODEL.getValueAt(row, 4).toString());
                        str = DataCenter.PARAMS_MAP.get(dataId);
                    }
                    rows.add(str);
                }
                list.add(StringUtils.join(rows, Constants.Separator));
            }
            try {
                FileUtils.writeLines(new File(path), "gb2312", list);
                JOptionPane.showMessageDialog(contentTable, "导出成功!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void in() {
        // 选择导入文件
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), project, null);
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            try {
                List<String> list = FileUtils.readLines(new File(path), "gb2312");
                list.forEach(row -> {
                    if (StringUtils.isNotBlank(row)) {
                        String[] params = row.split(Constants.Separator, -1);
                        if (params.length != 5) {
                            return;
                        }
                        String file = params[2];
                        if (file.contains("/")) {
                            file = file.substring(file.lastIndexOf("/") + 1);
                        }
                        PsiFile[] files = FilenameIndex.getFilesByName(project, file, GlobalSearchScope.allScope(project));
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].getVirtualFile().getPath().contains(params[2])) {
                                DataCenter.FILE_MAP.put(DataCenter.DATA_ID, files[i]);
                                DataCenter.PARAMS_MAP.put(DataCenter.DATA_ID, params[4]);
                                params[4] = DataCenter.DATA_ID.toString();
                                DataCenter.DATA_ID++;
                                DataCenter.TABLE_MODEL.addRow(params);
                                break;
                            }
                        }
                    }
                });
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}

