package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;

// 로그아웃과 회원 탈퇴 메뉴를 보여주는 View 클래스입니다.
// 실제 회원 데이터 삭제 기능은 아직 연결되어 있지 않고, 현재는 화면 이동과 안내 메시지를 담당합니다.
public class LogoutView extends JFrame {
    // 현재 로그인한 사용자 식별값입니다. 메인 화면으로 돌아갈 때 다시 전달합니다.
    private String userId;

    // 로그아웃, 회원 탈퇴, 메인 이동 버튼입니다.
    private JButton logoutButton;
    private JButton withdrawalButton;
    private JButton mainButton;

    public LogoutView(String userId) {
        // 다른 화면에서 전달받은 사용자 식별값을 저장합니다.
        this.userId = userId;

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

        // 각 버튼 클릭 시 실행할 화면 동작을 연결합니다.
        logoutButton.addActionListener(e -> logout());
        withdrawalButton.addActionListener(e -> withdraw());
        mainButton.addActionListener(e -> {
            dispose();
            new MainView(this.userId);
        });

        buttonPanel.add(logoutButton);
        buttonPanel.add(withdrawalButton);
        buttonPanel.add(mainButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    // 로그아웃 안내 후 로그인 화면으로 이동합니다.
    private void logout() {
        JOptionPane.showMessageDialog(this, "로그아웃되었습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        openLoginView();
    }

    // 회원 탈퇴 확인창을 보여주고, 사용자가 예를 누르면 로그인 화면으로 이동합니다.
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

    // 로그인 화면을 열고 LoginController를 다시 연결합니다.
    private void openLoginView() {
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }
}
