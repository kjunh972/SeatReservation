package com.tbfg.dto;

public class ReservationInfoDTO {
	private int reservNum;
    private int reservSeat;

    public ReservationInfoDTO(int reservNum, int reservSeat) {
        this.reservNum = reservNum;
        this.reservSeat = reservSeat;
    }

    // getter와 setter 메서드
    public int getReservNum() {
        return reservNum;
    }

    public void setReservNum(int reservNum) {
        this.reservNum = reservNum;
    }

    public int getReservSeat() {
        return reservSeat;
    }

    public void setReservSeat(int reservSeat) {
        this.reservSeat = reservSeat;
    }
}
