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
	
	// 특정 교실의 모든 예약 정보
    public List<ReserveList> getReservationsByClassroom(String classroomName) {
        String sql = "SELECT DISTINCT r.reservNum, r.user_id, r.classroom_name, " +
                    "r.subject, r.reservSeat, r.day, rh.reservHour " +
                    "FROM Reservation r " +
                    "JOIN ReservationHour rh ON r.reservNum = rh.reservNum " +
                    "WHERE r.classroom_name = ? " +
                    "ORDER BY r.day, rh.reservHour";

        try {
            return jdbcTemplate.query(sql, rowMapper, classroomName);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 특정 교실과 과목의 예약 정보
    public List<ReserveList> getReservations(String classroomName, String subject) {
        String sql = "SELECT DISTINCT r.reservNum, r.user_id, r.classroom_name, " +
                    "r.subject, r.reservSeat, r.day, rh.reservHour " +
                    "FROM Reservation r " +
                    "JOIN ReservationHour rh ON r.reservNum = rh.reservNum " +
                    "JOIN StuTimetable st ON r.classroom_name = st.classroomName " +
                    "AND r.day = st.day " +
                    "WHERE r.classroom_name = ? " +
                    "AND st.subject = ? " +
                    "ORDER BY r.day, rh.reservHour";

        try {
            return jdbcTemplate.query(sql, rowMapper, classroomName, subject);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
