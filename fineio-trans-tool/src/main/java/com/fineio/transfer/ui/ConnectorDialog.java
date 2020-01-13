package com.fineio.transfer.ui;

import com.fineio.storage.v3.Connector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

/**
 * @author yee
 * @date 2019-08-08
 */
public class ConnectorDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    Connector connector;
    private TransferTable table;
    private Class clazz;
    private Constructor<? extends Connector> constructor;


    /**
     * Create the dialog.
     */
    public ConnectorDialog(String clazz) throws ClassNotFoundException {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        initConstructors(clazz);
        initConstructParams();
        initBottoms();
    }

    private void initBottoms() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                constructor.setAccessible(true);
                try {
                    connector = constructor.newInstance(table.getArgs());
                    constructor.setAccessible(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ConnectorDialog.this.dispose();
            }
        }));
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
    }

    private void initConstructParams() {
        JLabel label = new JLabel("参数");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 1;
        contentPanel.add(label, gbc_label);
        table = new TransferTable();
        GridBagConstraints gbc_table = new GridBagConstraints();
        gbc_table.fill = GridBagConstraints.BOTH;
        gbc_table.gridx = 1;
        gbc_table.gridy = 1;
        contentPanel.add(table, gbc_table);
    }

    private void initConstructors(String clazz) throws ClassNotFoundException {
        JLabel label = new JLabel("构造器");
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(0, 0, 5, 5);
        gbcLabel.gridx = 0;
        gbcLabel.gridy = 0;
        contentPanel.add(label, gbcLabel);
        final JList<Constructor<? extends Connector>> list = new JList<>();
        GridBagConstraints gbcList = new GridBagConstraints();
        gbcList.insets = new Insets(0, 0, 5, 0);
        gbcList.fill = GridBagConstraints.BOTH;
        gbcList.gridx = 1;
        gbcList.gridy = 0;
        this.clazz = Class.forName(clazz);
        contentPanel.add(list, gbcList);
        final Constructor[] declaredConstructors = this.clazz.getDeclaredConstructors();
        DefaultListModel<Constructor<? extends Connector>> model = new DefaultListModel<>();
        for (Constructor constructor : declaredConstructors) {
            model.addElement(constructor);
        }
        list.setModel(model);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                constructor = list.getSelectedValue();
                final Class<?>[] parameterTypes = constructor.getParameterTypes();
                table.setData(parameterTypes);
            }
        });
    }

}