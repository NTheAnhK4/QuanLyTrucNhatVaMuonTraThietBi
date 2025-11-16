package Data;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable, Identifiable {
    private String id;
    private String msv;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private String password;
    public User(String id, String msv, String name, String email, String phoneNumber, String password){
        this.id = id;
        this.msv = msv;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        if(msv == null || msv.length() <= 2) role = Role.Admin;
        else{
            if(msv.charAt(0) == '0') role = Role.Admin;
            else if(msv.charAt(0) == '1'){
                if(msv.charAt(1) <= '5') role = Role.Admin;
                else role = Role.User;
            }
            else role = Role.User;
        }

    }

    public User(String msv, String name, String email, String phoneNumber, String password) {
        this.msv = msv;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        if(msv == null || msv.length() <= 2) role = Role.Admin;
        else{
            if(msv.charAt(0) == '0') role = Role.Admin;
            else if(msv.charAt(0) == '1'){
                if(msv.charAt(1) <= '5') role = Role.Admin;
                else role = Role.User;
            }
            else role = Role.User;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", msv='" + msv + '\'' +
                '}';
    }
}
