package com.tbfg.controller;

import com.tbfg.dao.ProfessorDAO;
import com.tbfg.dao.UserDAO;
import com.tbfg.dto.UserDTO;
import com.tbfg.dto.ProfessorDTO;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyPageController {

    @Autowired
    private UserDAO userDAO;  // UserDAO 객체를 주입받음
    @Autowired
    private ProfessorDAO professorDAO;  // ProfessorDAO 객체를 주입받음
    

    // 마이페이지를 보여주는 메서드
    @GetMapping("/myPage")
    public String showMyPage(HttpSession session, Model model) {
        // 세션에서 로그인된 사용자 정보를 가져옴
        Object user = session.getAttribute("loggedInUser");

        // 만약 사용자 정보가 없으면, 세션 만료로 간주하고 에러 메시지와 함께 로그인 페이지로 이동
        if (user == null) {
            model.addAttribute("error", "세션이 만료되었습니다. 다시 로그인 해주세요.");
            return "login"; // 로그인 페이지로 리다이렉트
        }

        // 사용자 정보를 모델에 추가하여 마이페이지를 렌더링
        model.addAttribute("user", user);
        return "myPage"; // 마이페이지로 이동
    }

    // 사용자 정보를 업데이트하는 메서드
    @PostMapping("/updateUser")
    public String updateUser(@RequestParam String id,          // 사용자 ID 파라미터
                             @RequestParam String oldPassword, // 기존 비밀번호 파라미터
                             @RequestParam String newPassword, // 새로운 비밀번호 파라미터
                             @RequestParam(required = false) String major,       // 전공 파라미터
                             @RequestParam(required = false) Integer grade,      // 학년 파라미터
                             @RequestParam(required = false) String classNumber, // 반 번호 파라미터
                             HttpSession session,              // 세션 객체
                             Model model) {                    // 모델 객체
        // ID를 통해 사용자 정보를 데이터베이스에서 가져옴
        Object user = null;
        boolean isProfessor = professorDAO.isProExists(id); // 사용자가 교수인지 확인

        if (isProfessor) {
            // 사용자가 교수인 경우, 교수 정보를 가져옴
            user = professorDAO.getProById(id);
        } else {
            // 사용자가 학생인 경우, 학생 정보를 가져옴
            user = userDAO.getUserById(id);
        }

        // 사용자가 존재하지 않는 경우
        if (user == null) {
            model.addAttribute("errorMessage", "사용자를 찾을 수 없습니다.");
        } 
        // 기존 비밀번호가 일치하지 않는 경우 (교수)
        else if (isProfessor && !professorDAO.checkProPassword(id, oldPassword)) {
            model.addAttribute("errorMessage", "기존 비밀번호가 일치하지 않습니다.");
        } 
        // 기존 비밀번호가 일치하지 않는 경우 (학생)
        else if (!isProfessor && !userDAO.checkPassword(id, oldPassword)) {
            model.addAttribute("errorMessage", "기존 비밀번호가 일치하지 않습니다.");
        } 
        // 모든 조건을 만족할 경우, 사용자의 정보를 업데이트
        else {
            if (user instanceof UserDTO) {
                // UserDTO에 대한 업데이트 로직
                UserDTO student = (UserDTO) user;
                student.setPass(newPassword); // 새로운 비밀번호로 업데이트
                student.setGrade(grade); // 학년 업데이트
                student.setClassNumber(classNumber); // 반 번호 업데이트
                student.setMajor(major); // 전공 업데이트
                userDAO.updateUser(student); // 데이터베이스에 사용자 정보 업데이트
            } else if (user instanceof ProfessorDTO) {
                // ProfessorDTO에 대한 업데이트 로직
                ProfessorDTO professor = (ProfessorDTO) user;
                professor.setPass(newPassword); // 새로운 비밀번호로 업데이트
                professorDAO.updatePro(professor); // 데이터베이스에 교수 정보 업데이트
            }

            // 세션에 최신 사용자 정보를 다시 저장
            session.setAttribute("loggedInUser", user);
            
            // 성공 메시지를 모델에 추가
            model.addAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
        }
        return "redirect:/classroomLike";  // classroomlike 페이지로 리다이렉트
    }


    // 사용자 계정을 삭제하는 메서드
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam String id, HttpSession session, Model model) {
        // ID를 통해 사용자가 교수인지 학생인지 확인
        boolean isProfessor = professorDAO.isProExists(id);
        
        // 사용자가 교수일 경우, 교수 정보를 조회
        if (isProfessor) {
            // ID를 통해 교수 정보를 데이터베이스에서 가져옴
            Object professor = professorDAO.getProById(id);

            // 사용자가 존재하지 않는 경우
            if (professor == null) {
                model.addAttribute("errorMessage", "교수를 찾을 수 없습니다.");
            } 
            // 사용자가 존재할 경우
            else {
                // 데이터베이스에서 교수 계정 삭제
                professorDAO.deletePro(id);
                
                // 세션 무효화
                session.invalidate();
                
                // 성공 메시지를 모델에 추가
                model.addAttribute("successMessage", "교수 계정이 성공적으로 삭제되었습니다.");
                
                // 회원가입 직책 선택 페이지로 리다이렉트
                return "login";
            }
        } else {
            // ID를 통해 학생 정보를 데이터베이스에서 가져옴
            Object user = userDAO.getUserById(id);

            // 사용자가 존재하지 않는 경우
            if (user == null) {
                model.addAttribute("errorMessage", "학생을 찾을 수 없습니다.");
            } 
            // 사용자가 존재할 경우
            else {
                // 데이터베이스에서 학생 계정 삭제
                userDAO.deleteUser(id);
                
                // 세션 무효화
                session.invalidate();
                
                // 성공 메시지를 모델에 추가
                model.addAttribute("successMessage", "학생 계정이 성공적으로 삭제되었습니다.");
                
                // 회원가입 직책 선택 페이지로 리다이렉트
                return "login";
            }
        }
        
        // 에러가 발생한 경우, 다시 마이페이지로 이동
        return "myPage"; 
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
