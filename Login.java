import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {
    private JLabel emailLabel, passLabel, welcomeLabel, shopLabel;
    private JTextField emailTF;
    private JPasswordField passPF;
    private JButton loginBtn, registerBtn;
    private JCheckBox showPassCheckBox;
    private BackgroundPanel panel;
    private Timer textTimer;
    private boolean showWelcome = true;

    public Login() {
        super("Login");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new BackgroundPanel("intro.jpeg");// Background Image
        panel.setLayout(null);

        welcomeLabel = new JLabel("Welcome To");
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 45));
        welcomeLabel.setForeground(Color.magenta);
        welcomeLabel.setBounds(130, 65, 300, 70);
        panel.add(welcomeLabel);

        shopLabel = new JLabel("City Shop 🛒");
        shopLabel.setFont(new Font("Times New Roman", Font.BOLD, 45));
        shopLabel.setForeground(Color.magenta);
        shopLabel.setBounds(130, 130, 300, 70);
        panel.add(shopLabel);

        textTimer = new Timer(2000, e -> {
            if (showWelcome) {
                welcomeLabel.setText("Please Login...");
                shopLabel.setText("");
                welcomeLabel.setForeground(Color.cyan);
            } else {
                welcomeLabel.setText("Welcome To");
                shopLabel.setText("City Shop 🛒");
                welcomeLabel.setForeground(Color.magenta);
                shopLabel.setForeground(Color.magenta);
            }
            showWelcome = !showWelcome;
        });// End of Timer
        textTimer.start();

        Font labelFont = new Font("Arial", Font.BOLD, 18);

        emailLabel = new JLabel("Email:");
        emailLabel.setBounds(500, 50, 200, 30);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(labelFont);
        panel.add(emailLabel);

        emailTF = new JTextField();
        emailTF.setBounds(600, 50, 300, 30);
        panel.add(emailTF);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(500, 100, 200, 30);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(labelFont);
        panel.add(passLabel);

        passPF = new JPasswordField();
        passPF.setBounds(600, 100, 300, 30);
        panel.add(passPF);

        showPassCheckBox = new JCheckBox("Show Password");
        showPassCheckBox.setBounds(600, 130, 150, 25);
        showPassCheckBox.setForeground(Color.WHITE);
        //showPassCheckBox.setOpaque(false);
        panel.add(showPassCheckBox);

        showPassCheckBox.addItemListener(e -> {
            if (showPassCheckBox.isSelected()) {
                passPF.setEchoChar((char) 0);
            } else {
                passPF.setEchoChar('•');
            }
        });

        loginBtn = new JButton("Login");
        loginBtn.setBounds(600, 170, 100, 30);
        loginBtn.addActionListener(this);
        panel.add(loginBtn);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(800, 170, 100, 30);
        registerBtn.addActionListener(this);
        panel.add(registerBtn);

        add(panel);
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals("Login")) {
            login();
        } else if (command.equals("Register")) {
            Registration reg = new Registration();
            reg.setVisible(true);
            this.setVisible(false);
        }
    }

    private void login() {
        String email = emailTF.getText().trim();
        String password = new String(passPF.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        User user = User.validateUser(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            if (user.getRole().equalsIgnoreCase("admin")) {
                AdminDashboard adminDash = new AdminDashboard(user);
                adminDash.setVisible(true);
            } else {
                UserDashboard userDash = new UserDashboard(user);
                userDash.setVisible(true);
            }
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    }

    // Custom JPanel to set Background Image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.out.println("Error loading background image.");
            }
        }

        // draw section
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);// prevents graphical glitches when resizing the window.
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);// Draw the background image
            }
        }
    }

}