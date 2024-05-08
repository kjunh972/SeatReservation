package com.tbfg.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ClassroomDAO {
	@Autowired // 자동 주입을 위한 어노테이션
    private final JdbcTemplate jdbcTemplate; // JdbcTemplate 객체 선언

    // 생성자
    public ClassroomDAO(JdbcTemplate jdbcTemplate) { 
        this.jdbcTemplate = jdbcTemplate; // JdbcTemplate 객체 초기화
    } 

    // 강의실 이름과 사용자 아이디를 받아 예약된 좌석을 가져오는 메서드
    public List<Integer> getReservedSeats(String classroomName, String userId) { 
    	// MySql 예약된 자석 쿼리문 생성
        String sql = "SELECT reservSeat FROM Reservation WHERE classroom_name = ? AND user_id = ?";
        // jdbcTemplate을 사용하여 SQL 쿼리 실행하여 결과 반환
        return jdbcTemplate.queryForList(sql, Integer.class, classroomName, userId);
    }
}