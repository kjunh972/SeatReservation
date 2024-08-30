package com.tbfg.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
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
}
