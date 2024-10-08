package com.tbfg.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.tbfg.dto.AttendanceDTO;
import com.tbfg.dto.BanSeatDTO;
import com.tbfg.dto.ClassroomDTO;
import com.tbfg.dto.ReserveList;

@Repository
public class ClassroomDAO {
	@Autowired // JdbcTemplate 객체를 자동으로 주입하기 위한 어노테이션
	private final JdbcTemplate jdbcTemplate; // JdbcTemplate 객체 선언

	// 생성자
	public ClassroomDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate; // JdbcTemplate 객체 초기화
	}

	// 좌석 예약을 추가하는 메서드
	public int reserveSeat(String userId, String classroomName, String subject, Integer seatNumber, String day,
			Integer randomNumber) {
		// Reservation 테이블에 예약 정보 (사용자 ID, 강의실 이름, 좌석 번호, 랜덤 번호)를 삽입하는 SQL 쿼리
		String sql = "INSERT INTO Reservation (user_id, classroom_name, subject, reservSeat, day, reservNum) VALUES (?, ?, ?, ?, ?, ?)";

		// jdbcTemplate을 사용해 SQL 쿼리를 실행하고, 영향받은 행의 수를 반환
		return jdbcTemplate.update(sql, userId, classroomName, subject, seatNumber, day, randomNumber);
	}

	// 예약 번호와 시간대를 ReservationHour 테이블에 추가하는 메서드
	public void addReservationHour(int reservNum, int hour) {
		// ReservationHour 테이블에 예약 번호와 해당 시간대를 삽입하는 SQL 쿼리
		String sql = "INSERT INTO ReservationHour (reservNum, reservHour) VALUES (?, ?)";

		// jdbcTemplate을 사용해 SQL 쿼리를 실행
		jdbcTemplate.update(sql, reservNum, hour);
	}

	@SuppressWarnings("deprecation")
	public List<ReservationInfoDTO> getReservationInfo(String classroomName, List<Integer> selectHours, String day,
			String subject) {
		String sql = "SELECT DISTINCT r.reservNum, r.reservSeat " + "FROM Reservation r "
				+ "JOIN ReservationHour rh ON r.reservNum = rh.reservNum "
				+ "WHERE r.classroom_name = ? AND r.day = ? AND r.subject = ? " + "AND rh.reservHour IN ("
				+ String.join(",", Collections.nCopies(selectHours.size(), "?")) + ")";

		List<Object> params = new ArrayList<>();
		params.add(classroomName);
		params.add(day);
		params.add(subject);
		params.addAll(selectHours);

		return jdbcTemplate.query(sql, params.toArray(),
				(rs, rowNum) -> new ReservationInfoDTO(rs.getInt("reservNum"), rs.getInt("reservSeat")));
	}

	public boolean updateReservationSeat(int reservNum, int newSeat) {
		String sql = "UPDATE Reservation SET reservSeat = ? WHERE reservNum = ?";
		int rowsAffected = jdbcTemplate.update(sql, newSeat, reservNum);
		return rowsAffected > 0;
	}

	// 특정 강의실, 시간대, 요일, 과목명에 따라 예약된 좌석을 가져오는 메서드
	public List<Integer> getReservedSeats(String classroomName, List<Integer> hours, String day, String subject) {
		if (hours == null || hours.isEmpty()) {
			return List.of(); // 빈 리스트 반환
		}

		// 예약된 좌석을 가져오는 SQL 쿼리
		String sql = "SELECT r.reservSeat " + "FROM Reservation r "
				+ "JOIN ReservationHour rh ON r.reservNum = rh.reservNum " + "WHERE r.classroom_name = ? " // 강의실 이름 필터링
				+ "AND rh.reservHour IN (?) " // 시간대 필터링
				+ "AND r.day = ? " // 요일 필터링
				+ "AND r.subject = ?"; // 과목명 필터링

		// 시간대를 쉼표로 구분된 문자열로 변환
		String hoursParam = hours.stream().map(String::valueOf).collect(Collectors.joining(","));

		// 쿼리 실행하고 결과 반환
		return jdbcTemplate.queryForList(sql, Integer.class, classroomName, hoursParam, day, subject);
	}

	// 모든 강의실의 예약 정보를 가져오는 메서드
	public List<ReserveList> reserveList() {
		// 모든 강의실과 예약 정보를 가져오는 SQL 쿼리
		String sql = "SELECT r.reservNum, r.user_id, r.classroom_name, r.subject, r.reservSeat, r.day, rh.reservHour "
				+ "FROM Reservation r " + "JOIN ReservationHour rh ON r.reservNum = rh.reservNum";

		// 쿼리를 실행하고 결과를 ReserveList 객체의 리스트로 반환
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			ReserveList reserveList = new ReserveList();
			reserveList.setReservNum(rs.getInt("reservNum"));
			reserveList.setUserId(rs.getString("user_id"));
			reserveList.setClassroomName(rs.getString("classroom_name"));
			reserveList.setSubject(rs.getString("subject"));
			reserveList.setReservSeat(rs.getInt("reservSeat"));
			reserveList.setDay(rs.getString("day"));
			reserveList.setReservHour(rs.getInt("reservHour"));
			return reserveList;
		});
	}

	// 강의실별로 예약 정보를 그룹화하고 예약 번호별로 시간대 묶기
	public Map<String, List<ReserveList>> getReserveList(String userId) {
		// 모든 예약 정보를 가져옴
		List<ReserveList> reserveList = reserveList();

		// 예약 번호별로 그룹화된 예약 정보를 저장할 맵
		Map<Integer, ReserveList> groupedByReservNum = new LinkedHashMap<>();

		// 각 예약 정보에 대해 반복
		for (ReserveList reserve : reserveList) {
			// 현재 예약이 로그인한 사용자와 일치하는지 확인
			if (reserve.getUserId().equals(userId)) {
				int reservNum = reserve.getReservNum(); // 현재 예약의 예약 번호를 가져옴

				// 동일한 예약 번호가 이미 맵에 존재하는지 확인
				if (groupedByReservNum.containsKey(reservNum)) {
					// 동일한 예약 번호가 이미 존재하면, 기존 예약 정보에 현재 시간대를 추가
					ReserveList existingReservation = groupedByReservNum.get(reservNum);
					String hoursString = existingReservation.getReservHourString() + "," + reserve.getReservHour();
					existingReservation.setReservHourString(hoursString); // 시간대를 문자열로 합침
				} else {
					// 동일한 예약 번호가 없으면, 현재 예약을 맵에 추가
					reserve.setReservHourString(String.valueOf(reserve.getReservHour())); // 초기 시간대 설정
					groupedByReservNum.put(reservNum, reserve);
				}
			}
		}

		// 예약 번호별로 그룹화된 예약 정보를 강의실 이름별로 다시 그룹화하여 반환
		return groupedByReservNum.values().stream().collect(Collectors.groupingBy(ReserveList::getClassroomName));
	}

	// 예약 취소 메서드
	public boolean cancelReservation(int reservNum, String userId) {
		// 예약 삭제를 위한 SQL 쿼리
		String deleteReservationSql = "DELETE FROM Reservation WHERE reservNum = ? AND user_id = ?";
		// 예약 시간 삭제를 위한 SQL 쿼리
		String deleteReservationHourSql = "DELETE FROM ReservationHour WHERE reservNum = ?";

		// Reservation 테이블에서 예약 삭제 시도
		int reservationSql = jdbcTemplate.update(deleteReservationSql, reservNum, userId);

		if (reservationSql > 0) {
			// 예약이 성공적으로 삭제된 경우, ReservationHour 테이블에서 해당 예약 번호에 관련된 모든 시간 삭제
			int reservationHourSql = jdbcTemplate.update(deleteReservationHourSql, reservNum);
			// 시간 삭제 작업이 성공적으로 수행되었는지 확인
			return reservationHourSql >= 0; // 예약 시간 삭제 여부를 확인
		}

		// 예약 삭제가 실패한 경우
		return false;
	}

	// 예약 시간 변경 메소드
	public boolean updateReservation(int reservNum, String newHour, String userId) {
		// 예약 시간 업데이트 전에 기존 예약 시간 삭제를 위한 SQL 쿼리
		// 예약이 존재하고 사용자 ID가 일치하는 경우에만 예약 시간 삭제
		String deleteSql = "DELETE FROM ReservationHour WHERE reservNum = ? AND EXISTS (SELECT 1 FROM Reservation WHERE reservNum = ? AND user_id = ?)";
		jdbcTemplate.update(deleteSql, reservNum, reservNum, userId);

		// 새로운 시간대 문자열을 쉼표로 분리하여 배열로 변환
		String[] hours = newHour.split(",");
		// 새로운 예약 시간을 삽입하기 위한 SQL 쿼리
		String insertSql = "INSERT INTO ReservationHour (reservNum, reservHour) VALUES (?, ?)";

		int insertHourSql = 0;
		// 새로운 시간대 배열을 순회하며 각각의 시간대에 대해 예약 시간 삽입
		for (String hour : hours) {
			insertHourSql += jdbcTemplate.update(insertSql, reservNum, Integer.parseInt(hour));
		}

		// 모든 새로운 예약 시간대가 성공적으로 삽입되었는지 확인
		return insertHourSql == hours.length;
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
		String sql = "INSERT INTO FavoriteClassrooms (user_id, classroom_num) " + "SELECT DISTINCT ?, classroomName "
				+ "FROM StuTimetable WHERE user_id = ? AND (user_id, classroomName) "
				+ "NOT IN (SELECT user_id, classroom_num FROM FavoriteClassrooms)";

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

	// 강의실 좌석 금지 정보를 데이터베이스에 삽입하는 메서드
	public int insertBanSeat(BanSeatDTO banSeat) {
		// 삽입할 SQL 쿼리
		String sql = "INSERT INTO BanSeat (user_id, classroom_name, subject, banSeat, day) VALUES (?, ?, ?, ?, ?)";
		// 자동 생성된 키(banNum)를 저장하기 위한 KeyHolder 객체 생성
		KeyHolder keyHolder = new GeneratedKeyHolder();

		// jdbcTemplate을 사용하여 쿼리 실행
		jdbcTemplate.update(connection -> {
			// PreparedStatement 생성 및 파라미터 설정
			PreparedStatement ps = connection.prepareStatement(sql, new String[] { "banNum" });
			ps.setString(1, banSeat.getUserId()); // 첫 번째 파라미터: 사용자 ID
			ps.setString(2, banSeat.getClassroomName()); // 두 번째 파라미터: 강의실 이름
			ps.setString(3, banSeat.getSubject()); // 세 번째 파라미터: 강의실 이름
			ps.setInt(4, banSeat.getBanSeat()); // 네 번째 파라미터: 금지된 좌석 번호
			ps.setString(5, banSeat.getDay()); // 다섯 번째 파라미터: 요일
			return ps; // PreparedStatement 반환
		}, keyHolder);

		// 생성된 banNum 반환
		return keyHolder.getKey().intValue();
	}

	// 특정 banNum에 대해 금지된 시간을 데이터베이스에 삽입하는 메서드
	public void insertBanSeatHour(int banNum, int banHour) {
		// 삽입할 SQL 쿼리
		String sql = "INSERT INTO BanSeatHour (banNum, banHour) VALUES (?, ?)";
		// jdbcTemplate을 사용하여 쿼리 실행
		jdbcTemplate.update(sql, banNum, banHour); // banNum과 banHour를 파라미터로 사용
	}

	@SuppressWarnings("deprecation")
	public List<Integer> getBannedSeats(String classroomName, List<Integer> selectHours, String subject, String day) {
		// SQL 쿼리 작성
		String sql = "SELECT DISTINCT b.banSeat " + "FROM BanSeat b " + "JOIN BanSeatHour h ON b.banNum = h.banNum "
				+ "WHERE b.classroom_name = ? " + "AND h.banHour IN ("
				+ String.join(",", selectHours.stream().map(String::valueOf).toArray(String[]::new)) + ") "
				+ "AND b.subject = ? " + "AND b.day = ?";

		// 쿼리 실행 및 결과 매핑
		return jdbcTemplate.query(sql, new Object[] { classroomName, subject, day }, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				// 쿼리 결과로부터 banSeat 컬럼 값을 Integer로 반환
				return rs.getInt("banSeat");
			}
		});
	}

	// 금지된 좌석 해제 기능
	public boolean unbanSeat(String classroomName, String banSeat, String day) {
		String sql = "DELETE FROM BanSEAT WHERE classroom_name = ? AND banSeat = ? AND day = ?";

		try {
			int rowsAffected = jdbcTemplate.update(sql, classroomName, banSeat, day);
			return rowsAffected > 0; // 해제 성공 시 true 반환
		} catch (Exception e) {
			e.printStackTrace();
			return false; // 예외 발생 시 false 반환
		}
	}

	// 강의실 이름 목록을 데이터베이스에서 가져오는 메서드
	public List<String> getAllClassrooms() {
		String sql = "SELECT classroom_name FROM Classrooms";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	// 특정 사용자가 주어진 강의실과 시간에 중복 예약이 있는지 확인하는 메서드
	public boolean checkSeat(String userId, String classroomName, List<Integer> selectHours, String day) {
		// SQL 쿼리를 작성하여 Reservation과 ReservationHour 테이블을 조인, 특정 조건에 맞는 예약이 있는지 COUNT
		String sql = "SELECT COUNT(*) FROM Reservation r " + "JOIN ReservationHour rh ON r.reservNum = rh.reservNum " + // ReservationHour
																														// 테이블을
																														// 예약번호로
																														// 조인
				"WHERE r.user_id = :userId AND r.classroom_name = :classroomName AND r.day = :day " + // 사용자 ID, 강의실 이름,
																										// 요일 조건 필터링
				"AND rh.reservHour IN (:selectHours)"; // 선택한 시간(selectHours)에 해당하는 예약이 있는지 확인

		// MapSqlParameterSource 객체를 사용하여 SQL의 매개변수 값을 설정 (selectHours는 IN 절에 바인딩)
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userId", userId); // 사용자 ID 매개변수 설정
		parameters.addValue("classroomName", classroomName); // 강의실 이름 매개변수 설정
		parameters.addValue("day", day); // 요일 매개변수 설정
		parameters.addValue("selectHours", selectHours); // 선택된 시간 리스트를 IN 절에 전달

		// NamedParameterJdbcTemplate을 사용하여 SQL 실행, IN 절 바인딩 지원
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		int count = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class); // 쿼리 결과를 COUNT로 가져옴

		return count > 0; // 중복 예약이 있으면 true 반환, 없으면 false 반환
	}

	// 주어진 강의실 이름이 Classrooms 테이블에 존재하는지 확인하는 메서드
	public boolean existByClassroomName(String classroomName) {
		// SQL 쿼리를 작성하여 강의실 이름으로 COUNT
		String sql = "SELECT COUNT(*) FROM Classrooms WHERE classroom_name = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, classroomName); // 쿼리 실행 후 COUNT 값을 반환
		return count != null && count > 0; // 강의실이 존재하면 true, 존재하지 않으면 false
	}

	// ClassroomDTO 객체를 받아서 새로운 강의실 정보를 Classrooms 테이블에 저장하는 메서드
	public void save(ClassroomDTO classroom) {
		// SQL 쿼리를 작성하여 강의실 정보를 INSERT
		String sql = "INSERT INTO Classrooms (classroom_name, leftCol, leftRow, rightCol, rightRow) VALUES (?, ?, ?, ?, ?)";
		// SQL에 강의실 이름, 좌석 배치 정보(leftCol, leftRow, rightCol, rightRow)를 전달하여 테이블에 삽입
		jdbcTemplate.update(sql, classroom.getClassroomName(), classroom.getLeftCol(), classroom.getLeftRow(),
				classroom.getRightCol(), classroom.getRightRow());
	}

	// 주어진 강의실 이름에 해당하는 강의실 정보를 Classrooms 테이블에서 조회하여 반환하는 메서드
	public ClassroomDTO getClassroomInfo(String classroomName) {
		// SQL 쿼리를 작성하여 강의실 이름으로 강의실 정보를 조회
		String sql = "SELECT * FROM Classrooms WHERE classroom_name = ?";

		// jdbcTemplate을 사용하여 쿼리 실행 후 ClassroomDTO 객체 리스트로 변환
		@SuppressWarnings("deprecation")
		List<ClassroomDTO> results = jdbcTemplate.query(sql, new Object[] { classroomName }, (rs, rowNum) -> {
			// ResultSet의 데이터를 ClassroomDTO 객체로 매핑
			ClassroomDTO classroom = new ClassroomDTO();
			classroom.setClassroomName(rs.getString("classroom_name")); // 강의실 이름 설정
			classroom.setLeftCol(rs.getInt("leftRow")); // 좌석 행과 열의 위치가 반대로 저장된 것을 수정
			classroom.setLeftRow(rs.getInt("leftCol"));
			classroom.setRightCol(rs.getInt("rightRow"));
			classroom.setRightRow(rs.getInt("rightCol"));
			return classroom;
		});

		if (results.isEmpty()) { // 결과가 없으면 null 반환
			return null;
		}
		return results.get(0); // 첫 번째 결과 반환 (강의실 정보)
	}

	// 특정 사용자가 주어진 강의실에서 예약한 좌석 리스트를 조회하는 메서드
	@SuppressWarnings("deprecation")
	public List<ReserveList> getReserveListByClassroom(String userId, String classroomName) {
		// SQL 쿼리를 작성하여 사용자 ID와 강의실 이름으로 예약 정보와 시간을 조인하여 조회
		String sql = "SELECT r.reservNum, r.user_id, r.classroom_name, r.reservSeat, rh.reservHour " + // 예약 번호, 사용자 ID,
																										// 강의실 이름, 좌석,
																										// 예약 시간 조회
				"FROM Reservation r " + "JOIN ReservationHour rh ON r.reservNum = rh.reservNum " + // Reservation과
																									// ReservationHour를
																									// 예약 번호로 조인
				"WHERE r.user_id = ? AND r.classroom_name = ?"; // 사용자 ID와 강의실 이름으로 필터링

		// 쿼리 실행 후 결과를 ReserveList 객체로 매핑하여 리스트 반환
		return jdbcTemplate.query(sql, new Object[] { userId, classroomName }, (rs, rowNum) -> {
			// ResultSet의 데이터를 ReserveList 객체로 변환
			ReserveList reserveList = new ReserveList();
			reserveList.setReservNum(rs.getInt("reservNum")); // 예약 번호 설정
			reserveList.setUserId(rs.getString("user_id")); // 사용자 ID 설정
			reserveList.setClassroomName(rs.getString("classroom_name")); // 강의실 이름 설정
			reserveList.setReservSeat(rs.getInt("reservSeat")); // 예약 좌석 번호 설정
			reserveList.setReservHour(rs.getInt("reservHour")); // 예약 시간 설정
			return reserveList;
		});
	}

	public List<AttendanceDTO> getTodayAttendance(String userId, String day, int currentHour) {
		String sql = "SELECT DISTINCT st.user_id, y.name AS user_name, y.studentId, st.subject, st.classroomName, st.start_hour, st.end_hour, "
				+ "CASE WHEN r.user_id IS NOT NULL THEN '출석' ELSE '결석' END AS status " + "FROM StuTimetable st "
				+ "JOIN Yuhan y ON st.user_id = y.id "
				+ "LEFT JOIN Reservation r ON st.user_id = r.user_id AND st.subject = r.subject "
				+ "AND st.classroomName = r.classroom_name AND st.day = r.day "
				+ "WHERE st.day = ? AND st.start_hour <= ? AND st.end_hour > ? "
				+ "AND st.user_id NOT IN (SELECT id FROM proYuhan WHERE position IN ('professor', 'admin'))";

		System.out.println("Executing SQL: " + sql);
		System.out.println("Parameters: day=" + day + ", currentHour=" + currentHour);

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			AttendanceDTO dto = new AttendanceDTO();
			dto.setUserId(rs.getString("user_id"));
			dto.setUserName(rs.getString("user_name"));
			dto.setStudentId(rs.getInt("studentId"));
			dto.setSubject(rs.getString("subject"));
			dto.setClassroomName(rs.getString("classroomName"));
			dto.setStartHour(rs.getInt("start_hour"));
			dto.setEndHour(rs.getInt("end_hour"));
			dto.setStatus(rs.getString("status"));
			return dto;
		}, day, currentHour, currentHour);
	}

	public boolean isProfessorOrAdmin(String userId) {
		String sql = "SELECT COUNT(*) FROM proYuhan WHERE id = ? AND position IN ('professor', 'admin')";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
		System.out.println("isProfessorOrAdmin check for user " + userId + ": result " + count);
		return count != null && count > 0;
	}

}
