package repository;

import model.Reservation;
import model.Seat;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 예약 정보 저장소
 *
 * [객체지향 적용]
 * - 캡슐화 : 내부 예약 목록은 private, 메서드로만 접근
 * - 상속/다형성 : Repository<Reservation, String> 인터페이스 구현
 *   (사용자 id 를 식별자로 사용 : 1인 1예약 정책)
 * - 멀티스레드 동기화 : synchronized 와 자동 취소 스케줄러
 * - 파일 I/O : reservations.txt 로 예약 영속화
 *
 * [자동 취소 스케줄러]
 * - 30초마다 모든 예약을 검사하여
 *   1) 예약 후 10분 미입실 -> 자동 예약 취소
 *   2) 입실 후 6시간 초과  -> 자동 퇴실 처리
 */
public class ReservationRepository implements Repository<Reservation, String> {

    private static final String FILE_PATH = "reservations.txt";
    private static ReservationRepository instance;

    private final List<Reservation> reservations = new ArrayList<>();
    private ScheduledExecutorService scheduler;

    private ReservationRepository() {
        loadFromFile();
        startAutoCancelScheduler();
    }

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    /* ===== Repository 구현 ===== */

    @Override
    public synchronized void save(Reservation reservation) {
        if (reservation == null || reservation.getUserId() == null) return;
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getUserId().equals(reservation.getUserId())) {
                reservations.set(i, reservation);
                saveToFile();
                return;
            }
        }
        reservations.add(reservation);
        saveToFile();
    }

    @Override
    public synchronized Reservation findById(String userId) {
        return findByUserId(userId);
    }

    @Override
    public synchronized List<Reservation> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(reservations));
    }

    @Override
    public synchronized boolean deleteById(String userId) {
        return deleteByUserId(userId);
    }

    @Override
    public synchronized int size() {
        return reservations.size();
    }

    /* ===== 추가 비즈니스 메서드 ===== */

    /** 사용자 id 로 예약 조회 (1인 1예약) */
    public synchronized Reservation findByUserId(String userId) {
        if (userId == null) return null;
        for (Reservation r : reservations) {
            if (userId.equals(r.getUserId())) return r;
        }
        return null;
    }

    /** 좌석 번호로 예약 조회 */
    public synchronized Reservation findBySeatNumber(int seatNumber) {
        for (Reservation r : reservations) {
            if (r.getSeatNumber() == seatNumber) return r;
        }
        return null;
    }

    /** 사용자 id 로 예약 삭제 */
    public synchronized boolean deleteByUserId(String userId) {
        if (userId == null) return false;
        boolean removed = reservations.removeIf(r -> userId.equals(r.getUserId()));
        if (removed) saveToFile();
        return removed;
    }

    /* ===== 자동 취소 스케줄러 (멀티스레드 동기화) ===== */

    private void startAutoCancelScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ReservationAutoCancelScheduler");
            t.setDaemon(true); // 메인 프로그램 종료 시 함께 종료
            return t;
        });
        // 30초마다 실행
        scheduler.scheduleAtFixedRate(this::checkExpiredReservations,
                30, 30, TimeUnit.SECONDS);
    }

    /**
     * 만료된 예약 검사 및 정리
     * - 예약 후 10분 미입실 : 좌석 해제 + 예약 삭제
     * - 입실 후 6시간 초과 : 좌석 해제 + 예약 삭제
     */
    public synchronized void checkExpiredReservations() {
        Iterator<Reservation> it = reservations.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            Reservation r = it.next();
            if (r.isCheckInExpired() || r.isUseExpired()) {
                Seat seat = SeatRepository.getInstance().findById(r.getSeatNumber());
                if (seat != null) {
                    seat.release();
                    SeatRepository.getInstance().save(seat);
                }
                it.remove();
                changed = true;
            }
        }
        if (changed) saveToFile();
    }

    /** 프로그램 종료 시 호출하여 스케줄러 종료 */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /* ===== 파일 I/O ===== */

    @Override
    public synchronized void loadFromFile() {
        reservations.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Reservation r = Reservation.fromFileString(line);
                if (r != null) reservations.add(r);
            }
        } catch (IOException e) {
            System.err.println("[ReservationRepository] 파일 읽기 실패 : " + e.getMessage());
        }
    }

    @Override
    public synchronized void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Reservation r : reservations) {
                bw.write(r.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ReservationRepository] 파일 쓰기 실패 : " + e.getMessage());
        }
    }
}
