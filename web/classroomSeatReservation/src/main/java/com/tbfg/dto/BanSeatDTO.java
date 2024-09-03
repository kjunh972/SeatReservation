package com.tbfg.dto;

import java.util.List;

public class BanSeatDTO {
	private int banNum;
    private String userId;
    private String classroomName;
    private int banSeat;
    private List<Integer> bannedSeats; // 금지된 좌석 리스트 추가
    
    public boolean isBanned(Integer seatNumber) {
        return bannedSeats != null && bannedSeats.contains(seatNumber);
    }
    
    public List<Integer> getBannedSeats() {
        return bannedSeats;
    }

    public void setBannedSeats(List<Integer> bannedSeats) {
        this.bannedSeats = bannedSeats;
    }
    
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
	public int getBanSeat() {
		return banSeat;
	}
	public void setBanSeat(int banSeat) {
		this.banSeat = banSeat;
	}
    
    
}
