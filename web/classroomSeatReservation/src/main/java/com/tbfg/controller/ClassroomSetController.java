package com.tbfg.controller;

import com.tbfg.dto.ClassroomDTO;

import jakarta.servlet.http.HttpSession;

import com.tbfg.dao.ClassroomDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ClassroomSetController {

    @Autowired
    private ClassroomDAO classroomDAO;
    private ClassroomDTO classroomDTO = new ClassroomDTO();
    @Autowired
    private Contoller ct = new Contoller();

    @GetMapping("/classroomSet")
    public String seatLayout(HttpSession session, Model model) {
    	
    	String userId = ct.GetId(session);
		if (userId == null) {
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login"; // 로그인 페이지로 리다이렉트
		}
		
		ct.getTimetablePage(model, session);
        model.addAttribute("predefinedLayouts", getPredefinedLayouts());
        return "classroomSet"; // 'classroomSet.html'이라는 이름의 뷰를 반환합니다.
    }
    

    @PostMapping("/resultLayout")
    public String resultLayout(
            @RequestParam(required = false) String seatTemplate,
            @RequestParam(required = false) Integer leftSeatRows,
            @RequestParam(required = false) Integer leftSeatColumns,
            @RequestParam(required = false) Integer rightSeatRows,
            @RequestParam(required = false) Integer rightSeatColumns,
            @RequestParam(required = false) String classroomNumber,
            HttpSession session, Model model) {
    	String userId = ct.GetId(session);
		if (userId == null) {
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login"; // 로그인 페이지로 리다이렉트
		}
        // 좌석 템플릿을 가져옵니다. 커스텀 템플릿의 경우 사용자가 입력한 값을 기반으로 템플릿을 생성
        if ("커스텀".equals(seatTemplate)) {
            if (leftSeatRows == null || leftSeatColumns == null || rightSeatRows == null || rightSeatColumns == null) {
                model.addAttribute("error", "커스텀 좌석 값을 모두 입력해주세요.");
                return "classroomSet";
            }
            // 입력받은 좌석 값을 기반으로 템플릿 생성
            seatTemplate = String.format("왼쪽 : %dX%d, 오른쪽 : %dX%d", leftSeatRows, leftSeatColumns, rightSeatRows, rightSeatColumns);
        } else {
            // 미리 정의된 템플릿은 그대로 사용
            seatTemplate = (seatTemplate != null && !seatTemplate.isEmpty()) ? seatTemplate : "커스텀";
        }

        Integer leftCol = null;
        Integer leftRow = null;
        Integer rightCol = null;
        Integer rightRow = null;

        // 미리 정의된 템플릿의 경우 좌석 정보를 파싱
        if (!"커스텀".equals(seatTemplate)) {
            Pattern pattern = Pattern.compile("왼쪽 : (\\d+)X(\\d+), 오른쪽 : (\\d+)X(\\d+)");
            Matcher matcher = pattern.matcher(seatTemplate);

            if (matcher.find()) {
            	leftCol = Integer.parseInt(matcher.group(1));
                leftRow = Integer.parseInt(matcher.group(2));
                rightCol = Integer.parseInt(matcher.group(3));
                rightRow = Integer.parseInt(matcher.group(4));              
            }
        } else {
            // 커스텀 값 저장
        	leftCol = leftSeatColumns;
            leftRow = leftSeatRows;
            rightCol = rightSeatColumns;
            rightRow = rightSeatRows;
        }

        model.addAttribute("seatTemplate", seatTemplate);  // 최종 좌석 템플릿을 뷰로 전달
        model.addAttribute("classroomNumber", classroomNumber != null ? classroomNumber : "");
        model.addAttribute("leftCol", leftCol);
        model.addAttribute("leftRow", leftRow);  
        model.addAttribute("rightCol", rightCol);
        model.addAttribute("rightRow", rightRow);

        // 강의실 이름 중복 체크
        if (classroomNumber != null && !classroomNumber.isEmpty()) {
            boolean exists = classroomDAO.existByClassroomName(classroomNumber);
            if (exists) {
                model.addAttribute("error", "이미 존재하는 강의실 이름입니다.");
                model.addAttribute("predefinedLayouts", getPredefinedLayouts());
                return "classroomSet";
            }
        }

        return "resultLayout"; // 결과 페이지로 이동
    }

    @PostMapping("/saveClassroom")
    public String saveClassroom(
            @RequestParam String seatTemplate,
            @RequestParam String classroomNumber,
            @RequestParam(required = false) Integer leftSeatRows,
            @RequestParam(required = false) Integer leftSeatColumns,
            @RequestParam(required = false) Integer rightSeatRows,
            @RequestParam(required = false) Integer rightSeatColumns,
            Model model) {

    	Integer leftCol = null;
        Integer leftRow = null;
        Integer rightCol = null;
        Integer rightRow = null;

        // 좌석 정보 처리
        if ("커스텀".equals(seatTemplate)) {
        	leftCol = leftSeatColumns;
            leftRow = leftSeatRows;
            rightCol = rightSeatColumns;
            rightRow = rightSeatRows;
        } else {
            Pattern pattern = Pattern.compile("왼쪽 : (\\d+)X(\\d+), 오른쪽 : (\\d+)X(\\d+)");
            Matcher matcher = pattern.matcher(seatTemplate);

            if (matcher.find()) {
            	leftRow = Integer.parseInt(matcher.group(1));
                leftCol = Integer.parseInt(matcher.group(2));
                rightRow = Integer.parseInt(matcher.group(3));
                rightCol = Integer.parseInt(matcher.group(4));  
            }
        }
        
        System.out.println("leftCol : "+leftCol);
        // ClassroomDTO에 데이터 저장
        
        classroomDTO.setClassroomName(classroomNumber);
        classroomDTO.setLeftCol(leftCol != null ? leftCol : 0);
        classroomDTO.setLeftRow(leftRow != null ? leftRow : 0);   
        classroomDTO.setRightCol(rightCol != null ? rightCol : 0);
        classroomDTO.setRightRow(rightRow != null ? rightRow : 0);

        // DB에 강의실 저장
        classroomDAO.save(classroomDTO);

        // 저장 후 classroomResult로 리다이렉트
        return "redirect:/classroomResult?classroomNumber=" + classroomNumber; // 저장 후 결과 페이지로 리다이렉트
    }

    // 강의실 중복 확인 API
    @PostMapping("/checkClassroomExists")
    @ResponseBody
    public boolean checkClassroomExists(@RequestParam String classroomNumber) {
        return classroomDAO.existByClassroomName(classroomNumber);
    }

    // 강의실 결과 페이지를 위한 메서드
    @GetMapping("/classroomResult")
    public String classroomResult(@RequestParam(required = false) String classroomNumber, Model model) {
        if (classroomNumber != null && !classroomNumber.isEmpty()) {
            ClassroomDTO classroom = classroomDAO.getClassroomInfo(classroomNumber);
            if (classroom != null) {
                model.addAttribute("classroom", classroom);
            } else {
                model.addAttribute("error", "강의실을 찾을 수 없습니다.");
            }
        }
        
        // 미리 정의된 좌석 레이아웃 추가
        model.addAttribute("predefinedLayouts", getPredefinedLayouts());
        
        // classroomDTO 객체에 값 설정
        classroomDTO.setClassroomNull(1);
        System.out.println("classroomDTO.getClassroomNull() Result : "+classroomDTO.getClassroomNull());
        model.addAttribute("classroom",classroomDTO);
        
        // 'classroomResult.html' 뷰를 반환
        return "classroomResult";
    }

    private List<String> getPredefinedLayouts() {
        List<String> layouts = new ArrayList<>();
        layouts.add("왼쪽 : 4X5, 오른쪽 : 4X5");
        layouts.add("왼쪽 : 4X6, 오른쪽 : 4X6");
        layouts.add("왼쪽 : 4X7, 오른쪽 : 4X7");
        return layouts;
    }
}
