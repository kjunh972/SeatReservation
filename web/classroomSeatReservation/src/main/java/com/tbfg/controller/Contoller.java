package com.tbfg.controller;

import java.util.ArrayList;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbfg.dao.ClassroomDAO;
import com.tbfg.dao.ProfessorDAO;
import com.tbfg.dao.TimetableDAO;
import com.tbfg.dto.BanSeatDTO;
import com.tbfg.dto.BanSeatList;
import com.tbfg.dto.ClassroomDTO;
import com.tbfg.dto.ProfessorDTO;
import com.tbfg.dto.ReserveList;
import com.tbfg.dto.TimeTableDTO;
import com.tbfg.dto.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class Contoller {
  
	private ClassroomDTO classroomDTO = new ClassroomDTO();
    private TimeTableDTO timetableDTO = new TimeTableDTO();
    private BanSeatDTO banSeatDTO = new BanSeatDTO();
    
    @Autowired
    // JdbcTemplate 인스턴스를 자동으로 주입
    private JdbcTemplate jdbcTemplate; 
    @Autowired
    private ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

    // 세션에서 사용자 ID를 가져오는 메서드
    public String GetId(HttpSession session) { 
        Object loggedInUser = session.getAttribute("loggedInUser"); // 세션에서 사용자 객체 가져오기
        String userID = null;

        if (loggedInUser instanceof UserDTO) { // UserDTO 객체인지 확인
            UserDTO userDTO = (UserDTO) loggedInUser; // UserDTO로 캐스팅
            userID = userDTO.getId(); // 사용자 ID 가져오기
        } else if (loggedInUser instanceof ProfessorDTO) { // ProfessorDTO 객체인지 확인
            ProfessorDTO proDTO = (ProfessorDTO) loggedInUser; // ProfessorDTO로 캐스팅
            userID = proDTO.getId(); // 교수 ID 가져오기
        }

        return userID; // 사용자 ID 반환
    }
    
    public String GetPosition(HttpSession session) { 
        Object loggedInUser = session.getAttribute("loggedInUser"); // 세션에서 사용자 객체 가져오기
        String userPosition = null;

        if (loggedInUser instanceof UserDTO) { // UserDTO 객체인지 확인
            UserDTO userDTO = (UserDTO) loggedInUser; // UserDTO로 캐스팅
            userPosition = userDTO.getPosition(); // 사용자 ID 가져오기
        } else if (loggedInUser instanceof ProfessorDTO) { // ProfessorDTO 객체인지 확인
            ProfessorDTO proDTO = (ProfessorDTO) loggedInUser; // ProfessorDTO로 캐스팅
            userPosition = proDTO.getPosition(); // 교수 ID 가져오기
        }

        return userPosition; // 사용자 ID 반환
    }

    // 메인페이지 메서드
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    // 사용자가 즐겨찾기한 강의실 목록을 보여주는 메서드
    @GetMapping("/classroomLike")
    public String classroomLike(HttpSession session, Model model) {
    	
    	if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
   		 model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
    	}
    	
    	ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
        
        // 사용자의 시간표 강의실 목록 가져오기
        classroomDAO.getTimetableClassrooms(GetId(session)); 
        // 즐겨찾기 강의실 목록 가져오기
        List<String> favoriteClassrooms = classroomDAO.getFavoriteClassrooms(GetId(session)); 
        // 모델에 즐겨찾기 강의실 추가
        model.addAttribute("classroomButtons", favoriteClassrooms); 

        return "classroomLike"; 
    }

    @PostMapping("/classroomStatus")
    public String classroomStatus(@RequestParam("classroomName") String classroomName,
                                  @RequestParam("selectHours") String selectHoursJson,
                                  @RequestParam("day") String day,
                                  HttpSession session, Model model) throws JsonProcessingException {

        String userId = GetId(session);
        if (userId == null) { 
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; 
        }

        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
        classroomDTO = classroomDAO.getClassroomInfo(classroomName);
        
        // 시간대 정보 처리
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> selectHours = objectMapper.readValue(selectHoursJson, new TypeReference<List<Integer>>() {});
        classroomDTO.setSelectHours(selectHours);
        classroomDTO.setDay(day);

        // 이미 예약된 좌석 목록 처리
        List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, selectHours);
        classroomDTO.setReservedSeats(reservedSeats); 
        model.addAttribute("reservedSeats",reservedSeats);
        System.out.println("reservedSeats : "+reservedSeats);
        
        // 예약된 좌석을 classroomDTO에 반영하여 예약 상태를 업데이트
        for (Integer seat : reservedSeats) {
            classroomDTO.reserveSeat(seat);
        }

        // 금지된 좌석 목록 처리
        List<Integer> bannedSeats = classroomDAO.getBannedSeats(classroomName, selectHours);
        BanSeatDTO banSeatDTO = new BanSeatDTO();
        banSeatDTO.setBannedSeats(bannedSeats);

        // 사용자 중복 예약 확인
        boolean isAlreadyReserved = classroomDAO.checkSeat(userId, classroomName, selectHours, day);
        model.addAttribute("alreadyReserved", isAlreadyReserved);

        // 사용자 직책에 따른 처리
        String userPosition = GetPosition(session);
        model.addAttribute("classroom", classroomDTO);
        model.addAttribute("banSeatDTO", banSeatDTO);
        model.addAttribute("userPosition", userPosition);

        return "classroomStatus";
    }
    
    // 강의실 좌석 예약 메서드
    @PostMapping("/reserveSeat")  
    public String reserveSeat(String classroomName, Integer seatNumber, String day, HttpSession session,
                              Model model) throws JsonProcessingException {
    	if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
        }
    	
        // JDBC 템플릿을 사용하여 ClassroomDAO 객체를 생성
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

        // 0부터 999 사이의 랜덤 번호를 생성
        int randomNum = new Random().nextInt(1000);
        // classroomDTO에 랜덤 번호 저장
        classroomDTO.setRandomNum(randomNum);
        
        // DAO를 사용하여 좌석을 예약하고, 해당 좌석에 랜덤 번호를 설정
        classroomDAO.reserveSeat(GetId(session), classroomName, seatNumber, day, randomNum);
        // 사용자가 선택한 시간대에 대해 예약된 시간 정보를 ReservationHour 테이블에 추가
        for (Integer hour : classroomDTO.getSelectHours()) {
            classroomDAO.addReservationHour(randomNum, hour);
        }
        
        // 선택한 시간대 문자열로 변환
        String hours = classroomDTO.getSelectHours().stream().map(String::valueOf).collect(Collectors.joining("시, ")); 

        model.addAttribute("seatNumber", seatNumber); // 좌석 번호 모델에 추가
        model.addAttribute("selectHours", hours); // 선택한 시간대 모델에 추가
        model.addAttribute("randomNum", randomNum); // 랜덤 번호 모델에 추가
        model.addAttribute("classroom", classroomDTO); // 강의실 정보 모델에 추가
        
        return "reserveSeat";
    }
    
    @PostMapping("banSeat")
    public String banSeat(String classroomName, Integer seatNumber, HttpSession session, 
    		Model model) throws JsonMappingException, JsonProcessingException {
        // 세션에서 사용자 ID를 가져옴
        String userId = GetId(session);

        // 사용자 ID가 없으면 로그인 페이지로 리다이렉트
        if (userId == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login";
        }

        banSeatDTO.setUserId(userId);
        banSeatDTO.setClassroomName(classroomDTO.getClassroomName());
        banSeatDTO.setBanSeat(seatNumber);
        banSeatDTO.setDay(classroomDTO.getDay());

        // 금지된 좌석 정보를 저장할 DAO 객체 생성
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);      

        // BanSeat 추가 후 자동 생성된 banNum을 가져오기
        int banNum = classroomDAO.insertBanSeat(banSeatDTO);

        // BanSeatHour 테이블에 시간대 정보 추가
        for (Integer hour : classroomDTO.getSelectHours()) {
            classroomDAO.insertBanSeatHour(banNum, hour);
        }

        // 금지 좌석 정보 갱신 후 강의실 상태 페이지로 리다이렉트
        model.addAttribute("success", "좌석이 성공적으로 금지되었습니다.");
        
        // DAO를 통해 선택한 시간대에 이미 예약된 좌석들을 조회
        List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, classroomDTO.getSelectHours());
        
        // 예약된 좌석을 classroomDTO에 반영하여 예약 상태를 업데이트
        for (Integer seat : reservedSeats) {
            classroomDTO.reserveSeat(seat);
        }
        
        // 금지 좌석 정보 조회
        List<Integer> bannedSeats = classroomDAO.getBannedSeats(classroomName, classroomDTO.getSelectHours());
        banSeatDTO.setBannedSeats(bannedSeats);

        String userPosition = GetPosition(session);
        model.addAttribute("classroom", classroomDTO);
        model.addAttribute("banSeatDTO", banSeatDTO); // 모델에 BanSeatDTO 추가
        model.addAttribute("userPosition", userPosition);

        return "classroomStatus";
    }

    
    // 예약 목록을 조회하는 메서드
    @GetMapping("/reserveList")
    public String reserveList(HttpSession session, Model model) {
        // 세션에서 사용자 ID 가져오기
        String userId = GetId(session);

        // 세션이 만료되었거나 사용자 ID가 없는 경우
        if (userId == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
        }

        // ClassroomDAO 인스턴스를 생성하여 데이터베이스 작업을 처리
        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

        // 로그인한 사용자의 예약 목록을 가져옴
        Map<String, List<ReserveList>> reserveList = classroomDAO.getReserveList(userId);
        
        // 예약 목록이 비어있는 경우
        if (reserveList.isEmpty()) {
            model.addAttribute("empty", "예약된 내용이 없습니다.");
        } else {
            // 강의실별로 그룹화된 예약 정보를 모델에 추가하여 뷰에서 사용 가능하도록 설정
            model.addAttribute("reserveList", reserveList);
        }

        return "reserveList";
    }
    
    // 예약 금지한 좌석 목록 조회하는 메서드
    @GetMapping("/banList")
    public String banList(HttpSession session, Model model) {
    	// 세션에서 사용자 ID 가져오기
        String userId = GetId(session);

        // 세션이 만료되었거나 사용자 ID가 없는 경우
        if (userId == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
        }
        
        ProfessorDAO proDAO = new ProfessorDAO(jdbcTemplate);

        // 로그인한 사용자의 금지된 좌석 목록을 가져옴
        Map<String, List<BanSeatList>> banSeatList = proDAO.getBanSeatList(userId);
        
        // 금지된 좌석 목록이 비어있는 경우
        if (banSeatList.isEmpty()) {
            model.addAttribute("empty", "금지된 좌석이 없습니다.");
        } else {
            // 강의실별로 그룹화된 금지된 좌석 정보를 모델에 추가하여 뷰에서 사용 가능하도록 설정
            model.addAttribute("banSeatList", banSeatList);
        }
    	
    	return "banList";
    }

    // 예약 취소를 처리하는 메서드
    @PostMapping("/cancelReservation")
    public String cancelReservation(
            @RequestParam("reservNum") int reservNum, // 요청 파라미터로 전달된 예약 번호
            HttpSession session, // HTTP 세션 객체
            Model model) { 

        // 세션에서 사용자 ID를 가져오는 메서드
        String userId = GetId(session);

        // 세션이 만료되었거나 사용자 ID가 없는 경우
        if (userId == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
        }

        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

        // 예약 취소 메서드 호출
        boolean success = classroomDAO.cancelReservation(reservNum, userId);

        if (success) {
            // 예약 취소 성공 시
            model.addAttribute("success", "예약이 취소되었습니다.");
        } else {
            // 예약 취소 실패 시
            model.addAttribute("error", "예약 취소에 실패하였습니다.");
        }

        // 로그인한 사용자의 예약 목록을 가져옴
        Map<String, List<ReserveList>> reserveList = classroomDAO.getReserveList(userId);
        System.out.println("reserveList : " + reserveList);

        // 예약 목록이 비어있는 경우
        if (reserveList.isEmpty()) {
            model.addAttribute("empty", "예약된 내용이 없습니다.");
        } else {
            // 강의실별로 그룹화된 예약 정보를 모델에 추가하여 뷰에서 사용 가능하도록 설정
            model.addAttribute("reserveList", reserveList);
        }

        return "reserveList";
    }

    // 예약 시간을 변경하는 메서드
    @PostMapping("/updateReservation")
    public String updateReservation(
            @RequestParam("reservNum") int reservNum, // 요청 파라미터로 전달된 예약 번호
            @RequestParam("newHour") String newHour, // 요청 파라미터로 전달된 새로운 시간대 (쉼표로 구분된 문자열)
            HttpSession session, // HTTP 세션 객체
            Model model) { 

        // 세션에서 사용자 ID를 가져오는 메서드
        String userId = GetId(session);

        // 세션이 만료되었거나 사용자 ID가 없는 경우
        if (userId == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
        }

        ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

        // 예약 시간 변경 메서드 호출
        boolean success = classroomDAO.updateReservation(reservNum, newHour, userId);

        if (success) {
            // 예약 시간 변경 성공 시
            model.addAttribute("success", "예약 시간이 변경되었습니다.");
        } else {
            // 예약 시간 변경 실패 시
            model.addAttribute("error", "예약 시간 변경에 실패하였습니다.");
        }

        // 로그인한 사용자의 예약 목록을 가져옴
        Map<String, List<ReserveList>> reserveList = classroomDAO.getReserveList(userId);
        System.out.println("reserveList : " + reserveList);

        // 예약 목록이 비어있는 경우
        if (reserveList.isEmpty()) {
            model.addAttribute("empty", "예약된 내용이 없습니다.");
        } else {
            // 강의실별로 그룹화된 예약 정보를 모델에 추가하여 뷰에서 사용 가능하도록 설정
            model.addAttribute("reserveList", reserveList);
        }

        return "reserveList";
    }

    // 선택한 강의 번호에 해당하는 강의실 목록을 조회하는 메서드
    @PostMapping("/classroomSelect")
    public String classroomSelect(String classNum, HttpSession session, Model model) {
    	
    	if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
   		 model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
    	}
    	
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
    public String getClassSelect(String classNumber, HttpSession session, Model model) {
    	if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
   		 model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
    	}
    	
    	ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);
    	
        // 모든 강의 번호 목록 조회
        List<String> classNum = classroomDAO.getClassNum();
        
        // 모델에 강의 번호 목록 추가
        model.addAttribute("classNum", classNum);
        
        return "classSelect";
    }
    
    // 시간 선택하는 페이지를 보여주는 메서드
    @PostMapping("/timeSelect")
    public String timeSelect(String classroomName, HttpSession session, Model model) {
    	if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
   		 model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
    	}
    	
    	List<Integer> hours = Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20); // 9시부터 20시까지의 시간
    	classroomDTO.setClassroomName(classroomName); // 강의실 번호 설정
    	
    	// 모델에 ClassroomDTO 객체 추가
        model.addAttribute("classroom", classroomDTO); 
        model.addAttribute("hours", hours);
        return "timeSelect";
    }
    
    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        // DAO를 통해 강의실 이름 목록을 가져옴
        List<String> classrooms = classroomDAO.getAllClassrooms();

        // 강의실을 호관 별로 나눔
        List<String> group5 = new ArrayList<>();
        List<String> group7 = new ArrayList<>();

        for (String classroom : classrooms) {
            if (classroom.startsWith("5")) {
                group5.add(classroom); // 5호관 강의실
            } else if (classroom.startsWith("7")) {
                group7.add(classroom); // 7호관 강의실
            }
        }

        // 모델에 데이터를 추가하여 뷰에 전달
        model.addAttribute("group5", group5);
        model.addAttribute("group7", group7);

        return "admin"; // admin.html 템플릿을 반환
    }
    
    @GetMapping("/timetable")
    public String timetable(HttpSession session, Model model, @ModelAttribute("warning") String warning) {
    	if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
   		 model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
    	}
    	
    	TimetableDAO timetableDAO = new TimetableDAO(jdbcTemplate);
    	
        // 사용자의 모든 시간표를 가져옴.
        List<TimeTableDTO> userTimeTable = timetableDAO.getTimetable(GetId(session));

        // 요일과 시간 목록을 생성하여 모델에 추가
        List<String> days = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일");
        List<Integer> hours = Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16);

 
        String userPosition = GetPosition(session);
        System.out.println("userPosition : "+userPosition);
        model.addAttribute("userPosition", userPosition);
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
            Integer endHour, Model model, HttpSession session) {
        TimetableDAO userDAO = new TimetableDAO(jdbcTemplate);
        
        try {
            if (startHour != null && endHour != null && startHour >= endHour) { // 시작시간이 종료시간보다 크면
                model.addAttribute("error", ":: Warning - 시작 시간이 종료 시간보다 클 수 없습니다. ::");
            } else {
            	if (subject=="" || classroomName=="" || day=="") // 시간표를 입력할때 칸 하나라도 공백있을떄.
            	{
            		model.addAttribute("error", ":: Warning - 시간표에 있는 내용을 다 입력해주세요. ::");
                    System.out.println("오류 : 시간표 내용 공백");
            	} else {
		            // 시간표 추가
		            timetableDTO.setUserId(GetId(session));
		            timetableDTO.setSubject(subject);
		            timetableDTO.setClassroomName(classroomName);
		            timetableDTO.setDay(day);
		            timetableDTO.setStartHour(startHour);
		            timetableDTO.setEndHour(endHour);
		
		            // setTimetable 메서드 호출
		            userDAO.setTimetable(timetableDTO);
            	}
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
        List<TimeTableDTO> userTimeTable = userDAO.getTimetable(GetId(session));

        // 요일과 시간 목록을 생성하여 모델에 추가합니다.
        List<String> days = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일");
        List<Integer> hours = Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16);

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
    public String deleteTimeTable(String delete_subject, Model model, HttpSession session) {
    	TimetableDAO userDAO = new TimetableDAO(jdbcTemplate);

        // 시간표 삭제
        userDAO.deleteTimeTable(GetId(session), delete_subject);

        // 시간표가 삭제된 후 다시 시간표를 가져와 모델에 추가
        List<TimeTableDTO> userTimeTable = userDAO.getTimetable(GetId(session));
        model.addAttribute("userTimeTable", userTimeTable);

        // 바로 timetable로 이동
        return "redirect:/timetable";
    }
}