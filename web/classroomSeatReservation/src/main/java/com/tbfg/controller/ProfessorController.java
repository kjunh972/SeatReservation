package com.tbfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tbfg.dao.ProfessorDAO;
import com.tbfg.dao.UserDAO;
import com.tbfg.dto.ProfessorDTO;

@Controller
public class ProfessorController {

    @Autowired
    private JdbcTemplate jdbcTemplate; // JdbcTemplate을 주입받아 데이터베이스 작업을 처리합니다.

    @Autowired
    private UserDAO userDAO = new UserDAO(jdbcTemplate); // UserDAO 인스턴스를 초기화합니다.

    @Autowired
    private ProfessorDAO proDAO = new ProfessorDAO(jdbcTemplate); // ProfessorDAO 인스턴스를 초기화합니다.
    private ProfessorDTO proDTO = new ProfessorDTO(); // 교수 정보를 저장할 DTO 객체를 초기화합니다.

    // 교수 회원가입 페이지를 보여주는 메서드
    @GetMapping("/professorSignup")
    public String showRegistrationForm(Model model) {
        return "professorSignup"; // professorSignup.html 뷰를 반환합니다.
    }

    // 교수 회원가입 요청을 처리하는 메서드
    @PostMapping("/professorSignUpCheck")
    public String signupPro(@RequestParam String id,
                            @RequestParam String password,
                            @RequestParam String school,
                            @RequestParam String major,
                            @RequestParam String name,
                            Model model) {

        // 허용된 학교 목록을 정의합니다.
        String[] allowedSchools = {"대학교1", "대학교2", "대학교3"};
        boolean isValidSchool = false; // 학교가 유효한지 확인하는 플래그 변수입니다.

        // 입력된 학교가 허용된 학교 목록에 있는지 확인합니다.
        for (String allowedSchool : allowedSchools) {
            if (allowedSchool.equals(school)) {
                isValidSchool = true;
                break;
            }
        }

        // 입력된 학교가 허용된 학교 목록에 없다면 에러 메시지를 모델에 추가하고, 회원가입 페이지로 다시 이동합니다.
        if (!isValidSchool) {
            model.addAttribute("errorMessage", "등록되지 않은 학교입니다. 등록된 학교에서만 회원가입이 가능합니다.");
            return "professorSignup";
        }
        
        // DTO에 입력된 교수 정보를 설정합니다.
        proDTO.setId(id);
        proDTO.setPass(password);
        proDTO.setSchool(school);
        proDTO.setMajor(major);
        proDTO.setName(name);
        proDTO.setPosition("professor");

        // DAO를 통해 교수 정보를 데이터베이스에 저장합니다.
        proDAO.savePro(proDTO);

        // 회원가입이 성공하면 로그인 페이지로 리다이렉트합니다.
        return "redirect:/login";
    }

    // 교수 아이디 중복 확인 요청을 처리하는 메서드
    @GetMapping("/checkProfessorId")
    @ResponseBody
    public String checkProfessorId(@RequestParam String id) {
        // 입력된 아이디가 이미 존재하는지 확인합니다.
        boolean isExists = userDAO.isUserExists(id);
        if (!isExists) {
            isExists = proDAO.isProExists(id);
        }
        return Boolean.toString(isExists); // 아이디가 존재하면 true, 존재하지 않으면 false를 반환합니다.
    }

    // 교수 로그인 페이지를 보여주는 메서드
    @GetMapping("/professorLogin")
    public String showLogin() {
        return "professorLogin"; // professorLogin.html 뷰를 반환합니다.
    }
}
