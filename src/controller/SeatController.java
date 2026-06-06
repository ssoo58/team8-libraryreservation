package controller;

import model.Reservation;
import model.ReservationState;
import repository.ReservationRepository;
import repository.SeatRepository;
import view.MainView;
import view.MyReservationView;
import view.SeatView;

import javax.swing.Timer;

public class SeatController {
    private SeatView view;
    private String userId;
    private Timer remainingTimeTimer;

    public SeatController(SeatView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
        loadSeats();
    }

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

        if (!ReservationState.isReservableTime()) {
            view.showErrorMessage("예약 가능 시간이 아닙니다.\n예약 가능 시간 : 9:00~22:00");
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

    // 사용 중인 좌석 클릭 시 남은 이용시간 표시
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

    private String formatUseRemainingTime(int seatNumber) {
        return formatSeconds(Math.max(0, getUseRemainingSeconds(seatNumber)));
    }

    private long getUseRemainingSeconds(int seatNumber) {
        Reservation reservation = ReservationRepository.getInstance().findBySeatNumber(seatNumber);
        if (reservation == null || !reservation.isCheckedIn()) {
            return 0;
        }
        return reservation.getRemainingUseSeconds();
    }

    private String formatSeconds(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void stopRemainingTimeTimer() {
        if (remainingTimeTimer != null) {
            remainingTimeTimer.stop();
            remainingTimeTimer = null;
        }
    }

    private void goBack() {
        stopRemainingTimeTimer();
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }

    private void openMyReservation() {
        stopRemainingTimeTimer();
        view.dispose();
        MyReservationView myView = new MyReservationView(userId);
        new MyReservationController(myView, userId);
    }
}
