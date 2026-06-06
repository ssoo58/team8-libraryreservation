package controller;

import repository.MemberRepository;
import view.LoginView;
import view.LogoutView;
import view.MainView;

// 로그아웃 화면을 담당하는 controller 클래스 입니다. 
// 로그아웃, 회원탈퇴, 메인 화면 복귀를 처리합니다. 
public class LogoutController {
    private LogoutView view;
    private String userId;

    public LogoutController(LogoutView view, String userId) {
        this.view = view;
        this.userId = userId;
        initListeners();
    }

    // 로그아웃, 회원탈퇴, 메인으로 버튼에 이벤트를 연결합니다. 
    private void initListeners() {
        view.getLogoutButton().addActionListener(e -> logout());
        view.getWithdrawalButton().addActionListener(e -> withdraw());
        view.getMainButton().addActionListener(e -> goMain());
    }

    // 로그아웃 처리 후 로그인 화면으로 이동합니다. 
    private void logout() {
        view.showInfoMessage("로그아웃되었습니다.");
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }

    // 확인 다이얼로그 후 회원탈퇴 처리하고 로그인 화면으로 이동합니다. 
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

    // 메인 화면으로 돌아갑니다. 
    private void goMain() {
        view.dispose();
        MainView mainView = new MainView(userId);
        new MainController(mainView, userId);
    }
}
