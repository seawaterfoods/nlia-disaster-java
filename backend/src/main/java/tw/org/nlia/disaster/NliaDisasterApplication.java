package tw.org.nlia.disaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NliaDisasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(NliaDisasterApplication.class, args);
    }
}
