package cotato.csquiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "login")
public class LoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long id;

    @Column(name = "login_email")
    @Email
    private String email;

    @Column(name = "login_password")
    private String encryptedPassword;

    @Column(name = "login_name")
    private String name;

    @Column(name = "access_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "OUTSTANDING")
    private AccessStatus accessStatus;
}
