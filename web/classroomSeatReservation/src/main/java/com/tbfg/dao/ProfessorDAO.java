package com.tbfg.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tbfg.dto.BanSeatList;
import com.tbfg.dto.ProfessorDTO;

@Repository
public class ProfessorDAO {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;  // 데이터베이스 작업을 위한 JdbcTemplate 객체

    // 생성자
    public ProfessorDAO(JdbcTemplate jdbcTemplate) { 
        this.jdbcTemplate = jdbcTemplate; // JdbcTemplate 객체 초기화
    }
    
    // ProfessorDTO 객체를 ResultSet으로부터 매핑하는 RowMapper 정의
    private final RowMapper<ProfessorDTO> professorRowMapper = (rs, rowNum) -> {
        ProfessorDTO professor = new ProfessorDTO();
        professor.setId(rs.getString("id"));  // ID 매핑
        professor.setPass(rs.getString("pass"));  // 비밀번호 매핑
        professor.setSchool(rs.getString("school"));  // 학교 매핑
        professor.setMajor(rs.getString("major"));  // 전공 매핑
        professor.setName(rs.getString("name"));  // 이름 매핑
        professor.setPosition(rs.getString("position"));  // 직위 매핑
        return professor;
    };

    // 교수 정보를 데이터베이스에 저장하는 메서드
    public void savePro(ProfessorDTO user) {
        // 중복 체크: id가 이미 존재하는지 확인
        if (isProExists(user.getId())) {
            throw new DuplicateKeyException("이미 존재하는 사용자입니다.");  // 중복 키 예외 발생
        }

        // 교수 정보를 데이터베이스에 삽입
        String sql = "INSERT INTO proYuhan (id, pass, school, major, name, position) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getPass(), user.getSchool(), user.getMajor(),
                user.getName(), user.getPosition());
    }
    
    // 특정 ID를 가진 교수 정보를 조회하는 메서드
    public ProfessorDTO getProById(String id) {
        String sql = "SELECT * FROM proYuhan WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, professorRowMapper, id);  // 교수 정보 조회 및 반환
    }

    // 특정 ID가 존재하는지 확인하는 메서드
    public boolean isProExists(String id) {
        String sql = "SELECT COUNT(*) FROM proYuhan WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);  // 해당 ID의 갯수를 조회
        return count > 0;  // 0보다 크면 true 반환
    }
    
    // 교수 비밀번호를 확인하는 메서드
    public boolean checkProPassword(String id, String password) {
        String sql = "SELECT pass FROM proYuhan WHERE id = ?";
        String oldPassword = jdbcTemplate.queryForObject(sql, String.class, id);  // ID로 비밀번호 조회
        return oldPassword != null && oldPassword.equals(password);  // 비밀번호가 일치하는지 확인
    }
    
    // 교수 정보를 업데이트하는 메서드
    public void updatePro(ProfessorDTO professor) {
        String sql = "UPDATE proYuhan SET name = ?, pass = ?, major = ? WHERE id = ?";
        jdbcTemplate.update(sql, professor.getName(), professor.getPass(), professor.getMajor(), professor.getId());  // 교수 정보 업데이트
    }

    // 교수 정보를 삭제하는 메서드
    public void deletePro(String id) {
        String sql = "DELETE FROM proYuhan WHERE id = ?";
        jdbcTemplate.update(sql, id);  // 해당 ID의 교수 정보를 데이터베이스에서 삭제
    }
    
    // 모든 금지된 좌석 정보를 가져오는 메서드
    public List<BanSeatList> banSeatList() {
        // 모든 금지된 좌석과 시간대를 가져오는 SQL 쿼리
        String sql = "SELECT bs.banNum, bs.user_id, bs.classroom_name, bs.subject, bs.banSeat, bs.day, bsh.banHour "
                   + "FROM BanSeat bs "
                   + "JOIN BanSeatHour bsh ON bs.banNum = bsh.banNum";

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
                int banNum = banSeat.getBanNum();  // 현재 금지된 좌석의 금지 번호를 가져옴

                // 동일한 금지 번호가 이미 맵에 존재하는지 확인
                if (groupedByBanNum.containsKey(banNum)) {
                    // 동일한 금지 번호가 이미 존재하면, 기존 좌석 정보에 현재 시간대를 추가
                    BanSeatList existingBanSeat = groupedByBanNum.get(banNum);
                    String hoursString = existingBanSeat.getBanHourString() + "," + banSeat.getBanHour();
                    existingBanSeat.setBanHourString(hoursString);  // 시간대를 문자열로 합침
                } else {
                    // 동일한 금지 번호가 없으면, 현재 금지된 좌석을 맵에 추가
                    banSeat.setBanHourString(String.valueOf(banSeat.getBanHour()));  // 초기 시간대 설정
                    groupedByBanNum.put(banNum, banSeat);
                }
            }
        }

        // 금지 번호별로 그룹화된 좌석 정보를 강의실 이름별로 다시 그룹화하여 반환
        return groupedByBanNum.values().stream()
                .collect(Collectors.groupingBy(BanSeatList::getClassroomName));
    }

}
