package com.sky;

import com.intellij.psi.PsiFile;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class DataCenter {

    public static Integer DATA_ID = 0;
    /**
     * 当前的选择的文本
     */
    public static String SELECTED_TEXT = null;
    /**
     * 当前的文件名称
     */
    public static String CURRENT_FILE_NAME = null;
    /**
     * 当前的方法参数列表
     */
    public static String METHOD_PARAM = null;

    /**
     * 当前的文件
     */
    public static PsiFile FILE = null;

    /**
     * 保存方法参数 key为DATA_ID
     */
    public static Map<Integer, String> PARAMS_MAP = new HashMap<>();

    /**
     * 文件映射 key为DATA_ID
     */
    public static Map<Integer, PsiFile> FILE_MAP = new HashMap<>();


    public static String[] COLUMN_NAME = {"标题", "备注", "文件名", "关键字", "id"};

    public static DefaultTableModel TABLE_MODEL = new DefaultTableModel(null, COLUMN_NAME) {

        @Override
        public boolean isCellEditable(int row, int column) {
            // 第3列以后不允许编辑
            if (column >= 2) {
                return false;
            }
            return true;
        }
    };


}
