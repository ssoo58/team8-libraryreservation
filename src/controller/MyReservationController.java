package controller;

import model.ReservationState;
import view.MainView;
import view.MyReservationView;
import javax.swing.Timer;
import java.time.Duration;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

// 내 예약 확인 화면을 담당하는 Controller 클래스입니다.
// 예약 취소, 입실 처리, 남은 시간 타이머, 퇴실 자동 처리를 담당합니다.
public class MyReservationController {
    private MyReservationView view;
    private String userId;
    private Timer timer; // 남은 시간 갱신용 타이머 

    public MyReservationController(MyReservationView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
        updateReservationView();
        startTimer();
    }

    // 취소, 메인, 입실 버튼에 이벤트를 연결합니다.
    private void initListeners() {
        view.getCancelButton().addActionListener(e -> cancel());
        view.getMainButton().addActionListener(e -> goMain());
        view.getCheckInButton().addActionListener(e -> checkIn());
    }

    // 1초마다 남은 시간을 갱신하는 타이머를 시작합니다.
    private void startTimer() {
        timer = new Timer(1000, e -> updateTimeLeft());
        timer.start(); 
    }  

    // 예약 상태에 따라 남은 시간을 계산하고 View에 전달합니다.
    // 입실 전: 10분 제한 / 입실 후: 6시간 제한
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
    } 

    // 예약 취소 또는 퇴실 처리 후 타이머를 정지합니다.
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
        timer.stop(); 
        view.clearReservationInfo();
        view.showSuccessMessage(checkedIn ? "퇴실 처리되었습니다." : "예약 취소되었습니다.");
    }

    // 입실 처리 후 화면 정보를 갱신합니다.
    private void checkIn() {
        if (!ReservationState.hasReservationFor(userId)) {
            view.showErrorMessage("예약된 좌석이 없습니다.");
            return;
        }
        ReservationState.checkIn();
        updateReservationView();
        view.showSuccessMessage("입실 처리되었습니다.");
    }

    // 메인 화면으로 이동 시 타이머를 정지합니다.
    private void goMain() {
        if (timer != null) timer.stop();
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }

    // 현재 예약 상태를 조회해 View에 좌석 번호, 입실 여부, 시간 정보를 전달합니다.
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
