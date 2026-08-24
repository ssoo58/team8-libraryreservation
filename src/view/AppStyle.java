package view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

// View 패키지에서 공통으로 사용하는 색상, 폰트, 버튼 스타일을 모아둔 유틸리티 클래스입니다.
// 화면마다 같은 스타일을 반복 작성하지 않도록 만들어 UI 일관성과 코드 중복 감소에 도움을 줍니다.
final class AppStyle {
    // 화면 전체에서 공통으로 사용하는 색상 상수입니다.
    static final Color BACKGROUND = new Color(244, 245, 247);
    static final Color PANEL = Color.WHITE;
    static final Color PRIMARY = new Color(22, 104, 139);
    static final Color BORDER = new Color(54, 54, 54);
    static final Color SOFT_BLUE = new Color(214, 237, 245);
    static final Color SOFT_GRAY = new Color(222, 222, 222);
    static final Color SELECTED_GRAY = new Color(145, 145, 145);
    static final Color SOFT_PINK = new Color(244, 224, 239);
    static final Color TEXT = new Color(35, 35, 35);

    private AppStyle() {
        // 인스턴스를 만들 필요가 없는 스타일 전용 클래스이므로 생성자를 private으로 막습니다.
    }

    // 화면의 기본 패널을 만들고 전달받은 여백을 적용합니다.
    static JPanel page(int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return panel;
    }

    // 제목용 라벨을 생성합니다.
    static JLabel title(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, size));
        label.setForeground(PRIMARY);
        return label;
    }

    // 일반 안내 문구용 라벨을 생성합니다.
    static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        label.setForeground(TEXT);
        return label;
    }

    // 공통 스타일의 텍스트 입력창을 생성합니다.
    static JTextField textField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(160, 28));
        field.setFont(new Font("Dialog", Font.PLAIN, 12));
        field.setBorder(lineBorder());
        return field;
    }

    // 공통 스타일의 비밀번호 입력창을 생성합니다.
    static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(160, 28));
        field.setFont(new Font("Dialog", Font.PLAIN, 12));
        field.setBorder(lineBorder());
        return field;
    }

    // 테두리만 있는 기본 버튼을 생성합니다.
    static JButton outlineButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(PANEL);
        button.setForeground(TEXT);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorder(lineBorder());
        return button;
    }

    // 메인 메뉴처럼 큰 버튼이 필요할 때 사용하는 버튼 생성 메서드입니다.
    static JButton menuButton(String text) {
        JButton button = outlineButton(text, 260, 38);
        button.setFont(new Font("Dialog", Font.PLAIN, 13));
        return button;
    }

    // 모든 입력창과 버튼에서 공통으로 사용하는 선 테두리입니다.
    static Border lineBorder() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }
}
