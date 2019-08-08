package com.fineio.transfer.ui;


import com.fineio.transfer.ReflectUtils;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author yee
 * @date 2019-08-08
 */
public class TransferTable extends JTable {
    private Class[] classes;

    public TransferTable() {
        super(new Object[2][], new String[]{"参数", "值"});
        getTableHeader().setVisible(true);
        this.classes = new Class[0];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column > 0;
    }

    public void setData(Class[] classes) {
        this.classes = classes;
        final DefaultTableModel tableModel = new DefaultTableModel(0, 2);
        for (Class<?> parameterType : classes) {
            tableModel.addRow(new Object[]{parameterType.getName(), null});
        }
        setModel(tableModel);
    }

    public Object[] getArgs() {
        Object[] args = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            try {
                args[i] = ReflectUtils.parseObject(classes[i], getValueAt(i, 1).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return args;
    }
}
