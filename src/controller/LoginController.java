package controller;

import model.ReservationState;
import repository.MemberRepository;
import view.LoginView;
import view.MainView;
import view.SignupView;

public class LoginController {
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        initListeners();
    }

    private void initListeners() {
        view.getLoginButton().addActionListener(e -> login());
        view.getSignupButton().addActionListener(e -> openSignup());
    }

    private void login() {
        String id = view.getIdInput().trim();
        String pw = view.getPasswordInput();

        if (id.isEmpty() || pw.isEmpty()) {
            view.showErrorMessage("아이디와 비밀번호를 입력해주세요.");
            return;
        }

        if (MemberRepository.getInstance().authenticate(id, pw)) {
            ReservationState.setCurrentUser(id);
            view.dispose();
            new MainView(id);
        } else {
            view.showErrorMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
            view.clearInputs();
        }
    }

    private void openSignup() {
        view.dispose();
        new SignupView();
    }
}
