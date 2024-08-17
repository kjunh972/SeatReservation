package com.tbfg.dto;

public class ReserveList {
	private int reservNum;
    private String userId;
    private String classroomName;
    private int reservSeat;
    private int reservHour;
    private String reservHourString; // 시간대를 문자열로 묶은 필드

    // Getters and setters
    public int getReservNum() {
        return reservNum;
    }

    public void setReservNum(int reservNum) {
        this.reservNum = reservNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public int getReservSeat() {
        return reservSeat;
    }

    public void setReservSeat(int reservSeat) {
        this.reservSeat = reservSeat;
    }

    public int getReservHour() {
        return reservHour;
    }

    public void setReservHour(int reservHour) {
        this.reservHour = reservHour;
    }
    
    public String getReservHourString() {
        return reservHourString;
    }

    public void setReservHourString(String reservHourString) {
        this.reservHourString = reservHourString;
    }
}