package controller;

import view.MainView;
import view.SeatView;
import view.MyReservationView;
import view.LogoutView;

public class MainController {
    private MainView view;
    private String userName;

    public MainController(MainView view, String userName) {
        this.view = view;
        this.userName = userName;
        initListeners();
    }

    private void initListeners() {
        view.getSeatSearchButton().addActionListener(e -> openSeatView());
        view.getMyReservationButton().addActionListener(e -> openMyReservation());
        view.getLogoutButton().addActionListener(e -> openLogout());
    }

    private void openSeatView() {
        view.dispose();
        new SeatView(userName);
    }

    private void openMyReservation() {
        view.dispose();
        new MyReservationView(userName);
    }

    private void openLogout() {
        view.dispose();
        new LogoutView(userName);
    }
}
