package task.entities;

public class UserAccount {
    /* FIELDS */
    private final String id;
    private String username;
    private String email;
    private String password;
    private final String createTime;


    /* CONSTRUCTOR */
    public UserAccount(String id, String username, String email, String password, String createTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createTime = createTime;
    }


    /* GETTERS AND SETTERS */
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateTime() {
        return createTime;
    }

    /* TO STRING */
    @Override
    public String toString() {
        return "UserAccount{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
