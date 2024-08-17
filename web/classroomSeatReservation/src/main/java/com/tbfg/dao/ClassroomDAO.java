package com.tbfg.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public int reserveSeat(String userId, String classroomName, Integer seatNumber, Integer randomNumber) {
        // Reservation 테이블에 예약 정보 (사용자 ID, 강의실 이름, 좌석 번호, 랜덤 번호)를 삽입하는 SQL 쿼리
        String sql = "INSERT INTO Reservation (user_id, classroom_name, reservSeat, reservNum) VALUES (?, ?, ?, ?)";
        
        // jdbcTemplate을 사용해 SQL 쿼리를 실행하고, 영향받은 행의 수를 반환
        return jdbcTemplate.update(sql, userId, classroomName, seatNumber, randomNumber);
    }

    // 예약 번호와 시간대를 ReservationHour 테이블에 추가하는 메서드
    public void addReservationHour(int reservNum, int hour) {
        // ReservationHour 테이블에 예약 번호와 해당 시간대를 삽입하는 SQL 쿼리
        String sql = "INSERT INTO ReservationHour (reservNum, reservHour) VALUES (?, ?)";
        
        // jdbcTemplate을 사용해 SQL 쿼리를 실행
        jdbcTemplate.update(sql, reservNum, hour);
    }

    // 특정 강의실과 시간대에 예약된 좌석을 가져오는 메서드
    public List<Integer> getReservedSeats(String classroomName, List<Integer> hours) {
        // 만약 hours 리스트가 null이거나 비어있다면, 빈 리스트를 반환 (쿼리를 실행하지 않음)
        if (hours == null || hours.isEmpty()) {
            System.out.println("빈 리스트 반환");
            return List.of(); // 빈 리스트 반환
        }
        
        // 예약된 좌석을 가져오는 SQL 쿼리 (Reservation과 ReservationHour 테이블을 조인)
        String sql = "SELECT r.reservSeat "
                   + "FROM Reservation r "
                   + "JOIN ReservationHour rh ON r.reservNum = rh.reservNum "
                   + "WHERE r.classroom_name = ?"  // 강의실 이름에 대해 필터링
                   + "AND rh.reservHour IN (?)";  // 시간대 필터링 (in 절 사용)
        
        // List<Integer> (hours 리스트)를 쿼리에 사용하기 위해 쉼표로 구분된 문자열로 변환
        String hoursParam = hours.stream()
                                 .map(String::valueOf)  // 각 정수를 문자열로 변환
                                 .collect(Collectors.joining(",")); // 쉼표로 연결하여 단일 문자열로 만듦
        
        // 변환된 hoursParam을 사용하여 SQL 쿼리를 실행하고, 결과를 List<Integer>로 반환
        return jdbcTemplate.queryForList(sql, Integer.class, classroomName, hoursParam);
    }

    // 모든 강의실의 예약 정보를 가져오는 메서드
    public List<ReserveList> reserveList() {
        // 모든 강의실과 예약 정보를 가져오는 SQL 쿼리
        String sql = "SELECT r.reservNum, r.user_id, r.classroom_name, r.reservSeat, rh.reservHour "
                + "FROM Reservation r "
                + "JOIN ReservationHour rh ON r.reservNum = rh.reservNum";

        // 쿼리를 실행하고 결과를 ReserveList 객체의 리스트로 반환
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ReserveList reserveList = new ReserveList();
            reserveList.setReservNum(rs.getInt("reservNum"));
            reserveList.setUserId(rs.getString("user_id"));
            reserveList.setClassroomName(rs.getString("classroom_name"));
            reserveList.setReservSeat(rs.getInt("reservSeat"));
            reserveList.setReservHour(rs.getInt("reservHour"));
            return reserveList;
        });
    }
    
    // 강의실별로 예약 정보를 그룹화하고 예약 번호별로 시간대 묶기
    public Map<String, List<ReserveList>> getReserveList() {
        // 모든 예약 정보를 가져옴
        List<ReserveList> reserveList = reserveList();

        // 예약 번호별로 그룹화된 예약 정보를 저장할 맵
        Map<Integer, ReserveList> groupedByReservNum = new LinkedHashMap<>();

        // 각 예약 정보에 대해 반복
        for (ReserveList reserve : reserveList) {
            int reservNum = reserve.getReservNum();  // 현재 예약의 예약 번호를 가져옴

            // 동일한 예약 번호가 이미 맵에 존재하는지 확인
            if (groupedByReservNum.containsKey(reservNum)) {
                // 동일한 예약 번호가 이미 존재하면, 기존 예약 정보에 현재 시간대를 추가
                ReserveList existingReservation = groupedByReservNum.get(reservNum);
                String hoursString = existingReservation.getReservHourString() + "," + reserve.getReservHour();
                existingReservation.setReservHourString(hoursString);  // 시간대를 문자열로 합침
            } else {
                // 동일한 예약 번호가 없으면, 현재 예약을 맵에 추가
                reserve.setReservHourString(String.valueOf(reserve.getReservHour()));  // 초기 시간대 설정
                groupedByReservNum.put(reservNum, reserve);
            }
        }

        // 예약 번호별로 그룹화된 예약 정보를 강의실 이름별로 다시 그룹화하여 반환
        return groupedByReservNum.values().stream()
                .collect(Collectors.groupingBy(ReserveList::getClassroomName));
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
                   + "SELECT DISTINCT ?, classroomName "
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
