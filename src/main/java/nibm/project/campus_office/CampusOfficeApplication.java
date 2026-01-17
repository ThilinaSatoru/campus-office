package nibm.project.campus_office;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "nibm.project.campus_office.entity")
public class CampusOfficeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusOfficeApplication.class, args);
	}

}
