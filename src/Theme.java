

import java.awt.*;
import javax.swing.*;

public class Theme {

    // Netflix palette — red is accent only
    public static final Color BG       = new Color(0x141414);
    public static final Color SURFACE  = new Color(0x1F1F1F);
    public static final Color SURFACE2 = new Color(0x2F2F2F);
    public static final Color RED      = new Color(0xE50914);
    public static final Color TEXT     = new Color(0xFFFFFF);
    public static final Color TEXT_DIM = new Color(0xB3B3B3);

    // Recursively applies the dark theme to every component in a container
    public static void apply(Container container) {
        container.setBackground(BG);
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel lbl) {
                lbl.setForeground(TEXT_DIM);
            } else if (c instanceof JButton btn) {
                btn.setBackground(SURFACE2);
                btn.setForeground(TEXT);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            } else if (c instanceof JTextField tf) {
                tf.setBackground(SURFACE);
                tf.setForeground(TEXT);
                tf.setCaretColor(TEXT);
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SURFACE2),
                        BorderFactory.createEmptyBorder(4, 6, 4, 6)));
            } else if (c instanceof JComboBox<?> cb) {
                cb.setBackground(SURFACE);
                cb.setForeground(TEXT);
            } else if (c instanceof JScrollPane sp) {
                sp.setBackground(BG);
                sp.setBorder(BorderFactory.createLineBorder(SURFACE2));
                sp.getViewport().setBackground(BG);
                Component view = sp.getViewport().getView();
                if (view instanceof JTable t) styleTable(t);
                else if (view instanceof JTextArea ta) {
                    ta.setBackground(SURFACE);
                    ta.setForeground(TEXT);
                    ta.setCaretColor(TEXT);
                }
                else if (view instanceof Container inner) apply(inner);
            } else if (c instanceof JPanel p) {
                p.setBackground(BG);
                apply(p);
            }else if (c instanceof JCheckBox cb) {
                cb.setOpaque(false);
                cb.setForeground(TEXT);
                cb.updateUI(); 
            }
        }
    }

    public static void applyToFrame(JFrame frame) {
    UIManager.put("CheckBox.foreground", TEXT);
    UIManager.put("CheckBox.background", BG);
    frame.setSize(1280, 720);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setBackground(BG);
    apply((Container) frame.getContentPane());
}

    public static void applyToSub(JFrame frame) {
        UIManager.put("CheckBox.foreground", TEXT);
        UIManager.put("CheckBox.background", BG);
        frame.getContentPane().setBackground(BG);
        apply((Container) frame.getContentPane());
    }   

    public static void styleCard(JPanel card) {
        card.setBackground(BG);
        card.setBorder(BorderFactory.createLineBorder(SURFACE2));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void selectCard(JPanel card) {
        card.setBorder(BorderFactory.createLineBorder(RED, 2));
    }

    public static void deselectCard(JPanel card) {
        card.setBorder(BorderFactory.createLineBorder(SURFACE2));
    }

    private static void styleTable(JTable table) {
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setGridColor(SURFACE2);
        table.setSelectionBackground(RED);
        table.setSelectionForeground(TEXT);
        table.setRowHeight(24);
        // Nimbus also ignores setBackground on the header — need a custom header renderer
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setBackground(SURFACE2);
                setForeground(TEXT);
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BG));
                return this;
            }
        });
        // Nimbus LAF ignores setBackground/setForeground on JTable cells — override with a renderer
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setBackground(selected ? RED : SURFACE);
                setForeground(TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                return this;
            }
        });
    }
}
