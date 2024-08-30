package com.tbfg.dao;

import com.tbfg.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository  
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;  // 데이터베이스 작업을 위한 JdbcTemplate 객체

    // 생성자
    public UserDAO(JdbcTemplate jdbcTemplate) { 
        this.jdbcTemplate = jdbcTemplate;  // JdbcTemplate 객체 초기화
    }

    // UserDTO 객체를 ResultSet으로부터 매핑하는 RowMapper 정의
    private final RowMapper<UserDTO> userRowMapper = (rs, rowNum) -> {
        UserDTO user = new UserDTO();
        user.setId(rs.getString("id"));  // ID 매핑
        user.setPass(rs.getString("pass"));  // 비밀번호 매핑
        user.setSchool(rs.getString("school"));  // 학교 매핑
        user.setMajor(rs.getString("major"));  // 전공 매핑
        user.setName(rs.getString("name"));  // 이름 매핑
        user.setAge(rs.getInt("age"));  // 나이 매핑
        user.setGrade(rs.getInt("grade"));  // 학년 매핑
        user.setClassNumber(rs.getString("class"));  // 클래스 번호 매핑
        user.setStudentId(rs.getString("studentId"));  // 학번 매핑
        user.setPosition(rs.getString("position"));  // 직위 매핑
        return user;
    };
    
    // 두 테이블에서 비밀번호를 검색하는 메서드
    @SuppressWarnings("deprecation")  // 경고를 무시하도록 지시하는 애노테이션
    public String getPasswordFromTables(String id) {
        // 두 테이블에서 비밀번호를 조회하기 위한 SQL 쿼리
        String sql = "SELECT pass FROM Yuhan WHERE id = ? UNION SELECT pass FROM proYuhan WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id, id}, String.class);
        } catch (Exception e) {
            return null;  // 예외 발생 시 null 반환
        }
    }

    // 사용자 정보를 데이터베이스에 저장하는 메서드
    public void saveUser(UserDTO user) {
        // 사용자 ID가 이미 존재하는지 확인
        if (isUserExists(user.getId())) {
            throw new DuplicateKeyException("이미 존재하는 사용자입니다.");  // 중복 사용자 예외 처리
        }

        // 사용자 정보를 데이터베이스에 삽입
        String sql = "INSERT INTO yuhan (id, pass, school, major, name, age, grade, class, studentId, position) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getPass(), user.getSchool(), user.getMajor(),
                user.getName(), user.getAge(), user.getGrade(), user.getClassNumber(), user.getStudentId(),
                user.getPosition());  // 사용자 정보 저장
    }

    // 특정 ID가 사용자 테이블에 존재하는지 확인하는 메서드
    public boolean isUserExists(String id) {
        String sql = "SELECT COUNT(*) FROM yuhan WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);  // ID의 존재 여부를 카운트
        return count != null && count > 0;  // 카운트가 0보다 크면 true 반환
    }

    // 특정 ID를 가진 사용자 정보를 조회하는 메서드
    public UserDTO getUserById(String id) {
        String sql = "SELECT * FROM yuhan WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, userRowMapper, id);  // 사용자 정보 조회 및 반환
    }

    // 사용자 비밀번호를 확인하는 메서드
    public boolean checkPassword(String id, String password) {
        String sql = "SELECT pass FROM yuhan WHERE id = ?";
        String oldPassword = jdbcTemplate.queryForObject(sql, String.class, id);  // ID로 비밀번호 조회
        return oldPassword != null && oldPassword.equals(password);  // 비밀번호가 일치하는지 확인
    }

    // 사용자 정보를 업데이트하는 메서드
    public void updateUser(UserDTO user) {
        String sql = "UPDATE yuhan SET name = ?, studentId = ?, pass = ?, major = ?, grade = ?, class = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getStudentId(), user.getPass(), user.getMajor(), user.getGrade(), user.getClassNumber(), user.getId());  // 사용자 정보 업데이트
    }

    // 사용자 정보를 삭제하는 메서드
    public void deleteUser(String id) {
        String sql = "DELETE FROM yuhan WHERE id = ?";
        jdbcTemplate.update(sql, id);  // 해당 ID의 사용자 정보를 데이터베이스에서 삭제
    }
}
