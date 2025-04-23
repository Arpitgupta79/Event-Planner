package model;

import java.sql.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User {
    private int userId;
    private String name;
    private Date dob;
    private String email;
    private String password;
    private String role;

    public User( int userId,String name,Date dob ,String email, String password,String role){
        this.userId=userId;
        this.name=name;
        this.dob=dob;
        this.email=email;
        this.password=hashPassword(password);
        this.role=role;
    }

    public int getUserId(){ return userId;}
    public void setUserId(int userId){this.userId=userId;}

    public String getName(){ return name;}
    public void setName(String name){this.name=name;}

    public Date getDob(){return dob;}
    public void setDob(Date dob){this.dob=dob;}

    public String getEmail(){ return email;}
    public void setEmail(String email){this.email=email;}

    public String getPassword(){ return password;}
    public void setPassword(String password){this.password=hashPassword(password);}

    public String getRole(){ return role;}
    public void setRole(String role){this.role=role;}
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean authenticateUser(String inputEmail, String inputPassword) {
        return this.email.equals(inputEmail) && this.password.equals(hashPassword(inputPassword));
    }

    @Override
    public String toString(){
        return "User{"+
                "userId="+ userId+
                ",name=' " + name + '\'' +
                ",dob=" + dob +
                ", email=' " + email + '\'' +
                ",password=' " + password + '\'' +
                ",role="+ role +
                '}';
    }
}
