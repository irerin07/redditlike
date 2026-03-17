package irerin.ai.reminder.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    // ───────────────────────────────────────────────
    // 정상 생성
    // ───────────────────────────────────────────────

    @Nested
    @DisplayName("정상 생성")
    class ValidCreation {

        @Test
        @DisplayName("필수값만으로 User를 생성할 수 있다")
        void createWithRequiredFieldsOnly() {
            User user = User.builder()
                    .username("alice")
                    .email("alice@example.com")
                    .passwordHash("$2a$10$hash")
                    .build();

            assertThat(user.getUsername()).isEqualTo("alice");
            assertThat(user.getEmail()).isEqualTo("alice@example.com");
            assertThat(user.getPasswordHash()).isEqualTo("$2a$10$hash");
        }

        @Test
        @DisplayName("선택값(bio, avatarUrl, karma)을 포함해 User를 생성할 수 있다")
        void createWithAllFields() {
            User user = User.builder()
                    .username("bob")
                    .email("bob@example.com")
                    .passwordHash("$2a$10$hash")
                    .bio("안녕하세요")
                    .avatarUrl("https://example.com/avatar.png")
                    .karma(10)
                    .build();

            assertThat(user.getBio()).isEqualTo("안녕하세요");
            assertThat(user.getAvatarUrl()).isEqualTo("https://example.com/avatar.png");
            assertThat(user.getKarma()).isEqualTo(10);
        }

        @Test
        @DisplayName("선택값을 지정하지 않으면 bio, avatarUrl은 null, karma는 0이다")
        void optionalFieldDefaults() {
            User user = User.builder()
                    .username("carol")
                    .email("carol@example.com")
                    .passwordHash("$2a$10$hash")
                    .build();

            assertThat(user.getBio()).isNull();
            assertThat(user.getAvatarUrl()).isNull();
            assertThat(user.getKarma()).isZero();
        }

        @Test
        @DisplayName("생성 직후 id, createdAt은 null이다 (DB 저장 전)")
        void idAndCreatedAtAreNullBeforePersist() {
            User user = User.builder()
                    .username("dave")
                    .email("dave@example.com")
                    .passwordHash("$2a$10$hash")
                    .build();

            assertThat(user.getId()).isNull();
            assertThat(user.getCreatedAt()).isNull();
        }
    }

    // ───────────────────────────────────────────────
    // 필수값 누락 → NullPointerException
    // ───────────────────────────────────────────────

    @Nested
    @DisplayName("필수값 누락")
    class RequiredFieldValidation {

        @Test
        @DisplayName("username이 null이면 NullPointerException이 발생한다")
        void nullUsername() {
            assertThatThrownBy(() -> User.builder()
                    .username(null)
                    .email("alice@example.com")
                    .passwordHash("$2a$10$hash")
                    .build()
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("username");
        }

        @Test
        @DisplayName("email이 null이면 NullPointerException이 발생한다")
        void nullEmail() {
            assertThatThrownBy(() -> User.builder()
                    .username("alice")
                    .email(null)
                    .passwordHash("$2a$10$hash")
                    .build()
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("passwordHash가 null이면 NullPointerException이 발생한다")
        void nullPasswordHash() {
            assertThatThrownBy(() -> User.builder()
                    .username("alice")
                    .email("alice@example.com")
                    .passwordHash(null)
                    .build()
            ).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("passwordHash");
        }
    }
}
