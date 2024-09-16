-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS YuhanDB;

-- 사용할 데이터베이스 선택
USE YuhanDB;

-- 기존 테이블 삭제
DROP TABLE IF EXISTS FavoriteClassrooms;
DROP TABLE IF EXISTS ReservationHour;
DROP TABLE IF EXISTS Reservation;
DROP TABLE IF EXISTS BanSeat;
DROP TABLE IF EXISTS BanSeatHour;
DROP TABLE IF EXISTS StuTimetable;
DROP TABLE IF EXISTS Classrooms;
DROP TABLE IF EXISTS proYuhan;
DROP TABLE IF EXISTS Yuhan;

-- 학생 정보 테이블
CREATE TABLE Yuhan (
  id VARCHAR(20) PRIMARY KEY NOT NULL, -- 학생 ID (기본 키)
  pass VARCHAR(20) NOT NULL, -- 비밀번호
  school VARCHAR(20), -- 학교 이름
  position VARCHAR(10), -- 직위
  major VARCHAR(255), -- 전공
  name VARCHAR(10), -- 이름
  age INT, -- 나이
  grade INT, -- 학년
  class INT, -- 반
  studentId INT -- 학생 ID
);

-- 교수 정보 테이블
CREATE TABLE proYuhan (
  id VARCHAR(20) PRIMARY KEY NOT NULL, -- 교수 ID (기본 키)
  pass VARCHAR(20) NOT NULL, -- 비밀번호
  school VARCHAR(20), -- 학교 이름
  position VARCHAR(10), -- 직위
  major VARCHAR(255), -- 전공
  name VARCHAR(10) -- 이름
);

-- 강의실 이름 테이블
CREATE TABLE Classrooms (
  classroom_name VARCHAR(10) PRIMARY KEY NOT NULL, -- 강의실 이름 (기본 키)
  leftRow INT,
  leftCol INT,
  rightRow INT,
  rightCol INT
);

-- 즐겨찾기한 강의실 테이블
CREATE TABLE FavoriteClassrooms (
  user_id VARCHAR(20) NOT NULL, -- 사용자 ID
  classroom_num VARCHAR(10) NOT NULL, -- 강의실 번호
  PRIMARY KEY (user_id, classroom_num), -- 복합 기본 키 설정
  FOREIGN KEY (user_id) REFERENCES Yuhan(id), -- 사용자 ID 외래 키
  FOREIGN KEY (classroom_num) REFERENCES Classrooms(classroom_name) -- 강의실 번호 외래 키
);

-- 예약한 강의실 좌석 정보 테이블
CREATE TABLE Reservation (
  reservNum INT NOT NULL, -- 예약 번호 (기본 키)
  user_id VARCHAR(20) NOT NULL, -- 사용자 ID
  classroom_name VARCHAR(20) NOT NULL, -- 강의실 이름
  reservSeat INT NOT NULL, -- 예약된 좌석 번호
  day VARCHAR(20) NOT NULL, -- 요일
  PRIMARY KEY (reservNum), -- 기본 키 설정
  FOREIGN KEY (user_id) REFERENCES Yuhan(id), -- 사용자 ID 외래 키
  FOREIGN KEY (classroom_name) REFERENCES Classrooms(classroom_name) -- 강의실 이름 외래 키
);

-- 예약 시간 정보 테이블
CREATE TABLE ReservationHour (
  reservNum INT NOT NULL, -- 예약 번호 (외래 키)
  reservHour INT NOT NULL, -- 예약된 시간대
  PRIMARY KEY (reservNum, reservHour) -- 복합 기본 키 설정
);

-- 금지한 좌석 테이블
CREATE TABLE BanSeat (
  banNum INT NOT NULL AUTO_INCREMENT, -- 금지한 번호 (기본 키)
  user_id VARCHAR(20) NOT NULL, -- 사용자 ID
  classroom_name VARCHAR(20) NOT NULL, -- 강의실 이름
  banSeat INT NOT NULL, -- 금지한 좌석 번호
  PRIMARY KEY (banNum), -- 기본 키 설정
  FOREIGN KEY (user_id) REFERENCES proYuhan(id), -- 사용자 ID 외래 키
  FOREIGN KEY (classroom_name) REFERENCES Classrooms(classroom_name) -- 강의실 이름 외래 키
);

-- 금지한 좌석의 시간대 테이블
CREATE TABLE BanSeatHour (
  banNum INT NOT NULL, -- 금지한 번호 (외래 키)
  banHour INT NOT NULL, -- 금지한 좌석의 시간대
  PRIMARY KEY (banNum, banHour) -- 복합 기본 키 설정
);

-- 학생 시간표 테이블
CREATE TABLE StuTimetable (
  user_id VARCHAR(50) NOT NULL, -- 사용자 ID
  day VARCHAR(10), -- 요일
  start_hour INT, -- 시작 시간
  end_hour INT, -- 종료 시간
  classroomName VARCHAR(10), -- 강의실 이름
  subject VARCHAR(30), -- 과목
  FOREIGN KEY (classroomName) REFERENCES Classrooms(classroom_name) -- 강의실 이름 외래 키
);

-- 데이터 삽입
-- 학생 정보 삽입
INSERT INTO `Yuhan` VALUES 
('kjunh972','1234','Yuhan','student','computer','김준형',24,3,2,202007024);

-- 교수 정보 삽입
INSERT INTO `proYuhan` VALUES 
('admin','admin','Yuhan','admin','computer','admin');

-- 강의실 이름 삽입
INSERT INTO Classrooms (classroom_name) VALUES 
('5401'), ('5402'), ('5403'), ('5404'), ('5405'), ('5406'), ('5407'), ('5408'), ('5409'), ('5410'), ('5411'),
('7201'), ('7202'), ('7203'), ('7204'), ('7205'), ('7206'), ('7207'), ('7208'), ('7209'), ('7210');


-- 학생 시간표 삽입
INSERT INTO StuTimetable (user_id, day, start_hour, end_hour, subject, classroomName) VALUES
('kjunh972', '월요일', 13, 16, 'Java Framework', '7207'),  
('kjunh972', '화요일', 9, 12, '모바일 프로그래밍', '7207'),
('kjunh972', '화요일', 13, 16, 'C#', '7206'),              
('kjunh972', '수요일', 9, 10, '진로 탐색', '5406'),
('kjunh972', '수요일', 10, 11, '취창업', '5406'),
('kjunh972', '목요일', 9, 12, '소프트웨어 공학', '7202'),
('kjunh972', '목요일', 13, 16, '데이터베이스 프로그래밍', '7207');  
