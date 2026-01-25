package model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author anngu
 */
public class Appointment {
    private long appointmentId;
    private String patientName;
    private String status;
    private String symptom;

    public Appointment() {
    }

    public Appointment(long appointmentId, String patientName, String status, String symptom) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.status = status;
        this.symptom = symptom;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }
    
    
    
}
