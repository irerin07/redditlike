# Tasks

## Phase 1 — 프로젝트 기반 세팅

### Backend
- [ ] `application.yml` H2 + JPA 설정 확인
- [ ] 공통 응답 래퍼 `ApiResponse<T>` 클래스 작성 (`global/response/ApiResponse.java`)
- [ ] 글로벌 예외 핸들러 작성 (`global/exception/GlobalExceptionHandler.java`)
- [ ] 커스텀 예외 클래스 작성 (`global/exception/CustomException.java`, `ErrorCode.java`)
- [ ] CORS 설정 (`global/config/CorsConfig.java`, localhost:3000 허용)
- [ ] Health check 컨트롤러 (`GET /api/health`)

### Frontend
- [ ] Next.js 프로젝트 생성 (`irerin-web/`, TypeScript + Tailwind + App Router)
- [ ] Tailwind 컬러 토큰 설정 (`primary: #FF4500`, 배경, 서브텍스트 등)
- [ ] Axios 인스턴스 생성 (`lib/api/client.ts`)
- [ ] `Header` 컴포넌트 — 로고, 로그인/회원가입 버튼 (`components/layout/Header.tsx`)
- [ ] 루트 레이아웃 — 2컬럼 구조 (메인 740px + 사이드바 312px) (`app/layout.tsx`)
- [ ] `/api/health` 연결 확인 (브라우저 콘솔)

---

## Phase 2 — 사용자 인증

### Backend
- [ ] `User` 엔티티 작성 (`user/entity/User.java`)
- [ ] `UserRepository` 작성 (`user/repository/UserRepository.java`)
- [ ] 비밀번호 BCrypt 해싱 설정 (`PasswordEncoder` Bean)
- [ ] `POST /api/auth/signup` — 회원가입 API
- [ ] `POST /api/auth/login` — 로그인 API (Access Token + Refresh Token 반환)
- [ ] `POST /api/auth/refresh` — Access Token 재발급 API
- [ ] `POST /api/auth/logout` — Refresh Token 무효화 API
- [ ] JWT 유틸 클래스 작성 (`auth/util/JwtUtil.java`)
- [ ] JWT 인증 필터 작성 (`auth/filter/JwtAuthenticationFilter.java`)
- [ ] Spring Security 필터 체인 구성 (`auth/config/SecurityConfig.java`)
- [ ] `GET /api/users/me` — 내 정보 조회 API

### Frontend
- [ ] `AuthModal` 컴포넌트 — 오버레이 모달 (`components/ui/AuthModal.tsx`)
- [ ] 로그인 폼 — 이메일 + 비밀번호 (React Hook Form + Zod)
- [ ] 회원가입 폼 — 이메일 → 사용자명 → 비밀번호 단계별 입력
- [ ] Zustand 인증 스토어 (`store/authStore.ts`) — user, token, isLoggedIn
- [ ] Axios JWT 인터셉터 — Access Token 자동 첨부, 만료 시 Refresh 후 재시도
- [ ] 로그인 후 Header — `UserAvatar` + 드롭다운 메뉴 (프로필, 로그아웃)
- [ ] `UserAvatar` 컴포넌트 (`components/ui/UserAvatar.tsx`)

---

## Phase 3 — 커뮤니티

### Backend
- [ ] `Community` 엔티티 작성 (`community/entity/Community.java`)
- [ ] `UserCommunity` 조인 엔티티 작성 (M:N, `community/entity/UserCommunity.java`)
- [ ] `CommunityRepository` 작성
- [ ] `POST /api/communities` — 커뮤니티 생성 API (인증 필요)
- [ ] `GET /api/communities` — 목록 API (인기순/최신순 정렬)
- [ ] `GET /api/communities/{name}` — 상세 API
- [ ] `POST /api/communities/{name}/join` — 가입 API
- [ ] `DELETE /api/communities/{name}/join` — 탈퇴 API
- [ ] `CommunityService` 비즈니스 로직 작성
- [ ] `CommunityController` 작성

### Frontend
- [ ] `/communities` 페이지 — 커뮤니티 목록 (`app/communities/page.tsx`)
- [ ] `CommunityCard` 컴포넌트 (`components/community/CommunityCard.tsx`)
- [ ] `/r/[name]` 레이아웃 — 배너, 아이콘, 커뮤니티명 (`app/r/[name]/layout.tsx`)
- [ ] Join / Leave 버튼 (인증 상태에 따라 표시)
- [ ] 커뮤니티 사이드바 — 설명, 멤버 수, 생성일, 글쓰기 버튼
- [ ] 커뮤니티 생성 모달 (`components/community/CreateCommunityModal.tsx`)
- [ ] TanStack Query — 커뮤니티 데이터 캐싱 및 낙관적 업데이트 (가입/탈퇴)

---

## Phase 4 — 게시글

### Backend
- [ ] `Post` 엔티티 작성 (`post/entity/Post.java`, TEXT / LINK 타입)
- [ ] `Vote` 엔티티 작성 (`vote/entity/Vote.java`)
- [ ] `PostRepository`, `VoteRepository` 작성
- [ ] `POST /api/communities/{name}/posts` — 게시글 작성 API
- [ ] `GET /api/communities/{name}/posts` — 커뮤니티 게시글 목록 API (hot/new/top 정렬)
- [ ] `GET /api/posts` — 홈 피드 API
- [ ] `GET /api/posts/{id}` — 게시글 상세 API
- [ ] `PATCH /api/posts/{id}` — 게시글 수정 API (작성자만)
- [ ] `DELETE /api/posts/{id}` — 게시글 삭제 API (작성자만)
- [ ] `POST /api/posts/{id}/vote` — 투표 API (+1/-1, 동일 값 재투표 시 취소)
- [ ] Hot 스코어 계산 로직 구현
- [ ] `PostService`, `PostController` 작성

### Frontend
- [ ] `PostCard` 컴포넌트 (`components/post/PostCard.tsx`)
  - [ ] 좌측 세로 투표 컬럼 (▲ 점수 ▼)
  - [ ] 투표 색상 변경 (업보트: `#FF4500`, 다운보트: `#7193FF`)
  - [ ] 메타 정보: r/{community} · u/{author} · 상대 시간
  - [ ] 액션 바: 💬 댓글 수 · 🔗 Share
- [ ] `VoteButton` 컴포넌트 — 낙관적 업데이트 (`components/post/VoteButton.tsx`)
- [ ] `SortTabs` 컴포넌트 — Hot / New / Top (`components/post/SortTabs.tsx`)
- [ ] 홈 피드 페이지 (`app/page.tsx`) — PostCard 목록
- [ ] `/r/[name]` 피드 페이지 (`app/r/[name]/page.tsx`)
- [ ] 게시글 작성 페이지 (`app/r/[name]/submit/page.tsx`)
  - [ ] 텍스트 / 링크 탭 전환
  - [ ] TipTap 에디터 통합
- [ ] 게시글 상세 페이지 (`app/r/[name]/posts/[id]/page.tsx`)

---

## Phase 5 — 댓글

### Backend
- [ ] `Comment` 엔티티 작성 (`comment/entity/Comment.java`, self-reference + depth)
- [ ] `CommentRepository` 작성
- [ ] `POST /api/posts/{postId}/comments` — 댓글/대댓글 작성 API
- [ ] `GET /api/posts/{postId}/comments` — 트리 구조 반환 API
- [ ] `PATCH /api/comments/{id}` — 댓글 수정 API (작성자만)
- [ ] `DELETE /api/comments/{id}` — 소프트 삭제 API (`isDeleted=true`, 내용 제거)
- [ ] `POST /api/comments/{id}/vote` — 댓글 투표 API
- [ ] 댓글 작성/삭제 시 `Post.commentCount` 자동 업데이트
- [ ] `CommentService`, `CommentController` 작성

### Frontend
- [ ] `CommentTree` 컴포넌트 — 재귀 렌더링, 들여쓰기 (`components/comment/CommentTree.tsx`)
- [ ] `CommentItem` 컴포넌트 — 단일 댓글 (투표, 작성자, 시간, Reply 버튼)
- [ ] `CommentInput` 컴포넌트 — 작성 폼 + 취소 (`components/comment/CommentInput.tsx`)
- [ ] Reply 버튼 클릭 시 해당 댓글 아래 인라인 입력 폼 표시
- [ ] 댓글 정렬 탭 — Best / New / Old
- [ ] 삭제된 댓글 `[deleted]` 표시 (자식 댓글은 유지)
- [ ] 게시글 상세 페이지에 댓글 섹션 통합

---

## Phase 6 — 피드 고도화 + 검색 + 프로필

### Backend
- [ ] Cursor 기반 페이지네이션 공통 구현 (`global/util/CursorPageable.java`)
- [ ] 홈 피드 / 커뮤니티 피드 API에 Cursor 페이지네이션 적용
- [ ] `GET /api/search?q=&type=post|community` — 통합 검색 API
- [ ] `GET /api/users/{username}` — 프로필 조회 API
- [ ] `GET /api/users/{username}/posts` — 작성 게시글 목록 API
- [ ] `GET /api/users/{username}/comments` — 작성 댓글 목록 API
- [ ] `PATCH /api/users/me` — 프로필 수정 API (bio, avatarUrl)
- [ ] 카르마 계산 로직 (받은 업보트 합산)

### Frontend
- [ ] 무한 스크롤 — TanStack Query `useInfiniteQuery` 적용 (피드 전체)
- [ ] 검색 페이지 (`app/search/page.tsx`) — 게시글 / 커뮤니티 탭
- [ ] Header 검색바 입력 시 자동완성 드롭다운
- [ ] 프로필 페이지 (`app/u/[username]/page.tsx`)
  - [ ] 배너, 아바타, 카르마, 가입일
  - [ ] 게시글 / 댓글 탭 전환
- [ ] 상대 시간 유틸 함수 (`lib/utils/formatDate.ts`, `3 hours ago` 형식)

---

## Phase 7 — 마무리 및 품질

### Backend
- [ ] 모든 Request DTO에 `@Valid` + `@NotBlank` 등 유효성 검사 추가
- [ ] 에러 코드 enum 정리 및 에러 응답 표준화
- [ ] 주요 API 통합 테스트 작성 (Auth, Post, Comment)
- [ ] `spring.jpa.open-in-view=false` 설정
- [ ] N+1 쿼리 점검 및 `@EntityGraph` / `fetch join` 최적화

### Frontend
- [ ] 로딩 스켈레톤 UI — `PostCard`, `CommentTree` (`components/ui/Skeleton.tsx`)
- [ ] 에러 바운더리 설정 (`app/error.tsx`)
- [ ] 에러 토스트 알림 컴포넌트 (`components/ui/Toast.tsx`)
- [ ] 반응형 레이아웃 — 모바일(768px 이하) 사이드바 숨김
- [ ] 다크 모드 토글 — Tailwind `dark:` 클래스 + Zustand 테마 스토어
- [ ] SEO — `generateMetadata` 적용 (게시글 상세, 커뮤니티 페이지)
- [ ] 접근성 — 투표 버튼 키보드 동작, `aria-label` 추가
