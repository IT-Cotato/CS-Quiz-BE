package cotato.csquiz.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "login")
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long id;

    @Column(name = "login_email", unique = true, nullable = false)
    @Email
    private String email;

    @Column(name = "login_password", nullable = false)
    private String encryptedPassword;

    @Column(name = "login_name")
    private String name;

    @Column(name = "login_phone_num", unique = true, nullable = false)
    private String phoneNum;

    @Column(name = "access_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'OUTSTANDING'")
    private AccessStatus accessStatus;

    @Builder
    public LoginInfo(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.encryptedPassword = password;
        this.name = name;
        this.phoneNum = phoneNumber;
    }
}
