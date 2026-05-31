package view;

import controller.LoginController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SignupView extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField passwordConfirmField;
    private JLabel passwordMismatchLabel;
    private JButton signupButton;
    private JButton backButton;

    public SignupView() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        JPanel mainPanel = AppStyle.page(58, 160, 54, 160);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));
        titlePanel.add(AppStyle.title("회원가입", 15));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(AppStyle.PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = AppStyle.textField();
        passwordField = AppStyle.passwordField();
        passwordConfirmField = AppStyle.passwordField();
        passwordMismatchLabel = new JLabel("비밀번호가 동일하지 않습니다.");
        passwordMismatchLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        passwordMismatchLabel.setForeground(Color.RED);
        passwordMismatchLabel.setVisible(false);

        addInputRow(inputPanel, gbc, 0, "아이디(학번)", idField);
        addInputRow(inputPanel, gbc, 1, "비밀번호", passwordField);
        addInputRow(inputPanel, gbc, 2, "비밀번호 확인", passwordConfirmField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(-4, 6, 6, 6);
        inputPanel.add(passwordMismatchLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        signupButton = AppStyle.outlineButton("가입하기", 78, 28);
        backButton = AppStyle.outlineButton("돌아가기", 78, 28);

        addPasswordMatchListener();

        signupButton.addActionListener(e -> signup());
        backButton.addActionListener(e -> {
            dispose();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
        });

        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void addInputRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.insets = new Insets(4, 6, 8, 6);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(AppStyle.label(labelText), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void addPasswordMatchListener() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePasswordMismatchMessage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePasswordMismatchMessage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePasswordMismatchMessage();
            }
        };

        passwordField.getDocument().addDocumentListener(listener);
        passwordConfirmField.getDocument().addDocumentListener(listener);
    }

    private void updatePasswordMismatchMessage() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(passwordConfirmField.getPassword());
        passwordMismatchLabel.setVisible(!confirm.isEmpty() && !password.equals(confirm));
    }

    private void signup() {
        updatePasswordMismatchMessage();
        if (passwordMismatchLabel.isVisible()) {
            return;
        }

        JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
        String userName = idField.getText().trim();
        dispose();
        new MainView(userName.isEmpty() ? "사용자" : userName);
    }
}
