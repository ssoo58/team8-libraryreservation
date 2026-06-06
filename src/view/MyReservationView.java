package view;

import javax.swing.*;
import java.awt.*;

// 사용자의 현재 예약 정보를 보여주고 예약 취소, 입실 완료, 퇴실을 처리하는 View 클래스입니다.
// 예약 데이터의 실제 변경은 Controller가 처리하고, 이 클래스는 화면 갱신과 안내창을 담당합니다.
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

        cancelButton = AppStyle.outlineButton("예약 취소", 82, 30);

        mainButton = AppStyle.outlineButton("메인으로", 82, 30);

        checkInButton = AppStyle.outlineButton("입실 완료", 82, 30);

        buttonPanel.add(cancelButton);
        buttonPanel.add(mainButton);
        buttonPanel.add(checkInButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
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

    public boolean showCancelConfirmDialog(boolean checkedIn) {
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
        return result == 0;
    }

    public void showReservationInfo(int seatNumber, boolean checkedIn, String timeText) {
        statusLabel.setText("좌석 : " + seatNumber + "번   상태 : " + (checkedIn ? "입실 완료" : "예약 완료"));
        statusLabel.setBackground(checkedIn ? AppStyle.SOFT_BLUE : AppStyle.SOFT_PINK);
        cancelButton.setText(checkedIn ? "퇴실하기" : "예약 취소");
        checkInButton.setEnabled(!checkedIn);

        if (checkedIn) {
            reservationInfoArea.setText("예약자 : " + userId + "\n입실 시간 : " + timeText);
        } else {
            reservationInfoArea.setText("예약자 : " + userId + "\n예약 시간 : " + timeText);
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
