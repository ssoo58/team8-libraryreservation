package controller;

import model.ReservationState;
import repository.SeatRepository;
import view.MainView;
import view.MyReservationView;
import view.SeatView;

public class SeatController {
    private SeatView view;
    private String userId;

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
            view.showErrorMessage("이미 예약된 좌석이 있습니다.");
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

    private void goBack() {
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }

    private void openMyReservation() {
        view.dispose();
        MyReservationView myView = new MyReservationView(userId);
        new MyReservationController(myView, userId);
    }
}
