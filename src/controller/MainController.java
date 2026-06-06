package controller;

import view.MainView;
import view.SeatView;
import view.MyReservationView;
import view.LogoutView;

public class MainController {
    private MainView view;
    private String userId;

    public MainController(MainView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
    }

    private void initListeners() {
        view.getSeatSearchButton().addActionListener(e -> openSeatView());
        view.getMyReservationButton().addActionListener(e -> openMyReservation());
        view.getLogoutButton().addActionListener(e -> openLogout());
    }

    private void openSeatView() {
        view.dispose();
        SeatView seatView = new SeatView(userId);
        new SeatController(seatView, userId);
    }

    private void openMyReservation() {
        view.dispose();
        MyReservationView myView = new MyReservationView(userId);
        new MyReservationController(myView, userId);
    }

    private void openLogout() {
        view.dispose();
        LogoutView logoutView = new LogoutView(userId);
        new LogoutController(logoutView, userId);
    }
}
