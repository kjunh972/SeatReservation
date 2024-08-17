package com.tbfg.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbfg.dao.ClassroomDAO;
import com.tbfg.dao.UserDAO;
import com.tbfg.dto.ClassroomDTO;
import com.tbfg.dto.ReserveList;
import com.tbfg.dto.TimeTableDTO;

@Controller
public class Contoller {
	private ClassroomDTO classroomDTO = new ClassroomDTO();
    private TimeTableDTO timetableDTO = new TimeTableDTO();
    
    @Autowired
    private JdbcTemplate jdbcTemplate; 

    // 메인페이지 메서드
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    // 강의실 즐겨찾기 메서드
    @GetMapping("/classroomLike")
    public String classroomLike(Model model) {
    	ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
    	
        // 사용자가 즐겨찾기한 강의실 버튼 목록 가져오기
        classroomDAO.getTimetableClassrooms("kjunh972");
        List<String> favoriteClassrooms = classroomDAO.getFavoriteClassrooms("kjunh972");
        
        // 모델에 즐겨찾기한 강의실 버튼 목록 추가
        model.addAttribute("classroomButtons", favoriteClassrooms);

        return "classroomLike";
    }

    // 강의실 좌석 예약 메서드 
    @PostMapping("/classroomStatus") 
    public String classroomStatus(String classroomName,
                                  @RequestParam("selectHours") String selectHoursJson,
                                  Model model) throws JsonProcessingException {

        // JDBC 템플릿을 사용하여 ClassroomDAO 객체를 생성
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
        // classroomDTO 객체에 사용자가 입력한 강의실 이름을 설정
        classroomDTO.setClassroomName(classroomName);     
        // 강의실의 좌석 수를 48로 설정
        classroomDTO.setSeatCount(48);

        // ObjectMapper를 사용하여 JSON 문자열을 Java의 List<Integer>로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> selectHours = objectMapper.readValue(selectHoursJson, new TypeReference<List<Integer>>() {});
        // 변환된 시간대를 classroomDTO 객체에 설정
        classroomDTO.setSelectHours(selectHours);

        // DAO를 통해 선택한 시간대에 이미 예약된 좌석들을 조회
        List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, selectHours);
        // 예약된 좌석을 classroomDTO에 반영하여 예약 상태를 업데이트
        for (Integer seat : reservedSeats) {
            classroomDTO.reserveSeat(seat);
        }

        model.addAttribute("classroom", classroomDTO);
        
        return "classroomStatus";
    }

    // 강의실 좌석 예약 메서드
    @PostMapping("/reserveSeat")  
    public String reserveSeat(String classroomName, Integer seatNumber,
                              Model model) throws JsonProcessingException {

        // JDBC 템플릿을 사용하여 ClassroomDAO 객체를 생성
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

        // 0부터 999 사이의 랜덤 번호를 생성
        int randomNum = new Random().nextInt(1000);
        // classroomDTO에 랜덤 번호 저장
        classroomDTO.setRandomNum(randomNum);
        
        // DAO를 사용하여 좌석을 예약하고, 해당 좌석에 랜덤 번호를 설정
        classroomDAO.reserveSeat("kjunh972", classroomName, seatNumber, randomNum);
        // 사용자가 선택한 시간대에 대해 예약된 시간 정보를 ReservationHour 테이블에 추가
        for (Integer hour : classroomDTO.getSelectHours()) {
            classroomDAO.addReservationHour(randomNum, hour);
        }
        
        String hours = classroomDTO.getSelectHours().stream().map(String::valueOf).collect(Collectors.joining("시, "));

        model.addAttribute("seatNumber", seatNumber);
        model.addAttribute("selectHours", hours);
        model.addAttribute("randomNum", randomNum);
        model.addAttribute("classroom", classroomDTO);
        
        return "reserveSeat";
    }
    
    // 강의실 좌석 예약 확인 리스트 메서드
    @GetMapping("/reserveList")
    public String reserveList(Model model) {
        // ClassroomDAO 인스턴스를 생성하여 데이터베이스 작업을 처리
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

        // 모든 예약 정보를 강의실별로 그룹화하여 가져옴
        Map<String, List<ReserveList>> reserveList = classroomDAO.getReserveList();

        // 강의실별로 그룹화된 예약 정보를 모델에 추가하여 뷰에서 사용 가능하도록 설정
        model.addAttribute("reserveList", reserveList);

        return "reserveList";
    }



    // 선택한 강의 번호에 해당하는 강의실 목록을 조회하는 메서드
    @PostMapping("/classroomSelect")
    public String classroomSelect(String classNum, Model model) {
    	ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
    	
        // 선택한 강의 번호에 해당하는 강의실 목록 조회
        List<String> classrooms = classroomDAO.getClassrooms(classNum);
        
        // 모델에 강의실 목록과 선택한 강의 번호 추가
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("classNum", classNum);
        
        return "classroomSelect"; 
    }

    // 모든 강의 번호 목록을 조회하는 메서드
    @PostMapping("/classSelect")
    public String getClassSelect(String classNumber, Model model) {
    	ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
    	
        // 모든 강의 번호 목록 조회
        List<String> classNum = classroomDAO.getClassNum();
        
        // 모델에 강의 번호 목록 추가
        model.addAttribute("classNum", classNum);
        
        return "classSelect";
    }
    
    // 시간 선택하는 페이지를 보여주는 메서드
    @PostMapping("/timeSelect")
    public String timeSelect(String classroomName, Model model) {
    	List<Integer> hours = Arrays.asList(9, 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8); // 9시부터 8시까지의 시간
    	classroomDTO.setClassroomName(classroomName); // 강의실 번호 설정
    	
    	// 모델에 ClassroomDTO 객체 추가
        model.addAttribute("classroom", classroomDTO); 
        model.addAttribute("hours", hours);
        return "timeSelect";
    }
    
    @GetMapping("/timetable")
    public String timetable(Model model, @ModelAttribute("warning") String warning) {
    	UserDAO userDAO = new UserDAO(jdbcTemplate);
    	String userId = "kjunh972"; // 사용자 ID

        // 사용자의 모든 시간표를 가져옴.
        List<TimeTableDTO> userTimeTable = userDAO.getTimetable(userId);

        // 요일과 시간 목록을 생성하여 모델에 추가
        List<String> days = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일");
        List<Integer> hours = Arrays.asList(9, 10, 11, 12, 1, 2, 3, 4);

        // 모델에 사용자의 시간표 정보를 추가합니다.
        model.addAttribute("userTimeTable", userTimeTable);
        // 모델에 요일 목록을 추가합니다.
        model.addAttribute("days", days);
        // 모델에 시간 목록을 추가합니다.
        model.addAttribute("hours", hours);

        return "timetable"; 
    }
    
    @PostMapping("/timetable")
    public String postTimetable(String subject, String classroomName, String day, Integer startHour,
            Integer endHour, Model model) {
        UserDAO userDAO = new UserDAO(jdbcTemplate);
        String userId = "kjunh972"; // 사용자 ID
        
        try {
            if (startHour != null && endHour != null && startHour >= endHour) { // 시작시간이 종료시간보다 크면
                model.addAttribute("error", ":: Warning - 시작 시간이 종료 시간보다 클 수 없습니다. ::");
            } else {

            // 시간표 추가
            timetableDTO.setUserId(userId);
            timetableDTO.setSubject(subject);
            timetableDTO.setClassroomName(classroomName);
            timetableDTO.setDay(day);
            timetableDTO.setStartHour(startHour);
            timetableDTO.setEndHour(endHour);

            // `setTimetable` 메서드 호출
            userDAO.setTimetable(timetableDTO);
            }

        } catch (DataIntegrityViolationException e) {
            // `SQLIntegrityConstraintViolationException` 강의실이 존재 하지않을 때.
            model.addAttribute("error", ":: Warning - 강의실이 존재하지 않거나, 외래 키 제약 조건을 위반했습니다. ::");
            System.out.println("오류 : "+e.getMessage());
        } catch (DataAccessException e) {
            // 일반적인 `DataAccessException`을 처리합니다.
            model.addAttribute("error", ":: Warning - 데이터베이스 접근 중 오류가 발생했습니다. ::");
            System.out.println("오류 : "+e.getMessage());
        } catch (Exception e) {
            // 기타 예외를 처리합니다.
            model.addAttribute("error", ":: Warning - 예상치 못한 오류가 발생했습니다. ::");
            System.out.println("오류 : "+e.getMessage());
        }

        // 사용자의 모든 시간표를 가져옵니다.
        List<TimeTableDTO> userTimeTable = userDAO.getTimetable(userId);

        // 요일과 시간 목록을 생성하여 모델에 추가합니다.
        List<String> days = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일");
        List<Integer> hours = Arrays.asList(9, 10, 11, 12, 1, 2, 3, 4);

        // 모델에 사용자의 시간표 정보를 추가합니다.
        model.addAttribute("userTimeTable", userTimeTable);
        // 모델에 요일 목록을 추가합니다.
        model.addAttribute("days", days);
        // 모델에 시간 목록을 추가합니다.
        model.addAttribute("hours", hours);
        
        return "timetable";
    }

  
    // 시간표를 삭제하는 메서드
    @PostMapping("/deleteTimetable")
    public String deleteTimeTable(String delete_subject, Model model) {
    	UserDAO userDAO = new UserDAO(jdbcTemplate);
        String userId = "kjunh972"; // 사용자 ID

        // 시간표 삭제
        userDAO.deleteTimeTable(userId, delete_subject);

        // 시간표가 삭제된 후 다시 시간표를 가져와 모델에 추가
        List<TimeTableDTO> userTimeTable = userDAO.getTimetable(userId);
        model.addAttribute("userTimeTable", userTimeTable);

        // 바로 timetable로 이동
        return "redirect:/timetable";
    }
}