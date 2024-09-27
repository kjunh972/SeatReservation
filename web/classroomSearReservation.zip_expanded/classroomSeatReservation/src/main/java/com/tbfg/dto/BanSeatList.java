package com.tbfg.dto;

public class BanSeatList {
    private int banNum;
    private String userId;
    private String classroomName;
    private String Subject;
    private int banSeat;
    private String day;
    private int banHour;
    private String banHourString;

    // Getters and Setters
    public int getBanNum() {
        return banNum;
    }

    public void setBanNum(int banNum) {
        this.banNum = banNum;
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
    
    public String getSubject() {
		return Subject;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

    public int getBanSeat() {
        return banSeat;
    }

    public void setBanSeat(int banSeat) {
        this.banSeat = banSeat;
    }
    
    public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

    public int getBanHour() {
        return banHour;
    }

    public void setBanHour(int banHour) {
        this.banHour = banHour;
    }

    public String getBanHourString() {
        return banHourString;
    }

    public void setBanHourString(String banHourString) {
        this.banHourString = banHourString;
    }
}
