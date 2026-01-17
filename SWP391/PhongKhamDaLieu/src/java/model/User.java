package model;
import java.sql.Date;

public class User {
    private int id;
    private String fullName;
    private Date dob;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String password;
    private int roleId;
    private boolean status;

    public User() {}

    public User(String fullName, Date dob, String gender, String phone, String email, String address, String password) {
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.password = password;
        this.roleId = 1;
        this.status = true;
    }

    // Getter & Setter (Bắt buộc phải có để JSP đọc dữ liệu)
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}