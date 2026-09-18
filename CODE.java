import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Appliance {
    private String name;
    private double wattage;
    private double dailyHours;

    public Appliance(String name, double wattage, double dailyHours) {
        if (wattage <= 0 || dailyHours < 0 || dailyHours > 24) {
            throw new IllegalArgumentException("Invalid wattage (>0) or operational hours (0-24).");
        }
        this.name = name;
        this.wattage = wattage;
        this.dailyHours = dailyHours;
    }

    public String getName() { return name; }
    public double getWattage() { return wattage; }
    public double getDailyHours() { return dailyHours; }

    public double getDailyKWh() {
        return (wattage * dailyHours) / 1000.0;
    }

    public double getMonthlyKWh() {
        return getDailyKWh() * 30.0;
    }
}

class BillCalculator {
    private static final double FIXED_CHARGE = 50.0;
    private static final double TAX_RATE = 0.05;

    public double calculateEnergyCost(double totalKWh) {
        double cost = 0.0;
        double units = totalKWh;

        if (units > 200) {
            cost + = (units - 200) * 4.00;
            units = 200;
        }
        if (units > 100) {
            cost + = (units - 100) * 2.50;
            units = 100;
        }
        if (units > 0) {
            cost + = units * 1.50;
        }

        return cost;
    }

    public double getFixedCharge() { return FIXED_CHARGE; }
    public double getTaxRate() { return TAX_RATE; }
}

class ApplianceManager {
    private final List<Appliance> appliances = new ArrayList<>();

    public void addAppliance(Appliance appliance) {
        appliances.add(appliance);
    }

    public boolean removeAppliance(int index) {
        if (index >= 0 && index < appliances.size()) {
            appliances.remove(index);
            return true;
        }
        return false;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public double calculateTotalMonthlyKWh() {
        double total = 0.0;
        for (Appliance app : appliances) {
            total + = app.getMonthlyKWh();
        }
        return total;
    }
}

public class ElectricityBillEstimator {
    private static final ApplianceManager manager = new ApplianceManager();
    private static final BillCalculator calculator = new BillCalculator();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        System.out.println("================================================== ");
        System.out.println("   ELECTRICITY BILL ESTIMATOR & ANALYZER (JAVA)   ");
        System.out.println("================================================== ");

        while (!exit) {
            printMenu();
            int choice = getIntInput("Select an option: ");

            switch (choice) {
                case 1 -> addApplianceUI();
                case 2 -> viewAppliancesUI();
                case 3 -> deleteApplianceUI();
                case 4 -> generateBillReportUI();
                case 5 -> {
                    exit = true;
                    System.out.println("Thank you for using Electricity Bill Estimator. Goodbye!");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add Appliance");
        System.out.println("2. View All Appliances & Usage");
        System.out.println("3. Remove Appliance");
        System.out.println("4. Generate Estimated Monthly Bill");
        System.out.println("5. Exit");
    }

    private static void addApplianceUI() {
        System.out.print("\nEnter appliance name (e.g., Refrigerator): ");
        String name = scanner.nextLine();
        double wattage = getDoubleInput("Enter power rating in Watts: ");
        double hours = getDoubleInput("Enter daily usage hours (0-24): ");

        try {
            Appliance appliance = new Appliance(name, wattage, hours);
            manager.addAppliance(appliance);
            System.out.println("Appliance '" + name + "' added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAppliancesUI() {
        List<Appliance> list = manager.getAppliances();
        if (list.isEmpty()) {
            System.out.println("\nNo appliances added yet.");
            return;
        }

        System.out.println("\n--------------------------------------------------------------");
        System.out.printf("%-5s %-20s %-10s %-12s %-12s%n", "ID", "Name", "Watts", "Hours/Day", "Monthly kWh");
        System.out.println("--------------------------------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            Appliance app = list.get(i);
            System.out.printf("%-5d %-20s %-10.1f %-12.1f %-12.2f%n",
                    (i + 1), app.getName(), app.getWattage(), app.getDailyHours(), app.getMonthlyKWh());
        }
        System.out.println("--------------------------------------------------------------");
    }

    private static void deleteApplianceUI() {
        viewAppliancesUI();
        if (manager.getAppliances().isEmpty()) return;

        int index = getIntInput("Enter Appliance ID to remove: ") - 1;
        if (manager.removeAppliance(index)) {
            System.out.println("Appliance removed successfully.");
        } else {
            System.out.println("Invalid Appliance ID.");
        }
    }

    private static void generateBillReportUI() {
        double totalKWh = manager.calculateTotalMonthlyKWh();
        if (totalKWh == 0) {
            System.out.println("\nNo consumption data available. Please add appliances first.");
            return;
        }

        double energyCost = calculator.calculateEnergyCost(totalKWh);
        double fixedCharge = calculator.getFixedCharge();
        double subtotal = energyCost + fixedCharge;
        double tax = subtotal * calculator.getTaxRate();
        double finalBill = subtotal + tax;

        System.out.println("\n====================================== ");
        System.out.println("       ESTIMATED MONTHLY BILL         ");
        System.out.println("====================================== ");
        System.out.printf("Total Monthly Units : %.2f kWh%n", totalKWh);
        System.out.printf("Energy Charges      : $%.2f%n", energyCost);
        System.out.printf("Fixed Service Fee   : $%.2f%n", fixedCharge);
        System.out.printf("Tax (5%%)            : $%.2f%n", tax);
        System.out.println("--------------------------------------");
        System.out.printf("TOTAL ESTIMATED BILL: $%.2f%n", finalBill);
        System.out.println("====================================== ");
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        double val = scanner.nextDouble();
        scanner.nextLine();
        return val;
    }
}
