package com.tbfg.dto;

public class TimeTableDTO {
	private String userId;
    private String day;
    private Integer startHour;
    private Integer endHour;
    private String subject;
    private String classroomName;
    private Integer warning;
    private String reservationStatus;  // 예약 상태 필드 추가
    
    
 // 예약 정보를 저장할 필드 추가 (ReserveList)
    private ReserveList reservationInfo;
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public Integer getStartHour() {
		return startHour;
	}
	public void setStartHour(Integer startHour) {
		this.startHour = startHour;
	}
	public Integer getEndHour() {
		return endHour;
	}
	public void setEndHour(Integer endHour) {
		this.endHour = endHour;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getClassroomName() {
		return classroomName;
	}
	public void setClassroomName(String classroomName) {
		this.classroomName = classroomName;
	}
	public Integer getWarning() {
		return warning;
	}
	public void setWarning(Integer warning) {
		this.warning = warning;
	}
	public ReserveList getReservationInfo() {
		return reservationInfo;
	}
	public void setReservationInfo(ReserveList reservationInfo) {
		this.reservationInfo = reservationInfo;
	}
	public String getReservationStatus() {
		return reservationStatus;
	}
	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
    
    
}