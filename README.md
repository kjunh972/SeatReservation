# 강의실 좌석 예약 시스템 (SeatReservation)
이 시스템은 교육 기관에서 강의실 좌석 예약의 혼잡 문제를 해결하기 위해 개발되었습니다. 특히, PC 사용이 필수적인 전공이나 실습 과목에서는 체계적인 좌석 관리가 중요하지만, 대부분의 교육 기관에는 이러한 시스템이 부족합니다. 이 좌석 예약 시스템은 사용자가 모바일이나 웹으로 간편하게 강의실 좌석을 예약하고, 실시간으로 예약 상황을 확인할 수 있도록 지원합니다. 이를 통해 학생의 편의를 증진하고 학업 효율을 극대화하며, 관리자에게는 강의실 자원의 효율적인 활용과 체계적인 관리 기능을 제공합니다.


## 목차
- [개요](#개요)
- [기술 스택](#기술-스택)
- [화면 구성](#화면-구성)
- [시작하기](#시작하기)
- [주요 기능](#주요-기능)
- [주요 API](#주요-api)
- [참고 자료](#참고-자료)
- [지원](#지원)


## 개요
- 프로젝트 이름 : 강의실 좌석 예약 시스템 (SeatReservation)
- 프로젝트 개발 기간 : 2024.05 - 2024.11
- 프로젝트 서비스 기간 : 2024.11.13 - 2024.11.14 (학술제 기간)
- 팀 이름 : TBFG
- 팀 멤버 : 김준형, 송유진, 문현민, 정서연, 박영혜, 김진주
- 팀원 개발 업무
    - **김준형** : 팀장, 웹 개발, 앱 개발, 윈도우 락스크린 개발, DB 설계 및 구축
    - **송유진** : 웹 프론트 개발
    - **문현민** : 웹 백엔드 개발
    - **정서연** : 모바일 프론트 개발
    - **박영혜** : 웹 백엔드 개발
    - **김진주** : 락스크린 이미지 디자인

## 기술 스택

### Backend

![Java](https://img.shields.io/badge/Java-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### Frontend

![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) 
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) 
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%2330c58e?style=for-the-badge&logo=thymeleaf&logoColor=white)
![SweetAlert2](https://img.shields.io/badge/SweetAlert2-%236f42c1?style=for-the-badge&logo=javascript&logoColor=white)

### Mobile

![Flutter](https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)
![Dart](https://img.shields.io/badge/Dart-0175C2?style=for-the-badge&logo=dart&logoColor=white)

### Windows

![C#](https://img.shields.io/badge/C%23-239120?style=for-the-badge&logo=c-sharp&logoColor=white)
![.NET](https://img.shields.io/badge/.NET-512BD4?style=for-the-badge&logo=dotnet&logoColor=white)

### DevOps

![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white) 
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)

### Development Tools

![Eclipse STS](https://img.shields.io/badge/Eclipse_STS-2C2255?style=for-the-badge&logo=eclipse&logoColor=white)
![Visual Studio](https://img.shields.io/badge/Visual_Studio-5C2D91?style=for-the-badge&logo=visualstudio&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Xcode](https://img.shields.io/badge/Xcode-147EFB?style=for-the-badge&logo=xcode&logoColor=white)
![MySQL Workbench](https://img.shields.io/badge/MySQL_Workbench-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![MySQL CLI](https://img.shields.io/badge/MySQL_CLI-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

## 화면 구성
이미지가 너무 많은 관계로 가장 많이 사용할만한 화면 구성들만 이미지로 보여드리겠습니다.

모바일이랑 웹이랑 사용하는 기능들은 동일합니다.

### 1. 웹
<div align="center">
    
### 1-1. 메인페이지 로그인전 
  
![메인페이지-로그인전](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80_%EB%A1%9C%EA%B7%B8%EC%9D%B8%20%EC%A0%84.png)

로그인 하지 않고 다른 페이지로 이동할려고 하면 무조건 다시 메인페이지로 돌아온다.

### 1-2. 메인페이지 로그인후 

![메인페이지-로그인후](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80_%EB%A1%9C%EA%B7%B8%EC%9D%B8%20%ED%9B%84.png)

로그인후 바로 다른 페이지로 손쉽게 이동할 수도 있고 시간표를 클릭하면 자동으로 시간표 페이지로 이동한다.

### 1-3. 시간표
  
![시간표](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C.png)

과목이 없는 셀을 누르고 시간표 추가 누르면 시간표 추가 할수 있고 좌석 예약을 누르면 과목이 없는 셀도 예약 할수 있다.

과목이 있는 셀을 선택하고 시간표 수정이나 삭제를 누르면 수정이나 삭제를 할 수 있고 좌석 예약이나 좌석 관리를 누르면 좌석 예약이나 관리를 할 수 있다.

![시간표 추가](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C%20%EC%B6%94%EA%B0%80.png)

![시간표 수정](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C%20%EC%88%98%EC%A0%95.png)

![시간표 삭제](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C%20%EC%82%AD%EC%A0%9C.png)

### 1-4. 강의실 현황 

![강의실 현황](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EC%8B%A4%20%ED%98%84%ED%99%A9.png)

시간표에 추가한 강의실들을 볼 수 있다.

### 1-5. 강의실 정보 
![강의실 정보](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EC%8B%A4%20%EC%A0%95%EB%B3%B4.png)

해당 강의실의 모든 정보를 볼 수 있다.(Ex. 강의실에 추가한 강의들이나 예약내역등)

### 1-6. 좌석 예약 및 좌석 예약 금지
  
![좌석 예약 및 좌석 예약 금지](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EB%B0%8F%20%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EA%B8%88%EC%A7%80.png)

좌석을 클릭해서 학생이면 좌석 예약을 교수나 관리자이면 학생이 예약을 못하게 좌석 예약 금지를 할수 있다. 

또한 학생이 예약한 좌석을 클릭하면 예약한 좌석 자리이동, 예약번호 변경, 예약취소 (예약취소는 따로 예약 목록 페이지로 가서 관리 할수도 있다.) 기능들이 있다.

### 1-7. 예약확인 

![예약확인](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%98%88%EC%95%BD%ED%99%95%EC%9D%B8.png)

학생은 자기가 예약한 예약 목록들을 볼 수 있고, 교수는 자기 수업을 듣는 학생들의 예약목록을 볼 수 있고, 관리자는 전체 학생들의 예약 목록을 볼 수 있다.

### 1-8. 좌석 예약 금지 확인 

![좌석 예약 금지 확인](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EA%B8%88%EC%A7%80%20%ED%99%95%EC%9D%B8.png)

교수는 자기가 좌석 예약 금지한 정보들을 확인 및 관리 할 수 있고, 관리자는 전체 좌석 예약 금지한 정보들을 확인 및 관리 할 수 있다.

### 1-9. 출석부 

![출석부](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%B6%9C%EC%84%9D%EB%B6%80.png)

교수는 자기 수업을 듣는 학생 (교수가 등록한 시간표에 학생이 그대로 시간표에 등록했을 때.)의 출석 정보들을 확인 할 수 있고 관리 할 수 있다.(학생이 좌석 예약 후 락스크린을 해제했을때 출석 아니면 결석 처리)

### 1-10. 강의가 없는 시간 선택 후 좌석 예약 할떄 

![강의가 없는 시간 선택 후 좌석 예약 할떄](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EA%B0%80%20%EC%97%86%EB%8A%94%20%EC%8B%9C%EA%B0%84%20%EC%84%A0%ED%83%9D%20%ED%9B%84%20%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%ED%95%A0%EB%96%84.png)

주로 방과후나 강의가 없는 시간에 추가적으로 공부나 작업할때 사용하는 기능이다.

강의가 없는 시간표에서 셀을 선택 후 좌석 예약버튼을 누르면 어디서 할껀지 (강의실 이름), 왜 좌석 예약을 하는지 (예약하는 목적) 입력 후 좌석 예약 할 수 있다.

추후 교수나 관리자가 확인 후 예약 취소를 해서 학생에게 사용하면 안되는 이유를 통보를 제한 할 수 있다.

### 1-11. 강의실 생성 

![강의실 생성](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EC%8B%A4%20%EC%83%9D%EC%84%B1.png)

새로 컴퓨터실이 생겨 관리가 필요할때 강의실 생성을 하여 등록 할 수 있다.

미리 만들어놓은 템플릿으로 생성 하거나 커스텀으로 직접 좌석 수, 위치를 등록해서 사용 할 수도 있다.

### 1-12. 마이페이지 

![마이페이지](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80.png)

마이페이지에서 자기 정보를 관리하거나 수정할 수 있습니다.

</div>

## 2. 모바일

다음은 모바일을 소개하겠습니다. 웹과 사용 가능한 기능과 페이지는 동일하며, 전부 다 이미지를 보여주면 너무 길어지므로 몇 개만 소개하겠습니다.

| 모바일 메인 페이지 | 모바일 메뉴 |
| :-: | :-: |
| <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80.jpg" width="200" height="390" /> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EB%A9%94%EB%89%B4.jpg" width="200" height="390" /> |

| 모바일 시간표 | 모바일 좌석표 |
| :-: | :-: |
| <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EC%8B%9C%EA%B0%84%ED%91%9C.jpg" width="200" height="390" /> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EC%A2%8C%EC%84%9D%ED%91%9C.jpg" width="200" height="390" /> |

| 모바일 강의실 현황 | 모바일 진행되는 강의 |
| :-: | :-: |
| <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EA%B0%95%EC%9D%98%EC%8B%A4%20%ED%98%84%ED%99%A9.jpg" width="200" height="390" /> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EC%A7%84%ED%96%89%EB%90%98%EB%8A%94%20%EA%B0%95%EC%9D%98.jpg" width="200" height="390" /> |

| 모바일 강의실 정보 | 모바일 마이페이지 |
| :-: | :-: |
| <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EA%B0%95%EC%9D%98%EC%8B%A4%20%EC%A0%95%EB%B3%B4.jpg" width="200" height="390" /> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80.jpg" width="200" height="390" /> |

### 3. 락스크린 (윈도우)

원하는 요일, 원하는 시간, 원하는 강의실, 원하는 좌석을 예약하면 예약번호가 랜덤으로 나오는데 해당 좌석으로 가서 예약한 학생 이름, 학번, 그리고 예약번호를 입력했을때 3개 정보가 일치하면 락스크린이 해제가 되고 틀리면 해제가 되지 않는다.

락스크린이 해제되면 그 학생은 자동으로 출석으로 처리가 되고 컴퓨터를 자유롭게 사용 할 수 있다.

![락스크린](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%9D%BD%EC%8A%A4%ED%81%AC%EB%A6%B0.PNG)

![락스크린](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A1%B8%EC%9E%91%20%EB%8F%99%EC%98%81%EC%83%81.gif)

### 4. 좌석 예약 미리 보기

<img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD.gif" width="200" height="420"/>

### 5. 좌석 예약 금지 미리보기

<img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EA%B8%88%EC%A7%80.gif" width="200" height="420"/>

## 시작하기

### 서버 요구사항
- JDK 17 이상
- Spring Boot 3.2.0 이상
- MySQL 8.0.27 이상
- Maven 3.9 이상
- Thymeleaf 3.x

### 클라이언트 요구사항
- Flutter SDK 3.24.4 이상
- Dart SDK 3.5.0 이상
- Android Studio / VS Code
- Chrome 브라우저 최신 버전
- iOS 개발을 위한 Xcode (Mac OS에서 개발시)

### 모바일 앱 실행 환경
- Android 6.0 (API 23) 이상
- iOS 12.0 이상
- 인터넷 연결 필수
- 카메라 및 마이크 권한 필요

### 윈도우 락스크린
- Windows 10/11
- .NET Framework 4.8 이상
- Visual Studio 2022

### 설치 및 실행방법

### 1. 서버 설치 및 실행

1. 프로젝트 클론

```bash
git clone https://github.com/kjunh972/SeatReservation.git
```

2. 서버 디렉토리로 이동

```bash
cd SeatReservation/web/classroomSeatReservation
```

3. Maven 의존성 설치

```bash
mvn clean install
```

4. 스프링 부트 실행

```bash
mvn spring-boot:run
```

### 2. 모바일 앱 설치 및 실행

1. Flutter 앱 디렉토리로 이동

```bash
cd SeatReservation/app
```

2. Flutter 의존성 설치

```bash
flutter pub get
```

3. 앱 실행

```bash
flutter run
```

### 3. 윈도우 락스크린 설치

1. 방법 1

- Visual Studio에서 프로젝트 열기
- 빌드 후 실행

2. 방법 2

2-1. 프로젝트 클론

```bash
git clone https://github.com/kjunh972/SeatReservation.git
```

2-2. 디렉토리로 이동

```bash
cd SeatReservation/windows/lockScreen/lockScreen/bin/Debug
```

2-3. 락스크린 실행

- 명령어 실행: 아래 명령어를 입력하여 실행합니다.
```bash
lockScreen.exe 또는 ./lockScreen.exe
```

- GUI 실행:
  - 파일 탐색기를 엽니다.
  - `lockScreen.exe` 파일이 위치한 경로로 이동합니다.  
     (`SeatReservation/windows/lockScreen/lockScreen/bin/Debug`)
  - `lockScreen.exe` 파일을 더블 클릭합니다.
  - 프로그램이 실행되며 락스크린 화면이 표시됩니다.

## 주요 기능

### 1. 사용자 관리 시스템
- 학생/교수/관리자 역할 기반 권한 관리
- 회원가입 및 로그인/로그아웃
- 마이페이지를 통한 개인정보 관리
- 비밀번호 변경 및 회원탈퇴

### 2. 시간표 관리
- 강의 시간표 등록/수정/삭제
- 실시간 시간표 조회
- 강의실별 시간표 관리
- 교수-학생 간 시간표 연동

### 3. 좌석 예약 시스템
- 실시간 좌석 예약/취소
- 좌석 이동 기능
- 예약번호 자동 생성
- 좌석 예약 금지 설정(교수/관리자)
- 강의 외 시간 좌석 예약 관리

### 4. 출석 관리 시스템
- 좌석 예약과 연동된 자동 출석 체크
- 락스크린 해제를 통한 출석 확인
- 실시간 출석 현황 조회
- 교수용 출석부 관리

### 5. 강의실 관리
- 템플릿 기반 강의실 생성
- 커스텀 좌석 배치 설정
- 강의실 현황 실시간 모니터링
- 강의실별 통계 및 이용 현황

### 6. 크로스 플랫폼 지원
- 웹 인터페이스
- 모바일 앱(Android/iOS)
- 윈도우 락스크린
- 웹 반응형 디자인

## 주요 API

### 1. 인증 관련 API
- GET `/login` : 로그인 페이지
- POST `/login` : 로그인 처리
- GET `/logout` : 로그아웃
- GET `/studentSignup` : 회원가입 페이지
- POST `/signupCheck` : 회원가입 처리
- GET `/checkId` : 아이디 중복 확인
- GET `/selectPosition` : 회원 유형 선택 페이지

### 2. 강의실 관리 API
- GET `/admin` : 관리자 페이지
- GET `/classroomStatus` : 강의실 현황 조회
- GET `/classroomInfo` : 강의실별 과목 정보 조회
- GET `/classroomSet` : 강의실 설정 페이지
- POST `/classroomSet` : 강의실 설정 저장
- POST `/checkClassroomExists` : 강의실 중복 확인
- GET `/classroomResult` : 강의실 생성 결과
- GET `/classroomLike` : 강의실 목록

### 3. 예약 관리 API
- GET `/reservations` : 강의실별 예약 목록 조회
- POST `/updateReservation` : 예약 시간 변경
- POST `/moveSeat` : 좌석 이동
- POST `/banSeat` : 좌석 예약 금지
- POST `/unbanSeat` : 좌석 예약 금지 해제
- POST `/cancelReservation` : 예약 취소

### 4. 마이페이지 API
- GET `/mypage` : 마이페이지 조회
- POST `/checkPassword` : 비밀번호 확인
- POST `/updateUser` : 사용자 정보 업데이트
- POST `/deleteUser` : 계정 삭제

### 5. 출석 및 시간표 API
- GET `/attendanceCheck` : 출석 체크 페이지 표시
- POST `/updateAttendance` : 출석 상태 업데이트
- GET `/api/today-schedule` : 오늘의 시간표 조회

### 데이터베이스 연결 정보

```properties
# 애플리케이션 설정
spring.application.name=classroomSeatReservation
server.port=8055
spring.thymeleaf.prefix=classpath:/templates/thymeleaf/

# 데이터베이스 연결 설정
# MySQL 데이터베이스 URL (호스트, 포트, DB명을 환경에 맞게 수정)
spring.datasource.url=jdbc:mysql://localhost:3306/user_database_name

# 데이터베이스 접속 계정 (보안을 위해 변경 필요)
spring.datasource.username=username
spring.datasource.password=userpassword

# MySQL 드라이버 설정
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 세션 타임아웃 설정 (30분)
server.servlet.session.timeout=30m
```

### 데이터베이스 연결 정보 설정 방법

1. MySQL 서버 정보 수정

   - `localhost:3306` 부분을 실제 MySQL 서버의 호스트와 포트로 변경
   - `user_database_name` 부분을 실제 사용할 데이터베이스 이름으로 변경

2. 데이터베이스 계정 정보 수정

   - `username`: MySQL 사용자 이름으로 변경
   - `userpassword`: MySQL 비밀번호로 변경

3. 예시

```properties
# 로컬 환경
spring.datasource.url=jdbc:mysql://localhost:3306/user_database_name
spring.datasource.username=username
spring.datasource.password=userpassword

# 원격 환경
spring.datasource.url=jdbc:mysql://db.example.com:3306/user_database_name
spring.datasource.username=username
spring.datasource.password=userpassword
```

## 참고 자료

[FLutter로 아이폰 앱 만들 때 기본세팅](https://kjunh972.tistory.com/135)

[Flutter로 Spring Boot 서버 화면을 웹뷰로 띄우기](https://kjunh972.tistory.com/136)

[MySQL 접속 오류](https://kjunh972.tistory.com/134)

## 지원

[![Gmail Badge](https://img.shields.io/badge/Gmail-d14836?style=for-the-badge&logo=Gmail&logoColor=white&link=mailto:kjunh972@gmail.com)](mailto:kjunh972@gmail.com)
[![Tistory](https://img.shields.io/badge/Tistory-%23FF5A4A?style=for-the-badge&logo=tistory&logoColor=white)](https://kjunh972.tistory.com)
