package controller;

import view.MainView;
import view.SeatView;
import view.MyReservationView;
import view.LogoutView;

// 메인 화면을 담당하는 controller 클래스 입니다. 
// 좌석 조회, 내 예약 화면, 로그아웃 화면으로의 전환을 처리합니다. 
public class MainController {
    private MainView view;
    private String userId;

    public MainController(MainView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
    }

    // 각 버튼에 화면 전환 이벤트를 연결합니다. 
    private void initListeners() {
        view.getSeatSearchButton().addActionListener(e -> openSeatView());
        view.getMyReservationButton().addActionListener(e -> openMyReservation());
        view.getLogoutButton().addActionListener(e -> openLogout());
    }

    // 좌석 조회 화면으로 이동합니다. 
    private void openSeatView() {
        view.dispose();
        SeatView seatView = new SeatView(userId);
        new SeatController(seatView, userId);
    }

    // 내 예약 확인 화면으로 이동합니다. 
    private void openMyReservation() {
        view.dispose();
        MyReservationView myView = new MyReservationView(userId);
        new MyReservationController(myView, userId);
    }

    // 로그이웃 화면으로 이동합니다. 
    private void openLogout() {
        view.dispose();
        LogoutView logoutView = new LogoutView(userId);
        new LogoutController(logoutView, userId);
    }
}
