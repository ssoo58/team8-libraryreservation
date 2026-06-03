package controller;

import model.ReservationState;
import view.MainView;
import view.MyReservationView;

public class MyReservationController {
    private MyReservationView view;
    private String userName;

    public MyReservationController(MyReservationView view, String userName) {
        this.view = view;
        this.userName = userName;
        initListeners();
    }

    private void initListeners() {
        view.getCancelButton().addActionListener(e -> cancel());
        view.getMainButton().addActionListener(e -> goMain());
        view.getCheckInButton().addActionListener(e -> checkIn());
    }

    private void cancel() {
        if (!ReservationState.hasReservationFor(userName)) {
            view.showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }
        ReservationState.clear();
        view.clearReservationInfo();
        view.showSuccessMessage("예약이 취소되었습니다.");
    }

    private void checkIn() {
        if (!ReservationState.hasReservationFor(userName)) {
            view.showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }
        ReservationState.checkIn();
        view.showSuccessMessage("입실 처리되었습니다.");
    }

    private void goMain() {
        view.dispose();
        new MainView(userName);
    }
}
