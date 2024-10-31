package com.tbfg.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tbfg.dao.ProfessorDAO;
import com.tbfg.dao.UserDAO;
import com.tbfg.dto.ProfessorDTO;
import com.tbfg.dto.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class MyPageController {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private ProfessorDAO professorDAO;
	@Autowired
	private Contoller ct = new Contoller();

	// 마이페이지를 보여주는 메서드
	@GetMapping("/myPage")
	public String showMyPage(HttpSession session, Model model) {
		// 세션에서 로그인된 사용자 정보를 가져옴
		Object user = session.getAttribute("loggedInUser");

		// 만약 사용자 정보가 없으면, 세션 만료로 간주하고 에러 메시지와 함께 로그인 페이지로 이동
		if (user == null) {
			model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
			ct.getTimetablePage(model, session);
			return "login"; // 로그인 페이지로 리다이렉트
		}

		// 사용자 포지션 변수 초기화
		String userPosition = null;

		// 사용자 정보를 확인하고 포지션 설정
		if (user instanceof UserDTO) {
			UserDTO userDTO = (UserDTO) user; // UserDTO로 캐스팅
			userPosition = userDTO.getPosition(); // 사용자 포지션 가져오기
		} else if (user instanceof ProfessorDTO) {

			ProfessorDTO proDTO = (ProfessorDTO) user; // ProfessorDTO로 캐스팅
			userPosition = proDTO.getPosition(); // 교수 포지션 가져오기
		}

		// 모델에 사용자 정보와 포지션 추가
		model.addAttribute("user", user);
		model.addAttribute("userPosition", userPosition); // userPosition 추가

		return "myPage"; // 마이페이지로 이동
	}

	// 사용자 정보를 업데이트하는 메서드
	@PostMapping("/updateUser")
	@ResponseBody // JSON 응답을 위해 추가
	public Map<String, Object> updateUser(@RequestParam String id, @RequestParam String oldPassword,
			@RequestParam String newPassword, @RequestParam(required = false) String major,
			@RequestParam(required = false) Integer grade, @RequestParam(required = false) String classNumber,
			HttpSession session) {

		Map<String, Object> response = new HashMap<>();

		try {
			Object user = null;
			boolean isProfessor = professorDAO.isProExists(id);

			if (isProfessor) {
				user = professorDAO.getProById(id);
			} else {
				user = userDAO.getUserById(id);
			}

			if (user == null) {
				response.put("success", false);
				response.put("message", "사용자를 찾을 수 없습니다.");
			} else if (isProfessor && !professorDAO.checkProPassword(id, oldPassword)) {
				response.put("success", false);
				response.put("message", "기존 비밀번호가 일치하지 않습니다.");
			} else if (!isProfessor && !userDAO.checkPassword(id, oldPassword)) {
				response.put("success", false);
				response.put("message", "기존 비밀번호가 일치하지 않습니다.");
			} else {
				if (user instanceof UserDTO) {
					UserDTO student = (UserDTO) user;
					student.setPass(newPassword);
					if (grade != null)
						student.setGrade(grade);
					if (classNumber != null)
						student.setClassNumber(classNumber);
					if (major != null)
						student.setMajor(major);
					userDAO.updateUser(student);
				} else if (user instanceof ProfessorDTO) {
					ProfessorDTO professor = (ProfessorDTO) user;
					professor.setPass(newPassword);
					professorDAO.updatePro(professor);
				}

				session.setAttribute("loggedInUser", user);
				response.put("success", true);
				response.put("message", "정보가 성공적으로 수정되었습니다.");
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
		}

		return response;
	}

	@PostMapping("/deleteUser")
	@ResponseBody  // JSON 응답을 위해 추가
	public Map<String, Object> deleteUser(@RequestParam String id, HttpSession session) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        boolean isProfessor = professorDAO.isProExists(id);

	        if (isProfessor) {
	            Object professor = professorDAO.getProById(id);
	            if (professor == null) {
	                response.put("success", false);
	                response.put("message", "교수를 찾을 수 없습니다.");
	                return response;
	            }
	            
	            professorDAO.deletePro(id);
	        } else {
	            Object user = userDAO.getUserById(id);
	            if (user == null) {
	                response.put("success", false);
	                response.put("message", "학생을 찾을 수 없습니다.");
	                return response;
	            }
	            
	            userDAO.deleteUser(id);
	        }

	        session.invalidate();
	        response.put("success", true);
	        response.put("message", "계정이 성공적으로 삭제되었습니다.");
	        
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "계정 삭제 중 오류가 발생했습니다.");
	    }
	    
	    return response;
	}

	// 사용자가 입력한 비밀번호가 맞는지 확인하는 메서드
	@PostMapping("/checkPassword")
	@ResponseBody
	public boolean checkPassword(@RequestParam String id, @RequestParam String oldPassword) {
		// ID를 통해 사용자가 교수인지 학생인지 확인
		boolean isProfessor = professorDAO.isProExists(id);

		// 사용자가 교수인 경우
		if (isProfessor) {
			// 교수의 비밀번호 확인
			return professorDAO.checkProPassword(id, oldPassword);
		} else {
			// 학생의 비밀번호 확인
			Object user = userDAO.getUserById(id);

			// 사용자가 존재하고 비밀번호가 일치하면 true 반환, 그렇지 않으면 false 반환
			return user != null && userDAO.checkPassword(id, oldPassword);
		}
	}
}
