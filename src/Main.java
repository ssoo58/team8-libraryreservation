import view.LoginView;
import controller.LoginController;

public class Main {
    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        new LoginController(loginView);
    }
}
