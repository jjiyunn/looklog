# LOOK LOG (룩로그)

패션 코디 사진을 기록하고 공유하는 소셜 플랫폼입니다. Instagram과 Pinterest를 참고해 "오늘의 착장(OOTD)"을 아카이빙하고, 다른 사람들의 스타일을 태그로 탐색할 수 있도록 만들었습니다.

> 개인 포트폴리오 프로젝트로 1인 개발했습니다.

https://github.com/user-attachments/assets/01bf0d81-541a-4ec1-bb6a-da9c598f5092

## 주요 기능

### 회원가입 / 로그인 + 이메일 인증 시스템
이메일 인증을 거쳐야 서비스를 이용할 수 있는 세션 기반 인증 시스템입니다. 이메일·비밀번호 로그인과 Google OAuth2 소셜 로그인을 모두 지원하고, 비밀번호를 잊은 경우 이메일 인증으로 재설정할 수 있습니다.

**회원가입**
- 이메일, 비밀번호, 이름, 사용자이름 입력 후 가입
- 입력값 형식 검증, 이메일·사용자이름 중복 차단, 비밀번호 암호화 저장
- 가입 완료 후 이메일 인증 페이지로 자동 이동
  
<img width="292" height="512" alt="회원가입" src="https://github.com/user-attachments/assets/d76cc5c7-3042-452a-bba8-a611b28306e3" />

**이메일 인증**
- 가입 시 Gmail SMTP로 6자리 인증 코드 발송(유효시간 10분)
- `/email-verify`에서 코드 검증 및 재전송 지원
- 인터셉터로 미인증 세션은 인증 페이지로 강제 이동

https://github.com/user-attachments/assets/1bf5ff41-5943-4953-b91c-45b360200058

**로그인 / 로그아웃**
- 이메일·비밀번호 로그인 또는 Google 로그인
- 로그인 시 세션 생성, 로그아웃 시 세션 삭제
- 비로그인 사용자가 접근하면 로그인 페이지로 이동
  
<img width="309" height="425" alt="로그인" src="https://github.com/user-attachments/assets/f16f3a30-537a-49e4-a948-83ddde574238" />

**비밀번호 찾기**
- 가입한 이메일 입력 후 인증 메일 발송
- 인증 완료 후 새 비밀번호 설정
- 재설정이 끝나면 로그인 페이지로 이동

<img width="392" height="298" alt="비밀번호 찾기" src="https://github.com/user-attachments/assets/677d43a1-d090-444e-8794-2508ddbe4a36" />

### 게시글 업로드 + AI 태그 자동 추천
사진을 업로드하면 Google Gemini API가 이미지를 분석해 어울리는 태그를 자동으로 추천합니다. 태그는 미리 정의된 Style / Season / Color 목록 안에서만 선택되도록 프롬프트를 설계해, AI가 임의의 문자열을 반환해도 서버에서 검증 후 필터링합니다.

- 사진 선택 시 자동으로 서버에 전송 → AI 분석(약 5초) → 해당 태그 버튼 자동 선택
- API 실패(트래픽 초과 등) 시 재시도 로직 + 실패해도 게시글 작성은 정상 진행되도록 예외 처리
- 이미지 리사이즈 후 전송으로 속도 최적화

https://github.com/user-attachments/assets/9476f9fa-be2e-45a3-a909-4aad4e3b37eb

### 서랍(Drawer) 시스템
마음에 드는 게시글을 폴더별로 모아 저장할 수 있는 기능입니다. 옷장을 정리하듯 개인 취향으로 컬렉션을 만들 수 있습니다.

https://github.com/user-attachments/assets/53087bb5-44ae-42fa-a493-ee5c52482a34

### 최신순/인기순 정렬 시스템
최신 게시글을 먼저 볼 수 있는 최신순과 좋아요 순으로 게시글을 볼 수 있는 기능입니다.

https://github.com/user-attachments/assets/a89d23b7-e2a4-4f06-b64b-035912d13c9b

### 팔로우 & 프로필/서랍 공개 설정
사용자 간 팔로우 기능과, 프로필/서랍 단위로 전체공개·비공개를 설정할 수 있는 프라이버시 옵션을 제공합니다.

https://github.com/user-attachments/assets/9d307be8-87dd-44e6-9c01-7983f663e81c

### 신고 & 관리자 모더레이션
부적절한 게시글/회원을 신고할 수 있고, 관리자 페이지에서 신고 내역을 확인해 강제 삭제 등 조치를 취할 수 있습니다.

<img width="377" height="425" alt="신고" src="https://github.com/user-attachments/assets/bbb7f937-aec0-4f1a-9fd2-fca44b0241e9" />
<img width="1009" height="333" alt="관리자" src="https://github.com/user-attachments/assets/821283ad-2ad3-4365-a46f-37514318d4b5" />

### 태그 기반 검색
Style / Season / Color 카테고리로 나뉜 태그를 조합해 원하는 스타일의 게시글을 탐색할 수 있습니다.

https://github.com/user-attachments/assets/1c2b8fe9-15dc-4211-ac86-62cadce169ef


## 기술 스택

**Backend**
`JAVA` `Spring Boot` `Spring Data JPA` `Spring Security` `Thymeleaf` `Lombok` `Gradle`

**Database**
`MySQL`

**External API**
`Google Gemini API` (이미지 인식 기반 태그 추천)
`Gmail SMTP` (이메일 인증)
`Google OAuth2` (소셜 로그인)

**Frontend**
`JS` `jQuery` `Bootstrap Icons` `HTML` `CSS`

**Tools**
`IntelliJ IDEA` `DBeaver` `Git/GitHub`

## ERD

<img width="769" height="352" alt="ERD" src="https://github.com/user-attachments/assets/69d9b0ab-369e-4117-a8ba-97ea44c57ad1" />

- 주요 관계: 회원 1:N 게시글, 게시글 N:M 태그, 회원 1:N 서랍, 서랍 N:M 게시글 등
  
## 트러블슈팅

### 1. AI 태그 추천 API 안정성

- **문제**: Gemini API가 트래픽이 몰릴 때 503 에러를 반환해 태그 추천이 실패하는 경우 발생
- **원인**: 외부 API(Gemini)의 일시적 과부하로 인한 503 응답
- **해결**
  - 최대 2회 재시도 로직 추가 (지수 백오프 대신 단순 재시도 선택: 사진 업로드 후 대기 시간을 늘리지 않기 위함)
  - 모든 재시도가 실패해도 빈 태그 목록을 반환해 사용자가 직접 태그를 선택하고 게시글을 작성할 수 있도록 처리
- **배운 점**: 외부 API는 언제든 실패할 수 있다는 전제로 설계, 부가 기능(AI 추천)의 실패가 핵심 기능(게시글 작성)을 막지 않도록 분리

### 2. 게시글 삭제 시 외래키 제약 오류

- **문제**: 게시글 삭제 시 외래키 제약 오류로 삭제가 실패하는 경우 발생
- **원인**
  - `좋아요`, `게시글-태그 연결`, `서랍 담기` 테이블이 게시글을 참조하는 상태에서 자식 데이터를 지우지 않고 부모(게시글)부터 삭제 시도
  - 이미지 파일을 먼저 삭제한 뒤 DB를 삭제하는 순서로, DB 삭제 실패 시 게시글은 남고 이미지만 사라지는 불일치 가능
- **해결**
  - 연관 테이블(좋아요, 게시글-태그, 서랍 담기)을 먼저 삭제한 뒤 게시글을 삭제하도록 순서 변경
  - 이미지 파일 삭제를 DB 삭제 성공 이후로 이동해 중간 실패 시에도 데이터 정합성 유지
- **배운 점**: 테이블 간 참조 관계를 고려한 삭제 순서, DB와 파일 시스템처럼 서로 다른 저장소를 함께 다룰 때의 처리 순서 학습
