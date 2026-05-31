package view;

import model.ReservationState;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SeatView extends JFrame {
    private JButton[] seatButtons;
    private JLabel selectedSeatLabel;
    private JButton reserveButton;
    private JButton backButton;
    private JButton myReservationButton;
    private int selectedSeatNumber;
    private String userName;
    private JButton selectedSeatButton;
    private JLabel currentTimeLabel;
    private Timer currentTimeTimer;

    public SeatView() {
        this("사용자");
    }

    public SeatView(String userName) {
        this.userName = userName;
        setTitle("좌석 조회 및 예약");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        JPanel mainPanel = AppStyle.page(24, 26, 22, 26);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        JLabel titleLabel = AppStyle.title("좌석 현황 및 예약", 15);
        currentTimeLabel = AppStyle.label("");
        currentTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        titlePanel.add(Box.createHorizontalStrut(120), BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(currentTimeLabel, BorderLayout.EAST);
        startCurrentTimeClock();

        JPanel contentPanel = new JPanel(new BorderLayout(28, 0));
        contentPanel.setBackground(AppStyle.PANEL);

        JPanel seatPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        seatPanel.setBackground(AppStyle.PANEL);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        seatButtons = new JButton[24];
        for (int i = 0; i < 24; i++) {
            seatButtons[i] = createSeatButton(i + 1);
            seatPanel.add(seatButtons[i]);
        }
        showCurrentReservation();

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(AppStyle.PANEL);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        sidePanel.setPreferredSize(new Dimension(210, 230));

        JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        selectedPanel.setBackground(AppStyle.PANEL);
        selectedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedPanel.setMaximumSize(new Dimension(210, 32));

        JLabel selectedLabel = AppStyle.label("선택좌석 :");
        selectedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedSeatLabel = AppStyle.title("없음", 14);
        selectedPanel.add(selectedLabel);
        selectedPanel.add(selectedSeatLabel);

        JPanel legendPanel = new JPanel(new GridBagLayout());
        legendPanel.setBackground(AppStyle.PANEL);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        legendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        legendPanel.setMaximumSize(new Dimension(210, 112));

        addLegendRow(legendPanel, 0, AppStyle.PANEL, "예약 가능 좌석");
        addLegendRow(legendPanel, 1, AppStyle.SOFT_PINK, "예약 중인 좌석");
        addLegendRow(legendPanel, 2, AppStyle.SOFT_BLUE, "사용 중인 좌석");

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(160, 106));

        reserveButton = AppStyle.outlineButton("예약하기", 130, 30);
        reserveButton.addActionListener(e -> reserveSelectedSeat());

        myReservationButton = AppStyle.outlineButton("내 예약 확인", 130, 30);
        myReservationButton.addActionListener(e -> {
            dispose();
            new MyReservationView(userName);
        });

        backButton = AppStyle.outlineButton("메인으로", 130, 30);
        backButton.addActionListener(e -> {
            dispose();
            new MainView(userName);
        });

        buttonPanel.add(reserveButton);
        buttonPanel.add(myReservationButton);
        buttonPanel.add(backButton);

        JLabel noteLabel = AppStyle.label("좌석을 클릭하면 선택됩니다.");
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel useLimitLabel = AppStyle.label("이용 가능 시간은 6시간입니다.");
        useLimitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(selectedPanel);
        sidePanel.add(legendPanel);
        sidePanel.add(buttonPanel);
        sidePanel.add(Box.createVerticalStrut(18));
        sidePanel.add(noteLabel);
        sidePanel.add(Box.createVerticalStrut(6));
        sidePanel.add(useLimitLabel);

        contentPanel.add(seatPanel, BorderLayout.CENTER);
        contentPanel.add(sidePanel, BorderLayout.EAST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createSeatButton(int seatNumber) {
        JButton button = new JButton(String.valueOf(seatNumber)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        button.setPreferredSize(new Dimension(44, 42));
        button.setBackground(AppStyle.PANEL);
        button.setForeground(AppStyle.TEXT);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setRolloverEnabled(false);
        button.setBorder(AppStyle.lineBorder());

        int finalSeatNumber = seatNumber;
        button.addActionListener(e -> {
            if (ReservationState.isCheckedIn() && ReservationState.getSeatNumber() == finalSeatNumber) {
                showUsingSeatRemainingTime();
                return;
            }

            clearSelectedSeat();
            selectedSeatNumber = finalSeatNumber;
            selectedSeatButton = button;
            selectedSeatLabel.setText(finalSeatNumber + "번");
            button.setBackground(AppStyle.SELECTED_GRAY);
            button.setForeground(Color.WHITE);
        });

        return button;
    }

    private void addLegendRow(JPanel panel, int row, Color color, String text) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 0, 3, 8);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(createLegendBox(color), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(AppStyle.label(text), gbc);
    }

    private JLabel createLegendBox(Color color) {
        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(color);
        box.setBorder(AppStyle.lineBorder());
        box.setPreferredSize(new Dimension(18, 18));
        box.setMinimumSize(new Dimension(18, 18));
        box.setMaximumSize(new Dimension(18, 18));
        return box;
    }

    public JButton getSeatButton(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            return seatButtons[seatNumber - 1];
        }
        return null;
    }

    public JButton getReserveButton() {
        return reserveButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public int getSelectedSeatNumber() {
        return selectedSeatNumber;
    }

    private void reserveSelectedSeat() {
        if (ReservationState.hasReservationFor(userName)) {
            showErrorMessage("하나의 좌석만 예약 가능합니다.");
            return;
        }

        if (selectedSeatNumber == 0) {
            showErrorMessage("좌석을 먼저 선택하세요.");
            return;
        }

        // TODO: 실제 운영 단계에서는 아래 예약 가능 시간 검사를 다시 활성화해야 합니다.
        // 테스트 중에는 09:00~22:00 밖에서도 예약 흐름을 확인할 수 있도록 막지 않습니다.
        // if (!isReservableTime()) {
        //     showUnavailableTimeDialog();
        //     return;
        // }

        int result = JOptionPane.showOptionDialog(
                this,
                selectedSeatNumber + "번 좌석을 예약하시겠습니까?",
                "예약 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"예", "아니요"},
                "예"
        );
        if (result != 0) {
            return;
        }

        ReservationState.reserve(userName, selectedSeatNumber);
        setSeatReserved(selectedSeatNumber);
        showSuccessMessage(selectedSeatNumber + "번 좌석이 예약되었습니다.\n10분 이내에 입실완료해주세요.");
        selectedSeatNumber = 0;
        selectedSeatButton = null;
        selectedSeatLabel.setText("없음");
    }

    private boolean isReservableTime() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(22, 0));
    }

    private void showUnavailableTimeDialog() {
        JOptionPane.showOptionDialog(
                this,
                "죄송합니다. 예약 가능 시간이 아닙니다.\n예약 가능 시간 : 9:00~22:00",
                "예약 불가",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"돌아가기"},
                "돌아가기"
        );
    }

    private void startCurrentTimeClock() {
        currentTimeTimer = new Timer(1000, e -> updateCurrentTime());
        currentTimeTimer.start();
        updateCurrentTime();
    }

    private void updateCurrentTime() {
        currentTimeLabel.setText("현재 시간 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        autoCheckoutIfExpired();
    }

    private void showUsingSeatRemainingTime() {
        long remainingSeconds = getUseRemainingSeconds();
        if (remainingSeconds <= 0) {
            ReservationState.clear();
            JOptionPane.showMessageDialog(this, "이용 시간이 종료되어 퇴실처리되었습니다.", "퇴실 처리", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new MainView(userName);
            return;
        }

        JOptionPane.showMessageDialog(this, "퇴실까지 남은 시간 : " + formatSeconds(remainingSeconds), "남은 시간", JOptionPane.INFORMATION_MESSAGE);
    }

    private void autoCheckoutIfExpired() {
        if (!ReservationState.isCheckedIn()) {
            return;
        }

        if (getUseRemainingSeconds() <= 0) {
            ReservationState.clear();
            JOptionPane.showMessageDialog(this, "이용 시간이 종료되어 퇴실처리되었습니다.", "퇴실 처리", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private long getUseRemainingSeconds() {
        if (ReservationState.getCheckedInAt() == null) {
            return 0;
        }
        return 21600 - java.time.Duration.between(ReservationState.getCheckedInAt(), LocalDateTime.now()).getSeconds();
    }

    private String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void showCurrentReservation() {
        if (ReservationState.hasReservation()) {
            int seatNumber = ReservationState.getSeatNumber();
            if (ReservationState.isCheckedIn()) {
                setSeatInUse(seatNumber);
            } else {
                setSeatReserved(seatNumber);
            }
        }
    }

    private void clearSelectedSeat() {
        if (selectedSeatButton != null) {
            selectedSeatButton.setBackground(AppStyle.PANEL);
            selectedSeatButton.setForeground(AppStyle.TEXT);
        }
    }

    public void setSeatAvailable(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            seatButtons[seatNumber - 1].setBackground(Color.WHITE);
            seatButtons[seatNumber - 1].setForeground(Color.BLACK);
            seatButtons[seatNumber - 1].setEnabled(true);
        }
    }

    public void setSeatReserved(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            seatButtons[seatNumber - 1].setBackground(AppStyle.SOFT_PINK);
            seatButtons[seatNumber - 1].setForeground(Color.BLACK);
            seatButtons[seatNumber - 1].setEnabled(false);
        }
    }

    public void setSeatInUse(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            seatButtons[seatNumber - 1].setBackground(AppStyle.SOFT_BLUE);
            seatButtons[seatNumber - 1].setForeground(Color.BLACK);
            seatButtons[seatNumber - 1].setEnabled(true);
        }
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void dispose() {
        if (currentTimeTimer != null) {
            currentTimeTimer.stop();
        }
        super.dispose();
    }
}
