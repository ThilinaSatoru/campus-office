package nibm.project.campus_office;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "nibm.project.campus_office.entity")
@EnableJpaRepositories(basePackages = "nibm.project.campus_office.repository")
public class CampusOfficeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusOfficeApplication.class, args);
	}

}
