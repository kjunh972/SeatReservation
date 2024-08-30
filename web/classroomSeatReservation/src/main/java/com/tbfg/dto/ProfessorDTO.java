package com.tbfg.dto;

public class ProfessorDTO {

    // 교수의 ID
    private String id;
    // 교수의 비밀번호
    private String pass;
    // 교수의 소속 학교
    private String school;
    // 교수의 전공
    private String major;
    // 교수의 이름
    private String name;
    // 교수의 직위 
    private String position;

    // ID의 getter 메서드
    public String getId() { 
        return id; 
    }

    // ID의 setter 메서드
    public void setId(String id) { 
        this.id = id; 
    }

    // 비밀번호의 getter 메서드
    public String getPass() { 
        return pass; 
    }

    // 비밀번호의 setter 메서드
    public void setPass(String pass) { 
        this.pass = pass; 
    }

    // 소속 학교의 getter 메서드
    public String getSchool() { 
        return school; 
    }

    // 소속 학교의 setter 메서드
    public void setSchool(String school) { 
        this.school = school; 
    }

    // 전공의 getter 메서드
    public String getMajor() { 
        return major; 
    }

    // 전공의 setter 메서드
    public void setMajor(String major) { 
        this.major = major; 
    }

    // 이름의 getter 메서드
    public String getName() { 
        return name; 
    }

    // 이름의 setter 메서드
    public void setName(String name) { 
        this.name = name; 
    }

    // 직위의 getter 메서드
    public String getPosition() { 
        return position; 
    }

    // 직위의 setter 메서드
    public void setPosition(String position) { 
        this.position = position; 
    }
}
