package model;

/**
 * 좌석의 상태를 나타내는 열거형
 * - AVAILABLE : 예약 가능 (흰색)
 * - RESERVED  : 예약 중 (분홍)
 * - IN_USE    : 사용 중 (파랑)
 *
 * [객체지향 적용]
 * - 좌석 상태 값을 enum 으로 캡슐화하여 잘못된 값이 들어오는 것을 방지
 * - 상태 변경 규칙(AVAILABLE -> RESERVED -> IN_USE -> AVAILABLE)을 한 곳에서 관리
 */
public enum Status {
    AVAILABLE("예약 가능"),
    RESERVED("예약 중"),
    IN_USE("사용 중");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 다음 상태로의 전이가 유효한지 검사
     * AVAILABLE -> RESERVED, RESERVED -> IN_USE, IN_USE -> AVAILABLE, RESERVED -> AVAILABLE(취소)
     */
    public boolean canTransitTo(Status next) {
        switch (this) {
            case AVAILABLE: return next == RESERVED;
            case RESERVED:  return next == IN_USE || next == AVAILABLE;
            case IN_USE:    return next == AVAILABLE;
            default:        return false;
        }
    }
}