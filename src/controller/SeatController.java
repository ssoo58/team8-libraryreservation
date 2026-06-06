package controller;

import model.Reservation;
import model.ReservationState;
import repository.ReservationRepository;
import repository.SeatRepository;
import view.MainView;
import view.MyReservationView;
import view.SeatView;

import javax.swing.Timer;

// 좌석 조회 및 예약 화면을 담당하는 Controller 클래스입니다.
// 좌석 상태 로드, 예약 처리, 남은 이용시간 표시를 담당합니다.
public class SeatController {
    private SeatView view;
    private String userId;
    private Timer remainingTimeTimer; // 사용 중인 좌석의 남은 시간 갱신용 타이머

    public SeatController(SeatView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
        loadSeats();
    }

    // 예약, 뒤로가기, 내 예약 확인 버튼 및 24개 좌석 버튼에 이벤트를 연결합니다.
    private void initListeners() {
        view.getReserveButton().addActionListener(e -> reserve());
        view.getBackButton().addActionListener(e -> goBack());
        view.getMyReservationButton().addActionListener(e -> openMyReservation());
        for (int i = 1; i <= 24; i++) {
            int seatNum = i;
            if (view.getSeatButton(seatNum) != null) {
                view.getSeatButton(seatNum).addActionListener(e -> showRemainingTime(seatNum));
            }
        }
    }
    
    // Repository에서 전체 좌석 상태를 조회해 View에 색상으로 반영합니다. 
    private void loadSeats() {
        SeatRepository.getInstance().findAll().forEach(seat -> {
            switch (seat.getStatus()) {
                case RESERVED:
                    view.setSeatReserved(seat.getSeatNumber());
                    break;
                case IN_USE:
                    view.setSeatInUse(seat.getSeatNumber());
                    break;
                default:
                    view.setSeatAvailable(seat.getSeatNumber());
                    break;
            }
        });
    }

    // 선택한 좌석을 예약하고 View에 결과를 반영합니다.
    private void reserve() {
        if (ReservationState.hasReservationFor(userId)) {
            view.showErrorMessage("하나의 좌석만 예약 가능합니다.");
            return;
        }

        int seatNumber = view.getSelectedSeatNumber();
        if (seatNumber == 0) {
            view.showErrorMessage("좌석을 먼저 선택하세요.");
            return;
        }

        if (!view.showReserveConfirmDialog(seatNumber)) {
            return;
        }

        ReservationState.reserve(userId, seatNumber);
        view.setSeatReserved(seatNumber);
        view.showSuccessMessage(seatNumber + "번 좌석이 예약되었습니다.\n10분 이내에 입실완료해주세요.");
        view.clearSelectedSeatInfo();
    }

    // 사용 중인 좌석 클릭 시 남은 이용시간 다이얼로그를 열고 타이머를 시작합니다.
    private void showRemainingTime(int seatNumber) {
        Reservation reservation = ReservationRepository.getInstance().findBySeatNumber(seatNumber);
        if (reservation != null && reservation.isCheckedIn()) {
            stopRemainingTimeTimer();
            SeatView.RemainingTimeDialog dialog = view.showUseRemainingTimeDialog(formatUseRemainingTime(seatNumber));
            remainingTimeTimer = new Timer(1000, e -> updateRemainingTimeDialog(seatNumber, dialog));
            dialog.onClose(this::stopRemainingTimeTimer);
            remainingTimeTimer.start();
        }
    }

    // 1초마다 다이얼로그의 남은 시간을 갱신하고, 시간 초과 시 자동 퇴실 처리합니다.
    private void updateRemainingTimeDialog(int seatNumber, SeatView.RemainingTimeDialog dialog) {
        if (!dialog.isShowing()) {
            stopRemainingTimeTimer();
            return;
        }

        long remaining = getUseRemainingSeconds(seatNumber);
        if (remaining <= 0) {
            stopRemainingTimeTimer();
            ReservationRepository.getInstance().checkExpiredReservations();
            loadSeats();
            dialog.close();
            view.showSuccessMessage("이용 시간이 종료된 좌석입니다.");
            return;
        }

        dialog.updateTime(formatSeconds(remaining));
    }

    // 표시할 남은 시간 문자열을 반환합니다.
    private String formatUseRemainingTime(int seatNumber) {
        return formatSeconds(Math.max(0, getUseRemainingSeconds(seatNumber)));
    }

    // Repository에서 해당 좌석의 남은 이용 초를 계산합니다.
    private long getUseRemainingSeconds(int seatNumber) {
        Reservation reservation = ReservationRepository.getInstance().findBySeatNumber(seatNumber);
        if (reservation == null || !reservation.isCheckedIn()) {
            return 0;
        }
        return reservation.getRemainingUseSeconds();
    }

    // 초를 HH:MM:SS 형식으로 변환합니다.
    private String formatSeconds(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    // 남은 시간 타이머를 정지하고 초기화합니다.
    private void stopRemainingTimeTimer() {
        if (remainingTimeTimer != null) {
            remainingTimeTimer.stop();
            remainingTimeTimer = null;
        }
    }

    // 메인 화면으로 이동 시 타이머를 정지합니다.
    private void goBack() {
        stopRemainingTimeTimer();
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }

    // 내 예약 확인 화면으로 이동 시 타이머를 정지합니다.
    private void openMyReservation() {
        stopRemainingTimeTimer();
        view.dispose();
        MyReservationView myView = new MyReservationView(userId);
        new MyReservationController(myView, userId);
    }
}
