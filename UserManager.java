import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class UserData {
    private String password;
    private String email;
    private String userType;

    public UserData(String email, String password, String userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    @Override
    public String toString() {
        return email + "," + password + "," + userType;
    }
}

class UserManager {
    private Map<String, Map<String, UserData>> userDataMap = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private String csvFilePath = "user_data.csv";
    private ILog log;

    public UserManager(ILog ilog) {
        log = ilog;
        loadUserData();
    }

    public boolean addUser(String email, String password, String userType) {
        lock.writeLock().lock();
        try {
            if (!isValidEmail(email) || !isValidPassword(password)) {
                log.log(email, "[AddUser " + userType + "]", "Invalid email or password format");
                return false;
            }

            if (userDataMap.containsKey(userType) && userDataMap.get(userType).containsKey(email)) {
                log.log(email, "[AddUser " + userType + "]", "User email exists");
                return false;
            }

            UserData newUser = new UserData(email, password, userType);
            userDataMap.computeIfAbsent(userType, k -> new HashMap<>()).put(email, newUser);
            saveUserData();
            log.log(email, "[AddUser " + userType + "]", "Success");
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeUser(String email, String password, String userType) {
        lock.writeLock().lock();
        try {
            if (userDataMap.containsKey(userType)) {
                Map<String, UserData> usersOfType = userDataMap.get(userType);
                if (usersOfType.containsKey(email) && usersOfType.get(email).getPassword().equals(password)) {
                    usersOfType.remove(email);
                    saveUserData();
                    log.log(email, "[RemoveUser]", "Success");
                    return true;
                }
            }
            log.log(email, "[RemoveUser]", "User not found or incorrect password");
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean verifyUser(String email, String password, String userType) {
        lock.readLock().lock();
        try {
            if (userDataMap.containsKey(userType)) {
                Map<String, UserData> usersOfType = userDataMap.get(userType);
                if (usersOfType.containsKey(email)) {
                    UserData user = usersOfType.get(email);
                    return user.getPassword().equals(password);
                }
            }
            log.log(email, "[login]", "User not found or incorrect credentials");
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void loadUserData() {
        lock.writeLock().lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    UserData user = new UserData(parts[0], parts[1], parts[2]);
                    userDataMap.computeIfAbsent(user.getUserType(), k -> new HashMap<>()).put(user.getEmail(), user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.log("[data file]", "[load data]", "Failed");
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveUserData() {
        lock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            for (Map<String, UserData> usersOfType : userDataMap.values()) {
                for (UserData user : usersOfType.values()) {
                    writer.write(user.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isValidEmail(String email) {
        // Add email
        //return email.matches("");
        return true;
    }

    private boolean isValidPassword(String password) {
        return password.matches("^[a-zA-Z0-9!@#$%^&*()_]{1,10}$");
    }
}
