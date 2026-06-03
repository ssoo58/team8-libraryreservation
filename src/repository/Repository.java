package repository;

import java.util.List;

/**
 * 모든 Repository 가 구현하는 공통 인터페이스
 *
 * [객체지향 적용]
 * - 상속 / 다형성 : MemberRepository, SeatRepository, ReservationRepository 가
 *   이 인터페이스를 구현하므로, 같은 메서드 시그니처로 다른 동작을 수행
 * - 제네릭 : 도메인 객체 타입 T, 식별자 타입 ID 를 타입 파라미터로 받아
 *   타입 안정성을 보장
 *
 * @param <T>  관리할 도메인 객체 타입 (Member, Seat, Reservation)
 * @param <ID> 식별자 타입 (String, Integer 등)
 */
public interface Repository<T, ID> {

    /** 객체 저장 (없으면 추가, 있으면 갱신) */
    void save(T entity);

    /** 식별자로 단건 조회 */
    T findById(ID id);

    /** 전체 조회 */
    List<T> findAll();

    /** 식별자로 삭제 */
    boolean deleteById(ID id);

    /** 전체 개수 */
    int size();

    /** 파일에서 불러오기 */
    void loadFromFile();

    /** 파일로 저장 */
    void saveToFile();
}
