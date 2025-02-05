import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminDashboard extends JFrame implements ActionListener {
    private User user;
    private ArrayList<Product> products;
    private static final String PRODUCT_FILE = "products.txt";
    
    private JPanel topPanel;
    private JButton logoutBtn;
    private JTabbedPane tabbedPane;
    
    public AdminDashboard(User user) {
        super("Admin Dashboard");
        this.user = user;
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Top panel with a welcome message and logout button
        topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(this);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        
        // Create tabbed pane for management sections
        tabbedPane = new JTabbedPane();//multiple panels are associated with the tabbed pane conatiner
        tabbedPane.addTab("Products", createProductManagementPanel());
        tabbedPane.addTab("Users", createUserManagementPanel());
        
        // Set layout and add components
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();//action command is the string associated with the button(can set different string command for different buttons of same name ,this is the difference between getactioncommand and gettext)
        if (command.equals("Logout")) {
            Login login = new Login();
            login.setVisible(true);
            this.dispose();//closes the current frame and releases the resources
        }
    }
    
    // =========================
    // Products Management Panel
    // =========================

    private JPanel createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        loadProducts();
        
        // For each product show editable fields
        for (int i = 0; i < products.size(); i++) {
            final int index = i;//final keyword is used to restrict the user from changing the value of the variable
            Product p = products.get(i);
            JPanel prodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            prodPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));//create a border around the panel
            
            JTextField nameField = new JTextField(p.getName(), 10);
            JTextField priceField = new JTextField(String.valueOf(p.getPrice()), 5);
            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                if (newName.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
                    return;
                }
                try {
                    double newPrice = Double.parseDouble(priceText);
                    products.set(index, new Product(newName, newPrice));
                    saveProducts();//save the updated product in the file
                    JOptionPane.showMessageDialog(this, "Product updated.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid price.");
                }
            });
            
            prodPanel.add(new JLabel("Name:"));
            prodPanel.add(nameField);
            prodPanel.add(new JLabel("Price:"));
            prodPanel.add(priceField);
            prodPanel.add(updateButton);
            listPanel.add(prodPanel);
        }
        
        JButton addButton = new JButton("Add New Product");
        addButton.addActionListener(e -> {
            JTextField nameField = new JTextField(10);
            JTextField priceField = new JTextField(5);
            JPanel inputPanel = new JPanel(new FlowLayout());
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Price:"));
            inputPanel.add(priceField);
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "New Product", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                if (name.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
                    return;
                }
                try {
                    double price = Double.parseDouble(priceText);
                    products.add(new Product(name, price));
                    saveProducts();
                    JOptionPane.showMessageDialog(this, "Product added.");
                    // Refresh the Products tab
                    tabbedPane.setComponentAt(0, createProductManagementPanel());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid price.");
                }
            }
        });
        
        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);//association of the list panel with the scroll pane
        panel.add(addButton, BorderLayout.SOUTH);
        return panel;
    }
    
    // =========================
    // Users Management Panel
    // =========================

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        java.util.List<User> users = User.loadUsers();
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        for (User u : users) {
            JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            userPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            JLabel userLabel = new JLabel(u.getEmail() + " - " + u.getName() + " (" + u.getRole() + ")");// Display user email and name
            JButton deleteButton = new JButton("Delete");
            // Do not allow deletion of admin accounts.
            if (u.getRole().equalsIgnoreCase("admin")) {
                deleteButton.setEnabled(false);
            } else {
                deleteButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete user " + u.getEmail() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (User.removeUser(u.getEmail())) {
                            JOptionPane.showMessageDialog(this, "User deleted.");
                            // Refresh the Users tab
                            tabbedPane.setComponentAt(1, createUserManagementPanel());
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete user.");
                        }
                    }
                });
            }
            userPanel.add(userLabel);
            userPanel.add(deleteButton);
            listPanel.add(userPanel);
        }
        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        return panel;
    }
    
    // =========================
    // Product File Handling
    // =========================
    
    private void loadProducts() {
        products = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()) {
            // Create default products if file does not exist
            products.add(new Product("Rice", 10.0));
            products.add(new Product("Oil", 5.5));
            products.add(new Product("Soap", 2.0));
            saveProducts();
        } else {
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    Product p = Product.fromFileString(line);
                    if (p != null) {
                        products.add(p);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void saveProducts() {
        try (PrintWriter pw = new PrintWriter(new File(PRODUCT_FILE))) {//PrintWriter is used to write formatted text to a file and creates a new file if it does not exist
            for (Product p : products) {
                pw.println(p.toFileString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
