# PRD — Reddit-like Community Platform

## 1. 개요

### 1.1 프로젝트 목적
Reddit과 유사한 커뮤니티 기반 소셜 플랫폼을 개발한다.
사용자가 커뮤니티(서브레딧)를 생성하고, 게시글·댓글을 작성하며, 투표를 통해 콘텐츠의 가치를 평가할 수 있다.

### 1.2 기술 스택
| 구분 | 기술 |
|------|------|
| Backend | Spring Boot 4.0.3, Spring Data JPA, H2 (개발) / PostgreSQL (운영) |
| Frontend | Next.js (App Router), TypeScript, Tailwind CSS |
| 인증 | JWT (Access Token + Refresh Token) + OAuth2 (소셜 로그인, 별도 Phase) |
| API | REST |

### 1.3 프로젝트 구조
```
IdeaProjects/
├── irerinreminder/     # Spring Boot Backend
└── irerin-web/         # Next.js Frontend
```

---

## 2. 핵심 기능 (MVP)

### 2.1 사용자 (User)
| 기능 | 설명 |
|------|------|
| 회원가입 | 이메일, 사용자명, 비밀번호로 가입 |
| 로그인 / 로그아웃 | JWT 기반 인증 |
| 프로필 조회 | 작성한 게시글·댓글 목록, 카르마 점수 |
| 프로필 수정 | 아바타 이미지, 자기소개 |

### 2.2 커뮤니티 (Community, r/xxx 상당)
| 기능 | 설명 |
|------|------|
| 커뮤니티 생성 | 이름, 설명, 공개/비공개 설정 |
| 커뮤니티 가입/탈퇴 | 구독 개념 |
| 커뮤니티 목록 조회 | 인기순, 최신순 |
| 커뮤니티 상세 | 설명, 규칙, 멤버 수, 게시글 목록 |

### 2.3 게시글 (Post)
| 기능 | 설명 |
|------|------|
| 게시글 작성 | 텍스트 / 링크 타입 |
| 게시글 조회 | 단건 상세, 목록 (페이지네이션) |
| 게시글 수정·삭제 | 작성자 본인만 가능 |
| 게시글 투표 | 업보트 / 다운보트 (1인 1표) |
| 정렬 | Hot · New · Top (일간/주간/월간/전체) |

### 2.4 댓글 (Comment)
| 기능 | 설명 |
|------|------|
| 댓글 작성 | 게시글 및 댓글에 대한 답글 (중첩 구조) |
| 댓글 수정·삭제 | 작성자 본인만 가능 |
| 댓글 투표 | 업보트 / 다운보트 |
| 댓글 정렬 | Best · New · Old |

### 2.5 피드 (Feed)
| 기능 | 설명 |
|------|------|
| 홈 피드 | 가입한 커뮤니티의 게시글 모음 (비로그인: 인기 게시글) |
| 커뮤니티 피드 | 특정 커뮤니티의 게시글 목록 |
| 검색 | 게시글 제목 / 커뮤니티명 검색 |

---

## 3. 도메인 모델

### 3.1 엔티티 관계
```
User ─── (1:N) ─── Post
User ─── (1:N) ─── Comment
User ─── (M:N) ─── Community  (UserCommunity: 가입)
User ─── (1:N) ─── Vote

Community ─── (1:N) ─── Post

Post ─── (1:N) ─── Comment
Post ─── (1:N) ─── Vote

Comment ─── (1:N) ─── Comment  (self-reference: 대댓글)
Comment ─── (1:N) ─── Vote
```

### 3.2 주요 필드

**User**
- id, username (unique), email (unique), passwordHash
- bio, avatarUrl, karma
- createdAt

**Community**
- id, name (unique), description, rules
- isPrivate, memberCount
- createdBy (User), createdAt

**Post**
- id, title, content, url (링크 타입)
- type (TEXT / LINK)
- voteScore, commentCount
- author (User), community (Community)
- createdAt, updatedAt, isDeleted

**Comment**
- id, content
- voteScore
- author (User), post (Post), parent (Comment, nullable)
- depth (0~∞), createdAt, updatedAt, isDeleted

**Vote**
- id, value (+1 / -1)
- user (User), targetType (POST / COMMENT), targetId
- createdAt

---

## 4. API 설계 (REST)

### Auth
| Method | Path | 설명 |
|--------|------|------|
| POST | /api/auth/signup | 회원가입 |
| POST | /api/auth/login | 로그인 → JWT 반환 |
| POST | /api/auth/refresh | Access Token 갱신 |
| POST | /api/auth/logout | 로그아웃 |

### User
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/users/{username} | 프로필 조회 |
| PATCH | /api/users/me | 내 프로필 수정 |
| GET | /api/users/{username}/posts | 작성 게시글 목록 |
| GET | /api/users/{username}/comments | 작성 댓글 목록 |

### Community
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/communities | 커뮤니티 목록 |
| POST | /api/communities | 커뮤니티 생성 |
| GET | /api/communities/{name} | 커뮤니티 상세 |
| POST | /api/communities/{name}/join | 가입 |
| DELETE | /api/communities/{name}/join | 탈퇴 |

### Post
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/posts | 홈 피드 |
| GET | /api/communities/{name}/posts | 커뮤니티 게시글 목록 |
| POST | /api/communities/{name}/posts | 게시글 작성 |
| GET | /api/posts/{id} | 게시글 상세 |
| PATCH | /api/posts/{id} | 게시글 수정 |
| DELETE | /api/posts/{id} | 게시글 삭제 |
| POST | /api/posts/{id}/vote | 투표 |

### Comment
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/posts/{postId}/comments | 댓글 목록 (트리) |
| POST | /api/posts/{postId}/comments | 댓글 작성 |
| PATCH | /api/comments/{id} | 댓글 수정 |
| DELETE | /api/comments/{id} | 댓글 삭제 |
| POST | /api/comments/{id}/vote | 투표 |

### Search
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/search?q=&type= | 게시글·커뮤니티 통합 검색 |

---

## 5. UI/UX 설계 (Reddit 유사)

### 5.1 디자인 시스템

| 항목 | 값 |
|------|-----|
| 컬러 — 주요 | `#FF4500` (Reddit 오렌지) |
| 컬러 — 배경 | `#DAE0E6` (피드 배경), `#FFFFFF` (카드) |
| 컬러 — 텍스트 | `#1C1C1C` (본문), `#878A8C` (서브텍스트) |
| 컬러 — 업보트 | `#FF4500`, 다운보트: `#7193FF` |
| 폰트 | IBM Plex Sans / Noto Sans KR |
| 레이아웃 | 최대 너비 1200px, 중앙 정렬 |
| 테마 | 라이트 모드 (다크 모드 Phase 2) |

### 5.2 공통 레이아웃

```
┌─────────────────────────────────────────────────────┐
│  HEADER: 로고 | 검색바 | 로그인/회원가입 | 아바타    │
├─────────────────────────────────────────────────────┤
│                                                     │
│   MAIN CONTENT (740px)  │  SIDEBAR (312px)          │
│                         │                           │
└─────────────────────────────────────────────────────┘
```

**Header 구성 요소:**
- 좌측: 로고 (홈 링크)
- 중앙: 검색 바 (커뮤니티명 / 게시글 검색)
- 우측: 로그인 시 — 글쓰기 버튼, 알림, 아바타 드롭다운 / 비로그인 시 — 로그인, 회원가입 버튼

### 5.3 페이지별 상세 UI

#### 홈 피드 (`/`)
```
[Header]
──────────────────────────────────────────────
정렬 탭: [🔥 Hot] [✨ New] [📈 Top] [🚀 Rising]
──────────────────────────────────────────────
┌──────────────────────────────┐  ┌──────────┐
│ [▲] 1.2k  제목               │  │ 커뮤니티  │
│ [▼]       r/community • 3h   │  │ 생성 버튼 │
│           💬 234 댓글 | 공유  │  ├──────────┤
└──────────────────────────────┘  │ 인기      │
┌──────────────────────────────┐  │ 커뮤니티  │
│ ...                          │  │ Top 5    │
└──────────────────────────────┘  └──────────┘
```

**게시글 카드 (Post Card):**
- 좌측 투표 컬럼: 업보트 ▲ / 점수 / 다운보트 ▼ (세로 배치)
- 썸네일: 링크 타입일 경우 우측에 미리보기 이미지 (140×140)
- 메타: `r/{community}` · `u/{author}` · `{시간}` (예: 3 hours ago)
- 액션 바: 💬 Comments · 🔗 Share · ··· (더보기)
- 투표 색상: 업보트 시 주황, 다운보트 시 파란색으로 변경

#### 커뮤니티 상세 (`/r/{name}`)
```
[커뮤니티 배너 이미지 — 전체 너비]
[아이콘] r/{name}  [Join 버튼]
──────────────────────────────────────────────
정렬 탭 | 게시글 카드 목록     │  커뮤니티 정보
                               │  설명
                               │  멤버 수: X
                               │  생성일: YYYY
                               │  ─────────────
                               │  [글 작성하기]
                               │  ─────────────
                               │  커뮤니티 규칙
```

#### 게시글 상세 (`/r/{name}/posts/{id}`)
```
[← Back to r/{name}]
┌─────────────────────────────────────────────┐
│ [▲] 제목                                    │
│ [▼] r/{name} · u/{author} · 3h             │
│     본문 내용 (마크다운 렌더링)              │
│     ──────────────────────────────           │
│     💬 Share ··· Save Report                │
└─────────────────────────────────────────────┘

[댓글 작성 박스 — 로그인 시]
정렬: [Best ▼]

  ┌─ u/user1 · 2h  ▲ 45 ▼
  │  댓글 내용
  │  Reply Share
  │
  └─── u/user2 · 1h  ▲ 12 ▼
       대댓글 내용 (들여쓰기)
       Reply Share
```

#### 게시글 작성 (`/r/{name}/submit`)
```
┌──────────────────────────────────────────┐
│  탭: [📝 Post] [🔗 Link]                  │
│  ─────────────────────────────────────── │
│  제목 입력 (최대 300자)                   │
│  ─────────────────────────────────────── │
│  본문 입력 (리치 텍스트 에디터)           │
│  B I S | 링크 | 목록 | 코드블록           │
│  ─────────────────────────────────────── │
│  [취소]                    [게시하기 →]   │
└──────────────────────────────────────────┘
```

#### 프로필 (`/u/{username}`)
```
[배너]
[아바타] u/{username}
카르마: 🏆 1,234  가입일: Jan 2024
─────────────────────────────────────────
탭: [게시글] [댓글] [저장됨(본인만)]
─────────────────────────────────────────
게시글/댓글 카드 목록
```

#### 로그인 / 회원가입 — 모달
- 페이지 이동 없이 오버레이 모달로 표시 (Reddit 방식)
- 로그인: 이메일/비밀번호, 소셜 로그인 UI (기능은 Phase 2.5)
- 회원가입: 이메일 → 사용자명 → 비밀번호 단계별 입력

### 5.4 컴포넌트 목록

| 컴포넌트 | 설명 |
|----------|------|
| `PostCard` | 피드용 게시글 카드 (투표 포함) |
| `VoteButton` | ▲▼ 투표 버튼 + 점수 |
| `CommentTree` | 중첩 댓글 재귀 렌더링 |
| `CommentInput` | 댓글/대댓글 작성 폼 |
| `CommunityCard` | 커뮤니티 정보 사이드바 |
| `SortTabs` | Hot/New/Top 정렬 탭 |
| `AuthModal` | 로그인/회원가입 모달 |
| `Header` | 글로벌 네비게이션 바 |
| `UserAvatar` | 아바타 + 드롭다운 메뉴 |
| `RichTextEditor` | 게시글 본문 에디터 |

### 5.5 페이지 라우팅

| 페이지 | 경로 | 설명 |
|--------|------|------|
| 홈 | `/` | 피드 (인기 게시글) |
| 커뮤니티 상세 | `/r/{name}` | 게시글 목록 |
| 게시글 상세 | `/r/{name}/posts/{id}` | 댓글 포함 |
| 게시글 작성 | `/r/{name}/submit` | |
| 커뮤니티 목록 | `/communities` | |
| 프로필 | `/u/{username}` | |
| 검색 | `/search?q=` | |
| 로그인 | `/login` | 모달 우선, fallback 페이지 |
| 회원가입 | `/signup` | 모달 우선, fallback 페이지 |

---

## 6. 비기능 요구사항

| 항목 | 내용 |
|------|------|
| 인증 | JWT (Access: 15분, Refresh: 7일) |
| 페이지네이션 | Cursor 기반 (무한 스크롤) |
| 데이터베이스 | 개발: H2 인메모리, 운영: PostgreSQL |
| CORS | Next.js 개발 서버(3000) 허용 |
| 응답 형식 | 공통 응답 래퍼 `{ success, data, error }` |

---

## 7. 개발 우선순위

### Phase 1 — 기반
- [ ] User 인증 (회원가입, 로그인, JWT)
- [ ] Community CRUD
- [ ] Post CRUD + 투표
- [ ] Comment CRUD + 투표
- [ ] 홈 피드 / 커뮤니티 피드

### Phase 2 — 품질
- [ ] Hot 알고리즘 (Reddit 방식 스코어링)
- [ ] 무한 스크롤 (Cursor 페이지네이션)
- [ ] 검색
- [ ] 프로필 페이지

### Phase 3 — 고도화
- [ ] OAuth2 소셜 로그인 (Google, Kakao)
- [ ] 이미지 업로드 (게시글)
- [ ] 커뮤니티 운영자(Moderator) 권한
- [ ] 알림 시스템
- [ ] PostgreSQL 전환
