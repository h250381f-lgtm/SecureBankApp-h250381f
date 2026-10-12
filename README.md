# Secure Banking Application

## 1. About the Project
This is a simple banking application written in Java. It runs in the console and lets a user log in, create a bank account, check the balance, deposit money, and withdraw money.

The application saves user, account, and transaction information in text files so it can load the saved information when it starts again.

## 2. Student Details
- **Name:** Britney Nokutenda Mutsengi
- **Registration Number:** h250381F
- **Department:** Information science and security
- **Institution:** Harare Institute of Technology

## 3. Features
- User login with a username and password.
- Limited login attempts.
- Create a bank account by entering full name, date of birth, and national ID number.
- View saved personal details, account number, and balance in the application.
- Complete the personal details for an older account that does not yet have them.
- View an account balance.
- Deposit and withdraw money.
- Validate transaction amounts.
- Keep a transaction history.
- Save and load information using text files.
- Demonstrate Java object-oriented programming (OOP) concepts.
- Include secure coding documentation covering threats, input validation, access control, error handling, testing, and web security.

## 4. Files in This Project
- `SecureBankApp.java` — the Java application.
- `users.txt` — demonstration user information and password hash.
- `accounts.txt` — saved bank account information.
- `transactions.txt` — transaction history.
- `ThreatModel.md` — possible security threats and ways to reduce risk.
- `SecureCodingChecklist.md` — secure coding topics covered by the project.
- `TESTING.md` — testing ideas and commands.
- `README.md` — this guide.

## 5. Open the Project in Visual Studio Code (VS Code)

### What you need
Before starting, install:
1. **Java JDK 17 or newer**.
2. **Visual Studio Code (VS Code)**.
3. **Extension Pack for Java** in VS Code.
4. **Git**, if you want to download the project using the `git clone` command. Git is optional if you download the ZIP file.

Useful download pages:
- VS Code: https://code.visualstudio.com/Download
- Java JDK: https://adoptium.net/
- Java Extension Pack: https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack
- Git: https://git-scm.com/downloads

### Option A: Download the project from GitHub
1. Open this project's GitHub repository in your web browser.
2. Click the green **Code** button.
3. Select **Download ZIP**.
4. Once the download finishes, extract the ZIP file to a folder on your computer.
5. Open VS Code.
6. Select **File → Open Folder**.
7. Choose the extracted `SecureBankApp-h250381F` project folder and click **Select Folder**.
8. If VS Code asks whether you trust the folder, confirm only if you trust the source of the project.

### Option B: Download the project using Git in VS Code
1. Open the project's GitHub page and copy its repository link.
2. In VS Code, select **Terminal → New Terminal**.
3. Type the following command, replacing the example link with your real repository link:

   ```bash
   git clone YOUR_GITHUB_REPOSITORY_LINK
   ```

4. Press **Enter**. Then select **File → Open Folder** in VS Code and open the downloaded project folder.

## 6. Check That Java Is Installed
In VS Code, select **Terminal → New Terminal**. Type each command and press Enter:

```bash
java -version
```

```bash
javac -version
```

Both commands should show a Java version. If a command is not recognised, install the Java JDK and follow its setup instructions before continuing.

## 7. Compile and Run the Application
1. In VS Code, open the project folder.
2. Select **Terminal → New Terminal**.
3. Make sure the terminal is in the folder containing `SecureBankApp.java`. If needed, use `cd` to move into that folder.
4. Compile the program:

   ```bash
   javac SecureBankApp.java
   ```

5. Start the program:

   ```bash
   java SecureBankApp
   ```

6. Follow the instructions displayed in the terminal.

If VS Code or Java reports an error, check that the JDK is installed, that the terminal is in the correct folder, and that the file names match the commands above.

## 8. Demonstration Login
Use these demonstration details to try the application:
- **Username:** `student`
- **Password:** `Secure@123`

This is a sample login for learning and testing only. Do not use these credentials for a real account.

## 9. Where the Information Is Saved
The application uses these text files:
- `users.txt` — user login information.
- `accounts.txt` — account details, personal details, and balances.
- `transactions.txt` — transaction records.

Keep these files in the location expected by the application so saved information can be loaded again. For testing, make a backup before changing or deleting the files.

## 10. Security Notes
This project is for education and demonstration. It is **not a real banking system**. The personal details, including national ID numbers and dates of birth, are stored in a local text file (`accounts.txt`) without encryption. Use fictional details for testing, protect the project folder, and do not enter real identity or financial information. A production system would need encrypted storage, stronger password hashing, proper access controls, and extensive security testing.

The web security topics in the supporting documentation describe protections that would be important for a future web version. This project itself is a console application.

## Author
**Britney Nokutenda Mutsengi**  
Information science and security  
Harare Institute of Technology
