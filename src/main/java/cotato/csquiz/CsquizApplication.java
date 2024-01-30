package cotato.csquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CsquizApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsquizApplication.class, args);
    }

}
