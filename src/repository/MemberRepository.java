package repository;

import model.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 회원 정보 저장소
 *
 * [객체지향 적용]
 * - 캡슐화 : 내부 List<Member> 를 private 로 숨기고 메서드로만 접근
 * - 상속/다형성 : Repository<Member, String> 인터페이스 구현
 * - 제네릭 컬렉션 : List<Member> 로 타입 안정성 확보
 * - 파일 I/O : members.txt 로 영속성 유지 (프로그램 종료 후에도 회원 유지)
 * - 싱글톤 : 전역에서 동일한 저장소 인스턴스를 사용
 */
public class MemberRepository implements Repository<Member, String> {

    private static final String FILE_PATH = "members.txt";
    private static MemberRepository instance;

    private final List<Member> members = new ArrayList<>();

    private MemberRepository() {
        loadFromFile();
    }

    public static synchronized MemberRepository getInstance() {
        if (instance == null) {
            instance = new MemberRepository();
        }
        return instance;
    }

    /* ===== Repository 구현 ===== */

    @Override
    public synchronized void save(Member member) {
        if (member == null || member.getId() == null) return;
        // 동일 id 가 있으면 갱신, 없으면 추가
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId().equals(member.getId())) {
                members.set(i, member);
                saveToFile();
                return;
            }
        }
        members.add(member);
        saveToFile();
    }

    @Override
    public synchronized Member findById(String id) {
        if (id == null) return null;
        for (Member m : members) {
            if (id.equals(m.getId())) return m;
        }
        return null;
    }

    @Override
    public synchronized List<Member> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(members));
    }

    @Override
    public synchronized boolean deleteById(String id) {
        if (id == null) return false;
        boolean removed = members.removeIf(m -> id.equals(m.getId()));
        if (removed) saveToFile();
        return removed;
    }

    @Override
    public synchronized int size() {
        return members.size();
    }

    /* ===== 추가 비즈니스 메서드 ===== */

    /** 로그인 검증 (아이디/비밀번호) */
    public synchronized boolean authenticate(String id, String password) {
        Member m = findById(id);
        return m != null && m.matchPassword(password);
    }

    /** 회원가입 (중복 id 거부) */
    public synchronized boolean register(String id, String password) {
        if (id == null || id.isEmpty() || password == null || password.isEmpty()) return false;
        if (findById(id) != null) return false;
        members.add(new Member(id, password));
        saveToFile();
        return true;
    }

    /** 회원 탈퇴 */
    public synchronized boolean withdraw(String id) {
        return deleteById(id);
    }

    /* ===== 파일 I/O ===== */

    @Override
    public synchronized void loadFromFile() {
        members.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Member m = Member.fromFileString(line);
                if (m != null) members.add(m);
            }
        } catch (IOException e) {
            System.err.println("[MemberRepository] 파일 읽기 실패 : " + e.getMessage());
        }
    }

    @Override
    public synchronized void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Member m : members) {
                bw.write(m.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[MemberRepository] 파일 쓰기 실패 : " + e.getMessage());
        }
    }
}
