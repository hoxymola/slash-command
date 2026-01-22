# 슬래시 커맨드
사내 메신저에서 사용할 수 있는 커맨드 기반 유틸리티 서비스입니다.\
개인 프로젝트로 기획, 개발하여 약 1년간 실제 사내에서 운영했습니다.\
서버는 AWS 무료 인스턴스 기반으로 구축했습니다.

## 커맨드 목록
1. [익명투표](#1-익명-투표)
2. [MBTI](#2-mbti)
3. [아키네이터](#3-아키네이터)

---

## 1. 익명 투표

사내 메신저의 기존 투표 기능은 **기명 투표**만 가능하여\
의견 표현에 부담이 있다는 문제가 있었습니다.\
이를 개선하기 위해 동일한 사용 흐름을 유지하면서\
**익명**으로 참여 가능한 투표 기능을 구현했습니다.

- 투표 생성 / 참여 / 결과 조회
- 종료시까지 투표 현황 공개여부 설정 가능

<img width="549" alt="image" src="https://github.com/user-attachments/assets/8efcb783-7072-4f12-b2dd-ff54353df1ea" />
<img width="550" alt="image" src="https://github.com/user-attachments/assets/4b4bc016-a9cc-4e28-89f3-d10dc6e2763a" />

## 2. MBTI

가볍게 즐길 수 있는 MBTI 설문 기능을 구현했습니다.

- 랜덤 문항 기반 간단 MBTI 검사
- 개인 결과 확인
- 전체 응답 기반 MBTI **통계** 제공

<img width="547" alt="image" src="https://github.com/user-attachments/assets/2695116e-0079-40e4-a22b-7f0b4c5633d3" />
<img width="549" alt="image" src="https://github.com/user-attachments/assets/f8056b9d-c7ce-4471-9387-5d7abb4c9996" />
<img width="548" alt="image" src="https://github.com/user-attachments/assets/556300cd-9f35-489d-ada3-9391fb25b733" />
<img width="539" alt="image" src="https://github.com/user-attachments/assets/2a766795-68d9-4793-80ba-59c747c79b67" />

## 3. 아키네이터

외부에서 제공되는 Akinator API를 활용하여\
사내 메신저 환경에서도 즐길 수 있도록 연동했습니다.

- 외부 REST API 연동
- 질문/응답 흐름을 메신저 UI에 맞게 재구성

<img width="556" alt="image" src="https://github.com/user-attachments/assets/26b115b0-002e-46b0-9a35-06b319ff19c1" />
<img width="545" alt="image" src="https://github.com/user-attachments/assets/9cd122c0-1efc-4766-a3e0-379b7a5ba149" />
<img width="548" alt="image" src="https://github.com/user-attachments/assets/30148ce4-ea67-4e3e-9ccb-23b91ab88560" />
