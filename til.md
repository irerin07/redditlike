# TIL (Today I Learned)

---

## 2026-03-17

### Spring Boot 4 — `@WebMvcTest` 패키지 이동

**문제:** `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`를 import하면 컴파일 에러 발생.

**원인:** Spring Boot 4에서 `spring-boot-test-autoconfigure` JAR에서 웹 MVC 관련 테스트 슬라이스가 제거됐다.

**해결:** 패키지 경로 변경.

```java
// Spring Boot 3 (구)
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

// Spring Boot 4 (신)
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
```

---

### Spring Boot 4 — 테스트 슬라이스 의존성 분리

**문제:** `spring-boot-starter-test`만 추가해도 `@WebMvcTest`를 찾지 못함.

**원인:** Spring Boot 4부터 테스트 슬라이스가 도메인별 artifact로 분리됐다.

| 테스트 슬라이스 | 필요한 artifact |
|----------------|----------------|
| `@WebMvcTest` | `spring-boot-starter-webmvc-test` |
| `@DataJpaTest` | `spring-boot-starter-data-jpa-test` |

**해결:** `build.gradle`에 명시적으로 추가.

```groovy
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
```

---

### `@EnableJpaAuditing`을 `@SpringBootApplication` 클래스에 두면 `@WebMvcTest`가 깨진다

**문제:** `@WebMvcTest` 실행 시 `IllegalArgumentException: JPA metamodel must not be empty` 발생.

**원인:** `@WebMvcTest`는 `@SpringBootConfiguration`(= `@SpringBootApplication`)을 스캔해 컨텍스트를 구성한다.
메인 클래스에 `@EnableJpaAuditing`이 있으면 JPA Auditing 초기화가 시도되는데,
`@WebMvcTest`는 JPA 컨텍스트를 띄우지 않으므로 JPA 메타모델이 비어 있어 예외가 난다.

**해결:** `@EnableJpaAuditing`을 별도 `@Configuration` 클래스로 분리한다.
`@WebMvcTest`는 `@Configuration`을 자동 포함하지 않으므로 충돌이 사라진다.
`@DataJpaTest`에서는 `@Import(JpaAuditingConfig.class)`로 명시적으로 포함시킨다.

```java
// global/config/JpaAuditingConfig.java
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {}
```

```java
// IrerinreminderApplication.java
@SpringBootApplication  // @EnableJpaAuditing 제거
public class IrerinreminderApplication { ... }
```

```java
// 레포지토리 테스트
@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserRepositoryTest { ... }
```
