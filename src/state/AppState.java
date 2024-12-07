package state;

public class AppState {
    private int id;
    private String userName;
    private String fullName;
    private boolean isWorking;
    private String role;
    private boolean isOnBreak;
    private String breakType;

    private static AppState instance;

    private AppState() {
        this.id = 0;
        this.userName = null;
        this.fullName = null;
        this.isWorking = false;
        this.role = "";
        this.isOnBreak = false;
        this.breakType = "";
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public static AppState resetInstance() {
        instance = new AppState();

        return instance;
    }

    // USER NAME
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // USER ID
    public int getUserId() {
        return id;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    // FULL NAME
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // ISOWRKING
    public boolean getIsWorking() {
        return isWorking;
    }

    public void setIsWorking(boolean bool) {
        this.isWorking = bool;
    }

    // ROLE
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // BREAKS
    public boolean getIsOnBreak() {
        return isOnBreak;
    }

    public String getBreakType() {
        return breakType;
    }

    public void setIsOnBreak(boolean bool) {
        this.isOnBreak = bool;
    }

    public void setBreakType(String breakType) {
        this.breakType = breakType;
    }
}
