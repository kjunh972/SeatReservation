package com.tbfg.dto;

public class UserDTO {
    // 사용자 ID
    private String id;
    // 사용자 비밀번호
    private String pass;
    // 사용자 학교
    private String school;
    // 사용자 전공
    private String major;
    // 사용자 이름
    private String name;
    // 사용자 나이
    private int age;
    // 사용자 학년
    private int grade;
    // 사용자 반
    private String classNumber;
    // 사용자 학번
    private String studentId;
    // 사용자 직책
    private String position;
    //사용자 ID를 반환 
    public String getId() {
        return id;
    }

    //사용자 ID를 설정 
    public void setId(String id) {
        this.id = id;
    }

    //사용자 비밀번호를 반환 
    public String getPass() {
        return pass;
    }

    //사용자 비밀번호를 설정 
    public void setPass(String pass) {
        this.pass = pass;
    }

    //사용자 학교를 반환 
    public String getSchool() {
        return school;
    }

    //사용자 학교를 설정 
    public void setSchool(String school) {
        this.school = school;
    }

    //사용자 전공을 반환 
    public String getMajor() {
        return major;
    }

    //사용자 전공을 설정 
    public void setMajor(String major) {
        this.major = major;
    }

    //사용자 이름을 반환 
    public String getName() {
        return name;
    }

    //사용자 이름을 설정 
    public void setName(String name) {
        this.name = name;
    }

    //사용자 나이를 반환 
    public int getAge() {
        return age;
    }

    //사용자 나이를 설정 
    public void setAge(int age) {
        this.age = age;
    }

    //사용자 학년을 반환 
    public int getGrade() {
        return grade;
    }

    //사용자 학년을 설정 
    public void setGrade(int grade) {
        this.grade = grade;
    }

    //사용자 반을 반환 
    public String getClassNumber() {
        return classNumber;
    }

    //사용자 반을 설정
    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    //사용자 학번을 반환 
    public String getStudentId() {
        return studentId;
    }

    //사용자 학번을 설정
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    //사용자 직책을 반환 
    public String getPosition() {
        return position;
    }

    //사용자 직책을 설정 
    public void setPosition(String position) {
        this.position = position;
    }
}
