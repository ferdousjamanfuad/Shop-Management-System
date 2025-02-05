import java.io.*;
import java.util.*;

public class User {
    private String name;
    private String email;
    private String password;
    private String gender;
    private String birthYear;
    private String role;// Admin or User

    public User(String name, String email, String password, String gender, String birthYear, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthYear = birthYear;
        this.role = role;
    }

    // For normal registration
    public User(String name, String email, String password, String gender, String birthYear) {
        this(name, email, password, gender, birthYear, "user");
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getRole() {
        return role;
    }

    // Convert user to a string for file storage
    public String toFileString() {
        return name + "," + email + "," + password + "," + gender + "," + birthYear + "," + role;
    }

    // Create a User from string line
    public static User fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 6) {
            return new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
        return null;
    }

    // set the file path
    private static final String USER_FILE = "Userdata.txt";

    // Save a new user (append mode)
    public static void saveUser(User user) {
        try (FileWriter writer = new FileWriter(USER_FILE, true)) {
            writer.write(user.toFileString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all users from the file
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();// List of users
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return users;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User u = fromFileString(line);
                if (u != null) {
                    users.add(u);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static User validateUser(String email, String password) {
        for (User u : loadUsers()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Check if an email already exists
    public static boolean isEmailExists(String email) {
        for (User u : loadUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    // Remove a user by email
    public static boolean removeUser(String email) {
        List<User> users = loadUsers();
        boolean removed = users.removeIf(u -> u.getEmail().equalsIgnoreCase(email));  // true if removed
        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {  // Overwrite the file
                for (User u : users) {
                    writer.write(u.toFileString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return removed;
    }

    // Check if a default admin exists
    public static boolean defaultAdminExists() {
        for (User u : loadUsers()) {
            if (u.getRole().equalsIgnoreCase("admin")) {
                return true;
            }
        }
        return false;
    }

    // Create a default admin account
    public static void createDefaultAdmin() {
        User admin = new User("Administrator", "admin@admin.com", "admin123", "N/A", "N/A", "admin");
        saveUser(admin);
    }
}
