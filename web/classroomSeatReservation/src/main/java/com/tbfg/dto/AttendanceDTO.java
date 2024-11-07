package com.tbfg.dto;

public class AttendanceDTO {
	 /** 사용자 ID */
    private String userId; 
    /** 사용자 이름 */
    private String userName;
    /** 학번 */
    private int studentId;
    /** 과목명 */
    private String subject;
    /** 강의실명 */
    private String classroomName;
    /** 수업 시작 시간 */
    private int startHour;
    /** 수업 종료 시간 */
    private int endHour;
    /** 출석 상태 문자열 (출석/결석) */
    private String status;
    /** 출석 여부 (true: 출석, false: 결석) */
    private boolean attended;
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
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
	public int getStartHour() {
		return startHour;
	}
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getEndHour() {
		return endHour;
	}
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isAttended() {
        return attended;
    }
    public void setAttended(boolean attended) {
        this.attended = attended;
    }
    
}
