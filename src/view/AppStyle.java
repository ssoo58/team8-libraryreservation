package view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

final class AppStyle {
    static final Color BACKGROUND = new Color(244, 245, 247);
    static final Color PANEL = Color.WHITE;
    static final Color PRIMARY = new Color(22, 104, 139);
    static final Color BORDER = new Color(54, 54, 54);
    static final Color SOFT_BLUE = new Color(214, 237, 245);
    static final Color SOFT_GRAY = new Color(222, 222, 222);
    static final Color SELECTED_GRAY = new Color(145, 145, 145);
    static final Color SOFT_PINK = new Color(244, 224, 239);
    static final Color TEXT = new Color(35, 35, 35);

    private AppStyle() {
    }

    static JPanel page(int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return panel;
    }

    static JLabel title(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, size));
        label.setForeground(PRIMARY);
        return label;
    }

    static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        label.setForeground(TEXT);
        return label;
    }

    static JTextField textField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(160, 28));
        field.setFont(new Font("Dialog", Font.PLAIN, 12));
        field.setBorder(lineBorder());
        return field;
    }

    static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(160, 28));
        field.setFont(new Font("Dialog", Font.PLAIN, 12));
        field.setBorder(lineBorder());
        return field;
    }

    static JButton outlineButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(PANEL);
        button.setForeground(TEXT);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorder(lineBorder());
        return button;
    }

    static JButton menuButton(String text) {
        JButton button = outlineButton(text, 260, 38);
        button.setFont(new Font("Dialog", Font.PLAIN, 13));
        return button;
    }

    static Border lineBorder() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }
}
