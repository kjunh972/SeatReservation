package com.tbfg.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClassroomDAO {
    @Autowired // JdbcTemplate 객체를 자동으로 주입하기 위한 어노테이션
    private final JdbcTemplate jdbcTemplate; // JdbcTemplate 객체 선언

    // 생성자
    public ClassroomDAO(JdbcTemplate jdbcTemplate) { 
        this.jdbcTemplate = jdbcTemplate; // JdbcTemplate 객체 초기화
    } 

    // 강의실 이름과 사용자 아이디를 받아 예약된 좌석을 가져오는 메서드
    public List<Integer> getReservedSeats(String classroomName, String userId) { 
        // MySQL에서 예약된 좌석을 가져오기 위한 쿼리문
        String sql = "SELECT reservSeat FROM Reservation WHERE classroom_name = ? AND user_id = ?";
        // jdbcTemplate을 사용하여 SQL 쿼리를 실행하고 결과를 리스트로 반환
        return jdbcTemplate.queryForList(sql, Integer.class, classroomName, userId);
    }
    
    // 사용자 아이디를 받아 즐겨찾기한 강의실 목록을 가져오는 메서드
    public List<String> getFavoriteClassrooms(String userId) {
        // MySQL에서 즐겨찾기한 강의실 목록을 가져오기 위한 쿼리문
        String sql = "SELECT classroom_num FROM FavoriteClassrooms WHERE user_id = ?";
        // jdbcTemplate을 사용하여 SQL 쿼리를 실행하고 결과를 리스트로 반환
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }
    
    // 사용자 아이디를 받아 timetable의 강의실 정보를 favoriteClassrooms에 추가하는 메서드
    public void getTimetableClassrooms(String userId) {
        // MySQL에서 즐겨찾기할 강의실 목록을 가져와 favoriteClassrooms에 추가하는 쿼리문
        String sql = "INSERT INTO favoriteClassrooms (user_id, classroom_num) "
                   + "SELECT DISTINCT ?, classroomName AS classroom_num "
                   + "FROM stutimetable WHERE user_id = ? AND (user_id, classroomName) "
                   + "NOT IN (SELECT user_id, classroom_num FROM favoriteClassrooms)";

        // jdbcTemplate을 사용하여 SQL 쿼리를 실행
        jdbcTemplate.update(sql, userId, userId);
    }


    // 모든 강의 번호 목록을 가져오는 메서드
    public List<String> getClassNum() {
        // MySQL에서 모든 강의 번호를 가져오기 위한 쿼리문
        String sql = "SELECT DISTINCT LEFT(classroom_name, 1) AS classNum FROM Classrooms";
        // jdbcTemplate을 사용하여 SQL 쿼리를 실행하고 결과를 리스트로 반환
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    // 건물 번호를 받아 해당하는 강의실 목록을 가져오는 메서드
    public List<String> getClassrooms(String classNum) {
        // 해당 건물 번호로 시작하는 강의실을 가져오기 위한 검색 패턴 생성
        String prefix = classNum + "%";
        // MySQL에서 해당 건물 번호로 시작하는 강의실 목록을 가져오기 위한 쿼리문
        String sql = "SELECT classroom_name FROM Classrooms WHERE classroom_name LIKE ?";
        // jdbcTemplate을 사용하여 SQL 쿼리를 실행하고 결과를 리스트로 반환
        return jdbcTemplate.queryForList(sql, String.class, prefix);
    }
}
