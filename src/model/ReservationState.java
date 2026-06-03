package model;

import repository.ReservationRepository;
import repository.SeatRepository;

import java.time.LocalDateTime;

/**
 * View 가 호출하는 정적(static) 파사드(Facade) 클래스
 *
 * 기존 View 코드(LoginView, SeatView, MyReservationView, LogoutView)가
 * model.ReservationState 의 정적 메서드를 직접 호출하고 있기 때문에,
 * 호환성을 유지하기 위해 별도의 진입점을 제공한다.
 *
 * 내부적으로는 Repository(ReservationRepository, SeatRepository)에 위임하여
 * "현재 로그인 사용자의 예약 1건"이라는 단순한 시점 상태를 다룬다.
 *
 * [객체지향 적용]
 * - View 는 Repository 의 구체적 구조를 모르고 ReservationState 의
 *   단순한 API 만 호출하므로 의존성 역전(View -> Facade -> Repository) 구조
 * - 현재 사용자 컨텍스트를 보관하여 한 사용자가 한 좌석만 사용하도록 강제
 */
public class ReservationState {

    private static String currentUserId;       // 현재 로그인한 사용자
    private static Reservation currentReservation; // 현재 사용자의 예약(없으면 null)

    private ReservationState() {
        // 인스턴스화 금지
    }

    /** 현재 로그인 사용자 설정 (LoginController 등에서 호출) */
    public static void setCurrentUser(String userId) {
        currentUserId = userId;
        // 로그인 시 해당 사용자에게 저장된 예약을 불러옴 (재로그인/재실행 시에도 유지)
        currentReservation = ReservationRepository.getInstance().findByUserId(userId);
    }

    public static String getCurrentUser() {
        return currentUserId;
    }

    /* ===== View 가 호출하는 API ===== */

    /** 현재 사용자에게 예약이 있는지 */
    public static boolean hasReservation() {
        return currentReservation != null;
    }

    /** 지정한 사용자명으로 예약이 있는지 */
    public static boolean hasReservationFor(String userName) {
        return currentReservation != null
                && currentReservation.getUserId() != null
                && currentReservation.getUserId().equals(userName);
    }

    /** 입실 완료 여부 */
    public static boolean isCheckedIn() {
        return currentReservation != null && currentReservation.isCheckedIn();
    }

    /** 예약된 좌석 번호 (없으면 0) */
    public static int getSeatNumber() {
        return currentReservation == null ? 0 : currentReservation.getSeatNumber();
    }

    /** 예약 시간 */
    public static LocalDateTime getReservedAt() {
        return currentReservation == null ? null : currentReservation.getReservedAt();
    }

    /** 입실 시간 */
    public static LocalDateTime getCheckedInAt() {
        return currentReservation == null ? null : currentReservation.getCheckedInAt();
    }

    /**
     * 좌석 예약
     * - Reservation 객체 생성
     * - SeatRepository 의 좌석 상태를 RESERVED 로 변경
     * - ReservationRepository 에 저장 후 파일 동기화
     */
    public static synchronized void reserve(String userName, int seatNumber) {
        Reservation reservation = new Reservation(userName, seatNumber, LocalDateTime.now());
        currentReservation = reservation;
        currentUserId = userName;

        // Seat 상태 갱신
        Seat seat = SeatRepository.getInstance().findById(seatNumber);
        if (seat != null) {
            seat.reserve(userName);
            SeatRepository.getInstance().save(seat);
        }
        // Reservation 저장
        ReservationRepository.getInstance().save(reservation);
    }

    /**
     * 입실 처리
     * - Reservation.checkIn 호출(입실시각/종료시각 기록)
     * - Seat 상태 IN_USE 로 변경
     */
    public static synchronized void checkIn() {
        if (currentReservation == null) return;
        currentReservation.checkIn();
        Seat seat = SeatRepository.getInstance().findById(currentReservation.getSeatNumber());
        if (seat != null) {
            seat.checkIn();
            SeatRepository.getInstance().save(seat);
        }
        ReservationRepository.getInstance().save(currentReservation);
    }

    /**
     * 예약 취소 또는 퇴실
     * - Seat 상태 AVAILABLE 로 변경
     * - Reservation 삭제
     */
    public static synchronized void clear() {
        if (currentReservation == null) return;
        Seat seat = SeatRepository.getInstance().findById(currentReservation.getSeatNumber());
        if (seat != null) {
            seat.release();
            SeatRepository.getInstance().save(seat);
        }
        ReservationRepository.getInstance().deleteByUserId(currentReservation.getUserId());
        currentReservation = null;
    }
}