package nibm.project.campus_office;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Theme(value = "campus")
@PWA(
		name = "Campus Office",
		shortName = "Campus",
		iconPath = "campus.png"
)
@SpringBootApplication
@EntityScan(basePackages = "nibm.project.campus_office.entity")
@EnableJpaRepositories(basePackages = "nibm.project.campus_office.repository")
public class CampusOfficeApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(CampusOfficeApplication.class, args);
	}

}
