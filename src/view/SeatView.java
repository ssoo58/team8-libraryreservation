package view;

import model.ReservationState;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 좌석 현황을 보여주고 사용자가 좌석을 선택/예약할 수 있게 하는 View 클래스입니다.
// 화면 표시와 사용자 입력 처리를 맡고, 예약 상태 변경은 ReservationState(model facade)에 요청합니다.
public class SeatView extends JFrame {
    // 24개의 좌석 버튼을 배열로 관리해 좌석 번호와 버튼 인덱스를 쉽게 연결합니다.
    private JButton[] seatButtons;

    // 사용자가 현재 선택한 좌석을 화면 오른쪽에 보여주는 라벨입니다.
    private JLabel selectedSeatLabel;
    private JButton reserveButton;
    private JButton backButton;
    private JButton myReservationButton;

    // 아직 좌석을 선택하지 않았을 때는 0이고, 좌석 클릭 시 해당 좌석 번호가 저장됩니다.
    private int selectedSeatNumber;

    // 현재 로그인한 사용자 ID입니다.
    private String userId;

    // 이전에 선택한 좌석 버튼 색을 원래대로 돌리기 위해 보관합니다.
    private JButton selectedSeatButton;

    // 현재 시간 표시를 위해 사용하는 Swing Timer입니다.
    private JLabel currentTimeLabel;
    private Timer currentTimeTimer;

    public SeatView() {
        // 테스트나 기본 실행 시 사용자 정보가 없을 때 사용할 기본 생성자입니다.
        this("사용자");
    }

    public SeatView(String userId) {
        // MainView에서 전달받은 로그인 사용자 식별값을 저장합니다.
        this.userId = userId;

        // 좌석 조회/예약 화면의 기본 창 설정입니다.
        setTitle("좌석 조회 및 예약");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        // 화면 전체를 담는 기본 패널입니다.
        JPanel mainPanel = AppStyle.page(24, 26, 22, 26);

        // 상단 영역: 화면 제목과 현재 시간을 함께 보여줍니다.
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

        // 가운데는 좌석 표, 오른쪽은 선택 정보와 버튼 영역으로 나눕니다.
        JPanel contentPanel = new JPanel(new BorderLayout(28, 0));
        contentPanel.setBackground(AppStyle.PANEL);

        // 좌석은 4행 6열, 총 24개로 배치합니다.
        JPanel seatPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        seatPanel.setBackground(AppStyle.PANEL);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // 좌석 번호는 1번부터 시작하므로 i + 1을 createSeatButton에 넘깁니다.
        seatButtons = new JButton[24];
        for (int i = 0; i < 24; i++) {
            seatButtons[i] = createSeatButton(i + 1);
            seatPanel.add(seatButtons[i]);
        }
        // model에 이미 예약/입실 상태가 있으면 화면 색상에 반영합니다.
        showCurrentReservation();

        // 오른쪽 사이드 패널에는 선택 좌석, 색상 범례, 이동 버튼, 안내 문구를 배치합니다.
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

        // 좌석 색상의 의미를 알려주는 범례 영역입니다.
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

        // 예약 버튼은 현재 선택한 좌석을 기준으로 reserveSelectedSeat()를 실행합니다.
        reserveButton = AppStyle.outlineButton("예약하기", 130, 30);
        reserveButton.addActionListener(e -> reserveSelectedSeat());

        // 내 예약 확인 화면으로 이동할 때도 현재 사용자 식별값을 넘깁니다.
        myReservationButton = AppStyle.outlineButton("내 예약 확인", 130, 30);
        myReservationButton.addActionListener(e -> {
            dispose();
            new MyReservationView(userId);
        });

        // 메인 화면으로 돌아갑니다.
        backButton = AppStyle.outlineButton("메인으로", 130, 30);
        backButton.addActionListener(e -> {
            dispose();
            new MainView(userId);
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

    // 좌석 하나에 해당하는 버튼을 만들고, 클릭했을 때의 선택 동작을 연결합니다.
    private JButton createSeatButton(int seatNumber) {
        JButton button = new JButton(String.valueOf(seatNumber)) {
            @Override
            protected void paintComponent(Graphics g) {
                // 버튼 배경색을 직접 채워 좌석 상태 색상이 안정적으로 보이게 합니다.
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
            // 입실 완료된 본인 좌석은 새 예약 선택 대상이 아니므로 사용 중 안내만 보여줍니다.
            // 남은 사용시간 계산과 표시 호출은 Controller가 담당하고, 필요한 시간 데이터는 Model에서 제공합니다.
            if (ReservationState.isCheckedIn() && ReservationState.getSeatNumber() == finalSeatNumber) {
                showInUseSeatMessage();
                return;
            }

            // 새 좌석을 선택하기 전에 기존 선택 좌석의 색상을 원래대로 되돌립니다.
            clearSelectedSeat();
            selectedSeatNumber = finalSeatNumber;
            selectedSeatButton = button;
            selectedSeatLabel.setText(finalSeatNumber + "번");
            button.setBackground(AppStyle.SELECTED_GRAY);
            button.setForeground(Color.WHITE);
        });

        return button;
    }

    // 범례 한 줄을 추가합니다. 왼쪽은 색상 박스, 오른쪽은 설명 텍스트입니다.
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

    // 범례에 표시할 작은 색상 박스를 만듭니다.
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

    // Controller나 테스트 코드에서 특정 좌석 버튼에 접근할 수 있게 합니다.
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

    // 사용자가 예약하기 버튼을 눌렀을 때 실행되는 핵심 흐름입니다.
    private void reserveSelectedSeat() {
        // model에 현재 사용자의 예약이 이미 있는지 확인합니다.
        if (ReservationState.hasReservationFor(userId)) {
            showErrorMessage("하나의 좌석만 예약 가능합니다.");
            return;
        }

        // 좌석을 선택하지 않은 상태에서는 예약을 진행하지 않습니다.
        if (selectedSeatNumber == 0) {
            showErrorMessage("좌석을 먼저 선택하세요.");
            return;
        }

        // 실제 예약 전 사용자에게 한 번 더 확인을 받습니다.
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

        // 예약 상태 변경은 model 역할이므로 ReservationState에 요청합니다.
        // View는 요청 후 화면의 좌석 색상과 선택 표시를 갱신합니다.
        ReservationState.reserve(userId, selectedSeatNumber);
        setSeatReserved(selectedSeatNumber);
        showSuccessMessage(selectedSeatNumber + "번 좌석이 예약되었습니다.\n10분 이내에 입실완료해주세요.");
        selectedSeatNumber = 0;
        selectedSeatButton = null;
        selectedSeatLabel.setText("없음");
    }

    // 1초마다 현재 시간 표시를 갱신합니다.
    private void startCurrentTimeClock() {
        currentTimeTimer = new Timer(1000, e -> updateCurrentTime());
        currentTimeTimer.start();
        updateCurrentTime();
    }

    // 화면 오른쪽 상단의 현재 시간을 갱신합니다.
    private void updateCurrentTime() {
        currentTimeLabel.setText("현재 시간 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    // 입실 중인 좌석을 눌렀을 때 보여주는 단순 안내창입니다.
    // 남은 이용 시간 계산과 자동 퇴실 판단은 Controller가 담당하고, 필요한 시간 데이터는 Model에서 제공합니다.
    private void showInUseSeatMessage() {
        JOptionPane.showMessageDialog(this, "현재 사용 중인 좌석입니다.", "좌석 사용 중", JOptionPane.INFORMATION_MESSAGE);
    }

    // Controller가 계산해서 전달한 남은 사용시간 문구를 다이얼로그로 표시합니다.
    public void showUseRemainingTime(String remainingTimeText) {
        JOptionPane.showMessageDialog(this, "퇴실까지 남은 시간 : " + remainingTimeText, "남은 시간", JOptionPane.INFORMATION_MESSAGE);
    }

    // 화면을 열 때 model에 저장된 현재 예약 상태를 좌석 색상에 반영합니다.
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

    // 이전에 선택했던 좌석 버튼이 있으면 기본 색상으로 되돌립니다.
    private void clearSelectedSeat() {
        if (selectedSeatButton != null) {
            selectedSeatButton.setBackground(AppStyle.PANEL);
            selectedSeatButton.setForeground(AppStyle.TEXT);
        }
    }

    // 좌석을 예약 가능 상태로 표시합니다.
    public void setSeatAvailable(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            seatButtons[seatNumber - 1].setBackground(Color.WHITE);
            seatButtons[seatNumber - 1].setForeground(Color.BLACK);
            seatButtons[seatNumber - 1].setEnabled(true);
        }
    }

    // 좌석을 예약 중 상태로 표시합니다.
    public void setSeatReserved(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            seatButtons[seatNumber - 1].setBackground(AppStyle.SOFT_PINK);
            seatButtons[seatNumber - 1].setForeground(Color.BLACK);
            seatButtons[seatNumber - 1].setEnabled(false);
        }
    }

    // 좌석을 실제 사용 중 상태로 표시합니다.
    public void setSeatInUse(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 24) {
            seatButtons[seatNumber - 1].setBackground(AppStyle.SOFT_BLUE);
            seatButtons[seatNumber - 1].setForeground(Color.BLACK);
            seatButtons[seatNumber - 1].setEnabled(true);
        }
    }

    // 예약 성공, 입실 성공 같은 긍정 결과를 알립니다.
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
    }

    // 예약 실패, 선택 누락 같은 오류 상황을 알립니다.
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void dispose() {
        // 화면이 닫힌 뒤에도 Timer가 계속 실행되지 않도록 정리합니다.
        if (currentTimeTimer != null) {
            currentTimeTimer.stop();
        }
        super.dispose();
    }
}
