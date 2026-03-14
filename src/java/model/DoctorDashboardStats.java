/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author anngu
 */
public class DoctorDashboardStats {
    private int total;
    private int waiting;
    private int examining;
    private int done;
    private double completionRate;
    private int doneToday;
    private int doneThisWeek;
    private int doneThisMonth;
    
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWaiting() {
        return waiting;
    }

    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }

    public int getExamining() {
        return examining;
    }

    public void setExamining(int examining) {
        this.examining = examining;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
    
    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public int getDoneToday() {
        return doneToday;
    }

    public void setDoneToday(int doneToday) {
        this.doneToday = doneToday;
    }

    public int getDoneThisWeek() {
        return doneThisWeek;
    }

    public void setDoneThisWeek(int doneThisWeek) {
        this.doneThisWeek = doneThisWeek;
    }

    public int getDoneThisMonth() {
        return doneThisMonth;
    }

    public void setDoneThisMonth(int doneThisMonth) {
        this.doneThisMonth = doneThisMonth;
    }
    
}
