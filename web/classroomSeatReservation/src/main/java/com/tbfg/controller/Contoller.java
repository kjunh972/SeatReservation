package com.tbfg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.tbfg.dto.ClassroomDTO;

@Controller
public class Contoller {
	@GetMapping("/")
    public String index() {
        return "index"; // 이 부분이 템플릿의 파일 이름과 일치해야 합니다.
    }

    @PostMapping("/classroomStatus")
    public String classroomStatus(ClassroomDTO classroom, String classroomName, Model model) {
        // ClassroomDTO 객체 생성
        if (classroomName.equals("5406호")) {
        	classroom.setClassroomName("5406호");
            classroom.setSeatCount(48);
      
        } else if (classroomName.equals("5407호")) {
        	classroom.setClassroomName("5407호");
            classroom.setSeatCount(48);
        } else if (classroomName.equals("5408호")) {
        	classroom.setClassroomName("5408호");
            classroom.setSeatCount(48);
        } 

        // 좌석 예약 상태 설정 (임의의 예시)
        classroom.reserveSeat(2);
        classroom.reserveSeat(4);
        classroom.reserveSeat(6);
        classroom.reserveSeat(8);
        classroom.reserveSeat(10);
        classroom.reserveSeat(12);
        classroom.reserveSeat(14);
        classroom.reserveSeat(16);
        classroom.reserveSeat(18);
        classroom.reserveSeat(20);

        // 모델에 ClassroomDTO 객체 추가
        model.addAttribute("classroom", classroom);

        return "classroomStatus"; // 뷰 이름 리턴
    }

	
    @GetMapping("/classroomSelect")
    public String classroomSelect() {
        return "classroomSelect"; // 강의실 선택 페이지를 렌더링
    }

}