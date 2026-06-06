package controller;

import model.ReservationState;
import view.MainView;
import view.MyReservationView;

import java.time.format.DateTimeFormatter;

public class MyReservationController {
    private MyReservationView view;
    private String userId;

    public MyReservationController(MyReservationView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
        updateReservationView();
    }

    private void initListeners() {
        view.getCancelButton().addActionListener(e -> cancel());
        view.getMainButton().addActionListener(e -> goMain());
        view.getCheckInButton().addActionListener(e -> checkIn());
    }

    private void cancel() {
        if (!ReservationState.hasReservationFor(userId)) {
            view.showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }

        boolean checkedIn = ReservationState.isCheckedIn();
        if (!view.showCancelConfirmDialog(checkedIn)) {
            return;
        }

        ReservationState.clear();
        view.clearReservationInfo();
        view.showSuccessMessage(checkedIn ? "퇴실 처리되었습니다." : "예약 취소되었습니다.");
    }

    private void checkIn() {
        if (!ReservationState.hasReservationFor(userId)) {
            view.showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }
        ReservationState.checkIn();
        updateReservationView();
        view.showSuccessMessage("입실 처리되었습니다.");
    }

    private void goMain() {
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }

    private void updateReservationView() {
        if (!ReservationState.hasReservationFor(userId)) {
            view.clearReservationInfo();
            return;
        }

        int seatNumber = ReservationState.getSeatNumber();
        boolean checkedIn = ReservationState.isCheckedIn();
        String timeText;
        if (checkedIn) {
            timeText = ReservationState.getCheckedInAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            timeText = ReservationState.getReservedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        view.showReservationInfo(seatNumber, checkedIn, timeText);
    }
}
