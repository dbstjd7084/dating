# 데이팅 앱

## 빠른 이동
1. [목표](#목표)
2. [설치 방법](#설치-방법)
3. [사용 방법](#사용-방법)
4. [주요 기능](#주요-기능)
    1. [로딩 화면](#로딩-화면)
    2. [홈](#홈)
    3. [일기 작성](#일기-작성)
    4. [일기 검색](#일기-검색)
    5. [멍냥정보(추천 영상)](#멍냥정보추천-영상)
    6. [반려동물 동반 가능 시설 찾기](#반려동물-동반-가능-시설-찾기)
5. [버전 및 업데이트 정보](#버전-및-업데이트-정보)

## 목표

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/567e3c7d5176f3a3b88911f062869dc9a4270dd4/images/1%20-%20icon.png" alt="1 - icon" width="300"/>
이 앱은 매칭 시스템과 매칭된 상대에게 알림, 채팅을 제공합니다.
채팅 화면을 제외한 세로 화면 환경에서 구동됩니다.

[테스트 환경: Android API 35 Emulator]

## 설치 방법

1. 이 저장소를 클론합니다:
   ```bash
   git clone https://github.com/dbstjd7084/dating.git
   ```
2. 디렉토리로 이동합니다:
   ```bash
   cd dating
   ```
  
## 사용 방법

1. 외모 점수 평가 AI 모델을 다운합니다.

2.  /app/src/main 에 assets 폴더를 생성하여 모델 파일을 넣습니다.

3. Firebase 프로젝트를 생성하여 [ 프로젝트 설정 - 일 - 내 앱 ]에서 google-services.json 파일을 다운로드 후 /app 에 넣습니다.

4. [ 프로젝트 설정 - 서비스 계정 - 새 비공개 키 생성 ]에서 다운받은 service-account.json 파일을 /app/src/main/res/raw 에 넣습니다.
     
## 주요 기능

### 로딩 화면
<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/567e3c7d5176f3a3b88911f062869dc9a4270dd4/images/2%20-%20%EB%A1%9C%EB%94%A9%20%ED%99%94%EB%A9%B4.gif" alt="2 - 로딩 화면" width="300"/><br>

- 로딩 화면에서 gif 형태의 사진으로 고양이가 노는 모습을 볼 수 있습니다. 로딩하는 동안 반려동물 동반 시설에 대한 정보를 SQLite에 미리 가져와 저장합니다.

### 홈
<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/3%20-%20%ED%99%88%20%ED%99%94%EB%A9%B4.png" alt="3 - 홈 화면" width="300"/>

- 홈에서 반려동물의 사진을 올리고, 반려동물 정보를 작성해 홈 화면을 내 반려동물로 꾸밀 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/4%20-%20%ED%99%88%20%EC%82%AC%EC%A7%84%20%ED%99%95%EB%8C%80.png" alt="4 - 홈 사진 확대" width="300"/>

- 내가 올린 반려동물 사진을 눌러 사진을 확대할 수 있습니다. 또한, 사진을 길게 눌러 사진을 변경하거나 없앨 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/5%20-%20%ED%99%88%20%EC%9E%91%EC%84%B1%20%ED%99%94%EB%A9%B4.png" alt="5 - 홈 작성 화면" width="300"/>

- 반려동물 정보 작성 공간에서 달력을 눌러 생일을 지정하고, 이름과 좋아하는 것, 싫어하는 것을 적을 수 있습니다.
  
### 일기 작성
<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/6%20-%20%EB%85%84%EC%9B%94%EC%9D%BC%20%EC%84%A0%ED%83%9D.png" alt="6 - 년월일 선택" width="300"/>

- 년도 선택화면에서 상단의 화살표를 눌러 년도를 이동하고, 이동할 달력을 눌러 날짜 선택화면으로 이동합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/6%20-%20%EB%85%84%EC%9B%94%EC%9D%BC%20%EC%84%A0%ED%83%9D.png" alt="7 - 캘린더 화면" width="300"/>

- 상단의 년월을 누르면 년도 선택화면으로 다시 이동합니다. 요일 구분을 위해 토요일과 일요일에 색이 지정되어있으며, 작성된 일기의 기분은 날짜의 우측 상단에서 확인 가능합니다. 또한, 작성된 일기가 있다면 날짜 밑에 점으로 표시됩니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/8%20-%20%EC%9D%BC%EA%B8%B0%20%ED%99%94%EB%A9%B4.png" alt="8 - 일기 화면" width="300"/>

- 일기의 날짜 표시를 눌러 상위 날짜 선택화면으로 이동할 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/9%20-%20%EC%9D%BC%EA%B8%B0%20%EC%82%AC%EC%A7%84%20%EC%8A%A4%ED%81%AC%EB%A1%A4.png" alt="9 - 일기 사진 스크롤" width="300"/>

- 사진을 횡으로 스크롤하여 다른 사진을 볼 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/10%20-%20%EC%9D%BC%EA%B8%B0%20%ED%99%94%EB%A9%B4%20%ED%99%95%EB%8C%80.png" alt="10 - 일기 화면 확대" width="300"/>

- 사진을 눌러 확대합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/11%20-%20%EC%9D%BC%EA%B8%B0%20%EA%B3%B5%EA%B0%84%20%EA%B8%B8%EA%B2%8C%20%EB%88%84%EB%A5%B4%EA%B8%B0.png" alt="11 - 일기 공간 길게 누르기" width="300"/>

- 일기 공간을 길게 눌러 일기를 편집하고 삭제합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/12%20-%20%EC%9D%BC%EA%B8%B0%20%ED%8E%B8%EC%A7%91%20%EB%AA%A8%EB%93%9C.png" alt="12 - 일기 편집 모드" width="300"/>

- 편집모드에서 제목과 내용, 기분, 사진을 편집합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/13%20-%20%EC%9D%BC%EA%B8%B0%20%ED%8E%B8%EC%A7%91%20%EA%B3%BC%EC%A0%95.png" alt="13 - 일기 편집 과정" width="300"/>

- 기분 아이콘을 눌러 변경하고, 제목이나 내용 공간을 눌러 작성창을 띄웁니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/14%20-%20%EC%9D%BC%EA%B8%B0%20%EC%82%AC%EC%A7%84%20%EC%82%AD%EC%A0%9C.png" alt="14 - 일기 사진 삭제" width="300"/>

- 추가된 사진을 누르면 해당 사진을 삭제합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/15%20-%20%EC%9D%BC%EA%B8%B0%20%EC%82%AC%EC%A7%84%20%2B.png" alt="15 - 일기 사진 +" width="300"/>

- 사진 공간에서 가장 오른쪽에 있는 + 를 눌러 갤러리에서 사진을 추가합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/16%20-%20%EC%9D%BC%EA%B8%B0%20%EC%9E%90%EB%8F%99%20%EC%B6%9C%EC%84%9D%20%EC%B2%B4%ED%81%AC.png" alt="16 - 일기 자동 출석 체크" width="300"/>

- 앱 설치일로부터 현재까지 미작성된 날짜에 빈 일기를 생성합니다.

### 일기 검색
<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/17%20-%20%EC%9D%BC%EA%B8%B0%20%EA%B2%80%EC%83%89.png" alt="17 - 일기 검색" width="300"/>

- 작성된 일기의 제목으로 검색할 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/18%20-%20%ED%99%95%EC%9E%A5%20%EA%B2%80%EC%83%89.png" alt="18 - 확장 검색" width="300"/>

- 검색할 제목을 모르는 경우 최하단에 있는 확장 검색을 눌러 내용도 포함해 검색할 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/19%20-%20%EC%9D%BC%EA%B8%B0%20%EA%B2%80%EC%83%89%EC%97%90%EC%84%9C%20%EC%9D%BC%EA%B8%B0%20%EB%B3%B4%EA%B8%B0.png" alt="19 - 일기 검색에서 일기 보기" width="300"/>

- 화면 상의 원하는 일기를 눌러 볼 수 있습니다.

### 멍냥정보(추천 영상)
<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/20%20-%20AI%20%EB%B6%84%EC%84%9D.png" alt="20 - AI 분석" width="300"/>

- 작성된 반려동물 정보를 고려해 LLM(ChatGPT-4o-mini)을 활용해 분석하고 키워드를 선정합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/7c81dae361b50b7f27891b83f143dd546392ac02/images/21%20-%20%EC%B6%94%EC%B2%9C%20%EC%98%81%EC%83%81%20%ED%99%94%EB%A9%B4.png" alt="21 - 추천 영상 화면" width="300"/>

- 유튜브 검색 키워드를 선정하면, 검색 결과로 나온 최상단 10개의 영상을 보여줍니다.<br><br>

<div align="left">
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/22%20-%20%EC%83%9D%EC%9D%BC%EC%9D%B8%20%EA%B2%BD%EC%9A%B0.png" alt="22 - 생일인 경우" width="300"/>
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/23%20-%20%EC%83%9D%EC%9D%BC%EC%9D%B4%20%EB%8B%A4%EA%B0%80%EC%98%A4%EB%8A%94%20%EA%B2%BD%EC%9A%B0.png" alt="23 - 생일이 다가오는 경우" width="300"/>
</div><br>

- 오늘이 생일이면 축하해주며 생일이 다가오는 경우도 알려줍니다.<br><br>

<div align="left">
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/24%20-%20%EC%98%81%EC%83%81%EB%B7%B0.png" alt="24 - 영상뷰" width="300"/>
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/25%20-%20%EC%98%81%EC%83%81%20%EC%A0%84%EC%B2%B4%ED%99%94%EB%A9%B4.png" alt="25 - 영상 전체화면" width="300"/>
</div><br>

- 영상 클릭 시 해당 영상뷰를 띄우며, 전체화면도 제공합니다.

### 반려동물 동반 가능 시설 찾기
<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/26%20-%20%EC%9C%84%EC%B9%98%20%ED%95%84%ED%84%B0.png" alt="26 - 위치 필터" width="300"/>

- 위치 필터를 통해 내 위치 기반 주위 시설 또는 특정 지역의 시설을 선택해 표시할 수 있습니다.<br><br>

<div align="left">
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/27%20-%20%EC%A4%8C1.png" alt="27 - 줌1" width="270"/>&nbsp;
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/91f4efdb4da4d34972c69039a2fdd29a0500783b/images/28%20-%20%EC%A4%8C2.png" alt="28 - 줌2" width="270"/>&nbsp;
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/91f4efdb4da4d34972c69039a2fdd29a0500783b/images/29%20-%20%EC%A4%8C3.png" alt="29 - 줌3" width="270"/>
</div><br>

- 아이콘 가시성을 위해 지도의 확대 상태에 따라 아이콘 크기를 조절합니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/30%20-%20%EC%8B%9C%EC%84%A4%EC%9D%98%20%EC%9D%B4%EB%A6%84%EA%B3%BC%20%EC%A3%BC%EC%86%8C.png" alt="30 - 시설의 이름과 주소" width="300"/>

- 아이콘을 눌러 해당 시설의 이름과 주소를 볼 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/31%20-%20%EC%8B%9C%EC%84%A4%20%EC%A0%95%EB%B3%B4%20%ED%99%95%EC%9D%B8.png" alt="31 - 시설 정보 확인" width="300"/>

- 한 번 더 눌러 등록된 시설 정보(이름, 유형, 주소, 운영시간, 연락처, 홈페이지, 이용료, 동반 추가 요금, 반려동물 동반 조건, 주차 여부 등)를 확인할 수 있습니다.<br><br>

<div align="left">
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/32%20-%20%EC%9D%B4%EB%A6%84%20%EB%88%84%EB%A5%B4%EA%B8%B0.png" alt="32 - 이름 누르기" width="200"/>
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/33%20-%20%EC%A3%BC%EC%86%8C%20%EB%B3%B5%EC%82%AC.png" alt="33 - 주소 복사" width="200"/>
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/34%20-%20%EC%A0%84%ED%99%94%EB%B2%88%ED%98%B8%20%EB%88%84%EB%A5%B4%EA%B8%B0.png" alt="34 - 전화번호 누르기" width="200"/>
    <img src="https://github.com/dbstjd7084/dogcatgorithm/blob/2361995aef80ffe96f6f7daa68b3924ee730e249/images/35%20-%20%ED%99%88%ED%8E%98%EC%9D%B4%EC%A7%80%20%EC%9D%B4%EB%8F%99.png" alt="35 - 홈페이지 이동" width="200"/>
</div><br>

- 이름을 눌러 해당 시설을 검색합니다
- 주소를 눌러 클립보드에 복사합니다
- 전화번호를 눌러 바로 전화할 수 있습니다
- 홈페이지 주소를 눌러 바로 이동합니다

## 버전 및 업데이트 정보

- **현재 버전**: v1.0.0
- **업데이트 내역**:
  - v1.0.0: 초기 릴리스

