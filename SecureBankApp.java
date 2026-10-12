import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SecureBankApp {
    private static final String USERS_FILE = "users.txt";
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<User> users = new ArrayList<>();
    private static final List<BankAccount> accounts = new ArrayList<>();
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        loadData();
        seedDemoUserIfEmpty();

        System.out.println("====================================");
        System.out.println("       SECURE BANKING APPLICATION");
        System.out.println("====================================");

        User loggedInUser = login();
        if (loggedInUser == null) {
            System.out.println("Application closed.");
            return;
        }

        runMenu(loggedInUser);
        saveData();
        System.out.println("Thank you for using Secure Banking Application.");
    }

    private static User login() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = findUser(username);
            if (user != null && user.passwordHash.equals(hashPassword(password))) {
                System.out.println("Login successful. Welcome, " + user.fullName + "!");
                return user;
            }

            System.out.println("Invalid credentials. Attempts remaining: " + (3 - attempt));
        }
        System.out.println("Too many failed login attempts.");
        return null;
    }

    private static void runMenu(User user) {
        while (true) {
            System.out.println("\n---------- MAIN MENU ----------");
            System.out.println("1. Create bank account / enter personal details");
            System.out.println("2. View account and personal details");
            System.out.println("3. View account balance");
            System.out.println("4. Deposit funds");
            System.out.println("5. Withdraw funds");
            System.out.println("6. View transaction history");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createAccount(user);
                case "2" -> viewAccountDetails(user);
                case "3" -> viewBalance(user);
                case "4" -> deposit(user);
                case "5" -> withdraw(user);
                case "6" -> viewTransactions(user);
                case "7" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void createAccount(User user) {
        BankAccount account = findAccountForUser(user.username);
        if (account != null && account.hasPersonalDetails()) {
            System.out.println("You already have a bank account with personal details saved.");
            System.out.println("Choose option 2 to view your details.");
            return;
        }

        System.out.println("\n----- ACCOUNT CREATION / PERSONAL DETAILS -----");
        String fullName = readRequiredField("Enter your full name: ");
        String dateOfBirth = readDateOfBirth();
        String idNumber = readRequiredField("Enter your national ID number: ");

        if (account == null) {
            String accountNumber = generateAccountNumber();
            account = new BankAccount(accountNumber, user.username, 0.0, fullName, dateOfBirth, idNumber);
            accounts.add(account);
            transactions.add(new Transaction(accountNumber, "ACCOUNT_CREATED", 0.0));
            System.out.println("Bank account created successfully.");
        } else {
            // Supports older accounts created before personal details were added.
            account.setPersonalDetails(fullName, dateOfBirth, idNumber);
            System.out.println("Your existing account profile has been completed successfully.");
        }
        saveData();
        System.out.println("Account number: " + account.accountNumber);
        System.out.println("Use option 2 to view your saved account details.");
    }

    private static String readRequiredField(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank() && !value.contains("|")) return value;
            System.out.println("This field is required and must not contain the | character.");
        }
    }

    private static String readDateOfBirth() {
        while (true) {
            String value = readRequiredField("Enter your date of birth (YYYY-MM-DD): ");
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(value);
                if (!date.isAfter(java.time.LocalDate.now())) return date.toString();
            } catch (java.time.format.DateTimeParseException ignored) {
                // Show the validation message below.
            }
            System.out.println("Enter a valid date in YYYY-MM-DD format that is not in the future.");
        }
    }

    private static void viewAccountDetails(User user) {
        BankAccount account = findAccountForUser(user.username);
        if (account == null) {
            System.out.println("No bank account found. Choose option 1 to create one.");
            return;
        }

        System.out.println("\n---------- ACCOUNT DETAILS ----------");
        System.out.println("Full name: " + (account.fullName.isBlank() ? "Not provided" : account.fullName));
        System.out.println("Date of birth: " + (account.dateOfBirth.isBlank() ? "Not provided" : account.dateOfBirth));
        System.out.println("National ID number: " + (account.idNumber.isBlank() ? "Not provided" : account.idNumber));
        System.out.println("Account number: " + account.accountNumber);
        System.out.printf("Balance: $%.2f%n", account.balance);
        if (!account.hasPersonalDetails()) {
            System.out.println("Your profile is incomplete. Choose option 1 to enter your details.");
        }
    }

    private static void viewBalance(User user) {
        BankAccount account = findAccountForUser(user.username);
        if (account == null) {
            System.out.println("No bank account found. Create one first.");
            return;
        }
        System.out.printf("Account: %s%nBalance: $%.2f%n", account.accountNumber, account.balance);
    }

    private static void deposit(User user) {
        BankAccount account = findAccountForUser(user.username);
        if (account == null) {
            System.out.println("No bank account found.");
            return;
        }

        double amount = readPositiveAmount("Enter deposit amount: ");
        account.deposit(amount);
        transactions.add(new Transaction(account.accountNumber, "DEPOSIT", amount));
        saveData();
        System.out.printf("Deposit successful. New balance: $%.2f%n", account.balance);
    }

    private static void withdraw(User user) {
        BankAccount account = findAccountForUser(user.username);
        if (account == null) {
            System.out.println("No bank account found.");
            return;
        }

        double amount = readPositiveAmount("Enter withdrawal amount: ");
        if (account.withdraw(amount)) {
            transactions.add(new Transaction(account.accountNumber, "WITHDRAW", amount));
            saveData();
            System.out.printf("Withdrawal successful. New balance: $%.2f%n", account.balance);
        } else {
            System.out.println("Withdrawal rejected: insufficient funds.");
        }
    }

    private static void viewTransactions(User user) {
        BankAccount account = findAccountForUser(user.username);
        if (account == null) {
            System.out.println("No bank account found.");
            return;
        }

        System.out.println("\n----- TRANSACTION HISTORY -----");
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.accountNumber.equals(account.accountNumber)) {
                System.out.printf("%s | %-16s | $%.2f%n",
                        transaction.timestamp, transaction.type, transaction.amount);
                found = true;
            }
        }
        if (!found) System.out.println("No transactions found.");
    }

    private static double readPositiveAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());
                if (Double.isFinite(amount) && amount > 0) return amount;
            } catch (NumberFormatException ignored) {}
            System.out.println("Please enter a valid positive amount.");
        }
    }

    private static String generateAccountNumber() {
        Random random = new Random();
        String number;
        do {
            number = String.format("%010d", Math.abs(random.nextLong()) % 10_000_000_000L);
        } while (findAccount(number) != null);
        return number;
    }

    private static User findUser(String username) {
        for (User user : users) if (user.username.equalsIgnoreCase(username)) return user;
        return null;
    }

    private static BankAccount findAccount(String number) {
        for (BankAccount account : accounts) if (account.accountNumber.equals(number)) return account;
        return null;
    }

    private static BankAccount findAccountForUser(String username) {
        for (BankAccount account : accounts) if (account.username.equalsIgnoreCase(username)) return account;
        return null;
    }

    private static void loadData() {
        loadUsers();
        loadAccounts();
        loadTransactions();
    }

    private static void loadUsers() {
        users.clear();
        if (!Files.exists(Path.of(USERS_FILE))) return;
        try {
            for (String line : Files.readAllLines(Path.of(USERS_FILE))) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 3) users.add(new User(p[0], p[1], p[2]));
            }
        } catch (IOException e) {
            System.out.println("Could not load users.");
        }
    }

    private static void loadAccounts() {
        accounts.clear();
        if (!Files.exists(Path.of(ACCOUNTS_FILE))) return;
        try {
            for (String line : Files.readAllLines(Path.of(ACCOUNTS_FILE))) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 3) {
                    // Backward compatibility with accounts saved by earlier versions.
                    accounts.add(new BankAccount(p[0], p[1], Double.parseDouble(p[2])));
                } else if (p.length == 6) {
                    accounts.add(new BankAccount(p[0], p[1], Double.parseDouble(p[2]), p[3], p[4], p[5]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not load account data.");
        }
    }

    private static void loadTransactions() {
        transactions.clear();
        if (!Files.exists(Path.of(TRANSACTIONS_FILE))) return;
        try {
            for (String line : Files.readAllLines(Path.of(TRANSACTIONS_FILE))) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 4) transactions.add(new Transaction(p[0], p[1], Double.parseDouble(p[2]), p[3]));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not load transaction data.");
        }
    }

    private static void saveData() {
        saveUsers();
        saveAccounts();
        saveTransactions();
    }

    private static void saveUsers() {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(USERS_FILE))) {
            for (User user : users)
                writer.write(user.username + "|" + user.fullName + "|" + user.passwordHash + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not save users.");
        }
    }

    private static void saveAccounts() {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(ACCOUNTS_FILE))) {
            for (BankAccount account : accounts)
                writer.write(account.accountNumber + "|" + account.username + "|" + account.balance + "|" +
                        account.fullName + "|" + account.dateOfBirth + "|" + account.idNumber + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not save accounts.");
        }
    }

    private static void saveTransactions() {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(TRANSACTIONS_FILE))) {
            for (Transaction transaction : transactions)
                writer.write(transaction.accountNumber + "|" + transaction.type + "|" +
                        transaction.amount + "|" + transaction.timestamp + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not save transactions.");
        }
    }

    private static void seedDemoUserIfEmpty() {
        if (users.isEmpty()) {
            users.add(new User("student", "HIT Student", hashPassword("Secure@123")));
            saveData();
            System.out.println("Demo user created: username=student, password=Secure@123");
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static class User {
        private final String username;
        private final String fullName;
        private final String passwordHash;

        User(String username, String fullName, String passwordHash) {
            this.username = username;
            this.fullName = fullName;
            this.passwordHash = passwordHash;
        }
    }

    private static class BankAccount {
        private final String accountNumber;
        private final String username;
        private double balance;
        private String fullName;
        private String dateOfBirth;
        private String idNumber;

        BankAccount(String accountNumber, String username, double balance) {
            this(accountNumber, username, balance, "", "", "");
        }

        BankAccount(String accountNumber, String username, double balance,
                    String fullName, String dateOfBirth, String idNumber) {
            this.accountNumber = accountNumber;
            this.username = username;
            this.balance = balance;
            this.fullName = fullName == null ? "" : fullName;
            this.dateOfBirth = dateOfBirth == null ? "" : dateOfBirth;
            this.idNumber = idNumber == null ? "" : idNumber;
        }

        boolean hasPersonalDetails() {
            return !fullName.isBlank() && !dateOfBirth.isBlank() && !idNumber.isBlank();
        }

        void setPersonalDetails(String fullName, String dateOfBirth, String idNumber) {
            this.fullName = fullName;
            this.dateOfBirth = dateOfBirth;
            this.idNumber = idNumber;
        }

        void deposit(double amount) { balance += amount; }

        boolean withdraw(double amount) {
            if (amount <= 0 || amount > balance) return false;
            balance -= amount;
            return true;
        }
    }

    private static class Transaction {
        private final String accountNumber;
        private final String type;
        private final double amount;
        private final String timestamp;

        Transaction(String accountNumber, String type, double amount) {
            this(accountNumber, type, amount, java.time.LocalDateTime.now().toString());
        }

        Transaction(String accountNumber, String type, double amount, String timestamp) {
            this.accountNumber = accountNumber;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }
    }
}
