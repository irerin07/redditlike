# 코딩 관례 (Coding Conventions)

## 패키지 구조

도메인 중심으로 패키지를 구성한다.

```
irerin.ai.reminder
├── global
│   ├── config        # CORS 등 설정
│   ├── controller    # 공통 컨트롤러 (health 등)
│   ├── exception     # CustomException, ErrorCode, GlobalExceptionHandler
│   └── response      # ApiResponse
└── {domain}          # ex) user, reminder
    ├── entity
    ├── repository
    ├── service
    └── controller
```

## 엔티티

- `@Entity` + `@Table(name = "...")` 명시 — H2 예약어 충돌 방지 (`user` → `users`)
- `@NoArgsConstructor(access = PROTECTED)` — JPA 기본 생성자, 외부 직접 생성 차단
- 생성자에 `@Builder` 부착 — 클래스 레벨 `@Builder` 사용 금지
- 필수 파라미터에 `@NonNull` — 애플리케이션 레벨 null 검사 강제
- `@Builder.Default` 대신 생성자 파라미터로 기본값 처리
- `createdAt` 등 Auditing 필드는 생성자 파라미터에서 제외 (`@CreatedDate`로 자동 주입)
- `@EnableJpaAuditing`은 `global/config/JpaAuditingConfig`에 선언 (`@WebMvcTest` 충돌 방지)

```java
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User {
    // ...

    @Builder
    private User(@NonNull String username, @NonNull String email, @NonNull String passwordHash,
                 String bio, String avatarUrl, int karma) { ... }
}
```

## 예외 처리

- 비즈니스 예외는 `CustomException(ErrorCode)` 사용
- `ErrorCode` enum에 HTTP 상태코드와 메시지 정의
- 전역 처리는 `GlobalExceptionHandler`(`@RestControllerAdvice`)에서 담당

```java
throw new CustomException(ErrorCode.NOT_FOUND);
```

## API 응답

모든 응답은 `ApiResponse<T>` 래퍼로 반환한다.

```java
ApiResponse.success(data)   // 200 성공 (데이터 있음)
ApiResponse.success()       // 200 성공 (데이터 없음)
ApiResponse.error(message)  // 오류
```

### 컨트롤러 응답 관례

- 모든 컨트롤러 메서드는 `ResponseEntity<ApiResponse<T>>`를 반환한다
- 성공 응답은 `ResponseEntity.ok(ApiResponse.success(data))`
- 오류 응답은 `GlobalExceptionHandler`가 `ResponseEntity` + 적절한 HTTP 상태코드로 처리

## 테스트

### 레이어별 전략

| 레이어 | 사용 어노테이션 | 비고 |
|--------|----------------|------|
| 엔티티 | 없음 (순수 JUnit5) | Spring/JPA 컨텍스트 사용 금지 |
| 레포지토리 | `@DataJpaTest` | `@Import(JpaAuditingConfig.class)` 포함 |
| 서비스 | `@ExtendWith(MockitoExtension.class)` | 의존성 Mocking |
| 컨트롤러 | `@WebMvcTest` (Spring Boot 4: `org.springframework.boot.webmvc.test.autoconfigure`) | |

### 작성 규칙

- 기능을 추가하거나 수정할 때 검증 테스트를 항상 함께 작성한다
- `@Nested` + `@DisplayName`으로 테스트를 계층적으로 구성한다
- Assertion은 AssertJ(`assertThat`, `assertThatThrownBy`)를 사용한다
- 테스트 메서드명은 camelCase 동사로 작성한다 (`nullUsername`, `createWithRequiredFieldsOnly`)

```java
@Nested
@DisplayName("필수값 누락")
class RequiredFieldValidation {

    @Test
    @DisplayName("username이 null이면 NullPointerException이 발생한다")
    void nullUsername() {
        assertThatThrownBy(() -> User.builder().username(null)...build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("username");
    }
}
```
