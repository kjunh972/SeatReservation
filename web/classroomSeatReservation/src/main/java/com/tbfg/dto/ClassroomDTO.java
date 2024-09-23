package com.tbfg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassroomDTO {
    // 강의실 이름을 저장하는 변수
    private String classroomName;
    
    // 과목명
    private String subject;
    
    // 좌석 수를 저장하는 변수
    private int seatCount;
    
    // 요일을 저장하는 변수
    private String day;

    // 각 좌석의 예약 상태를 저장하는 리스트 (true = 예약됨, false = 예약되지 않음)
    private List<Boolean> seatStatusList = new ArrayList<>();
    
    // 선택한 시간대 목록을 저장하는 리스트
    private List<Integer> selectHours = new ArrayList<>();
    
    // 랜덤하게 생성된 예약 번호 목록을 저장하는 리스트
    private List<Integer> randomNumbers = new ArrayList<>();
    
    // 추가된 좌석 레이아웃 관련 변수
    private int leftRow; // 왼쪽 구역의 행 수
    private int leftCol; // 왼쪽 구역의 열 수
    private int rightRow; // 오른쪽 구역의 행 수
    private int rightCol; // 오른쪽 구역의 열 수

    // 강의실 이름을 반환하는 메서드
    public String getClassroomName() {
        return classroomName;
    }

    // 강의실 이름을 설정하는 메서드
    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }
    
    public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

    // 좌석 상태 리스트를 반환하는 메서드
    public List<Boolean> getSeatStatusList() {
        return seatStatusList;
    }

    // 좌석 수를 반환하는 메서드
    public int getSeatCount() {
        return seatCount;
    }
    
    // 요일을 반환하는 메서드
    public String getDay() {
        return day;
    }
    
    // 요일을 설정하는 메서드
    public void setDay(String day) {
        this.day = day;
    }
    
    // 선택한 시간대 목록을 반환하는 메서드
    public List<Integer> getSelectHours() {
        return selectHours;
    }

    // 선택한 시간대 목록을 설정하는 메서드
    public void setSelectHours(List<Integer> selectHours) {
        this.selectHours = selectHours;
    }
    
    // 선택한 시간대 목록을 초기화하는 메서드
    public void selectHour() {
        this.selectHours = new ArrayList<>();
    }

    // 좌석 수를 설정하고, 해당 수만큼 예약 상태 리스트를 초기화하는 메서드
    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
        seatStatusList = new ArrayList<>(Collections.nCopies(seatCount, false));
    }

    // 예약된 좌석 목록을 설정하여 해당 좌석의 예약 상태를 업데이트하는 메서드
    public void setReservedSeats(List<Integer> reservedSeats) {
        for (int seat : reservedSeats) {
            if (seat > 0 && seat <= seatStatusList.size()) {
                seatStatusList.set(seat - 1, true); // 좌석 번호는 1부터 시작하므로 -1
            }
        }
    }
    
    // 특정 좌석 번호를 예약하는 메서드
    public void reserveSeat(int seatNumber) {
        if (seatNumber > 0 && seatNumber <= seatStatusList.size()) {
            seatStatusList.set(seatNumber - 1, true); // 좌석 번호는 1부터 시작하므로 -1
        }
    }

    // 랜덤하게 생성된 예약 번호를 추가하는 메서드
    public void setRandomNum(int randomNumber) {
        randomNumbers.add(randomNumber);
    }

    // 랜덤 번호 목록을 반환하는 메서드
    public List<Integer> getRandomNum() {
        return randomNumbers;
    }

    // 왼쪽 구역의 행 수를 반환하는 메서드
    public int getLeftRow() {
        return leftRow;
    }

    // 왼쪽 구역의 행 수를 설정하는 메서드
    public void setLeftRow(int leftRow) {
        this.leftRow = leftRow;
    }

    // 왼쪽 구역의 열 수를 반환하는 메서드
    public int getLeftCol() {
        return leftCol;
    }

    // 왼쪽 구역의 열 수를 설정하는 메서드
    public void setLeftCol(int leftCol) {
        this.leftCol = leftCol;
    }

    // 오른쪽 구역의 행 수를 반환하는 메서드
    public int getRightRow() {
        return rightRow;
    }

    // 오른쪽 구역의 행 수를 설정하는 메서드
    public void setRightRow(int rightRow) {
        this.rightRow = rightRow;
    }

    // 오른쪽 구역의 열 수를 반환하는 메서드
    public int getRightCol() {
        return rightCol;
    }

    // 오른쪽 구역의 열 수를 설정하는 메서드
    public void setRightCol(int rightCol) {
        this.rightCol = rightCol;
    }
}