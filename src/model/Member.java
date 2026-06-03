package model;

import java.util.Objects;

/**
 * 회원(Member) 도메인 객체
 *
 * [객체지향 적용]
 * - 캡슐화 : id, password 를 private 으로 선언하고 getter/setter 로만 접근
 * - 식별자(id) 기준의 equals/hashCode 로 컬렉션에서 안전하게 비교
 */
public class Member {
    private String id;       // 학번
    private String password; // 비밀번호

    public Member() {
    }

    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** 비밀번호 일치 여부 확인 (로그인 검증에 사용) */
    public boolean matchPassword(String input) {
        return password != null && password.equals(input);
    }

    /** 파일 저장 직렬화 : "id|password" */
    public String toFileString() {
        return id + "|" + password;
    }

    /** 파일 역직렬화 */
    public static Member fromFileString(String line) {
        String[] tokens = line.split("\\|", -1);
        if (tokens.length < 2) {
            return null;
        }
        return new Member(tokens[0], tokens[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Member{id='" + id + "'}";
    }
}
