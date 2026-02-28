/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author Admin
 */
public class Patients {
    private long patientId;
    private int userId;
    private String fullName;
    private String phone;
    private Date dob;
    private String address;
    private String email;
    private String gender;

    public Patients() {
    }

    public Patients(int userId, String fullName, String phone, Date dob, String address, String email, String gender) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.gender = gender;
    }

    public Patients(long patientId, int userId, String fullName, String phone, Date dob, String address, String email, String gender) {
        this.patientId = patientId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.gender = gender;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    
}
