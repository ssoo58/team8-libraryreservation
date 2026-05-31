package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;

public class LogoutView extends JFrame {
    private String userName;
    private JButton logoutButton;
    private JButton withdrawalButton;
    private JButton mainButton;

    public LogoutView(String userName) {
        this.userName = userName;
        setTitle("로그아웃 / 회원 탈퇴");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        JPanel mainPanel = AppStyle.page(76, 170, 74, 170);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JLabel titleLabel = AppStyle.title("로그아웃 / 회원 탈퇴", 15);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        logoutButton = AppStyle.menuButton("로그아웃하기");
        withdrawalButton = AppStyle.menuButton("회원탈퇴하기");
        mainButton = AppStyle.menuButton("메인으로");

        logoutButton.addActionListener(e -> logout());
        withdrawalButton.addActionListener(e -> withdraw());
        mainButton.addActionListener(e -> {
            dispose();
            new MainView(userName);
        });

        buttonPanel.add(logoutButton);
        buttonPanel.add(withdrawalButton);
        buttonPanel.add(mainButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "로그아웃되었습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        openLoginView();
    }

    private void withdraw() {
        int result = JOptionPane.showOptionDialog(
                this,
                "회원탈퇴를 진행할까요?",
                "회원 탈퇴",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"예", "아니요"},
                "예"
        );
        if (result == 0) {
            JOptionPane.showMessageDialog(this, "회원탈퇴되었습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            openLoginView();
        }
    }

    private void openLoginView() {
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }
}
