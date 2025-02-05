public class Start {
    public static void main(String[] args) {
        if (!User.defaultAdminExists()) {
            User.createDefaultAdmin();
        }

        Login login = new Login();
        login.setVisible(true);
    }
}
