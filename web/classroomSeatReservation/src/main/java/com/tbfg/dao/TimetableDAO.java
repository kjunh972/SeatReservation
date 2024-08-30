package com.tbfg.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tbfg.dto.TimeTableDTO;

@Repository
public class TimetableDAO {
    @Autowired // JdbcTemplate 객체를 자동으로 주입하기 위한 어노테이션
    private final JdbcTemplate jdbcTemplate; // JdbcTemplate 객체 선언

    // 생성자
    public TimetableDAO(JdbcTemplate jdbcTemplate) { 
        this.jdbcTemplate = jdbcTemplate; // JdbcTemplate 객체 초기화
    } 

    // 사용자의 시간표를 조회하는 메서드
    // 사용자 ID를 기반으로 해당 사용자의 시간표 정보를 데이터베이스에서 가져와 리스트로 반환
    public List<TimeTableDTO> getTimetable(String userId) {
        String sql = "SELECT * FROM StuTimetable WHERE user_id = ?";
        RowMapper<TimeTableDTO> rowMapper = (rs, rowNum) -> {
            TimeTableDTO timeTableDTO = new TimeTableDTO(); // TimeTableDTO 객체 생성
            // ResultSet에서 데이터 추출하여 TimeTableDTO 객체에 설정
            timeTableDTO.setUserId(rs.getString("user_id")); // 사용자 ID 설정
            timeTableDTO.setDay(rs.getString("day")); // 요일 설정
            timeTableDTO.setStartHour(rs.getInt("start_hour")); // 시작 시간 설정
            timeTableDTO.setEndHour(rs.getInt("end_hour")); // 종료 시간 설정
            timeTableDTO.setSubject(rs.getString("subject")); // 과목 설정
            timeTableDTO.setClassroomName(rs.getString("classroomName")); // 강의실 이름 설정
            return timeTableDTO; // 설정된 TimeTableDTO 객체 반환
        };
        return jdbcTemplate.query(sql, rowMapper, userId); // SQL 쿼리 실행 및 결과 반환
    }
    
    // 사용자의 시간표를 설정하는 메서드
    // 사용자가 시간표를 추가하거나 수정할 때 사용됩니다.
    public void setTimetable(TimeTableDTO timeTableDTO) {
        // 해당 유저의 시간표에 같은 과목명이 있는지 확인하는 SQL 쿼리
        String existingSql = "SELECT COUNT(*) FROM StuTimetable WHERE user_id = ? AND subject = ?";
        // 이미 해당 과목명이 있는지 확인하기 위해 실행하고 결과를 반환하는 쿼리
        int existingCount = jdbcTemplate.queryForObject(existingSql, Integer.class, timeTableDTO.getUserId(), timeTableDTO.getSubject());
        if (existingCount > 0) { // 이미 해당 과목명이 있는 경우
            // 이미 있는 과목의 시간표를 업데이트하는 SQL 쿼리
            String updateSql = "UPDATE StuTimetable SET classroomName = ?, day = ?, start_hour = ?, end_hour = ? WHERE user_id = ? AND subject = ?";
            // 해당 과목의 시간표 정보를 업데이트하는 쿼리를 실행
            jdbcTemplate.update(updateSql, timeTableDTO.getClassroomName(), timeTableDTO.getDay(), timeTableDTO.getStartHour(), timeTableDTO.getEndHour(), timeTableDTO.getUserId(), timeTableDTO.getSubject());
        } else { // 해당 과목명이 없는 경우
            // 새로운 시간표를 추가하는 SQL 쿼리
            String insertSql = "INSERT INTO StuTimetable (user_id, subject, classroomName, day, start_hour, end_hour) VALUES (?, ?, ?, ?, ?, ?)";
            // 새로운 과목의 시간표 정보를 데이터베이스에 추가하는 쿼리를 실행
            jdbcTemplate.update(insertSql, timeTableDTO.getUserId(), timeTableDTO.getSubject(), timeTableDTO.getClassroomName(), timeTableDTO.getDay(), timeTableDTO.getStartHour(), timeTableDTO.getEndHour());
        }
    }

    // 사용자의 시간표를 삭제하는 메서드
    public void deleteTimeTable(String userId, String subject) {
        // 해당 사용자의 시간표에서 특정 과목을 삭제하는 SQL 쿼리
        String sql = "DELETE FROM StuTimetable WHERE user_id = ? AND subject = ?";
        // 사용자 ID와 삭제할 과목명을 조건으로 하는 쿼리를 실행하여 해당 과목을 삭제
        jdbcTemplate.update(sql, userId, subject);
    }
}
