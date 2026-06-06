package controller;

import repository.MemberRepository;
import view.LoginView;
import view.LogoutView;
import view.MainView;

public class LogoutController {
    private LogoutView view;
    private String userId;

    public LogoutController(LogoutView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
    }

    private void initListeners() {
        view.getLogoutButton().addActionListener(e -> logout());
        view.getWithdrawalButton().addActionListener(e -> withdraw());
        view.getMainButton().addActionListener(e -> goMain());
    }

    private void logout() {
        view.showInfoMessage("로그아웃되었습니다.");
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }

    private void withdraw() {
        if (!view.showWithdrawalConfirmDialog()) {
            return;
        }

        MemberRepository.getInstance().withdraw(userId);
        view.showInfoMessage("회원탈퇴되었습니다.");
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }

    private void goMain() {
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }
}
