package com.tbfg.dto;

import java.util.ArrayList;
import java.util.List;

public class ClassroomDTO {
    // 강의실 이름을 저장하는 변수
    private String classroomName;
    // 좌석 수를 저장하는 변수
    private int seatCount;
    // 각 좌석의 예약 상태를 저장하는 리스트 (true = 예약됨, false = 예약되지 않음)
    private List<Boolean> seatStatusList = new ArrayList<>();
    // 선택한 시간대 목록을 저장하는 리스트
    private List<Integer> selectHours = new ArrayList<>();
    // 랜덤하게 생성된 예약 번호 목록을 저장하는 리스트
    private List<Integer> randomNumbers = new ArrayList<>();

    // 강의실 이름을 반환하는 메서드
    public String getClassroomName() {
        return classroomName;
    }

    // 강의실 이름을 설정하는 메서드
    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    // 좌석 상태 리스트를 반환하는 메서드
    public List<Boolean> getSeatStatusList() {
        return seatStatusList;
    }

    // 좌석 수를 반환하는 메서드
    public int getSeatCount() {
        return seatCount;
    }

    // 좌석 수를 설정하고, 좌석 상태 리스트를 초기화하는 메서드
    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
        // 좌석 수에 맞춰 좌석 상태 리스트를 초기화
        this.seatStatusList = new ArrayList<>(seatCount);
        // 초기 상태는 모두 예약되지 않음 (false)
        for (int i = 0; i < seatCount; i++) {
            this.seatStatusList.add(false);
        }
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
    
    // 특정 좌석을 예약 상태로 설정하는 메서드
    public void reserveSeat(int seatNumber) {
        // 좌석 번호가 유효한 경우에만 예약 상태로 설정
        if (seatNumber > 0 && seatNumber <= seatStatusList.size()) {
            seatStatusList.set(seatNumber - 1, true);
        }
    }

    // 예약된 좌석 목록을 설정하여 좌석 상태를 업데이트하는 메서드
    public void setReservedSeats(List<Integer> reservedSeats) {
        // 각 예약된 좌석을 순회하며 예약 상태로 설정
        for (Integer seat : reservedSeats) {
            reserveSeat(seat);
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
}
