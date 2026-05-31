package view;

import model.ReservationState;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyReservationView extends JFrame {
    private JTextArea reservationInfoArea;
    private JLabel statusLabel;
    private JLabel timeLeftLabel;
    private JButton cancelButton;
    private JButton mainButton;
    private JButton checkInButton;
    private String userName;
    private Timer countdownTimer;

    public MyReservationView() {
        this("사용자");
    }

    public MyReservationView(String userName) {
        this.userName = userName;
        setTitle("내 예약 확인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        JPanel mainPanel = AppStyle.page(64, 145, 58, 145);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        JLabel titleLabel = AppStyle.label("내 예약 정보");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        titlePanel.add(titleLabel);

        JPanel infoPanel = new JPanel(new BorderLayout(0, 12));
        infoPanel.setBackground(AppStyle.PANEL);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(AppStyle.SOFT_GRAY);
        statusLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        statusLabel.setForeground(AppStyle.TEXT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));

        reservationInfoArea = new JTextArea();
        reservationInfoArea.setBackground(AppStyle.PANEL);
        reservationInfoArea.setForeground(AppStyle.TEXT);
        reservationInfoArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        reservationInfoArea.setEditable(false);
        reservationInfoArea.setLineWrap(true);
        reservationInfoArea.setWrapStyleWord(true);
        reservationInfoArea.setText("");
        reservationInfoArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        timeLeftLabel = AppStyle.label("");
        timeLeftLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel detailPanel = new JPanel(new BorderLayout(0, 6));
        detailPanel.setBackground(AppStyle.PANEL);
        detailPanel.add(reservationInfoArea, BorderLayout.CENTER);
        detailPanel.add(timeLeftLabel, BorderLayout.SOUTH);

        infoPanel.add(statusLabel, BorderLayout.NORTH);
        infoPanel.add(detailPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(AppStyle.PANEL);

        cancelButton = AppStyle.outlineButton("예약 취소", 82, 30);
        cancelButton.addActionListener(e -> cancelReservation());

        mainButton = AppStyle.outlineButton("메인으로", 82, 30);
        mainButton.addActionListener(e -> {
            dispose();
            new MainView(userName);
        });

        checkInButton = AppStyle.outlineButton("입실 완료", 82, 30);
        checkInButton.addActionListener(e -> checkInReservation());

        buttonPanel.add(cancelButton);
        buttonPanel.add(mainButton);
        buttonPanel.add(checkInButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateReservationView();
        startCountdown();
        setVisible(true);
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getMainButton() {
        return mainButton;
    }

    public JButton getCheckInButton() {
        return checkInButton;
    }

    public void setReservationInfo(String info) {
        reservationInfoArea.setText(info);
    }

    public void appendReservationInfo(String info) {
        reservationInfoArea.append(info);
    }

    public void clearReservationInfo() {
        reservationInfoArea.setText("예약 정보가 없습니다.");
        statusLabel.setText("예약된 좌석이 없습니다.");
        statusLabel.setBackground(AppStyle.SOFT_GRAY);
        timeLeftLabel.setText("");
        cancelButton.setText("예약 취소");
        checkInButton.setEnabled(true);
    }

    private void cancelReservation() {
        if (!ReservationState.hasReservationFor(userName)) {
            showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }

        boolean checkedIn = ReservationState.isCheckedIn();
        int result = JOptionPane.showOptionDialog(
                this,
                checkedIn ? "사용을 중단하시겠습니까?" : "예약 취소하시겠습니까?",
                checkedIn ? "퇴실하기" : "예약 취소",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"예", "아니요"},
                "예"
        );
        if (result == 0) {
            ReservationState.clear();
            clearReservationInfo();
            showSuccessMessage(checkedIn ? "퇴실 처리되었습니다." : "예약 취소되었습니다.");
        }
    }

    private void checkInReservation() {
        if (!ReservationState.hasReservationFor(userName)) {
            showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }

        ReservationState.checkIn();
        updateReservationView();
        showSuccessMessage("입실 처리되었습니다.");
    }

    private void updateReservationView() {
        if (!ReservationState.hasReservationFor(userName)) {
            clearReservationInfo();
            return;
        }

        int seatNumber = ReservationState.getSeatNumber();
        boolean checkedIn = ReservationState.isCheckedIn();
        statusLabel.setText("좌석 : " + seatNumber + "번   상태 : " + (checkedIn ? "입실 완료" : "예약 완료"));
        statusLabel.setBackground(checkedIn ? AppStyle.SOFT_BLUE : AppStyle.SOFT_PINK);
        cancelButton.setText(checkedIn ? "퇴실하기" : "예약 취소");
        checkInButton.setEnabled(!checkedIn);

        if (checkedIn) {
            String checkedInAt = ReservationState.getCheckedInAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            reservationInfoArea.setText("예약자 : " + userName + "\n입실 시간 : " + checkedInAt + "\n이용 가능 시간은 6시간입니다.");
            updateUseCountdownText();
        } else {
            String reservedAt = ReservationState.getReservedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            reservationInfoArea.setText("예약자 : " + userName + "\n예약 시간 : " + reservedAt);
            updateCheckInCountdownText();
        }
    }

    private void startCountdown() {
        countdownTimer = new Timer(1000, e -> {
            if (ReservationState.isCheckedIn()) {
                updateUseCountdownText();
            } else {
                updateCheckInCountdownText();
            }
        });
        countdownTimer.start();
    }

    private void updateCheckInCountdownText() {
        if (!ReservationState.hasReservationFor(userName) || ReservationState.isCheckedIn()) {
            timeLeftLabel.setText("");
            return;
        }

        long remainingSeconds = 600 - Duration.between(ReservationState.getReservedAt(), LocalDateTime.now()).getSeconds();
        if (remainingSeconds <= 0) {
            ReservationState.clear();
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            JOptionPane.showMessageDialog(
                    this,
                    "예약 후 10분 내에 입실하지 않아 자동으로 예약취소 처리되었습니다.",
                    "자동 예약 취소",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            return;
        }

        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        timeLeftLabel.setText(String.format("남은 시간 : %02d:%02d", minutes, seconds));
    }

    private void updateUseCountdownText() {
        if (!ReservationState.hasReservationFor(userName) || !ReservationState.isCheckedIn()) {
            timeLeftLabel.setText("");
            return;
        }

        long remainingSeconds = 21600 - Duration.between(ReservationState.getCheckedInAt(), LocalDateTime.now()).getSeconds();
        if (remainingSeconds <= 0) {
            ReservationState.clear();
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            JOptionPane.showMessageDialog(
                    this,
                    "이용 시간이 종료되어 자동으로 퇴실처리되었습니다.",
                    "자동 퇴실 처리",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            return;
        }

        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        timeLeftLabel.setText(String.format("퇴실까지 남은 시간 : %02d:%02d:%02d", hours, minutes, seconds));
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public void showConfirmDialog(String message) {
        JOptionPane.showConfirmDialog(this, message, "확인", JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void dispose() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        super.dispose();
    }
}
