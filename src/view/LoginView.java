package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginView() {
        setTitle("숙명여자대학교 도서관 좌석 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        JPanel mainPanel = AppStyle.page(72, 160, 70, 160);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JLabel titleLabel = AppStyle.title("숙명여자대학교 도서관 좌석 예약 시스템", 15);
        titlePanel.add(titleLabel);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(AppStyle.PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = AppStyle.label("아이디(학번)");
        idField = AppStyle.textField();

        JLabel passwordLabel = AppStyle.label("비밀번호");
        passwordField = AppStyle.passwordField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        centerPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        centerPanel.add(idField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        centerPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        loginButton = AppStyle.outlineButton("로그인", 72, 28);

        signupButton = AppStyle.outlineButton("회원가입", 78, 28);

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public String getIdInput() {
        return idField.getText();
    }

    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public void clearInputs() {
        idField.setText("");
        passwordField.setText("");
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getSignupButton() {
        return signupButton;
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "안내", JOptionPane.INFORMATION_MESSAGE);
    }
}
