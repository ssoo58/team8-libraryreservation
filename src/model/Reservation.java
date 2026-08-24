package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 예약(Reservation) 도메인 객체
 *
 * [객체지향 적용]
 * - 캡슐화 : 예약자/좌석/시간 정보를 한 객체에 묶어 응집도 향상
 * - 비즈니스 규칙(자동 취소 10분, 이용 시간 6시간) 판정 메서드를 객체 안에서 제공
 *
 * 시간 정책
 * - 예약 후 10분(600초) 이내 입실하지 않으면 자동 취소
 * - 입실 후 6시간(21600초) 이용 가능
 */
public class Reservation {

    /** 예약 자동 취소 제한 시간 (초) */
    public static final long CHECK_IN_LIMIT_SECONDS = 600L;
    /** 입실 후 이용 가능 시간 (초) */
    public static final long USE_LIMIT_SECONDS = 21600L;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String userId;              // 예약자
    private int seatNumber;             // 예약 좌석
    private LocalDateTime reservedAt;   // 예약 시작 시간
    private LocalDateTime checkedInAt;  // 입실 시간 (null 가능)
    private LocalDateTime endTime;      // 종료 시간 (null 가능, 입실 시점 기준 6시간 후)

    public Reservation() {
    }

    public Reservation(String userId, int seatNumber, LocalDateTime reservedAt) {
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.reservedAt = reservedAt;
        this.checkedInAt = null;
        this.endTime = null;
    }

    public Reservation(String userId, int seatNumber, LocalDateTime reservedAt,
                       LocalDateTime checkedInAt, LocalDateTime endTime) {
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.reservedAt = reservedAt;
        this.checkedInAt = checkedInAt;
        this.endTime = endTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /** 입실 처리 : 입실시각을 기록하고 종료시간을 6시간 뒤로 계산 */
    public void checkIn() {
        this.checkedInAt = LocalDateTime.now();
        this.endTime = this.checkedInAt.plusSeconds(USE_LIMIT_SECONDS);
    }

    /** 입실 여부 */
    public boolean isCheckedIn() {
        return checkedInAt != null;
    }

    /** 예약 후 10분 자동 취소 대상인지 확인 */
    public boolean isCheckInExpired() {
        if (isCheckedIn() || reservedAt == null) {
            return false;
        }
        long elapsed = Duration.between(reservedAt, LocalDateTime.now()).getSeconds();
        return elapsed >= CHECK_IN_LIMIT_SECONDS;
    }

    /** 입실 후 6시간 이용시간 초과 여부 */
    public boolean isUseExpired() {
        if (!isCheckedIn()) {
            return false;
        }
        long elapsed = Duration.between(checkedInAt, LocalDateTime.now()).getSeconds();
        return elapsed >= USE_LIMIT_SECONDS;
    }

    /** 입실 마감까지 남은 초 */
    public long getRemainingCheckInSeconds() {
        if (isCheckedIn() || reservedAt == null) return 0L;
        return CHECK_IN_LIMIT_SECONDS - Duration.between(reservedAt, LocalDateTime.now()).getSeconds();
    }

    /** 퇴실까지 남은 초 */
    public long getRemainingUseSeconds() {
        if (!isCheckedIn()) return 0L;
        return USE_LIMIT_SECONDS - Duration.between(checkedInAt, LocalDateTime.now()).getSeconds();
    }

    /** 파일 저장 직렬화 : "userId|seatNumber|reservedAt|checkedInAt|endTime" */
    public String toFileString() {
        return userId + "|" + seatNumber + "|"
                + (reservedAt == null ? "" : reservedAt.format(FORMATTER)) + "|"
                + (checkedInAt == null ? "" : checkedInAt.format(FORMATTER)) + "|"
                + (endTime == null ? "" : endTime.format(FORMATTER));
    }

    /** 파일 역직렬화 */
    public static Reservation fromFileString(String line) {
        String[] tokens = line.split("\\|", -1);
        if (tokens.length < 3) {
            return null;
        }
        try {
            String userId = tokens[0];
            int seatNumber = Integer.parseInt(tokens[1]);
            LocalDateTime reservedAt = parseOrNull(tokens[2]);
            LocalDateTime checkedInAt = tokens.length >= 4 ? parseOrNull(tokens[3]) : null;
            LocalDateTime endTime = tokens.length >= 5 ? parseOrNull(tokens[4]) : null;
            return new Reservation(userId, seatNumber, reservedAt, checkedInAt, endTime);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDateTime parseOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return LocalDateTime.parse(s, FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return seatNumber == that.seatNumber && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, seatNumber);
    }

    @Override
    public String toString() {
        return "Reservation{user='" + userId + "', seat=" + seatNumber
                + ", reservedAt=" + reservedAt + ", checkedInAt=" + checkedInAt + "}";
    }
}