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
            MainView mainView = new MainView(id);
            new MainController(mainView, id);
        } else {
            view.showErrorMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
            view.clearInputs();
        }
    }

    private void openSignup() {
        view.dispose();
        SignupView signupView = new SignupView();
        signupView.getSignupButton().addActionListener(e -> signup(signupView));
        signupView.getBackButton().addActionListener(e -> openLogin(signupView));
    }

    private void signup(SignupView signupView) {
        String id = signupView.getIdInput();
        String pw = signupView.getPasswordInput();
        String confirm = signupView.getPasswordConfirmInput();

        if (id.isEmpty() || pw.isEmpty() || confirm.isEmpty()) {
            signupView.showErrorMessage("아이디와 비밀번호를 입력해주세요.");
            return;
        }

        if (!pw.equals(confirm)) {
            signupView.showErrorMessage("비밀번호가 동일하지 않습니다.");
            return;
        }

        if (!MemberRepository.getInstance().register(id, pw)) {
            signupView.showErrorMessage("이미 존재하는 아이디입니다.");
            return;
        }

        ReservationState.setCurrentUser(id);
        signupView.showInfoMessage("회원가입이 완료되었습니다.");
        signupView.dispose();
        MainView mainView = new MainView(id);
        new MainController(mainView, id);
    }

    private void openLogin(SignupView signupView) {
        signupView.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }
}
