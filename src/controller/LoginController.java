package controller;

import model.ReservationState;
import repository.MemberRepository;
import view.LoginView;
import view.MainView;
import view.SignupView;

// 로그인 화면을 담당하는 controller 클래스입니다. 
// 로그인 검증, 회원가입 처리, 화면 전환을 담당합니다. 
public class LoginController {
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        initListeners();
    }

    // 로그인 버튼과 회원가입 버튼에 이벤트를 연결합니다. 
    private void initListeners() {
        view.getLoginButton().addActionListener(e -> login());
        view.getSignupButton().addActionListener(e -> openSignup());
    }

    // 입력된 아이디와 비밀번호를 검증하고, 성공 시 메인 화면으로 이동합니다. 
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

    // 회원가입 화면을 열고 버튼 이벤트를 직접 처리합니다. 
    private void openSignup() {
        view.dispose();
        SignupView signupView = new SignupView();
        signupView.getSignupButton().addActionListener(e -> signup(signupView));
        signupView.getBackButton().addActionListener(e -> openLogin(signupView));
    }

    // 입력값 유효성 검사 후 회원가입을 처리하고 메인 화면으로 이동합니다. 
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

    // 회원가입 화면을 닫고 로그인 화면으로 돌아갑니다. 
    private void openLogin(SignupView signupView) {
        signupView.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView); 
    }
}
