import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Registration extends JFrame implements ActionListener {
    private JLabel nameLabel, emailLabel, passLabel, genderLabel, birthLabel;
    private JTextField nameTF, emailTF;
    private JPasswordField passPF;
    private JRadioButton male, female;
    private ButtonGroup genderGroup;
    private JComboBox<String> birthYearCB;
    private JButton regBtn, clearBtn;
    private JPanel panel;

    public Registration() {
        super("Registration");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.black);

        Font labelFont = new Font("Arial", Font.BOLD, 18);

        nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 30, 100, 30);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(labelFont);
        panel.add(nameLabel);

        nameTF = new JTextField();
        nameTF.setBounds(150, 30, 200, 30);
        panel.add(nameTF);

        emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 70, 100, 30);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(labelFont);
        panel.add(emailLabel);

        emailTF = new JTextField();
        emailTF.setBounds(150, 70, 200, 30);
        panel.add(emailTF);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 110, 100, 30);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(labelFont);
        panel.add(passLabel);

        passPF = new JPasswordField();
        passPF.setBounds(150, 110, 200, 30);
        panel.add(passPF);

        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(50, 150, 100, 30);
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setFont(labelFont);
        panel.add(genderLabel);

        male = new JRadioButton("Male");
        male.setBounds(150, 150, 70, 30);
        male.setBackground(Color.DARK_GRAY);
        male.setForeground(Color.WHITE);
        panel.add(male);

        female = new JRadioButton("Female");
        female.setBounds(230, 150, 90, 30);
        female.setBackground(Color.DARK_GRAY);
        female.setForeground(Color.WHITE);
        panel.add(female);

        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);

        birthLabel = new JLabel("Birth Year:");
        birthLabel.setBounds(50, 190, 100, 30);
        birthLabel.setForeground(Color.WHITE);
        birthLabel.setFont(labelFont);
        panel.add(birthLabel);

        String[] years = new String[21];
        for (int i = 0; i < 21; i++) {
            years[i] = String.valueOf(1990 + i);
        }
        birthYearCB = new JComboBox<>(years);
        birthYearCB.setBounds(150, 190, 200, 30);
        panel.add(birthYearCB);

        // Register and Clear Buttons
        regBtn = new JButton("Register");
        regBtn.setBounds(150, 230, 100, 30);
        regBtn.addActionListener(this);
        panel.add(regBtn);

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(260, 230, 80, 30);
        clearBtn.addActionListener(this);
        panel.add(clearBtn);

        add(panel);
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals("Register")) {
            register();
        } else if (command.equals("Clear")) {
            clearFields();
        }
    }

    private void register() {
        String name = nameTF.getText().trim();
        String email = emailTF.getText().trim();
        String password = new String(passPF.getPassword());
        String gender = "";
        if (male.isSelected()) {
            gender = "Male";
        } else if (female.isSelected()) {
            gender = "Female";
        }
        String birthYear = (String) birthYearCB.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty() || birthYear.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        if (User.isEmailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already exists.");
            return;
        }

        User newUser = new User(name, email, password, gender, birthYear);
        User.saveUser(newUser);
        JOptionPane.showMessageDialog(this, "Registration successful!");

        Login login = new Login();
        login.setVisible(true);
        this.setVisible(false);
    }

    private void clearFields() {
        nameTF.setText("");
        emailTF.setText("");
        passPF.setText("");
        genderGroup.clearSelection();
        birthYearCB.setSelectedIndex(0);
    }
}
