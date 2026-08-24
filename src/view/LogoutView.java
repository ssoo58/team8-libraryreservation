package view;

import javax.swing.*;
import java.awt.*;

// 로그아웃과 회원 탈퇴 메뉴를 보여주는 View 클래스입니다.
// 실제 로그아웃, 회원 탈퇴, 화면 이동 처리는 Controller가 담당합니다.
public class LogoutView extends JFrame {
    // 로그아웃, 회원 탈퇴, 메인 이동 버튼입니다.
    private JButton logoutButton;
    private JButton withdrawalButton;
    private JButton mainButton;

    public LogoutView(String userId) {
        // 로그아웃/회원 탈퇴 화면의 기본 창 설정입니다.
        setTitle("로그아웃 / 회원 탈퇴");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        // 공통 스타일의 기본 패널을 사용합니다.
        JPanel mainPanel = AppStyle.page(76, 170, 74, 170);

        // 상단 제목 영역입니다.
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JLabel titleLabel = AppStyle.title("로그아웃 / 회원 탈퇴", 15);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        // 세 가지 메뉴 버튼을 생성합니다.
        logoutButton = AppStyle.menuButton("로그아웃하기");
        withdrawalButton = AppStyle.menuButton("회원탈퇴하기");
        mainButton = AppStyle.menuButton("메인으로");

        buttonPanel.add(logoutButton);
        buttonPanel.add(withdrawalButton);
        buttonPanel.add(mainButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JButton getWithdrawalButton() {
        return withdrawalButton;
    }

    public JButton getMainButton() {
        return mainButton;
    }

    public boolean showWithdrawalConfirmDialog() {
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
        return result == 0;
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "안내", JOptionPane.INFORMATION_MESSAGE);
    }
}
