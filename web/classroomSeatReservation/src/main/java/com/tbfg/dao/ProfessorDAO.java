package com.tbfg.dao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tbfg.dto.BanSeatList;
import com.tbfg.dto.ProfessorDTO;
import com.tbfg.dto.ReserveList;

@Repository
public class ProfessorDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate; // 데이터베이스 작업을 위한 JdbcTemplate 객체

	// 생성자
	public ProfessorDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate; // JdbcTemplate 객체 초기화
	}

	// ProfessorDTO 객체를 ResultSet으로부터 매핑하는 RowMapper 정의
	private final RowMapper<ProfessorDTO> professorRowMapper = (rs, rowNum) -> {
		ProfessorDTO professor = new ProfessorDTO();
		professor.setId(rs.getString("id")); // ID 매핑
		professor.setPass(rs.getString("pass")); // 비밀번호 매핑
		professor.setSchool(rs.getString("school")); // 학교 매핑
		professor.setMajor(rs.getString("major")); // 전공 매핑
		professor.setName(rs.getString("name")); // 이름 매핑
		professor.setPosition(rs.getString("position")); // 직위 매핑
		return professor;
	};

	// 교수 정보를 데이터베이스에 저장하는 메서드
	public void savePro(ProfessorDTO user) {
		// 중복 체크: id가 이미 존재하는지 확인
		if (isProExists(user.getId())) {
			throw new DuplicateKeyException("이미 존재하는 사용자입니다."); // 중복 키 예외 발생
		}

		// 교수 정보를 데이터베이스에 삽입
		String sql = "INSERT INTO proYuhan (id, pass, school, major, name, position) " + "VALUES (?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, user.getId(), user.getPass(), user.getSchool(), user.getMajor(), user.getName(),
				user.getPosition());
	}

	// 특정 ID를 가진 교수 정보를 조회하는 메서드
	public ProfessorDTO getProById(String id) {
		String sql = "SELECT * FROM proYuhan WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, professorRowMapper, id); // 교수 정보 조회 및 반환
	}

	// 특정 ID가 존재하는지 확인하는 메서드
	public boolean isProExists(String id) {
		String sql = "SELECT COUNT(*) FROM proYuhan WHERE id = ?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id); // 해당 ID의 갯수를 조회
		return count > 0; // 0보다 크면 true 반환
	}

	// 교수 비밀번호를 확인하는 메서드
	public boolean checkProPassword(String id, String password) {
		String sql = "SELECT pass FROM proYuhan WHERE id = ?";
		String oldPassword = jdbcTemplate.queryForObject(sql, String.class, id); // ID로 비밀번호 조회
		return oldPassword != null && oldPassword.equals(password); // 비밀번호가 일치하는지 확인
	}

	// 교수 정보를 업데이트하는 메서드
	public void updatePro(ProfessorDTO professor) {
		String sql = "UPDATE proYuhan SET name = ?, pass = ?, major = ? WHERE id = ?";
		jdbcTemplate.update(sql, professor.getName(), professor.getPass(), professor.getMajor(), professor.getId()); // 교수
																														// 정보
																														// 업데이트
	}

	// 교수 정보를 삭제하는 메서드
	public void deletePro(String id) {
		String sql = "DELETE FROM proYuhan WHERE id = ?";
		jdbcTemplate.update(sql, id); // 해당 ID의 교수 정보를 데이터베이스에서 삭제
	}

	// 모든 금지된 좌석 정보를 가져오는 메서드
	public List<BanSeatList> banSeatList() {
		// 모든 금지된 좌석과 시간대를 가져오는 SQL 쿼리
		String sql = "SELECT bs.banNum, bs.user_id, bs.classroom_name, bs.subject, bs.banSeat, bs.day, bsh.banHour "
				+ "FROM BanSeat bs " + "JOIN BanSeatHour bsh ON bs.banNum = bsh.banNum";

		// 쿼리를 실행하고 결과를 BanSeatList 객체의 리스트로 반환
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			BanSeatList banSeatList = new BanSeatList();
			banSeatList.setBanNum(rs.getInt("banNum"));
			banSeatList.setUserId(rs.getString("user_id"));
			banSeatList.setClassroomName(rs.getString("classroom_name"));
			banSeatList.setSubject(rs.getString("subject"));
			banSeatList.setBanSeat(rs.getInt("banSeat"));
			banSeatList.setDay(rs.getString("day"));
			banSeatList.setBanHour(rs.getInt("banHour"));
			return banSeatList;
		});
	}

	// 강의실별로 금지된 좌석 정보를 그룹화하고 금지 번호별로 시간대 묶기
	public Map<String, List<BanSeatList>> getBanSeatList(String userId) {
		// 모든 금지된 좌석 정보를 가져옴
		List<BanSeatList> banSeatList = banSeatList();

		// 금지 번호별로 그룹화된 좌석 정보를 저장할 맵
		Map<Integer, BanSeatList> groupedByBanNum = new LinkedHashMap<>();

		// 각 금지 좌석 정보에 대해 반복
		for (BanSeatList banSeat : banSeatList) {
			// 현재 금지된 좌석이 로그인한 사용자와 일치하는지 확인
			if (banSeat.getUserId().equals(userId)) {
				int banNum = banSeat.getBanNum(); // 현재 금지된 좌석의 금지 번호를 가져옴

				// 동일한 금지 번호가 이미 맵에 존재하는지 확인
				if (groupedByBanNum.containsKey(banNum)) {
					// 동일한 금지 번호가 이미 존재하면, 기존 좌석 정보에 현재 시간대를 추가
					BanSeatList existingBanSeat = groupedByBanNum.get(banNum);
					String hoursString = existingBanSeat.getBanHourString() + "," + banSeat.getBanHour();
					existingBanSeat.setBanHourString(hoursString); // 시간대를 문자열로 합침
				} else {
					// 동일한 금지 번호가 없으면, 현재 금지된 좌석을 맵에 추가
					banSeat.setBanHourString(String.valueOf(banSeat.getBanHour())); // 초기 시간대 설정
					groupedByBanNum.put(banNum, banSeat);
				}
			}
		}

		// 금지 번호별로 그룹화된 좌석 정보를 강의실 이름별로 다시 그룹화하여 반환
		return groupedByBanNum.values().stream().collect(Collectors.groupingBy(BanSeatList::getClassroomName));
	}

	// 모든 금지된 좌석 정보를 가져오는 메서드
	public Map<String, List<BanSeatList>> getAllBanSeatList() {
		// 모든 금지된 좌석 정보를 가져옴
		List<BanSeatList> banSeatList = banSeatList();

		// 금지 번호별로 그룹화된 좌석 정보를 저장할 맵
		Map<Integer, BanSeatList> groupedByBanNum = new LinkedHashMap<>();

		// 각 금지 좌석 정보에 대해 반복
		for (BanSeatList banSeat : banSeatList) {
			int banNum = banSeat.getBanNum(); // 현재 금지된 좌석의 금지 번호를 가져옴

			// 동일한 금지 번호가 이미 맵에 존재하는지 확인
			if (groupedByBanNum.containsKey(banNum)) {
				// 동일한 금지 번호가 이미 존재하면, 기존 좌석 정보에 현재 시간대를 추가
				BanSeatList existingBanSeat = groupedByBanNum.get(banNum);
				String hoursString = existingBanSeat.getBanHourString() + "," + banSeat.getBanHour();
				existingBanSeat.setBanHourString(hoursString); // 시간대를 문자열로 합침
			} else {
				// 동일한 금지 번호가 없으면, 현재 금지된 좌석을 맵에 추가
				banSeat.setBanHourString(String.valueOf(banSeat.getBanHour())); // 초기 시간대 설정
				groupedByBanNum.put(banNum, banSeat);
			}
		}

		// 금지 번호별로 그룹화된 좌석 정보를 강의실 이름별로 다시 그룹화하여 반환
		return groupedByBanNum.values().stream().collect(Collectors.groupingBy(BanSeatList::getClassroomName));
	}

	// 특정 좌석이 이미 예약되었는지 확인하는 메서드
	public boolean isAlreadyReserved(int seat) {
		// SQL 쿼리를 작성하여 해당 좌석이 Reservation 테이블에 이미 예약되었는지 확인 (COUNT로 반환)
		String sql = "SELECT COUNT(*) FROM Reservation WHERE reservSeat = ?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, seat); // 쿼리 실행 후 결과로 COUNT 값을 반환
		return count > 0; // 예약된 좌석이 있으면 true 반환, 없으면 false 반환
	}

	// 예약 번호로 예약 정보를 조회하는 메서드
	@SuppressWarnings("deprecation")
	public ReserveList findByReservNum(int reservNum) {
		// SQL 쿼리를 작성하여 Reservation과 ReservationHour 테이블을 조인, 예약 정보를 GROUP_CONCAT으로 예약
		// 시간 문자열로 반환
		String sql = "SELECT r.*, GROUP_CONCAT(rh.reservHour ORDER BY rh.reservHour) AS reservHours "
				+ "FROM Reservation r " + "JOIN ReservationHour rh ON r.reservNum = rh.reservNum "
				+ "WHERE r.reservNum = ? " + "GROUP BY r.reservNum";
		try {
			// 쿼리 실행 후 예약 정보를 ReserveList 객체로 매핑
			return jdbcTemplate.queryForObject(sql, new Object[] { reservNum }, (rs, rowNum) -> {
				ReserveList reserveList = new ReserveList();
				reserveList.setReservNum(rs.getInt("reservNum")); // 예약 번호 설정
				reserveList.setUserId(rs.getString("user_id")); // 사용자 ID 설정
				reserveList.setClassroomName(rs.getString("classroom_name")); // 강의실 이름 설정
				reserveList.setSubject(rs.getString("subject")); // 과목명 설정
				reserveList.setReservSeat(rs.getInt("reservSeat")); // 예약 좌석 설정
				reserveList.setDay(rs.getString("day")); // 요일 설정

				// GROUP_CONCAT으로 가져온 예약 시간들을 처리하여 예약 시간 문자열로 설정
				String reservHours = rs.getString("reservHours");
				if (reservHours != null) {
					reserveList.setReservHourString(reservHours); // 예약 시간 문자열 설정
					// 첫 번째 예약 시간을 reservHour 필드에 설정 (필요한 경우 첫 번째 시간만 사용)
					String[] hours = reservHours.split(",");
					if (hours.length > 0) {
						reserveList.setReservHour(Integer.parseInt(hours[0])); // 첫 번째 예약 시간을 숫자로 변환하여 설정
					}
				}

				return reserveList;
			});
		} catch (EmptyResultDataAccessException e) {
			return null; // 예약 번호에 해당하는 데이터가 없으면 null 반환
		}
	}

	// 예약을 취소하는 메서드 (트랜잭션 적용: 모든 작업이 성공적으로 완료되면 커밋, 하나라도 실패하면 롤백)
	@Transactional
	public boolean cancelReservation(int reservNum) {
		try {
			// 외래 키 제약 조건을 일시적으로 해제
			jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");

			// ReservationHour 테이블에서 해당 예약 번호로 예약 시간을 삭제
			String sqlReservationHour = "DELETE FROM ReservationHour WHERE reservNum = ?";
			int rowsAffectedReservationHour = jdbcTemplate.update(sqlReservationHour, reservNum);

			// Reservation 테이블에서 해당 예약 번호로 예약을 삭제
			String sqlReservation = "DELETE FROM Reservation WHERE reservNum = ?";
			int rowsAffectedReservation = jdbcTemplate.update(sqlReservation, reservNum);

			// 외래 키 제약 조건을 다시 활성화
			jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");

			// 두 테이블에서 모두 성공적으로 삭제되었는지 확인 (성공 시 true 반환, 실패 시 false)
			return rowsAffectedReservationHour > 0 && rowsAffectedReservation > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false; // 예외 발생 시 false 반환
		}
	}

	// 예약 번호를 변경하는 메서드 (트랜잭션 처리 없이, 예약 번호 수정)
	public boolean changeReservationNumber(int oldReservNum, int newReservNum) {
		try {
			// 외래 키 제약 조건을 일시적으로 해제
			jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=0");

			// Reservation 테이블에서 기존 예약 번호를 새로운 예약 번호로 업데이트
			String sqlReservation = "UPDATE Reservation SET reservNum = ? WHERE reservNum = ?";
			int rowsAffectedReservation = jdbcTemplate.update(sqlReservation, newReservNum, oldReservNum);

			// ReservationHour 테이블에서도 동일한 예약 번호 변경 작업 수행
			String sqlReservationHour = "UPDATE ReservationHour SET reservNum = ? WHERE reservNum = ?";
			int rowsAffectedReservationHour = jdbcTemplate.update(sqlReservationHour, newReservNum, oldReservNum);

			// 외래 키 제약 조건을 다시 활성화
			jdbcTemplate.update("SET FOREIGN_KEY_CHECKS=1");

			// 두 테이블에서 모두 성공적으로 업데이트되었는지 확인 (성공 시 true 반환, 실패 시 false)
			return rowsAffectedReservation > 0 && rowsAffectedReservationHour > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false; // 예외 발생 시 false 반환
		}
	}
	
	// 예약 번호로 학생 정보를 조회하는 메서드
	public Optional<Map<String, Object>> findStudentInfo(int reservNum) {
	    // Yuhan 테이블과 Reservation 테이블을 조인하여 학생 정보를 조회하는 SQL 쿼리
	    String sql = "SELECT y.name, y.studentId " +
	                 "FROM Yuhan y " +
	                 "JOIN Reservation r ON y.id = r.user_id " +
	                 "WHERE r.reservNum = ?";
	    
	    try {
	        // JdbcTemplate을 사용하여 쿼리 실행 및 결과 매핑
	        return jdbcTemplate.query(
	            sql,
	            (rs, rowNum) -> {
	                // 결과셋에서 이름과 학번을 추출하여 Map으로 변환
	                Map<String, Object> result = new HashMap<>();
	                result.put("name", rs.getString("name"));
	                result.put("studentId", rs.getInt("studentId"));
	                return result;
	            },
	            reservNum // SQL 쿼리의 파라미터로 예약 번호 전달
	        ).stream().findFirst(); // 결과 중 첫 번째 항목만 선택하여 Optional로 반환
	    } catch (EmptyResultDataAccessException e) {
	        // 조회 결과가 없는 경우 빈 Optional 반환
	        return Optional.empty();
	    }
	}
}
