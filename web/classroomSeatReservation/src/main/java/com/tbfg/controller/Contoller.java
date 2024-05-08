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

	@GetMapping("/index")
    public String index() {
        return "index";
    }

	@Autowired // JdbcTemplate 객체 자동 주입을 위한 어노테이션
    private JdbcTemplate jdbcTemplate; // JdbcTemplate 객체 선언 및 주입

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
	
    @GetMapping("/classroomSelect")
    public String classroomSelect() {
        return "classroomSelect"; 
    }

}