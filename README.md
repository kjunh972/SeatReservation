# 강의실 좌석 예약 시스템 (SeatReservation)
이 시스템은 교육 기관에서 강의실 좌석 예약의 혼잡 문제를 해결하기 위해 개발되었습니다. 특히, PC 사용이 필수적인 전공이나 실습 과목에서는 체계적인 좌석 관리가 중요하지만, 대부분의 교육 기관에는 이러한 시스템이 부족합니다. 이 좌석 예약 시스템은 사용자가 모바일이나 웹으로 간편하게 강의실 좌석을 예약하고, 실시간으로 예약 상황을 확인할 수 있도록 지원합니다. 이를 통해 학생의 편의를 증진하고 학업 효율을 극대화하며, 관리자에게는 강의실 자원의 효율적인 활용과 체계적인 관리 기능을 제공합니다.


## 목차
- [개요](#개요)
- [개발언어](#개발언어)
- [화면구성](#화면구성)
- [참고자료](#참고자료)
- [지원](#지원)


## 개요
- 프로젝트 이름 : 강의실 좌석 예약 시스템 (SeatReservation)
- 프로젝트 개발 기간 : 2024.05 - 2024.11
- 프로젝트 서비스 기간 : 2024.11.13 - 2024.11.14 (학술제 기간)
- 팀 이름 : TBFG
- 팀 맴버 : 김준형, 송유진, 문현민, 정서연, 박영혜, 김진주
- 팀원 개발 업무
    - 김준형 : 팀장, 웹 개발, 앱 개발, 윈도우 락스크린 개발, DB 설계 및 구축
    - 송유진 : 웹 프론트 개발
    - 문현민 : 웹 백엔드 개발
    - 정서연 : 모바일 프론트 개발
    - 박영혜 : 웹 백엔드 개발
    - 김진주 : 락스크린 이미지 디자인

## 개발언어

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![C#](https://img.shields.io/badge/c%23-%23239120.svg?style=for-the-badge&logo=csharp&logoColor=white) ![Flutter](https://img.shields.io/badge/Flutter-%2302569B.svg?style=for-the-badge&logo=Flutter&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)

## 화면구성
이미지가 너무 많은 관계로 가장 많이 사용할만한 화면 구성들만 이미지로 보여드리겠습니다.

모바일이랑 웹이랑 사용하는 기능들은 동일합니다.

1. 웹
<div align="center">
:star: 메인페이지 로그인전 :star:
  
![메인페이지-로그인전](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80_%EB%A1%9C%EA%B7%B8%EC%9D%B8%20%EC%A0%84.png)

로그인 하지 않고 다른 페이지로 이동할려고 하면 무조건 다시 메인페이지로 돌아온다.

:star: 메인페이지 로그인후 :star:

![메인페이지-로그인후](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80_%EB%A1%9C%EA%B7%B8%EC%9D%B8%20%ED%9B%84.png)

로그인후 바로 다른 페이지로 손쉽게 이동할 수도 있고 시간표를 클릭하면 자동으로 시간표 페이지로 이동한다.

:star: 시간표 :star:
  
![시간표](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C.png)

과목이 없는 셀을 누르고 시간표 추가 누르면 시간표 추가 할수 있고 좌석 예약을 누르면 과목이 없는 셀도 예약 할수 있다.

과목이 있는 셀을 선택하고 시간표 수정이나 삭제를 누르면 수정이나 삭제를 할 수 있고 좌석 예약이나 좌석 관리를 누르면 좌석 예약이나 관리를 할 수 있다.

![시간표 추가](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C%20%EC%B6%94%EA%B0%80.png)

![시간표 수정](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C%20%EC%88%98%EC%A0%95.png)

![시간표 삭제](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%8B%9C%EA%B0%84%ED%91%9C%20%EC%82%AD%EC%A0%9C.png)

:star: 강의실 현황 :star:

![강의실 현황](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EC%8B%A4%20%ED%98%84%ED%99%A9.png)

시간표에 추가한 강의실들을 볼 수 있다.

:star: 강의실 정보 :star:
![강의실 정보](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EC%8B%A4%20%EC%A0%95%EB%B3%B4.png)

해당 강의실의 모든 정보를 볼 수 있다.(Ex. 강의실에 추가한 강의들이나 예약내역등)

:star: 좌석 예약 및 좌석 예약 금지 :star:
  
![좌석 예약 및 좌석 예약 금지](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EB%B0%8F%20%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EA%B8%88%EC%A7%80.png)

좌석을 클릭해서 학생이면 좌석 예약을 교수나 관리자이면 학생이 예약을 못하게 좌석 예약 금지를 할수 있다. 

또한 학생이 예약한 좌석을 클릭하면 예약한 좌석 자리이동, 예약번호 변경, 예약취소 (예약취소는 따로 예약 목록 페이지로 가서 관리 할수도 있다.) 기능들이 있다.

:star: 예약확인 :star:

![예약확인](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%98%88%EC%95%BD%ED%99%95%EC%9D%B8.png)

학생은 자기가 예약한 예약 목록들을 볼 수 있고, 교수는 자기 수업을 듣는 학생들의 예약목록을 볼 수 있고, 관리자는 전체 학생들의 예약 목록을 볼 수 있다.

:star: 좌석 예약 금지 확인 :star:

![좌석 예약 금지 확인](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%EA%B8%88%EC%A7%80%20%ED%99%95%EC%9D%B8.png)

교수는 자기가 좌석 예약 금지한 정보들을 확인 및 관리 할 수 있고, 관리자는 전체 좌석 예약 금지한 정보들을 확인 및 관리 할 수 있다.

:star: 출석부 :star:

![출석부](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EC%B6%9C%EC%84%9D%EB%B6%80.png)

교수는 자기 수업을 듣는 학생 (교수가 등록한 시간표에 학생이 그대로 시간표에 등록했을떄.)의 출석 정보들을 확인 할 수 있고 관리 할 수 있다.(학생이 좌석 예약 후 락스크린을 해제했을때 출석 아니면 결석 처리)

:star: 강의가 없는 시간 선택 후 좌석 예약 할떄 :star:

![강의가 없는 시간 선택 후 좌석 예약 할떄](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EA%B0%80%20%EC%97%86%EB%8A%94%20%EC%8B%9C%EA%B0%84%20%EC%84%A0%ED%83%9D%20%ED%9B%84%20%EC%A2%8C%EC%84%9D%20%EC%98%88%EC%95%BD%20%ED%95%A0%EB%96%84.png)

주로 방과후나 강의가 없는 시간에 추가적으로 공부나 작업할때 사용하는 기능이다.

강의가 없는 시간표에서 셀을 선택 후 좌석 예약버튼을 누르면 어디서 할껀지 (강의실 이름), 왜 좌석 예약을 하는지 (예약하는 목적) 입력 후 좌석 예약 할 수 있다.

추후 교수나 관리자가 확인 후 예약 취소를 해서 학생에게 사용하면 안되는 이유를 통보를 제한 할 수 있다.

:star: 강의실 생성 :star:

![강의실 생성](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EA%B0%95%EC%9D%98%EC%8B%A4%20%EC%83%9D%EC%84%B1.png)

새로 컴퓨터실이 생겨 관리가 필요할때 강의실 생성을 하여 등록 할 수 있다.

미리 만들어놓은 템플릿으로 생성 하거나 커스텀으로 직접 좌석 수, 위치를 등록해서 사용 할 수도 있다.

:star: 마이페이지 :star:

![마이페이지](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80.png)

마이페이지에서 자기 정보를 관리하거나 수정할 수 있습니다.
</div>

2. 모바일

다음은 모바일을 소개하겠습니다. 웹이랑 사용 할 수 있는 기능과 페이지는 동일하며 전부 다 이미지를 보여주면 너무 길어지는 관계로 간단하게 몇개만 소개해드리도록 하겠습니다.

| 모바일 메인페이지 | 모바일 메뉴 | 모바일 시간표 | 모바일 좌석표 |
| :-: | :-: | :-: | :-: |
| <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80.jpg" width="200" height="400"/> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EB%A9%94%EB%89%B4.jpg" width="200" height="400"/> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EC%8B%9C%EA%B0%84%ED%91%9C.jpg" width="200" height="400"/> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EC%A2%8C%EC%84%9D%ED%91%9C.jpg" width="200" height="400"/>  |
| 모바일 강의실 현황 | 모바일 진행되는 강의 | 모바일 강의실 정보 | 모바일 마이페이지 |
| <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EA%B0%95%EC%9D%98%EC%8B%A4%20%ED%98%84%ED%99%A9.jpg" width="200" height="400"/> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EC%A7%84%ED%96%89%EB%90%98%EB%8A%94%20%EA%B0%95%EC%9D%98.jpg" width="200" height="400"/> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EA%B0%95%EC%9D%98%EC%8B%A4%20%EC%A0%95%EB%B3%B4.jpg" width="200" height="400"/> | <img src="https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%AA%A8%EB%B0%94%EC%9D%BC%20%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80.jpg" width="200" height="400"/>  |

3. 락스크린 (윈도우)

원하는 요일, 원하는 시간, 원하는 강의실, 원하는 좌석을 예약하면 예약번호가 랜덤으로 나오는데 해당 좌석으로 가서 예약한 학생 이름, 학번, 그리고 예약번호를 입력했을때 3개 정보가 일치하면 락스크린이 해제가 되고 틀리면 해제가 되지 않는다.

락스크린이 해제되면 그 학생은 자동으로 출석으로 처리가 되고 컴퓨터를 자유롭게 사용 할 수 있다.

![락스크린](https://github.com/kjunh972/SeatReservation/blob/main/web/images/%EB%9D%BD%EC%8A%A4%ED%81%AC%EB%A6%B0.PNG)

## 참고자료

[FLutter로 아이폰 앱 만들 때 기본세팅](https://kjunh972.tistory.com/135)

[MySQL 접속 오류](https://kjunh972.tistory.com/134)

## 지원

[![Gmail Badge](https://img.shields.io/badge/Gmail-d14836?style=flat-square&logo=Gmail&logoColor=white&link=mailto:kjunh972@gmail.com)](mailto:kjunh972@gmail.com)
