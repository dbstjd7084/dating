# 데이팅 앱

## 빠른 이동
1. [목표](#목표)
2. [설치 방법](#설치-방법)
3. [사용 방법](#사용-방법)
4. [주요 기능](#주요-기능)
    1. [계정 화면](#계정-화면)
    2. [홈](#홈)
    3. [내 프로필](#내-프로필)
    4. [채팅](#채팅)
5. [버전 및 업데이트 정보](#버전-및-업데이트-정보)

## 목표

<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/1%20-%20icon.png" alt="1 - icon" width="300"/>
이 프로젝트는 Firebase 기반 데이팅 앱 기능을 제공합니다.

[테스트 환경: Android API 35 Emulator, Galaxy Tab S7 FE]

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

1. Firebase 프로젝트를 생성합니다.

2. '프로젝트 설정 - 일반 - SDK 설정 및 구성' 에서 google-services.json 파일을 받아 앱 프로젝트 경로에 넣습니다.

3. '프로젝트 설정 - 서비스 계정 - 새 비공개 키 생성' 을 통해 파일의 이름을 'service_account.json'으로 수정 후 앱 프로젝트의 res/raw 경로에 넣습니다.

4. 'app/src/main' 에서 assets 폴더를 생성 후 'Facial_rating_model.tflite' 파일을 다운받아 넣습니다.

      - [배포용 얼굴 평가 모델 TFLite 파일](https://drive.google.com/file/d/1x_3WhjfkptAFdRUOCpaxcwQxM8u59D_W/view?usp=sharing, "파일 다운로드")
     
## 주요 기능

### 계정 화면
<div align="left">
    <img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/2%20-%20%EC%9D%B8%ED%8A%B8%EB%A1%9C%20%ED%99%94%EB%A9%B4.png" alt="2 - 인트로 화면" width="300"/>
    <img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/3%20-%20%EA%B3%84%EC%A0%95%20%ED%99%94%EB%A9%B4.png" alt="3 - 계정 화면" width="300"/>
</div><br>

- 계정 화면에서 Firebase 기반 계정을 생성하고 로그인할 수 있습니다. 이미 로그인 되어 있는 경우 계정 화면을 띄우지 않습니다.<br><br>

### 홈
<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/4%20-%20%ED%99%88%20%ED%99%94%EB%A9%B4.png" alt="4 - 홈 화면" width="300"/>

- CardStackView를 활용하여 상대방의 프로필 카드를 확인합니다.<br><br>

<div align="left">
    <img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/5%20-%20%EC%88%98%EB%9D%BD.png" alt="5 - 수락" width="300"/>
    <img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/6%20-%20%EA%B1%B0%EC%A0%88.png" alt="6 - 거절" width="300"/>
</div><br>

- 상대방이 마음에 들면 오른쪽, 마음에 들지 않으면 왼쪽으로 넘깁니다.<br><br>

<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/7%20-%20%EB%A7%A4%EC%B9%AD%20%EC%99%84%EB%A3%8C%20%EC%8B%9C.png" alt="7 - 매칭 완료 시" width="300"/>

- 매칭 완료 시 상대방에게 알림을 보냅니다.<br><br>
  
### 내 프로필
<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/8%20-%20%EB%82%B4%20%ED%94%84%EB%A1%9C%ED%95%84.png" alt="8 - 내 프로필" width="300"/>

- 하단의 내 프로필 아이콘을 통해 내 프로필 화면에 진입할 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/9%20-%20%EB%82%B4%20%ED%94%84%EB%A1%9C%ED%95%84%20%EC%82%AC%EC%A7%84%20%EC%88%98%EC%A0%95.png" alt="9 - 내 프로필 사진 수정" width="300"/>

- 나의 프로필 이미지 영역을 클릭 시 갤러리에서 이미지를 가져올 수 있습니다.<br><br>

<div align="left">
    <img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/10%20-%20%EC%86%8C%EA%B0%9C%EB%A7%90%20%EC%88%98%EC%A0%95.png" alt="10 - 소개말 수정" width="300"/>
    <img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/11%20-%20%EC%86%8C%EA%B0%9C%EB%A7%90%20%ED%99%95%EC%9D%B8%20%EB%B2%84%ED%8A%BC%20%ED%99%9C%EC%84%B1%ED%99%94.png" alt="11 - 소개말 확인 버튼 활성화" width="300"/>
</div><br>

- 소개말 영역을 클릭하여 나의 소개말을 설정할 수 있으며, 소개말에 변화가 생기면 확인 버튼이 활성화됩니다.<br><br>

### 채팅
<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/12%20-%20%EC%B1%84%ED%8C%85%20%ED%99%94%EB%A9%B4.png" alt="12 - 채팅 화면" width="650"/>

- 하단의 대화 아이콘을 통해 채팅 화면에 진입할 수 있으며, 팔로워와 팔로우하는 사람을 볼 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/13%20-%20%ED%8C%94%EB%A1%9C%EC%9B%8C%20%ED%94%84%EB%A1%9C%ED%95%84%20%ED%99%95%EC%9D%B8.png" alt="13 - 팔로워 프로필 확인" width="300"/>

- 팔로우 한 상대와 팔로워를 클릭하 프로필을 확인할 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/14%20-%20%EC%8B%A4%EC%8B%9C%EA%B0%84%20%EC%B1%84%ED%8C%85.png" alt="14 - 실시간 채팅" width="650"/>

- 매칭된 상대와 실시간으로 대화할 수 있습니다.<br>
- Firebase의 알림 서버 활성화에 시간이 걸려, 초반에는 작동하지 않거나 지연될 수 있습니다.<br><br>

<img src="https://github.com/dbstjd7084/dating/blob/e53c490a66504c2003c86bbb88dbd9246e1dd3a1/images/15%20-%20%EC%B1%84%ED%8C%85%20%EC%95%8C%EB%A6%BC.png" alt="15 - 채팅 알" width="650"/>

- 상대방이 대화방에 없는 경우 알림을 전송합니다.<br><br>

## 버전 및 업데이트 정보

- **현재 버전**: v1.0.0
- **업데이트 내역**:
  - v1.0.0: 초기 릴리스

