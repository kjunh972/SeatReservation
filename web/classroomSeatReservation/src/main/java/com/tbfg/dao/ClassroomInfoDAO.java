package com.tbfg.dao;

import com.tbfg.dto.ReserveList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;

@Repository
public class ClassroomInfoDAO {
    private final JdbcTemplate jdbcTemplate;

    public ClassroomInfoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ReserveList 매핑을 위한 RowMapper
    private final RowMapper<ReserveList> rowMapper = (rs, rowNum) -> {
        ReserveList reserveList = new ReserveList();
        reserveList.setReservNum(rs.getInt("reservNum")); // 예약 번호
        reserveList.setUserId(rs.getString("user_id")); // 사용자 ID
        reserveList.setClassroomName(rs.getString("classroom_name")); // 교실 이름
        reserveList.setReservSeat(rs.getInt("reservSeat")); // 예약 좌석
        reserveList.setDay(rs.getString("day")); // 예약 요일
        reserveList.setReservHour(rs.getInt("reservHour")); // 예약 시간
        reserveList.setSubject(rs.getString("subject")); // 과목

        return reserveList;
    };

    // 특정 교실의 예약 정보를 가져오는 메서드
    public List<ReserveList> getReservationsByClassroom(String classroomName) {
        String sql = "SELECT r.*, rh.reservHour, st.subject " +
                     "FROM reservation r " +
                     "JOIN reservationhour rh ON r.reservNum = rh.reservNum " +
                     "JOIN stutimetable st ON r.classroom_name = st.classroomName " +
                     "WHERE r.classroom_name = ?";

        try {
            System.out.println("Query: " + sql + " | Parameter: " + classroomName);
            return jdbcTemplate.query(sql, rowMapper, classroomName);
        } catch (Exception e) {
            System.err.println("Error while fetching reservations: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 특정 교실과 과목으로 예약 정보를 가져오는 메서드
    public List<ReserveList> getReservations(String classroomName, String subject) {
        String sql = "SELECT r.*, rh.reservHour, st.subject " +
                     "FROM reservation r " +
                     "JOIN reservationhour rh ON r.reservNum = rh.reservNum " +
                     "JOIN stutimetable st " +
                     "ON r.classroom_name = st.classroomName AND r.day = st.day " + // 요일 매칭
                     "WHERE r.classroom_name = ? AND st.subject = ?";

        try {
            System.out.println("Executing SQL: " + sql + " | Parameters: " + classroomName + ", " + subject);
            List<ReserveList> results = jdbcTemplate.query(sql, rowMapper, classroomName, subject);

            if (results.isEmpty()) {
                System.out.println("No reservations found for classroom: " + classroomName + " and subject: " + subject);
            } else {
                System.out.println("Reservations found: " + results.size());
            }

            return results;
        } catch (Exception e) {
            System.err.println("Error while fetching reservations: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
