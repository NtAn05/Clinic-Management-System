package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author anngu
 */
public class DoctorQueueItem {
    private int queuePosition;
    private String patientName;
    private String gender;
    private Date dob;
    private String symptom;
    private String status;

    public DoctorQueueItem() {
    }

    public DoctorQueueItem(int queuePosition, String patientName, String gender, Date dob, String symptom, String status) {
        this.queuePosition = queuePosition;
        this.patientName = patientName;
        this.gender = gender;
        this.dob = dob;
        this.symptom = symptom;
        this.status = status;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
