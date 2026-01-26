/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author anngu
 */
public class Doctor {
    private int doctorId;
    private int userId;
    private String specialization;

    // join từ users
    private String fullName;
    private String phone;
    private String email;

    public Doctor() {
    }

    public Doctor(int doctorId, int userId, String specialization, String fullName, String phone, String email) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.specialization = specialization;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

   

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
