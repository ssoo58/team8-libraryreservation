package repository;

import model.Seat;
import model.Status;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 좌석 정보 저장소
 *
 * [객체지향 적용]
 * - 캡슐화 : 좌석 컬렉션은 private, 외부에서는 메서드로만 변경 가능
 * - 상속/다형성 : Repository<Seat, Integer> 인터페이스 구현
 * - 멀티스레드 동기화 : synchronized 로 좌석 상태 동시 접근 방지
 * - 파일 I/O : seats.txt 로 좌석 상태 영속화
 * - 좌석은 1~24번까지 고정으로 초기화
 */
public class SeatRepository implements Repository<Seat, Integer> {

    public static final int TOTAL_SEATS = 24;
    private static final String FILE_PATH = "seats.txt";
    private static SeatRepository instance;

    private final List<Seat> seats = new ArrayList<>();

    private SeatRepository() {
        loadFromFile();
        if (seats.isEmpty()) {
            initializeSeats();
            saveToFile();
        }
    }

    public static synchronized SeatRepository getInstance() {
        if (instance == null) {
            instance = new SeatRepository();
        }
        return instance;
    }

    private void initializeSeats() {
        seats.clear();
        for (int i = 1; i <= TOTAL_SEATS; i++) {
            seats.add(new Seat(i));
        }
    }

    /* ===== Repository 구현 ===== */

    @Override
    public synchronized void save(Seat seat) {
        if (seat == null) return;
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getSeatNumber() == seat.getSeatNumber()) {
                seats.set(i, seat);
                saveToFile();
                return;
            }
        }
        seats.add(seat);
        saveToFile();
    }

    @Override
    public synchronized Seat findById(Integer seatNumber) {
        if (seatNumber == null) return null;
        for (Seat s : seats) {
            if (s.getSeatNumber() == seatNumber) return s;
        }
        return null;
    }

    @Override
    public synchronized List<Seat> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(seats));
    }

    @Override
    public synchronized boolean deleteById(Integer seatNumber) {
        if (seatNumber == null) return false;
        boolean removed = seats.removeIf(s -> s.getSeatNumber() == seatNumber);
        if (removed) saveToFile();
        return removed;
    }

    @Override
    public synchronized int size() {
        return seats.size();
    }

    /* ===== 추가 비즈니스 메서드 ===== */

    /** 예약 가능한 좌석만 반환 */
    public synchronized List<Seat> findAvailableSeats() {
        List<Seat> result = new ArrayList<>();
        for (Seat s : seats) {
            if (s.isAvailable()) result.add(s);
        }
        return result;
    }

    /** 특정 사용자가 사용 중인 좌석 반환 (1인 1좌석이므로 단건) */
    public synchronized Seat findByUser(String userId) {
        if (userId == null) return null;
        for (Seat s : seats) {
            if (userId.equals(s.getCurrentUser()) && !s.isAvailable()) return s;
        }
        return null;
    }

    /** 모든 좌석을 초기 상태로 리셋 (테스트/관리자용) */
    public synchronized void resetAllSeats() {
        for (Seat s : seats) {
            s.setStatus(Status.AVAILABLE);
            s.setCurrentUser("");
        }
        saveToFile();
    }

    /* ===== 파일 I/O ===== */

    @Override
    public synchronized void loadFromFile() {
        seats.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Seat s = Seat.fromFileString(line);
                if (s != null) seats.add(s);
            }
        } catch (IOException e) {
            System.err.println("[SeatRepository] 파일 읽기 실패 : " + e.getMessage());
        }
    }

    @Override
    public synchronized void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Seat s : seats) {
                bw.write(s.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[SeatRepository] 파일 쓰기 실패 : " + e.getMessage());
        }
    }
}
