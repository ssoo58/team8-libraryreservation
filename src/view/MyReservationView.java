package view;

import model.ReservationState;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

// 사용자의 현재 예약 정보를 보여주고 예약 취소, 입실 완료, 퇴실을 처리하는 View 클래스입니다.
// 예약 데이터의 실제 변경은 ReservationState에 요청하고, 이 클래스는 화면 갱신과 안내창을 담당합니다.
public class MyReservationView extends JFrame {
    // 예약 상세 정보, 상태, 남은 시간을 화면에 표시하는 컴포넌트입니다.
    private JTextArea reservationInfoArea;
    private JLabel statusLabel;
    private JLabel timeLeftLabel;

    // 사용자가 실행할 수 있는 주요 기능 버튼입니다.
    private JButton cancelButton;
    private JButton mainButton;
    private JButton checkInButton;

    // 현재 로그인한 사용자 ID입니다.
    private String userId;

    public MyReservationView() {
        // 테스트나 기본 실행 시 사용할 기본 사용자값입니다.
        this("사용자");
    }

    public MyReservationView(String userId) {
        // 다른 View에서 전달받은 사용자 식별값을 보관합니다.
        this.userId = userId;

        // 내 예약 확인 화면의 기본 창 설정입니다.
        setTitle("내 예약 확인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        // 공통 여백을 가진 기본 패널입니다.
        JPanel mainPanel = AppStyle.page(64, 145, 58, 145);

        // 상단 제목 영역입니다.
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        JLabel titleLabel = AppStyle.label("내 예약 정보");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        titlePanel.add(titleLabel);

        // 예약 상태와 상세 정보를 담는 중앙 영역입니다.
        JPanel infoPanel = new JPanel(new BorderLayout(0, 12));
        infoPanel.setBackground(AppStyle.PANEL);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // 예약 없음/예약 완료/입실 완료 상태를 색상과 텍스트로 보여줍니다.
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(AppStyle.SOFT_GRAY);
        statusLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        statusLabel.setForeground(AppStyle.TEXT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));

        // 예약자, 예약 시간, 입실 시간 같은 상세 정보를 보여주는 읽기 전용 영역입니다.
        reservationInfoArea = new JTextArea();
        reservationInfoArea.setBackground(AppStyle.PANEL);
        reservationInfoArea.setForeground(AppStyle.TEXT);
        reservationInfoArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        reservationInfoArea.setEditable(false);
        reservationInfoArea.setLineWrap(true);
        reservationInfoArea.setWrapStyleWord(true);
        reservationInfoArea.setText("");
        reservationInfoArea.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        // 남은 시간 계산은 Controller가 담당하고, 필요한 시간 데이터는 Model에서 제공합니다.
        timeLeftLabel = AppStyle.label("");
        timeLeftLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel detailPanel = new JPanel(new BorderLayout(0, 6));
        detailPanel.setBackground(AppStyle.PANEL);
        detailPanel.add(reservationInfoArea, BorderLayout.CENTER);
        detailPanel.add(timeLeftLabel, BorderLayout.SOUTH);

        infoPanel.add(statusLabel, BorderLayout.NORTH);
        infoPanel.add(detailPanel, BorderLayout.CENTER);

        // 하단 버튼 영역입니다.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(AppStyle.PANEL);

        // 예약 전이면 예약 취소, 입실 후면 퇴실하기 역할을 합니다.
        cancelButton = AppStyle.outlineButton("예약 취소", 82, 30);
        cancelButton.addActionListener(e -> cancelReservation());

        // 메인 화면으로 돌아갑니다.
        mainButton = AppStyle.outlineButton("메인으로", 82, 30);
        mainButton.addActionListener(e -> {
            dispose();
            new MainView(userId);
        });

        // 예약 상태를 입실 완료 상태로 바꿉니다.
        checkInButton = AppStyle.outlineButton("입실 완료", 82, 30);
        checkInButton.addActionListener(e -> checkInReservation());

        buttonPanel.add(cancelButton);
        buttonPanel.add(mainButton);
        buttonPanel.add(checkInButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        // 화면이 열리자마자 model의 현재 예약 상태를 읽어 표시합니다.
        updateReservationView();
        setVisible(true);
    }

    // Controller 방식으로 이벤트를 연결할 수 있도록 버튼 getter를 제공합니다.
    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getMainButton() {
        return mainButton;
    }

    public JButton getCheckInButton() {
        return checkInButton;
    }

    // 예약 정보 텍스트를 외부에서 직접 설정할 때 사용합니다.
    public void setReservationInfo(String info) {
        reservationInfoArea.setText(info);
    }

    // 기존 예약 정보 뒤에 문구를 추가할 때 사용합니다.
    public void appendReservationInfo(String info) {
        reservationInfoArea.append(info);
    }

    // 예약이 없거나 취소되었을 때 화면을 초기 상태로 되돌립니다.
    public void clearReservationInfo() {
        reservationInfoArea.setText("예약 정보가 없습니다.");
        statusLabel.setText("예약된 좌석이 없습니다.");
        statusLabel.setBackground(AppStyle.SOFT_GRAY);
        clearTimeLeftText();
        cancelButton.setText("예약 취소");
        checkInButton.setEnabled(true);
    }

    // Controller가 계산해서 전달한 남은 시간 문구를 화면에 표시합니다.
    public void setTimeLeftText(String text) {
        timeLeftLabel.setText(text);
    }

    // 남은 시간 표시가 필요 없는 상태에서 라벨을 비웁니다.
    public void clearTimeLeftText() {
        timeLeftLabel.setText("");
    }

    // 예약 취소 또는 입실 후 퇴실하기 버튼을 눌렀을 때 실행됩니다.
    private void cancelReservation() {
        // 현재 사용자에게 예약이 없으면 더 진행하지 않고 오류 메시지를 보여줍니다.
        if (!ReservationState.hasReservationFor(userId)) {
            showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }

        // 입실 상태에 따라 버튼 의미와 확인 메시지를 다르게 보여줍니다.
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
            // 실제 예약 삭제와 좌석 해제는 model에 요청합니다.
            ReservationState.clear();
            clearReservationInfo();
            showSuccessMessage(checkedIn ? "퇴실 처리되었습니다." : "예약 취소되었습니다.");
        }
    }

    // 예약 상태를 입실 완료 상태로 변경합니다.
    private void checkInReservation() {
        if (!ReservationState.hasReservationFor(userId)) {
            showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }

        // 실제 상태 변경은 ReservationState가 처리하고, View는 변경된 결과를 다시 그립니다.
        ReservationState.checkIn();
        updateReservationView();
        showSuccessMessage("입실 처리되었습니다.");
    }

    // model의 예약 상태를 읽어 화면의 텍스트, 색상, 버튼 상태를 갱신합니다.
    private void updateReservationView() {
        if (!ReservationState.hasReservationFor(userId)) {
            clearReservationInfo();
            return;
        }

        int seatNumber = ReservationState.getSeatNumber();
        boolean checkedIn = ReservationState.isCheckedIn();
        statusLabel.setText("좌석 : " + seatNumber + "번   상태 : " + (checkedIn ? "입실 완료" : "예약 완료"));
        statusLabel.setBackground(checkedIn ? AppStyle.SOFT_BLUE : AppStyle.SOFT_PINK);
        cancelButton.setText(checkedIn ? "퇴실하기" : "예약 취소");
        checkInButton.setEnabled(!checkedIn);

        // 입실 완료 상태와 예약 완료 상태는 보여줄 시간 정보가 다릅니다.
        // 제한 시간 계산과 자동 취소/퇴실 판단은 Controller가 담당하고, 실제 상태 변경은 Model에 요청합니다.
        if (checkedIn) {
            String checkedInAt = ReservationState.getCheckedInAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            reservationInfoArea.setText("예약자 : " + userId + "\n입실 시간 : " + checkedInAt);
        } else {
            String reservedAt = ReservationState.getReservedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            reservationInfoArea.setText("예약자 : " + userId + "\n예약 시간 : " + reservedAt);
        }
    }

    // 성공 상황을 안내하는 메시지 창입니다.
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
    }

    // 오류 상황을 안내하는 메시지 창입니다.
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

}
