package view;

import javax.swing.*;
import java.awt.*;

// 로그인 후 처음 보이는 메인 메뉴 화면입니다.
// 좌석 조회, 내 예약 확인, 로그아웃 화면으로 이동하는 내비게이션 역할을 합니다.
public class MainView extends JFrame {
    // 사용자 환영 문구와 메뉴 버튼을 화면 상태로 보관합니다.
    private JLabel welcomeLabel;
    private JButton seatSearchButton;
    private JButton myReservationButton;
    private JButton logoutButton;

    public MainView(String userId) {
        // 메인 화면의 기본 창 설정입니다.
        setTitle("메인 화면");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        // 공통 스타일의 기본 패널을 사용해 화면 전체 여백을 통일합니다.
        JPanel mainPanel = AppStyle.page(76, 170, 74, 170);

        // 현재 로그인한 사용자를 보여주는 환영 문구 영역입니다.
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        welcomePanel.setBackground(AppStyle.PANEL);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        welcomeLabel = AppStyle.title(userId + "님, 환영합니다!", 15);
        welcomePanel.add(welcomeLabel);

        // 세 개의 주요 기능 버튼을 세로로 배치합니다.
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        seatSearchButton = AppStyle.menuButton("좌석 조회 및 예약");
        myReservationButton = AppStyle.menuButton("내 예약 확인");
        logoutButton = AppStyle.menuButton("로그아웃 / 회원 탈퇴");

        // 버튼 클릭 시 현재 메인 창을 닫고 선택한 화면으로 이동합니다.
        // userId는 현재 로그인한 사용자 식별값으로 다음 View에 전달됩니다.
        seatSearchButton.addActionListener(e -> {
            dispose();
            new SeatView(userId);
        });
        myReservationButton.addActionListener(e -> {
            dispose();
            new MyReservationView(userId);
        });
        logoutButton.addActionListener(e -> {
            dispose();
            new LogoutView(userId);
        });

        buttonPanel.add(seatSearchButton);
        buttonPanel.add(myReservationButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    // 아래 getter들은 Controller 방식으로 이벤트를 연결할 때 사용할 수 있는 접근 메서드입니다.
    public JButton getSeatSearchButton() {
        return seatSearchButton;
    }

    public JButton getMyReservationButton() {
        return myReservationButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    // 로그인 사용자 ID가 바뀌었을 때 환영 문구를 갱신합니다.
    public void updateWelcomeLabel(String userId) {
        welcomeLabel.setText(userId + "님, 환영합니다!");
    }
}
