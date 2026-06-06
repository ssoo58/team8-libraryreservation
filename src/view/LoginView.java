package view;

import javax.swing.*;
import java.awt.*;

// 로그인 화면을 담당하는 View 클래스입니다.
// 사용자의 입력을 받고, 실제 로그인 판단은 Controller가 처리할 수 있도록 입력값과 버튼을 제공합니다.
public class LoginView extends JFrame {
    // 사용자가 입력하는 아이디와 비밀번호 입력 컴포넌트입니다.
    private JTextField idField;
    private JPasswordField passwordField;

    // Controller가 이벤트를 연결할 수 있도록 필드로 보관하는 버튼입니다.
    private JButton loginButton;
    private JButton signupButton;

    public LoginView() {
        // JFrame 기본 설정: 창 제목, 종료 방식, 크기, 위치를 정합니다.
        setTitle("숙명여자대학교 도서관 좌석 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        // AppStyle을 사용해 모든 화면에서 같은 여백과 색을 가진 기본 패널을 만듭니다.
        JPanel mainPanel = AppStyle.page(72, 160, 70, 160);

        // 화면 상단 제목 영역입니다.
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JLabel titleLabel = AppStyle.title("숙명여자대학교 도서관 좌석 예약 시스템", 15);
        titlePanel.add(titleLabel);

        // 가운데 입력 영역입니다. GridBagLayout으로 라벨과 입력창을 행 단위로 정렬합니다.
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(AppStyle.PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = AppStyle.label("아이디(학번)");
        idField = AppStyle.textField();

        JLabel passwordLabel = AppStyle.label("비밀번호");
        passwordField = AppStyle.passwordField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        centerPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        centerPanel.add(idField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        centerPanel.add(passwordField, gbc);

        // 하단 버튼 영역입니다. 버튼 자체의 동작은 LoginController가 연결합니다.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        loginButton = AppStyle.outlineButton("로그인", 72, 28);

        signupButton = AppStyle.outlineButton("회원가입", 78, 28);

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Controller가 로그인 검사를 할 때 입력된 아이디를 가져가는 메서드입니다.
    public String getIdInput() {
        return idField.getText();
    }

    // JPasswordField는 char[]를 사용하지만, 현재 Controller와 맞추기 위해 String으로 변환해 반환합니다.
    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    // 비밀번호 입력창 자체가 필요할 때 Controller가 접근할 수 있게 제공합니다.
    public JPasswordField getPasswordField() {
        return passwordField;
    }

    // 로그인 실패 후 입력칸을 비울 때 사용합니다.
    public void clearInputs() {
        idField.setText("");
        passwordField.setText("");
    }

    // 버튼 getter들은 Controller가 View에 직접 ActionListener를 등록하기 위한 통로입니다.
    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getSignupButton() {
        return signupButton;
    }

    // View의 역할에 맞게 오류 메시지를 화면에 보여줍니다.
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    // 안내 메시지를 화면에 보여줍니다.
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "안내", JOptionPane.INFORMATION_MESSAGE);
    }
}
