package com.fineio.transfer.ui;

import com.fineio.storage.v3.Connector;
import com.fineio.transfer.Transfer;
import com.fineio.transfer.TransferProgressListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author yee
 */
public class TransferDialog extends JDialog {
    private static final int UPGRADE = 1;
    private static final int DOWNGRADE = 2;
    private static final int TRANSFER = 3;
    private final JPanel contentPanel = new JPanel();
    private JTextField fromConnector;
    private JTextField toConnector;
    private JProgressBar progressBar;
    private int mode;
    private Connector from;
    private Connector to;


    public TransferDialog() {
        initUi();
    }

    public static void main(String[] args) {
        TransferDialog dialog = new TransferDialog();
        dialog.pack();
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        System.exit(0);
    }

    private void initUi() {
        setBounds(100, 100, 558, 395);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        initMode();
        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        initVersion(panel);
        initConnector(panel);
        initShowArea(panel);
        progressBar = new JProgressBar();
        progressBar.setMaximum(1);
        progressBar.setMaximum(0);
        contentPanel.add(progressBar, BorderLayout.SOUTH);
        initDialogButton();
    }

    private void initDialogButton() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransferDialog.this.onOK();
            }
        });
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransferDialog.this.onCancel();
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
    }

    private void initShowArea(JPanel panel) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setColumns(45);
        textArea.setRows(10);
        panel.add(textArea);
    }

    private void initConnector(JPanel panel) {
        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(p);
        p.setLayout(new GridLayout(0, 3, 0, 0));
        JLabel label = new JLabel("开始Connector");
        p.add(label);
        fromConnector = new JTextField();
        p.add(fromConnector);
        fromConnector.setColumns(10);
        JButton btnNewButton = new JButton("配置");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final ConnectorDialog connectorDialog = new ConnectorDialog(fromConnector.getText());
                    connectorDialog.setVisible(true);
                    from = connectorDialog.connector;
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(TransferDialog.this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p.add(btnNewButton);
        JLabel l = new JLabel("结束Connector");
        p.add(l);
        toConnector = new JTextField();
        p.add(toConnector);
        toConnector.setColumns(10);
        JButton button = new JButton("配置");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final ConnectorDialog connectorDialog = new ConnectorDialog(toConnector.getText());
                    connectorDialog.setVisible(true);
                    to = connectorDialog.connector;
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(TransferDialog.this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p.add(button);
    }

    private void initVersion(JPanel panel) {
        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(p);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JLabel label = new JLabel("开始版本：");
        p.add(label);
        JComboBox fromVersion = new JComboBox();
        fromVersion.setModel(new DefaultComboBoxModel(new String[]{"2", "3"}));
        p.add(fromVersion);
        JLabel lblNewLabel = new JLabel("目标版本：");
        p.add(lblNewLabel);
        JComboBox targetVersion = new JComboBox();
        targetVersion.setModel(new DefaultComboBoxModel(new String[]{"2", "3"}));
        p.add(targetVersion);
    }

    private void initMode() {
        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(0, 4, 0, 0));
        JLabel label = new JLabel("模式：");
        panel.add(label);
        final JRadioButton upgrade = new JRadioButton("升级");
        panel.add(upgrade);
        final JRadioButton downgrade = new JRadioButton("降级");
        panel.add(downgrade);
        final JRadioButton transfer = new JRadioButton("迁移");
        panel.add(transfer);
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(upgrade);
        buttonGroup.add(downgrade);
        buttonGroup.add(transfer);

        upgrade.setSelected(true);
        this.mode = UPGRADE;
        upgrade.addChangeListener((new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (upgrade.isSelected()) {
                    mode = UPGRADE;
                }
            }
        }));

        downgrade.addChangeListener((new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (downgrade.isSelected()) {
                    mode = DOWNGRADE;
                }
            }
        }));

        transfer.addChangeListener((new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (transfer.isSelected()) {
                    mode = TRANSFER;
                }
            }
        }));
    }

    private void onOK() {
        final TransferProgressListener listener = new TransferProgressListener() {
            @Override
            public void progress(int progress, String path) {
                progressBar.setValue(progress);
            }
        };
        try {
            switch (mode) {
                case UPGRADE:
                    Transfer.upgrade(from, to, listener);
                    break;
                case DOWNGRADE:
                    Transfer.downgrade(from, to, listener);
                    break;
                case TRANSFER:
                    Transfer.transfer(from, to, listener);
                    break;
                default:
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "迁移失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }
}
