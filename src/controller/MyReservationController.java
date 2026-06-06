package controller;

import model.ReservationState;
import view.MainView;
import view.MyReservationView;
import javax.swing.Timer;
import java.time.Duration;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

public class MyReservationController {
    private MyReservationView view;
    private String userId;
    private Timer timer;

    public MyReservationController(MyReservationView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
        updateReservationView();
        startTimer();
    }

    private void initListeners() {
        view.getCancelButton().addActionListener(e -> cancel());
        view.getMainButton().addActionListener(e -> goMain());
        view.getCheckInButton().addActionListener(e -> checkIn());
    }

    private void startTimer() {
        timer = new Timer(1000, e -> updateTimeLeft());
        timer.start(); 
    } // <- 추가 

    private void updateTimeLeft() {
        if (!ReservationState.hasReservationFor(userId)) return;
        if (ReservationState.isCheckedIn()) {
            long remaining = 21600 - Duration.between(ReservationState.getCheckedInAt(), LocalDateTime.now()).getSeconds();
            if (remaining <= 0) {
                ReservationState.clear(); timer.stop();
                view.clearTimeLeftText();
                view.showSuccessMessage("이용 시간이 종료되어 퇴실처리되었습니다.");
                goMain();
            } else {
                long h=remaining/3600, m=(remaining%3600)/60, s=remaining%60;
                view.setTimeLeftText(String.format("퇴실까지 남은 시간 : %02d:%02d:%02d", h, m, s));
            }
        } else {
            long remaining = 600 - Duration.between(ReservationState.getReservedAt(), LocalDateTime.now()).getSeconds();
            if (remaining <= 0) {
                ReservationState.clear(); timer.stop();
                view.clearReservationInfo();
                view.showSuccessMessage("10분 내 미입실로 자동 취소되었습니다.");
                goMain();
            } else {
                long m=remaining/60, s=remaining%60;
                view.setTimeLeftText(String.format("남은 시간 : %02d:%02d", m, s));
            }
        }
    } // <- 추가 

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
        timer.stop(); // ← 이 줄 추가
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
        if (timer != null) timer.stop();
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
