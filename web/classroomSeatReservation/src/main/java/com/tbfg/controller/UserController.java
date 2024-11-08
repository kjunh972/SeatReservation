package com.tbfg.controller;

import java.util.Collections;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tbfg.dao.ProfessorDAO;
import com.tbfg.dao.UserDAO;
import com.tbfg.dto.ProfessorDTO;
import com.tbfg.dto.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("userDTO") // 'userDTO' 속성을 세션에 저장하도록 지정하는 애노테이션
public class UserController {

	// Autowired 애노테이션은 의존성을 자동으로 주입합니다.
	@Autowired
	private JdbcTemplate jdbcTemplate; // 데이터베이스 작업을 위한 JDBC 템플릿
	@Autowired
	private UserDAO userDAO = new UserDAO(jdbcTemplate); // UserDAO 객체 생성 및 주입
	private UserDTO userDTO = new UserDTO(); // UserDTO 객체 생성
	@Autowired
	private ProfessorDAO proDAO = new ProfessorDAO(jdbcTemplate); // ProfessorDAO 객체 생성 및 주입
	private ProfessorDTO proDTO = new ProfessorDTO(); // ProfessorDTO 객체 생성
	@Autowired
	private Contoller ct = new Contoller();

	public boolean isValidPassword(String password) {
	    // 최소 8자, 하나 이상의 문자, 숫자, 특수문자를 포함하는 정규식
	    String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
	    return Pattern.matches(regex, password);
	}

	@GetMapping("/login")
	public String showLoginPage(HttpSession session, Model model) {
		String userPosition = ct.GetPosition(session);
		String userId = ct.GetId(session);
		
		ct.getTimetablePage(model, session);

		// 로그인 세션이 만료 된 경우
		if (userId == null) {
			model.addAttribute("userNull", true);
			return "login";
		}
		
		model.addAttribute("userPosition", userPosition);

		return "login";
	}

	// 로그아웃 처리를 위한 메서드
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // 세션을 초기화하여 로그아웃 처리
		return "redirect:/login"; // 로그인 페이지로 리다이렉트
	}

	@PostMapping("/signupCheck")
	public String signupUser(@RequestParam String id, @RequestParam String pass, @RequestParam String school,
			@RequestParam String major, @RequestParam String name, @RequestParam Integer age,
			@RequestParam Integer grade, @RequestParam String classNumber, @RequestParam String studentId,
			Model model) {

		// 비밀번호 유효성 검사
		if (!isValidPassword(pass)) {
			model.addAttribute("errorMessage", "비밀번호는 최소 8자이며, 하나 이상의 문자 및 숫자를 포함해야 합니다.");
			return "studentSignup"; // 회원가입 페이지로 다시 이동
		}

		// 학교 유효성 검사
		String[] allowedSchools = { "가톨릭대학교", "고려대학교", "동양미래대학교", "서울대학교", "성공회대학교", "연세대학교", "유한대학교", "중앙대학교" };
		boolean isValidSchool = false;

		for (String allowedSchool : allowedSchools) {
			if (allowedSchool.equals(school)) {
				isValidSchool = true;
				break;
			}
		}

		if (!isValidSchool) {
			model.addAttribute("errorSchool", "등록되지 않은 학교입니다. 등록된 학교에서만 회원가입이 가능합니다.");
			return "studentSignup"; // 회원가입 페이지로 다시 이동
		}

		// 새로운 사용자 정보 설정 및 저장
		UserDTO userDTO = new UserDTO();
		userDTO.setId(id);
		userDTO.setPass(pass);
		userDTO.setSchool(school);
		userDTO.setMajor(major);
		userDTO.setName(name);
		userDTO.setAge(age);
		userDTO.setGrade(grade);
		userDTO.setClassNumber(classNumber);
		userDTO.setStudentId(studentId);
		userDTO.setPosition("student");

		userDAO.saveUser(userDTO); // 사용자 정보를 데이터베이스에 저장

		return "redirect:/login"; // 회원가입 후 로그인 페이지로 리다이렉트
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam("id") String id, @RequestParam("password") String password, HttpSession session) {
	    // DAO에서 비밀번호를 가져옴
	    String storedPassword = userDAO.getPasswordFromTables(id);

	    // 비밀번호 확인
	    if (storedPassword != null && storedPassword.equals(password)) {
	        // 사용자 정보를 DAO에서 가져옴
	        if (userDAO.isUserExists(id)) {
	            userDTO = userDAO.getUserById(id);
	            // 세션에 사용자 정보 저장
	            session.setAttribute("loggedInUser", userDTO);

	            // 학생 여부를 세션에 저장
	            session.setAttribute("isStudent", true);
	        } else if (proDAO.isProExists(id)) {
	            proDTO = proDAO.getProById(id);
	            // 세션에 교수 정보 저장
	            session.setAttribute("loggedInUser", proDTO);

	            // 교수이므로 학생 여부를 false로 설정
	            session.setAttribute("isStudent", false);
	        }

	        return ResponseEntity.ok().build();  // 로그인 성공
	    }

	    // 로그인 실패 시 JSON 형식으로 에러 메시지 반환
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "아이디 또는 비밀번호가 잘못되었습니다."));
	}


	// 학생 회원가입 폼을 보여주는 메서드
	@GetMapping("/studentSignup")
	public String showRegistrationForm(Model model) {
		return "studentSignup";
	}

	// 아이디 중복 체크를 위한 메서드
	@GetMapping("/checkId")
	@ResponseBody
	public String checkId(@RequestParam String id) {
		boolean isExists = userDAO.isUserExists(id); // 사용자 아이디 존재 여부 확인
		if (!isExists) {
			isExists = proDAO.isProExists(id); // 존재하지 않으면 교수 아이디 존재 여부 확인
		}
		return Boolean.toString(isExists); // 결과를 문자열로 반환
	}

	// 포지션 선택 페이지를 보여주는 메서드
	@GetMapping("/selectPosition")
	public String showPositionSelectPage() {
		return "selectPosition"; // 포지션 선택 페이지로 이동
	}

	// 포지션 선택 처리 메서드
	@PostMapping("/selectPosition")
	public String selectPosition(@RequestParam String position) {
		if ("학생".equals(position)) {
			return "redirect:/studentSignup"; // 학생 회원가입 페이지로 리다이렉트
		} else if ("교수".equals(position)) {
			return "redirect:/professorSignup"; // 교수 회원가입 페이지로 리다이렉트
		} else {
			// 처리할 수 없는 경우 예외 처리 또는 다른 작업 수행
			return "redirect:/selectPosition"; // 다시 포지션 선택 페이지로 리다이렉트
		}
	}
}
