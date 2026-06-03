package model;

import java.util.Objects;

/**
 * 좌석(Seat) 도메인 객체
 *
 * [객체지향 적용]
 * - 캡슐화 : 좌석 번호와 상태를 private 으로 보호
 * - 상태 전이 로직(reserve / checkIn / release)을 Seat 객체 내부에 두어 결합도를 낮춤
 * - 상태 변경은 Status.canTransitTo 로 유효성 검사 후에만 적용
 */
public class Seat {
    private int seatNumber;     // 좌석 번호 (1~24)
    private Status status;      // 현재 상태
    private String currentUser; // 현재 사용자 (id), 비어있을 수 있음

    public Seat() {
        this.status = Status.AVAILABLE;
    }

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.status = Status.AVAILABLE;
        this.currentUser = "";
    }

    public Seat(int seatNumber, Status status, String currentUser) {
        this.seatNumber = seatNumber;
        this.status = status;
        this.currentUser = currentUser == null ? "" : currentUser;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser == null ? "" : currentUser;
    }

    /** 예약 가능 여부 */
    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }

    /**
     * 좌석 예약 처리. AVAILABLE 상태에서만 가능
     * @return 성공 여부
     */
    public boolean reserve(String userId) {
        if (!status.canTransitTo(Status.RESERVED)) {
            return false;
        }
        this.status = Status.RESERVED;
        this.currentUser = userId;
        return true;
    }

    /**
     * 입실 처리. RESERVED -> IN_USE
     */
    public boolean checkIn() {
        if (!status.canTransitTo(Status.IN_USE)) {
            return false;
        }
        this.status = Status.IN_USE;
        return true;
    }

    /**
     * 좌석 해제(예약 취소 또는 퇴실). -> AVAILABLE
     */
    public boolean release() {
        if (!status.canTransitTo(Status.AVAILABLE)) {
            return false;
        }
        this.status = Status.AVAILABLE;
        this.currentUser = "";
        return true;
    }

    /** 파일 저장 직렬화 : "seatNumber|status|currentUser" */
    public String toFileString() {
        return seatNumber + "|" + status.name() + "|" + (currentUser == null ? "" : currentUser);
    }

    /** 파일 역직렬화 */
    public static Seat fromFileString(String line) {
        String[] tokens = line.split("\\|", -1);
        if (tokens.length < 2) {
            return null;
        }
        try {
            int num = Integer.parseInt(tokens[0]);
            Status st = Status.valueOf(tokens[1]);
            String user = tokens.length >= 3 ? tokens[2] : "";
            return new Seat(num, st, user);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat)) return false;
        Seat seat = (Seat) o;
        return seatNumber == seat.seatNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNumber);
    }

    @Override
    public String toString() {
        return "Seat{no=" + seatNumber + ", status=" + status + ", user='" + currentUser + "'}";
    }
}
