package view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private JLabel welcomeLabel;
    private JButton seatSearchButton;
    private JButton myReservationButton;
    private JButton logoutButton;

    public MainView(String userName) {
        setTitle("메인 화면");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        JPanel mainPanel = AppStyle.page(76, 170, 74, 170);

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        welcomePanel.setBackground(AppStyle.PANEL);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        welcomeLabel = AppStyle.title(userName + "님, 환영합니다!", 15);
        welcomePanel.add(welcomeLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        seatSearchButton = AppStyle.menuButton("좌석 조회 및 예약");
        myReservationButton = AppStyle.menuButton("내 예약 확인");
        logoutButton = AppStyle.menuButton("로그아웃 / 회원 탈퇴");

        seatSearchButton.addActionListener(e -> {
            dispose();
            new SeatView(userName);
        });
        myReservationButton.addActionListener(e -> {
            dispose();
            new MyReservationView(userName);
        });
        logoutButton.addActionListener(e -> {
            dispose();
            new LogoutView(userName);
        });

        buttonPanel.add(seatSearchButton);
        buttonPanel.add(myReservationButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    public JButton getSeatSearchButton() {
        return seatSearchButton;
    }

    public JButton getMyReservationButton() {
        return myReservationButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public void updateWelcomeLabel(String userName) {
        welcomeLabel.setText(userName + "님, 환영합니다!");
    }
}
