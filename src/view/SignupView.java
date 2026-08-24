package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

// 회원가입 화면을 담당하는 View 클래스입니다.
// 아이디와 비밀번호를 입력받고, 비밀번호 확인이 일치하는지 화면에서 즉시 안내합니다.
public class SignupView extends JFrame {
    // 사용자가 입력하는 회원가입 정보입니다.
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField passwordConfirmField;

    // 비밀번호와 비밀번호 확인이 다를 때만 보이는 경고 라벨입니다.
    private JLabel passwordMismatchLabel;

    // 가입과 돌아가기 기능을 실행하는 버튼입니다.
    private JButton signupButton;
    private JButton backButton;

    public SignupView() {
        // 회원가입 화면의 기본 창 설정입니다.
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(AppStyle.BACKGROUND);

        // 공통 여백과 배경을 가진 기본 패널입니다.
        JPanel mainPanel = AppStyle.page(58, 160, 54, 160);

        // 상단 제목 영역입니다.
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(AppStyle.PANEL);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));
        titlePanel.add(AppStyle.title("회원가입", 15));

        // 입력 영역입니다. GridBagLayout으로 라벨과 입력창을 정렬합니다.
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(AppStyle.PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = AppStyle.textField();
        passwordField = AppStyle.passwordField();
        passwordConfirmField = AppStyle.passwordField();
        passwordMismatchLabel = new JLabel("비밀번호가 동일하지 않습니다.");
        passwordMismatchLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        passwordMismatchLabel.setForeground(Color.RED);
        passwordMismatchLabel.setVisible(false);

        // 반복되는 입력 행 생성 코드를 addInputRow로 분리해 중복을 줄였습니다.
        addInputRow(inputPanel, gbc, 0, "아이디(학번)", idField);
        addInputRow(inputPanel, gbc, 1, "비밀번호", passwordField);
        addInputRow(inputPanel, gbc, 2, "비밀번호 확인", passwordConfirmField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(-4, 6, 6, 6);
        inputPanel.add(passwordMismatchLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(AppStyle.PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        signupButton = AppStyle.outlineButton("가입하기", 78, 28);
        backButton = AppStyle.outlineButton("돌아가기", 78, 28);

        // 비밀번호 입력값이 바뀔 때마다 일치 여부를 다시 검사합니다.
        addPasswordMatchListener();

        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 라벨과 입력창을 한 줄에 추가하는 공통 메서드입니다.
    private void addInputRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.insets = new Insets(4, 6, 8, 6);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(AppStyle.label(labelText), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    // 비밀번호 입력창 두 개에 DocumentListener를 붙여 입력 즉시 검증합니다.
    private void addPasswordMatchListener() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePasswordMismatchMessage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePasswordMismatchMessage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePasswordMismatchMessage();
            }
        };

        passwordField.getDocument().addDocumentListener(listener);
        passwordConfirmField.getDocument().addDocumentListener(listener);
    }

    // 비밀번호와 비밀번호 확인이 다르면 경고 라벨을 보이게 합니다.
    private void updatePasswordMismatchMessage() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(passwordConfirmField.getPassword());
        passwordMismatchLabel.setVisible(!confirm.isEmpty() && !password.equals(confirm));
    }

    // Controller가 회원가입 처리에 사용할 아이디 입력값을 제공합니다.
    public String getIdInput() {
        return idField.getText().trim();
    }

    // Controller가 회원가입 처리에 사용할 비밀번호 입력값을 제공합니다.
    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    // Controller가 비밀번호 확인 검사를 할 수 있도록 확인 입력값을 제공합니다.
    public String getPasswordConfirmInput() {
        return new String(passwordConfirmField.getPassword());
    }

    // Controller가 회원가입 버튼 이벤트를 연결할 수 있도록 버튼을 제공합니다.
    public JButton getSignupButton() {
        return signupButton;
    }

    // Controller가 돌아가기 버튼 이벤트를 연결할 수 있도록 버튼을 제공합니다.
    public JButton getBackButton() {
        return backButton;
    }

    // 회원가입 실패, 중복 아이디 같은 오류 메시지를 화면에 표시합니다.
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    // 회원가입 성공 같은 안내 메시지를 화면에 표시합니다.
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "안내", JOptionPane.INFORMATION_MESSAGE);
    }
}
