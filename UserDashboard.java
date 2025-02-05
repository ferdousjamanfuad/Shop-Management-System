import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class UserDashboard extends JFrame implements ActionListener {
    private ArrayList<Product> products;
    private ArrayList<Product> cart;
    
    private JPanel mainPanel;
    private JButton checkoutBtn, logoutBtn;
    
    private static final String PRODUCT_FILE = "products.txt";
        
    public UserDashboard(User user) {
        super("User Dashboard");

        this.cart = new ArrayList<>();
        
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        //Top Panel: Welcome Message
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome, " + user.getName() + "!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        //Center Panel: List of Products
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));//
        loadProducts();//load products in user dashboard
        for (Product p : products) {
            JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            productPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            JLabel nameLabel = new JLabel(p.getName() + " - $" + p.getPrice());
            productPanel.add(nameLabel);
            
            JButton buyNowBtn = new JButton("Buy Now");
            buyNowBtn.addActionListener(e -> {
                int response = JOptionPane.showConfirmDialog(
                        UserDashboard.this,
                        "Do you want to buy " + p.getName() + " for $" + p.getPrice() + " now?",
                        "Confirm Purchase", JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Purchase successful for " + p.getName() + "!");
                }
            });
            productPanel.add(buyNowBtn);
            
            JButton addToCartBtn = new JButton("Add to Cart");
            addToCartBtn.addActionListener(e -> {
                cart.add(p);
                JOptionPane.showMessageDialog(UserDashboard.this, p.getName() + " added to cart.");
            });
            productPanel.add(addToCartBtn);
            
            productsPanel.add(productPanel);
        }
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        //  Bottom Panel: Checkout and Logout Buttons 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        checkoutBtn = new JButton("Checkout");
        checkoutBtn.addActionListener(e -> checkout());
        logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(this);
        
        bottomPanel.add(checkoutBtn);
        bottomPanel.add(logoutBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == logoutBtn) {
            Login login = new Login();
            login.setVisible(true);
            this.dispose();
        }
    }
    
    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        double total = 0;
        StringBuilder details = new StringBuilder();//StringBuilder for better string manipulation 
        for (Product p : cart) {
            total += p.getPrice();
            details.append(p.getName()).append(" - $").append(p.getPrice()).append("\n");//mutable
        }
        int response = JOptionPane.showConfirmDialog(
                this,
                "Cart:\n" + details.toString() + "\nTotal: $" + total + "\nConfirm purchase?",//immutable
                "Checkout", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Purchase successful!");
            cart.clear();
        }
    }
    
    private void loadProducts() {
        products = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()) {
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
        try (PrintWriter pw = new PrintWriter(new File(PRODUCT_FILE))) {
            for (Product p : products) {
                pw.println(p.toFileString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}