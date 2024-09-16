package com.tbfg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ClassroomSetController {

    @GetMapping("/classroomSet")
    public String seatLayout(Model model) {
        model.addAttribute("predefinedLayouts", getPredefinedLayouts());
        return "classroomSet"; // 'classroomSet.html'이라는 이름의 뷰를 반환합니다.
    }
    
    @PostMapping("/classroomSet/create")
    public String createLayout(
            @RequestParam(required = false) String seatTemplate, // 좌석 템플릿의 이름을 요청에서 가져옵니다.
            @RequestParam(required = false) Integer leftSeatRows, // 왼쪽 템플릿의 행 수를 요청에서 가져옵니다.
            @RequestParam(required = false) Integer leftSeatColumns, // 왼쪽 템플릿의 열 수를 요청에서 가져옵니다.
            @RequestParam(required = false) Integer rightSeatRows, // 오른쪽 템플릿의 행 수를 요청에서 가져옵니다.
            @RequestParam(required = false) Integer rightSeatColumns, // 오른쪽 템플릿의 열 수를 요청에서 가져옵니다.
            @RequestParam(required = false) String classroomNumber, // 강의실 이름을 요청에서 가져옵니다.
            Model model) {

        // 좌석 템플릿을 가져옵니다. 커스텀 템플릿의 경우 빈 값일 수 있으므로 "커스텀"으로 설정합니다.
        seatTemplate = (seatTemplate != null && !seatTemplate.isEmpty()) ? seatTemplate : "커스텀";

        String seatTemplateText;
        if ("커스텀".equals(seatTemplate)) {
            seatTemplateText = String.format("왼쪽: %dx%d, 오른쪽: %dx%d",
                leftSeatRows != null ? leftSeatRows : 0,
                leftSeatColumns != null ? leftSeatColumns : 0,
                rightSeatRows != null ? rightSeatRows : 0,
                rightSeatColumns != null ? rightSeatColumns : 0
            );
        } else {
            seatTemplateText = seatTemplate;
        }

        model.addAttribute("seatTemplate", seatTemplateText);
        model.addAttribute("classroomNumber", classroomNumber != null ? classroomNumber : "");

        return "resultLayout"; // 결과 페이지로 리다이렉트합니다.
    }

    private List<String> getPredefinedLayouts() {
        List<String> layouts = new ArrayList<>();
        layouts.add("왼쪽 : 4X5, 오른쪽 : 4X5");
        layouts.add("왼쪽 : 4X6, 오른쪽 : 4X6");
        layouts.add("왼쪽 : 4X7, 오른쪽 : 4X7");
        return layouts;
    }
}