# Development Plan — Reddit-like Community Platform

## 기술 스택 상세

### Backend (`irerinreminder/`)
| 항목 | 기술 | 비고 |
|------|------|------|
| Framework | Spring Boot 4.0.3 | Java 21 |
| ORM | Spring Data JPA + Hibernate 7 | |
| Database | H2 (개발) → PostgreSQL (운영) | |
| 인증 | Spring Security + JWT | Access 15분, Refresh 7일 |
| 빌드 | Gradle 9 (Groovy DSL) | |
| 설정 | application.yml | |
| 테스트 | JUnit 5 + Spring Boot Test | |
| 유틸 | Lombok | |

### Frontend (`irerin-web/`)
| 항목 | 기술 | 비고 |
|------|------|------|
| Framework | Next.js (App Router) | TypeScript |
| 스타일 | Tailwind CSS v4 | Reddit 오렌지 `#FF4500` |
| 상태 관리 | Zustand | 전역 상태 (인증, 테마 등) |
| 서버 상태 | TanStack Query v5 | API 캐싱 + 무한 스크롤 |
| 폼 | React Hook Form + Zod | 유효성 검사 |
| 에디터 | TipTap | 게시글 리치 텍스트 |
| HTTP | Axios | JWT 인터셉터 |
| 테스트 | Jest + React Testing Library | |

### 패키지 구조

```
# Backend
irerin.ai.reminder/
├── auth/           # JWT 필터, Security 설정
├── user/           # User 엔티티, 서비스, 컨트롤러
├── community/      # Community
├── post/           # Post
├── comment/        # Comment
├── vote/           # Vote
└── global/         # 공통 응답, 예외 처리, 설정

# Frontend
irerin-web/
├── app/
│   ├── (auth)/       # login, signup
│   ├── r/[name]/     # 커뮤니티
│   │   ├── page.tsx  # 커뮤니티 피드
│   │   ├── submit/   # 글 작성
│   │   └── posts/[id]/
│   ├── u/[username]/ # 프로필
│   ├── search/
│   └── communities/
├── components/
│   ├── post/         # PostCard, VoteButton
│   ├── comment/      # CommentTree, CommentInput
│   ├── community/    # CommunityCard
│   ├── layout/       # Header, Sidebar
│   └── ui/           # 공통 UI (Button, Modal, Avatar)
├── lib/
│   ├── api/          # Axios 인스턴스, API 함수
│   └── auth/         # JWT 유틸
└── store/            # Zustand 스토어
```

---

## Phase 1 — 프로젝트 기반 세팅

> 목표: 두 프로젝트가 통신하는 최소 골격 완성

### Backend
- [ ] `application.yml` H2 + JPA 설정 완료
- [ ] 공통 응답 래퍼 `ApiResponse<T>` 구현
- [ ] 글로벌 예외 핸들러 (`@RestControllerAdvice`)
- [ ] CORS 설정 (localhost:3000 허용)
- [ ] Health check endpoint `GET /api/health`

### Frontend
- [ ] Next.js 프로젝트 생성 (`irerin-web/`)
  ```bash
  npx create-next-app@latest irerin-web --typescript --tailwind --app
  ```
- [ ] Tailwind 컬러 토큰 설정 (Reddit 오렌지 `#FF4500` 등)
- [ ] Axios 인스턴스 생성 (`lib/api/client.ts`)
- [ ] Header 컴포넌트 (로고, 로그인/회원가입 버튼)
- [ ] 기본 2컬럼 레이아웃 (메인 740px + 사이드바 312px)
- [ ] `/api/health` 연결 확인

### 완료 기준
- 브라우저에서 Header가 보이고 백엔드 응답을 받을 수 있다

---

## Phase 2 — 사용자 인증

> 목표: 회원가입 → 로그인 → JWT 발급 → 인증된 요청

### Backend
- [ ] `User` 엔티티 + `UserRepository`
- [ ] 비밀번호 BCrypt 해싱
- [ ] `POST /api/auth/signup` — 회원가입
- [ ] `POST /api/auth/login` — 로그인 (Access + Refresh Token 반환)
- [ ] `POST /api/auth/refresh` — Access Token 재발급
- [ ] `POST /api/auth/logout` — Refresh Token 무효화
- [ ] Spring Security JWT 필터 체인 구성
- [ ] `GET /api/users/me` — 내 정보 조회

### Frontend
- [ ] AuthModal 컴포넌트 (오버레이 모달)
  - 로그인 폼 (이메일 + 비밀번호)
  - 회원가입 폼 (이메일 → 사용자명 → 비밀번호)
- [ ] Zustand 인증 스토어 (`store/authStore.ts`)
- [ ] Axios JWT 인터셉터 (Access Token 자동 첨부 + 만료 시 Refresh)
- [ ] 로그인 후 Header — 아바타 + 드롭다운 메뉴 표시
- [ ] React Hook Form + Zod 폼 유효성 검사

### 완료 기준
- 회원가입 → 로그인 → 아바타 표시 → 로그아웃 흐름 동작

---

## Phase 2.5 — OAuth2 소셜 로그인

> 목표: Google/Kakao 소셜 로그인 연동

### Backend
- [ ] `User` 엔티티에 `provider` (AuthProvider enum), `providerId` 필드 추가
- [ ] `CustomOAuth2UserService` — 소셜 계정 연동 또는 신규 가입
- [ ] `OAuth2AuthenticationSuccessHandler` — JWT 발급 후 `/oauth2/callback?token=xxx` 리다이렉트
- [ ] `OAuth2AuthenticationFailureHandler`
- [ ] `SecurityConfig` `.oauth2Login()` 활성화
- [ ] `application.yml` OAuth2 클라이언트 설정

### Frontend
- [ ] AuthModal에 소셜 로그인 버튼 (Google, Kakao)
- [ ] `/oauth2/callback` 페이지 — 토큰 파싱 → authStore 저장 → 홈 리다이렉트

### 완료 기준
- Google 로그인 버튼 클릭 → 인증 → 아바타 표시

---

## Phase 3 — 커뮤니티

> 목표: 커뮤니티 생성, 조회, 가입/탈퇴

### Backend
- [ ] `Community` 엔티티 + `UserCommunity` (M:N 조인 테이블)
- [ ] `POST /api/communities` — 생성 (인증 필요)
- [ ] `GET /api/communities` — 목록 (인기순/최신순)
- [ ] `GET /api/communities/{name}` — 상세
- [ ] `POST /api/communities/{name}/join` — 가입
- [ ] `DELETE /api/communities/{name}/join` — 탈퇴

### Frontend
- [ ] `/communities` — 커뮤니티 목록 페이지
- [ ] `/r/{name}` — 커뮤니티 상세 레이아웃
  - 배너, 아이콘, 커뮤니티명
  - Join/Leave 버튼
  - 우측 사이드바: 설명, 멤버 수, 생성일
- [ ] 커뮤니티 생성 모달
- [ ] TanStack Query로 커뮤니티 데이터 캐싱

### 완료 기준
- 커뮤니티 생성 → 목록 조회 → 상세 진입 → 가입/탈퇴 동작

---

## Phase 4 — 게시글

> 목표: 게시글 CRUD + 투표 + 피드

### Backend
- [ ] `Post` 엔티티 (TEXT / LINK 타입)
- [ ] `Vote` 엔티티
- [ ] `POST /api/communities/{name}/posts` — 작성
- [ ] `GET /api/communities/{name}/posts` — 목록 (정렬: hot/new/top)
- [ ] `GET /api/posts` — 홈 피드
- [ ] `GET /api/posts/{id}` — 상세
- [ ] `PATCH /api/posts/{id}` — 수정 (작성자만)
- [ ] `DELETE /api/posts/{id}` — 삭제 (작성자만)
- [ ] `POST /api/posts/{id}/vote` — 투표 (+1/-1, 토글)
- [ ] Hot 스코어 계산 로직
  ```
  score = log10(max(voteScore, 1)) + (createdAt - epoch) / 45000
  ```

### Frontend
- [ ] `PostCard` 컴포넌트
  - 좌측 세로 투표 컬럼 (▲ 점수 ▼)
  - 투표 시 색상 변경 (업보트: 오렌지, 다운보트: 파란색)
  - 메타 정보: r/{community} · u/{author} · {시간}
  - 액션 바: 💬 댓글 수 | 🔗 Share
- [ ] `VoteButton` 컴포넌트 (낙관적 업데이트)
- [ ] `SortTabs` 컴포넌트 (Hot / New / Top)
- [ ] 홈 피드 (`/`) — PostCard 목록
- [ ] 게시글 작성 페이지 (`/r/{name}/submit`)
  - 텍스트 / 링크 탭 전환
  - TipTap 에디터
- [ ] 게시글 상세 페이지 (`/r/{name}/posts/{id}`)

### 완료 기준
- 게시글 작성 → 피드에 노출 → 업/다운보트 → 점수 반영

---

## Phase 5 — 댓글

> 목표: 중첩 댓글 CRUD + 투표

### Backend
- [ ] `Comment` 엔티티 (self-reference, depth 필드)
- [ ] `POST /api/posts/{postId}/comments` — 댓글/대댓글 작성
- [ ] `GET /api/posts/{postId}/comments` — 트리 구조 반환
- [ ] `PATCH /api/comments/{id}` — 수정
- [ ] `DELETE /api/comments/{id}` — 삭제 (내용만 제거, isDeleted=true)
- [ ] `POST /api/comments/{id}/vote` — 투표
- [ ] 댓글 수 자동 업데이트 (Post.commentCount)

### Frontend
- [ ] `CommentTree` 컴포넌트 (재귀 렌더링, 들여쓰기)
- [ ] `CommentInput` 컴포넌트 (작성 + 취소)
- [ ] Reply 버튼 클릭 시 인라인 입력 폼 표시
- [ ] 댓글 정렬 탭 (Best / New / Old)
- [ ] 삭제된 댓글: `[deleted]` 표시 (자식 댓글은 유지)
- [ ] 게시글 상세 페이지에 댓글 섹션 통합

### 완료 기준
- 댓글 작성 → 대댓글 작성 (3단계 이상) → 투표 → 삭제 동작

---

## Phase 6 — 피드 고도화 + 검색 + 프로필

> 목표: 완성도 높은 사용자 경험

### Backend
- [ ] Cursor 기반 페이지네이션 (무한 스크롤용)
- [ ] `GET /api/search?q=&type=post|community` — 검색
- [ ] `GET /api/users/{username}` — 프로필 조회
- [ ] `GET /api/users/{username}/posts` — 작성 게시글
- [ ] `GET /api/users/{username}/comments` — 작성 댓글
- [ ] `PATCH /api/users/me` — 프로필 수정 (bio, avatarUrl)
- [ ] 카르마 계산 (받은 업보트 합산)

### Frontend
- [ ] 무한 스크롤 (TanStack Query `useInfiniteQuery`)
- [ ] 검색 페이지 (`/search`) — 게시글 / 커뮤니티 탭
- [ ] Header 검색바 자동완성
- [ ] 프로필 페이지 (`/u/{username}`)
  - 배너, 아바타, 카르마, 가입일
  - 게시글 / 댓글 탭 전환
- [ ] 상대 시간 표시 (`3 hours ago` 형식)

### 완료 기준
- 무한 스크롤 동작 · 검색 결과 표시 · 프로필 페이지 완성

---

## Phase 7 — 마무리 및 품질

> 목표: 안정성, UX 완성도

### Backend
- [ ] 입력값 유효성 검사 (`@Valid`, `@NotBlank` 등)
- [ ] 에러 응답 표준화 (에러 코드 + 메시지)
- [ ] 주요 API 통합 테스트 작성
- [ ] `spring.jpa.open-in-view=false` 설정 + N+1 쿼리 점검

### Frontend
- [ ] 로딩 스켈레톤 UI (PostCard, CommentTree)
- [ ] 에러 바운더리 + 에러 토스트 알림
- [ ] 반응형 대응 (모바일 — 사이드바 숨김)
- [ ] 다크 모드 토글 (Tailwind `dark:` 클래스)
- [ ] SEO: Next.js `generateMetadata` (게시글/커뮤니티 페이지)
- [ ] 접근성: 키보드 투표, aria 레이블

### 완료 기준
- 주요 기능 에러 없이 동작 · 모바일 레이아웃 정상 표시

---

## 개발 순서 요약

```
Phase 1  기반 세팅        (백엔드 골격 + Next.js 설치 + 레이아웃)
Phase 2  사용자 인증      (JWT + 로그인/회원가입 모달)
Phase 3  커뮤니티         (생성/조회/가입)
Phase 4  게시글           (CRUD + 투표 + 피드)
Phase 5  댓글             (중첩 구조 + 투표)
Phase 6  고도화           (무한 스크롤 + 검색 + 프로필)
Phase 7  품질             (테스트 + 반응형 + 다크모드 + SEO)
```
