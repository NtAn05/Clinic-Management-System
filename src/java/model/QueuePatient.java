package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author anngu
 */
public class QueuePatient {
    private int queueId;
    private long appointmentId;
    private String patientName;
    private String status;
    private int queuePosition;
    private LocalDateTime createdAt;

    public QueuePatient() {
    }

    public QueuePatient(int queueId, long appointmentId, String patientName, String status, int queuePosition, LocalDateTime createdAt) {
        this.queueId = queueId;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.status = status;
        this.queuePosition = queuePosition;
        this.createdAt = createdAt;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
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

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
