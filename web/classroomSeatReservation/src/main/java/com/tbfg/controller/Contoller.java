package com.tbfg.controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbfg.dao.ClassroomDAO;
import com.tbfg.dao.ClassroomInfoDAO;
import com.tbfg.dao.ProfessorDAO;
import com.tbfg.dao.TimetableDAO;
import com.tbfg.dto.AttendanceDTO;
import com.tbfg.dto.BanSeatDTO;
import com.tbfg.dto.BanSeatList;
import com.tbfg.dto.ClassroomDTO;
import com.tbfg.dto.ProfessorDTO;
import com.tbfg.dto.ReservationInfoDTO;
import com.tbfg.dto.ReserveList;
import com.tbfg.dto.TimeTableDTO;
import com.tbfg.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
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
	@Autowired
	private ProfessorDAO proDAO = new ProfessorDAO(jdbcTemplate);
	@Autowired
	private TimetableDAO timetableDAO = new TimetableDAO(jdbcTemplate);
	@Autowired
	private ClassroomInfoDAO classroomInfo = new ClassroomInfoDAO(jdbcTemplate);

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

	// 시간표 정보 보여주는 메서드
	public void getTimetablePage(Model model, HttpSession session) {
		// 사용자의 모든 시간표를 가져옴.
		List<TimeTableDTO> userTimeTable = timetableDAO.getTimetable(GetId(session));

		// 요일과 시간 목록을 생성하여 모델에 추가
		List<String> days = Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일");
		List<Integer> hours = Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17);

		// 모델에 사용자의 시간표 정보를 추가합니다.
		model.addAttribute("userTimeTable", userTimeTable);
		// 모델에 요일 목록을 추가합니다.
		model.addAttribute("days", days);
		// 모델에 시간 목록을 추가합니다.
		model.addAttribute("hours", hours);
	}

	// 사용자의 직책를 가져오는 메서드
	public String GetPosition(HttpSession session) {
		// 세션에서 로그인한 사용자 객체 가져오기
		Object loggedInUser = session.getAttribute("loggedInUser");
		String userPosition = null;

		// 로그인한 사용자가 학생인 경우
		if (loggedInUser instanceof UserDTO) {
			UserDTO userDTO = (UserDTO) loggedInUser; // UserDTO로 형변환
			userPosition = userDTO.getPosition(); // 학생의 직책 가져오기
		}
		// 로그인한 사용자가 교수인 경우
		else if (loggedInUser instanceof ProfessorDTO) {
			ProfessorDTO proDTO = (ProfessorDTO) loggedInUser; // ProfessorDTO로 형변환
			userPosition = proDTO.getPosition(); // 교수의 직책 가져오기
		}

		return userPosition;
	}

	// 영어로 된 요일을 한국어로 변환하는 메서드
	private String convertWeek(DayOfWeek dayOfWeek) {
		// 입력받은 DayOfWeek 열거형에 따라 해당하는 한국어 요일 반환
		switch (dayOfWeek) {
		case MONDAY:
			return "월요일";
		case TUESDAY:
			return "화요일";
		case WEDNESDAY:
			return "수요일";
		case THURSDAY:
			return "목요일";
		case FRIDAY:
			return "금요일";
		case SATURDAY:
			return "토요일";
		case SUNDAY:
			return "일요일";
		default:
			return ""; // 일치하는 요일이 없을 경우 빈 문자열 반환
		}
	}

	// 메인페이지 메서드
	@GetMapping("index.html")
	public String index(HttpSession session, Model model) {
		String userId = GetId(session);

		// 로그인 세션이 만료 된 경우
		if (userId == null) {
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		return "redirect:/login";
	}

	// 사용자가 즐겨찾기한 강의실 목록을 보여주는 메서드
	@GetMapping("/classroomLike")
	public String classroomLike(HttpSession session, Model model) {

		if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			getTimetablePage(model, session);
			return "login"; // 로그인 페이지로 리다이렉트
		}

		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

		// 사용자의 시간표 강의실 목록 가져오기
		classroomDAO.getTimetableClassrooms(GetId(session));
		// 즐겨찾기 강의실 목록 가져오기
		List<String> favoriteClassrooms = classroomDAO.getFavoriteClassrooms(GetId(session));

		// 사용자 이름을 세션에서 가져와 모델에 추가
		UserDTO userDTO = (UserDTO) session.getAttribute("loggedInUser");
		String userName = userDTO != null ? userDTO.getName() : "";

		// 모델에 즐겨찾기 강의실 추가
		model.addAttribute("classroomButtons", favoriteClassrooms);
		// 사용자의 이름 추가
		model.addAttribute("userName", userName);
		return "classroomLike";
	}

	// 강의실 예약하는 메서드 메소드
	@GetMapping("/classroomStatus")
	public String getClassroomStatus(HttpServletRequest request, HttpSession session, Model model) {
		String userId = GetId(session); // 세션에서 사용자 ID를 가져옴
		String userPosition = GetPosition(session); // 세션에서 사용자 직책을 가져옴

		if (userId == null) { // 세션이 만료된 경우 로그인 페이지로 리다이렉트
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		if (classroomDTO.getClassroomNull() == 1) { // classroomDTO의 상태에 따라 시간표 페이지로 리다이렉트
			return "redirect:/timetable";
		}
		classroomDTO.setClassroomNull(0); // GET일떄 classroomNull을 0으로 설정

		String hour = classroomDTO.getSelectHours().stream().map(String::valueOf).collect(Collectors.joining("시, ")); // 선택한
																														// 시간대를
																														// 문자열로
																														// 변환

		getTimetablePage(model, session); // 시간표 정보를 모델에 추가

		model.addAttribute("requestMethod", request.getMethod());
		model.addAttribute("banSeatDTO", banSeatDTO);
		model.addAttribute("selectHours", hour);
		model.addAttribute("classroom", classroomDTO);
		model.addAttribute("userPosition", userPosition);
		return "classroomStatus";
	}

	// 강의실 예약하는 메서드 메소드
	@PostMapping("/classroomStatus")
	public String classroomStatus(@RequestParam("classroomName") String classroomName,
			@RequestParam("selectHours") String selectHoursJson, @RequestParam("day") String day,
			@RequestParam("subject") String subject, HttpServletRequest request, HttpSession session, Model model)
			throws JsonProcessingException {

		String userId = GetId(session); // 세션에서 사용자 ID를 가져옴
		String userPosition = GetPosition(session); // 세션에서 사용자 직책을 가져옴

		if (userId == null) { // 세션이 만료된 경우 로그인 페이지로 리다이렉트
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate); // ClassroomDAO 객체 생성
		classroomDTO = classroomDAO.getClassroomInfo(classroomName); // 강의실 정보 조회
		classroomDTO.setSeatCount(classroomDTO.getLeftCol() + classroomDTO.getLeftRow() + classroomDTO.getRightCol()
				+ classroomDTO.getRightRow()); // 좌석 수 설정

		ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper 객체 생성
		List<Integer> selectHours = objectMapper.readValue(selectHoursJson, new TypeReference<List<Integer>>() {
		}); // 시간대 정보 처리
		classroomDTO.setSelectHours(selectHours);
		classroomDTO.setDay(day);
		classroomDTO.setSubject(subject);

		List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, selectHours, day,
				classroomDTO.getSubject()); // 예약된 좌석 목록 조회
		classroomDTO.setReservedSeats(reservedSeats);
		model.addAttribute("reservedSeats", reservedSeats);

		for (Integer seat : reservedSeats) { // 예약된 좌석 상태 업데이트
			classroomDTO.reserveSeat(seat);
		}

		List<Integer> bannedSeats = classroomDAO.getBannedSeats(classroomName, selectHours, classroomDTO.getSubject(),
				classroomDTO.getDay()); // 금지된 좌석 목록 조회
		BanSeatDTO banSeatDTO = new BanSeatDTO();
		banSeatDTO.setBannedSeats(bannedSeats);

		classroomDTO.setClassroomNull(1); // POST일떄 classroomNull을 1로 설정
		if (classroomDTO.getClassroomNull() == 0) {
			return "redirect:/timetable";
		}

		getTimetablePage(model, session); // 시간표 정보를 모델에 추가

		boolean isAlreadyReserved = classroomDAO.checkSeat(userId, classroomName, selectHours, day); // 사용자 중복 예약 확인
		model.addAttribute("alreadyReserved", isAlreadyReserved);
		model.addAttribute("requestMethod", request.getMethod());
		model.addAttribute("classroom", classroomDTO);
		model.addAttribute("banSeatDTO", banSeatDTO);
		model.addAttribute("userPosition", userPosition);

		return "classroomStatus"; // classroomStatus 뷰 반환
	}

	// 관리자나 교수 좌석 관리하는 메서드
	@GetMapping("/classroomSeat")
	public String classroomSeatGet() {
		return "redirect:/timetable"; // 시간표 페이지로 리다이렉트
	}

	// 관리자나 교수 좌석 관리하는 메서드
	@PostMapping("/classroomSeat")
	public String classroomSeat(@RequestParam("classroomName") String classroomName,
			@RequestParam("selectHours") String selectHoursJson, @RequestParam("day") String day,
			@RequestParam("subject") String subject, HttpServletRequest request, HttpSession session, Model model)
			throws JsonProcessingException {

		String userId = GetId(session); // 세션에서 사용자 ID를 가져옴
		String userPosition = GetPosition(session); // 세션에서 사용자 직책을 가져옴

		if (userId == null) { // 세션이 만료된 경우 로그인 페이지로 리다이렉트
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate); // ClassroomDAO 객체 생성
		classroomDTO = classroomDAO.getClassroomInfo(classroomName); // 강의실 정보 조회
		classroomDTO.setSeatCount(classroomDTO.getLeftCol() + classroomDTO.getLeftRow() + classroomDTO.getRightCol()
				+ classroomDTO.getRightRow()); // 좌석 수 설정

		ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper 객체 생성
		List<Integer> selectHours = objectMapper.readValue(selectHoursJson, new TypeReference<List<Integer>>() {
		}); // 시간대 정보 처리
		classroomDTO.setSelectHours(selectHours);
		classroomDTO.setDay(day);
		classroomDTO.setSubject(subject);

		List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, selectHours, day,
				classroomDTO.getSubject()); // 예약된 좌석 목록 조회
		classroomDTO.setReservedSeats(reservedSeats);
		model.addAttribute("reservedSeats", reservedSeats);

		for (Integer seat : reservedSeats) { // 예약된 좌석 상태 업데이트
			classroomDTO.reserveSeat(seat);
		}

		List<ReservationInfoDTO> reservationInfoList = classroomDAO.getReservationInfo(classroomName, selectHours, day,
				subject); // 예약 정보 목록 조회

		Map<Integer, Integer> seatToReservNum = new HashMap<>(); // 좌석 번호와 예약 번호 매핑
		for (ReservationInfoDTO info : reservationInfoList) {
			seatToReservNum.put(info.getReservSeat(), info.getReservNum());
		}
		model.addAttribute("seatToReservNum", seatToReservNum);

		List<Integer> bannedSeats = classroomDAO.getBannedSeats(classroomName, selectHours, classroomDTO.getSubject(),
				classroomDTO.getDay()); // 금지된 좌석 목록 조회
		BanSeatDTO banSeatDTO = new BanSeatDTO();
		banSeatDTO.setBannedSeats(bannedSeats);

		classroomDTO.setClassroomNull(1); // classroomDTO 상태 설정
		if (classroomDTO.getClassroomNull() == 0) {
			return "redirect:/timetable";
		}

		getTimetablePage(model, session); // 시간표 정보를 모델에 추가

		boolean isAlreadyReserved = classroomDAO.checkSeat(userId, classroomName, selectHours, day); // 사용자 중복 예약 확인
		model.addAttribute("alreadyReserved", isAlreadyReserved);
		model.addAttribute("requestMethod", request.getMethod());
		model.addAttribute("classroom", classroomDTO);
		model.addAttribute("banSeatDTO", banSeatDTO);
		model.addAttribute("userPosition", userPosition);

		return "classroomSeat";
	}

	// 예약 번호로 학생 정보를 조회
	@GetMapping("/getStudentInfo")
	public ResponseEntity<Map<String, Object>> getStudentInfo(@RequestParam int reservNum) {
		Map<String, Object> response = new HashMap<>(); // 응답 데이터를 저장할 Map 객체 생성
		try {
			Optional<Map<String, Object>> studentInfo = proDAO.findStudentInfo(reservNum); // ProfessorDAO를 통해 학생 정보 조회

			if (studentInfo.isPresent()) { // 학생 정보가 존재하는 경우
				Map<String, Object> info = studentInfo.get();
				response.put("success", true);
				response.put("studentName", info.get("name"));
				response.put("studentId", info.get("studentId"));
			} else { // 학생 정보가 없는 경우
				response.put("success", false);
				response.put("message", "학생 정보를 찾을 수 없습니다.");
			}
		} catch (Exception e) { // 예외 발생 시
			response.put("success", false);
			response.put("message", "학생 정보를 찾는 중 오류가 발생했습니다: " + e.getMessage());
		}
		return ResponseEntity.ok(response); // HTTP 상태 코드 200(OK)와 함께 응답 반환
	}

	// 예약된 좌석 자리 이동
	@PostMapping("/moveSeat")
	public ResponseEntity<Map<String, Object>> moveSeat(@RequestBody Map<String, Integer> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			int reservNum = request.get("reservNum");
			int newSeat = request.get("newSeat");

			// 기존 예약 정보 조회
			ReserveList oldReservation = proDAO.findByReservNum(reservNum);
			if (oldReservation == null) {
				response.put("success", false);
				response.put("message", "유효하지 않은 예약 번호입니다.");
				return ResponseEntity.ok(response);
			}

			// 좌석 이동 처리
			boolean updateSuccess = classroomDAO.updateReservationSeat(reservNum, newSeat);
			if (updateSuccess) {
				response.put("success", true);
				response.put("message", "자리 이동이 성공적으로 완료되었습니다.");
				response.put("oldSeat", oldReservation.getReservSeat());
				response.put("newSeat", newSeat);
				response.put("classroomName", oldReservation.getClassroomName());
			} else {
				response.put("success", false);
				response.put("message", "자리 이동 중 오류가 발생했습니다.");
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류: " + e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(response);
	}

	// 예약을 취소하는 API 엔드포인트
	@PostMapping("/cancelReservationNew")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> cancelReservationNew(@RequestBody Map<String, Integer> request) {
		// 응답을 위한 Map 객체 생성
		Map<String, Object> response = new HashMap<>();
		try {
			// 요청에서 예약 번호 추출
			int reservNum = request.get("reservNum");
			// DAO를 통해 예약 취소 실행
			boolean success = proDAO.cancelReservation(reservNum);
			if (success) {
				// 취소 성공 시 응답 설정
				response.put("success", true);
				response.put("message", "예약이 성공적으로 취소되었습니다.");
			} else {
				// 취소 실패 시 응답 설정
				response.put("success", false);
				response.put("message", "예약 취소에 실패했습니다.");
			}
		} catch (Exception e) {
			// 예외 발생 시 오류 응답 설정
			response.put("success", false);
			response.put("message", "서버 오류: " + e.getMessage());
			e.printStackTrace();
		}
		// HTTP 200 상태코드와 함께 응답 반환
		return ResponseEntity.ok(response);
	}

	// 예약 번호를 변경하는 API 엔드포인트
	@PostMapping("/changeReservationNumberNew")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> changeReservationNumberNew(@RequestBody Map<String, Integer> request) {
		// 응답을 위한 Map 객체 생성
		Map<String, Object> response = new HashMap<>();
		try {
			// 요청에서 기존 예약 번호와 새 예약 번호 추출
			int oldReservNum = request.get("oldReservNum");
			int newReservNum = request.get("newReservNum");
			// DAO를 통해 예약 번호 변경 실행
			boolean success = proDAO.changeReservationNumber(oldReservNum, newReservNum);
			if (success) {
				// 변경 성공 시 응답 설정
				response.put("success", true);
				response.put("message", "예약 번호가 성공적으로 변경되었습니다.");
			} else {
				// 변경 실패 시 응답 설정
				response.put("success", false);
				response.put("message", "예약 번호 변경에 실패했습니다.");
			}
		} catch (Exception e) {
			// 예외 발생 시 오류 응답 설정
			response.put("success", false);
			response.put("message", "서버 오류: " + e.getMessage());
			e.printStackTrace();
		}
		// HTTP 200 상태코드와 함께 응답 반환
		return ResponseEntity.ok(response);
	}

	// 강의실 좌석 예약 메서드
	@PostMapping("/reserveSeat")
	public String reserveSeat(String classroomName, Integer seatNumber, String day, String subject, HttpSession session,
			Model model) throws JsonProcessingException {
		if (GetId(session) == null) { // 세션에 사용자 ID가 있는지 확인
			getTimetablePage(model, session);
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
		classroomDAO.reserveSeat(GetId(session), classroomName, classroomDTO.getSubject(), seatNumber, day, randomNum);
		// 사용자가 선택한 시간대에 대해 예약된 시간 정보를 ReservationHour 테이블에 추가
		for (Integer hour : classroomDTO.getSelectHours()) {
			classroomDAO.addReservationHour(randomNum, hour);
		}

		// 선택한 시간대 문자열로 변환
		String hours = classroomDTO.getSelectHours().stream().map(String::valueOf).collect(Collectors.joining("시, "));

		classroomDTO.setClassroomNull(0);
		classroomDTO.setSeatNumber(seatNumber);

		model.addAttribute("seatNumber", seatNumber); // 좌석 번호 모델에 추가
		model.addAttribute("selectHours", hours); // 선택한 시간대 모델에 추가
		model.addAttribute("randomNum", randomNum); // 랜덤 번호 모델에 추가
		model.addAttribute("classroom", classroomDTO); // 강의실 정보 모델에 추가

		return "redirect:/classroomStatus";
	}

	// 좌석 예약을 금지하는 메서드
	@PostMapping("banSeat")
	public String banSeat(String classroomName, Integer seatNumber, HttpSession session, Model model)
			throws JsonMappingException, JsonProcessingException {
		// 세션에서 사용자 ID를 가져옴
		String userId = GetId(session);
		// 세션에서 사용자 직책을 가져옴
		String userPosition = GetPosition(session);

		// 사용자 ID가 없으면 로그인 페이지로 리다이렉트
		if (userId == null) {
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		// banSeatDTO에 필요한 정보 설정
		banSeatDTO.setSubject(classroomDTO.getSubject());
		banSeatDTO.setUserId(userId);
		banSeatDTO.setClassroomName(classroomDTO.getClassroomName());
		banSeatDTO.setBanSeat(seatNumber);
		banSeatDTO.setDay(classroomDTO.getDay());

		// 금지된 좌석 정보를 저장할 DAO 객체 생성
		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

		// DAO를 통해 선택한 시간대에 이미 예약된 좌석들을 조회
		List<Integer> reservedSeats = classroomDAO.getReservedSeats(classroomName, classroomDTO.getSelectHours(),
				classroomDTO.getDay(), classroomDTO.getSubject());

		// BanSeat 추가 후 자동 생성된 banNum을 가져오기
		int banNum = classroomDAO.insertBanSeat(banSeatDTO);

		// BanSeatHour 테이블에 시간대 정보 추가
		for (Integer hour : classroomDTO.getSelectHours()) {
			classroomDAO.insertBanSeatHour(banNum, hour);
		}

		// 예약된 좌석을 classroomDTO에 반영하여 예약 상태를 업데이트
		for (Integer seat : reservedSeats) {
			classroomDTO.reserveSeat(seat);
		}

		// 금지 좌석 정보 조회
		List<Integer> bannedSeats = classroomDAO.getBannedSeats(classroomName, classroomDTO.getSelectHours(),
				classroomDTO.getSubject(), classroomDTO.getDay());
		banSeatDTO.setBannedSeats(bannedSeats);

		// 선택한 시간대 문자열로 변환
		String hours = classroomDTO.getSelectHours().stream().map(String::valueOf).collect(Collectors.joining("시, "));
		banSeatDTO.setSeatNumber(seatNumber);

		// classroomDTO의 상태를 0으로 설정
		classroomDTO.setClassroomNull(0);

		// 모델에 필요한 데이터 추가
		model.addAttribute("reservedSeats", reservedSeats);
		model.addAttribute("classroom", classroomDTO);
		model.addAttribute("banSeatDTO", banSeatDTO);
		model.addAttribute("userPosition", userPosition);
		model.addAttribute("selectHours", hours);

		// classroomStatus 페이지로 리다이렉트
		return "redirect:/classroomStatus";
	}

	// 예약 리스트를 확인하는 메서드
	@GetMapping("/reserveList")
	public String reserveList(HttpSession session, Model model) {
		// 세션에서 사용자 ID와 직책 가져오기
		String userId = GetId(session);
		String userPosition = GetPosition(session);

		// 세션이 만료되었거나 사용자 ID가 없는 경우 로그인 페이지로 리다이렉트
		if (userId == null) {
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		// ClassroomDAO 인스턴스를 생성하여 데이터베이스 작업을 처리
		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

		// 예약 리스트를 저장할 Map 선언
		Map<String, List<ReserveList>> reserveList;

		// 사용자 직책에 따라 다른 예약 리스트 조회
		if ("student".equals(userPosition)) {
			// 학생인 경우 자신의 예약 목록만 가져옴
			reserveList = classroomDAO.getReserveList(userId);
		} else if ("admin".equals(userPosition)) {
			// 관리자인 경우 전체 예약 목록을 가져옴
			reserveList = classroomDAO.getAdminReserveList();
		} else if ("professor".equals(userPosition)) {
			// 교수인 경우 자신의 시간표에 등록된 수업과 일치하는 학생 예약 목록을 가져옴
			List<String> professorSubjects = classroomDAO.getSubjectsFromStuTimeTable(userId);
			reserveList = new LinkedHashMap<>();
			for (String subject : professorSubjects) {
				Map<String, List<ReserveList>> subjectReserveList = classroomDAO.getAdminReserveListBySubject(subject);
				subjectReserveList.forEach((key, value) -> reserveList.merge(key, value, (existing, newList) -> {
					existing.addAll(newList);
					return existing;
				}));
			}
		} else {
			// 그 외의 경우 빈 Map 생성
			reserveList = new LinkedHashMap<>();
		}

		// 예약 목록이 비어있는 경우 메시지 추가
		if (reserveList.isEmpty()) {
			model.addAttribute("empty", "예약된 내용이 없습니다.");
		} else {
			// 강의실별로 그룹화된 예약 정보를 모델에 추가
			model.addAttribute("reserveList", reserveList);
		}

		// 시간표 정보를 모델에 추가
		getTimetablePage(model, session);

		// 사용자 직책을 모델에 추가
		model.addAttribute("userPosition", userPosition);

		return "reserveList";
	}

	// 예약 좌석 금지 리스트를 확인하는 메서드
	@GetMapping("/banList")
	public String banList(HttpSession session, Model model) {
		// 세션에서 사용자 ID와 직책 가져오기
		String userId = GetId(session);
		String userPosition = GetPosition(session);

		// 세션이 만료되었거나 사용자 ID가 없는 경우 로그인 페이지로 리다이렉트
		if (userId == null) {
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login";
		}

		// admin인 경우 모든 금지된 좌석 목록을, 그 외의 경우 해당 사용자의 금지된 좌석 목록을 가져옴
		Map<String, List<BanSeatList>> banSeatList;
		if ("admin".equals(userPosition)) {
			banSeatList = proDAO.getAllBanSeatList();
		} else {
			banSeatList = proDAO.getBanSeatList(userId);
		}

		// 금지된 좌석 목록이 비어있는 경우 메시지 추가
		if (banSeatList.isEmpty()) {
			model.addAttribute("empty", "금지된 좌석이 없습니다.");
		} else {
			// 강의실별로 그룹화된 금지된 좌석 정보를 모델에 추가
			model.addAttribute("banSeatList", banSeatList);
		}

		// 시간표 정보를 모델에 추가
		getTimetablePage(model, session);

		// 사용자 직책을 모델에 추가
		model.addAttribute("userPosition", userPosition);

		return "banList";
	}

	// 금지된 자리 해제 기능
	@PostMapping("/unbanSeat")
	public String unbanSeat(@RequestParam String classroomName, @RequestParam String banSeat, @RequestParam String day,
			HttpSession session, Model model) {
		// 세션에서 사용자 ID를 가져오는 메서드
		String userId = GetId(session);

		// 세션이 만료되었거나 사용자 ID가 없는 경우
		if (userId == null) {
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login"; // 로그인 페이지로 리다이렉트
		}

		boolean success = classroomDAO.unbanSeat(classroomName, banSeat, day);

		// 로그인한 사용자의 금지된 좌석 목록을 가져옴
		Map<String, List<BanSeatList>> banSeatList = proDAO.getBanSeatList(userId);

		// 금지된 좌석 목록이 비어있는 경우
		if (banSeatList.isEmpty()) {
			model.addAttribute("empty", "금지된 좌석이 없습니다.");
		}

		// 강의실별로 그룹화된 금지된 좌석 정보를 모델에 추가하여 뷰에서 사용 가능하도록 설정
		model.addAttribute("banSeatList", banSeatList);

		if (success) {

			return "banList"; // 해제 성공 시 banList.html로 리다이렉트
		} else {
			model.addAttribute("error", "예약 금지 좌석 해제 중 오류가 발생했습니다.");
			return "banList"; // 오류 발생 시 예약 목록으로 리다이렉트
		}
	}

	// 예약 리스트에서 예약 취소하는 메서드
	@PostMapping("/cancelReservation")
	public String cancelReservation(@RequestParam("reservNum") int reservNum, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		String userId = GetId(session);

		if (userId == null) {
			getTimetablePage(model, session);
			redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "redirect:/login";
		}

		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

		boolean success = classroomDAO.cancelReservation(reservNum, userId);

		if (success) {
			redirectAttributes.addFlashAttribute("success", "예약이 취소되었습니다.");
		} else {
			redirectAttributes.addFlashAttribute("error", "예약 취소에 실패하였습니다.");
		}

		return "redirect:/timetable";
	}

	// 예약 리스트에서 시간 변경하는 메서드
	@PostMapping("/updateReservation")
	public String updateReservation(@RequestParam("reservNum") int reservNum, @RequestParam("newHour") String newHour,
			Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		String userId = GetId(session);

		if (userId == null) {
			getTimetablePage(model, session);
			redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "redirect:/login";
		}

		ClassroomDAO classroomDAO = new ClassroomDAO(jdbcTemplate);

		boolean success = classroomDAO.updateReservation(reservNum, newHour, userId);

		if (success) {
			redirectAttributes.addFlashAttribute("success", "예약 시간이 변경되었습니다.");
		} else {
			redirectAttributes.addFlashAttribute("error", "예약 시간 변경에 실패하였습니다.");
		}

		return "redirect:/timetable";
	}

	// 강의실 정보 조회하는 메서드
	@PostMapping("/classroomInfo")
	public String showClassroomInfo(@RequestParam String classroomName,
			@RequestParam(required = false) String selectedSubject, Model model, HttpSession session) {
		// 세션 및 사용자 ID 확인
		String userId = GetId(session);
		// 사용자 직책을 가져와 모델에 추가
		String userPosition = GetPosition(session);

		if (userId == null) {
			getTimetablePage(model, session);
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "login"; // 로그인 페이지로 리다이렉트
		}

		// 강의 목록(과목명 + 요일) 조회
		List<Map<String, Object>> subjectsAndDays = timetableDAO.getSubjectsDay(classroomName, userId);
		model.addAttribute("lectureNames", subjectsAndDays); // 모델에 강의 목록 추가

		// 예약 정보 조회
		List<ReserveList> reservations = classroomInfo.getReservationsByClassroom(classroomName);
		model.addAttribute("reservations", reservations); // 예약 정보 모델에 추가

		// 선택된 과목이 있는 경우 처리
		if (selectedSubject != null) {
			List<ReserveList> subjectReservations = classroomInfo.getReservations(classroomName, selectedSubject);
			model.addAttribute("subjectReservations", subjectReservations);
			model.addAttribute("selectedSubject", selectedSubject);
		}

		System.out.println("userPosition : " + userPosition);

		model.addAttribute("userPosition", userPosition);

		model.addAttribute("title", classroomName + " 강의실 정보");
		return "classroomInfo";
	}

	// 특정 강의실과 과목에 대한 예약 정보를 조회하는 API 엔드포인트
	@GetMapping("/reservations")
	public ResponseEntity<List<ReserveList>> getReservations(@RequestParam String classroomName,
			@RequestParam String subject) {
		// 선택된 과목 로그 출력
		System.out.println("Selected Subject: " + subject);

		// 과목에 따른 예약 정보 조회
		List<ReserveList> reservations = classroomInfo.getReservations(classroomName, subject);

		// 예약 내역 로그 출력
		for (ReserveList reservation : reservations) {
			System.out.println("Reservation: " + reservation.toString());
		}

		// 예약 목록이 비어있을 경우 204 상태 반환
		if (reservations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		// 예약 목록이 있을 경우 200 상태와 함께 예약 목록 반환
		return ResponseEntity.ok(reservations);
	}

	// 관리자 페이지를 보여주는 메서드
	@GetMapping("/admin")
	public String showAdminPage(Model model) {
		// DAO를 통해 모든 강의실 이름 목록을 가져옴
		List<String> classrooms = classroomDAO.getAllClassrooms();

		// 강의실을 호관 별로 나누기 위한 리스트 초기화
		List<String> group5 = new ArrayList<>();
		List<String> group7 = new ArrayList<>();

		// 강의실을 호관 별로 분류
		for (String classroom : classrooms) {
			if (classroom.startsWith("5")) {
				group5.add(classroom); // 5호관 강의실
			} else if (classroom.startsWith("7")) {
				group7.add(classroom); // 7호관 강의실
			}
		}

		// 모델에 분류된 강의실 데이터를 추가
		model.addAttribute("group5", group5);
		model.addAttribute("group7", group7);

		return "admin";
	}

	// 시간표 페이지를 보여주는 GET 메서드
	@GetMapping("/timetable")
	public String timetable(HttpSession session, Model model, @ModelAttribute("warning") String warning) {
		// 세션에 사용자 ID가 있는지 확인
		if (GetId(session) == null) {
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			getTimetablePage(model, session);
			return "login"; // 로그인 페이지로 리다이렉트
		}

		// 시간표 정보를 모델에 추가
		getTimetablePage(model, session);

		// classroomDTO 초기화
		this.classroomDTO = new ClassroomDTO();

		// classroomDTO의 상태를 1로 설정
		classroomDTO.setClassroomNull(1);

		// 모든 강의실 목록을 가져와 모델에 추가
		List<String> classrooms = classroomDAO.getAllClassrooms();
		model.addAttribute("classrooms", classrooms);

		// 사용자 직책을 가져와 모델에 추가
		String userPosition = GetPosition(session);
		model.addAttribute("userPosition", userPosition);

		return "timetable";
	}

	// 시간표를 추가하는 POST 메서드
	@PostMapping("/timetable")
	public String postTimetable(String subject, String classroomName, String day, Integer startHour, Integer endHour,
			Model model, HttpSession session) {
		try {
			// 시작 시간이 종료 시간보다 크거나 같으면 에러 메시지 추가
			if (startHour != null && endHour != null && startHour >= endHour) {
				model.addAttribute("error", ":: Warning - 시작 시간이 종료 시간보다 클 수 없습니다. ::");
			} else {
				// 입력 필드 중 하나라도 비어있으면 에러 메시지 추가
				if (subject == "" || classroomName == "" || day == "") {
					model.addAttribute("error", ":: Warning - 시간표에 있는 내용을 다 입력해주세요. ::");
					System.out.println("오류 : 시간표 내용 공백");
				} else {
					// 시간표 DTO에 입력 정보 설정
					timetableDTO.setUserId(GetId(session));
					timetableDTO.setSubject(subject);
					timetableDTO.setClassroomName(classroomName);
					timetableDTO.setDay(day);
					timetableDTO.setStartHour(startHour);
					timetableDTO.setEndHour(endHour);

					// 시간표 추가
					timetableDAO.setTimetable(timetableDTO);
				}
			}
		} catch (DataIntegrityViolationException e) {
			// 강의실이 존재하지 않거나 외래 키 제약 조건 위반 시 에러 메시지 추가
			model.addAttribute("error", ":: Warning - 강의실이 존재하지 않거나, 외래 키 제약 조건을 위반했습니다. ::");
			System.out.println("오류 : " + e.getMessage());
		} catch (DataAccessException e) {
			// 데이터베이스 접근 중 오류 발생 시 에러 메시지 추가
			model.addAttribute("error", ":: Warning - 데이터베이스 접근 중 오류가 발생했습니다. ::");
			System.out.println("오류 : " + e.getMessage());
		} catch (Exception e) {
			// 기타 예외 발생 시 에러 메시지 추가
			model.addAttribute("error", ":: Warning - 예상치 못한 오류가 발생했습니다. ::");
			System.out.println("오류 : " + e.getMessage());
		}

		// 시간표 정보를 모델에 추가
		getTimetablePage(model, session);

		return "redirect:/timetable";
	}

	// 시간표를 삭제하는 메서드
	@PostMapping("/deleteTimetable")
	public String deleteTimeTable(String delete_subject, Model model, HttpSession session) {
		// 시간표 삭제
		timetableDAO.deleteTimeTable(GetId(session), delete_subject);

		// 시간표가 삭제된 후 다시 시간표를 가져와 모델에 추가
		List<TimeTableDTO> userTimeTable = timetableDAO.getTimetable(GetId(session));
		model.addAttribute("userTimeTable", userTimeTable);

		return "redirect:/timetable";
	}

	// 출석 체크 페이지를 보여주는 메서드
	@GetMapping("/attendanceCheck")
	public String attendanceCheck(Model model, HttpSession session) {
		// 세션에서 사용자 ID와 직책을 가져옴
		String userId = GetId(session);
		String userPosition = GetPosition(session);

		// 세션이 만료되었으면 로그인 페이지로 리다이렉트
		if (userId == null) {
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			return "redirect:/login";
		}

		// 교수나 관리자가 아니면 접근 제한
		if (!classroomDAO.isProfessorOrAdmin(userId)) {
			return "redirect:/timetable";
		}

		// 현재 날짜/시간 정보
		LocalDateTime now = LocalDateTime.now();
		String koreanDayOfWeek = convertWeek(now.getDayOfWeek());
		int currentHour = now.getHour();

		// 오늘의 출석 정보를 가져옴
		List<AttendanceDTO> attendanceList = classroomDAO.getTodayAttendance(userId, koreanDayOfWeek, currentHour);

		// 모델에 데이터를 추가
		model.addAttribute("userPosition", userPosition);
		model.addAttribute("attendanceList", attendanceList);
		model.addAttribute("currentDate", now.toLocalDate());
		model.addAttribute("currentTime", now.toLocalTime());
		model.addAttribute("userId", userId);
		model.addAttribute("currentDay", koreanDayOfWeek);
		model.addAttribute("currentHour", currentHour);

		// 시간표 정보를 모델에 추가
		getTimetablePage(model, session);

		return "attendanceCheck";
	}

	@PostMapping("/updateAttendance")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateAttendance(@RequestBody Map<String, Object> payload) {
		try {
			String userId = (String) payload.get("userId");
			String day = (String) payload.get("day");
			int startHour = Integer.parseInt(payload.get("startHour").toString());
			String subject = (String) payload.get("subject");
			boolean isAttended = (boolean) payload.get("isAttended");

			// StuTimetable의 attendance 필드만 업데이트
			classroomDAO.updateAttendance(userId, day, startHour, subject, isAttended);

			return ResponseEntity.ok(Map.of("success", true, "message", "출석 상태가 성공적으로 업데이트되었습니다."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@GetMapping("/api/today-schedule")
	@ResponseBody
	public ResponseEntity<List<TimeTableDTO>> getTodaySchedule(HttpSession session) {
		String userId = GetId(session);
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
		}

		try {
			LocalDateTime now = LocalDateTime.now();
			String koreanDay = convertWeek(now.getDayOfWeek());

			List<TimeTableDTO> todayTimetable = timetableDAO.getTodayTimetableWithReservation(userId, koreanDay);

			// 예약 정보 추가
			todayTimetable.forEach(timeTable -> {
				ReserveList reserveInfo = classroomDAO.getReservationInfo(timeTable.getClassroomName(), userId,
						timeTable.getStartHour());
				timeTable.setReservationInfo(reserveInfo);
			});

			return ResponseEntity.ok(todayTimetable);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
		}
	}
}