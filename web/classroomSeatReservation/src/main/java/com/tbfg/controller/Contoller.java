package com.tbfg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.tbfg.dao.ClassroomDAO;
import com.tbfg.dto.ClassroomDTO;

@Controller
public class Contoller {
	@Autowired // JdbcTemplate 객체 자동 주입을 위한 어노테이션
    private JdbcTemplate jdbcTemplate; // JdbcTemplate 객체 선언 및 주입

	@GetMapping("/index")
    public String index() {
        return "index";
    }
	
	@GetMapping("/classroomLike")
    public String classroomLike(Model model) {
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
        
        // 즐겨찾기한 강의실 버튼 목록 가져오기
        List<String> favoriteClassrooms = classroomDAO.getFavoriteClassrooms("kjunh972");
        
        // 모델에 즐겨찾기한 강의실 버튼 목록 추가
        model.addAttribute("classroomButtons", favoriteClassrooms);

        return "classroomLike";
    }

    @PostMapping("/classroomStatus") 
    public String classroomStatus(String classroomName, Model model) { 
        ClassroomDTO classroom = new ClassroomDTO(); // ClassroomDTO 객체 생성
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate); // ClassroomDAO 객체 생성 및 주입
        classroom.setClassroomName(classroomName); // 강의실 이름 설정
        classroom.setSeatCount(48); // 강의실 좌석 수 설정

        // 선택한 강의실에 대한 예약된 좌석 조회
        List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, "kjunh972");

        // 예약된 좌석 상태 설정
        for (Integer seat : reservedSeats) { // 모든 예약된 좌석에 대해 반복
            classroom.reserveSeat(seat); // 해당 좌석을 예약 상태로 설정
        }

        model.addAttribute("classroom", classroom); // 모델에 ClassroomDTO 객체 추가

        return "classroomStatus";
    }
	
    @PostMapping("/classroomSelect")
    public String classroomSelect(String classNum, Model model) {
        // ClassroomDAO 객체 생성 및 JdbcTemplate 주입
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
        
        // 선택한 강의 번호에 해당하는 강의실 목록 조회
        List<String> classrooms = classroomDAO.getClassrooms(classNum);
        
        // 모델에 강의실 목록과 선택한 강의 번호 추가
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("classNum", classNum);
        
        return "classroomSelect"; 
    }

    @PostMapping("/classSelect")
    public String getClassSelect(String classNumber, Model model) {
        // ClassroomDAO 객체 생성 및 JdbcTemplate 주입
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
        
        // 모든 강의 번호 목록 조회
        List<String> classNum = classroomDAO.getClassNum();
        
        // 모델에 강의 번호 목록 추가
        model.addAttribute("classNum", classNum);
        
        return "classSelect";
    }



}