package com.tbfg.dto;

import java.util.ArrayList;
import java.util.List;

public class ClassroomDTO {
    private String classroomName; // 강의실 이름
    private int seatCount;
    private List<Boolean> seatStatusList = new ArrayList<>(); // 좌석 상태를 저장하는 리스트
    

    // 강의실 이름 getter 메서드
    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
		this.classroomName = classroomName;
	}

	// 좌석 상태 리스트 getter 메서드
    public List<Boolean> getSeatStatusList() {
        return seatStatusList;
    }

    // 좌석 예약 메서드
    public void reserveSeat(int seatNumber) {
        // 좌석 번호가 유효한 범위 내에 있을 때
        if (seatNumber > 0 && seatNumber <= seatStatusList.size()) {
            seatStatusList.set(seatNumber - 1, true); // 해당 좌석을 예약 상태로 변경
        }
    }

    // 좌석 예약 취소 메서드
    public void cancelReservation(int seatNumber) {
        // 좌석 번호가 유효한 범위 내에 있을 때
        if (seatNumber > 0 && seatNumber <= seatStatusList.size()) {
            seatStatusList.set(seatNumber - 1, false); // 해당 좌석을 비어있는 상태로 변경
        }
    }

	public int getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
		this.seatStatusList = new ArrayList<>(seatCount);
		for (int i = 0; i < seatCount; i++) {
            this.seatStatusList.add(false);
        }
	}
}
