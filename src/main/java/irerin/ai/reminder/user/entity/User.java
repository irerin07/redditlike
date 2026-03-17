package irerin.ai.reminder.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(length = 500)
    private String bio;

    private String avatarUrl;

    private int karma;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private User(@NonNull String username, @NonNull String email, @NonNull String passwordHash,
                 String bio, String avatarUrl, int karma) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.karma = karma;
    }
}
