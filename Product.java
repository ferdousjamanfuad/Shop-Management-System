public class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    // Convert product to a string for file storage 
    public String toFileString() {
        return name + "," + price;
    }

    // Create a Product from string line
    public static Product fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 2) {
            try {
                String name = parts[0];
                double price = Double.parseDouble(parts[1]);
                return new Product(name, price);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
