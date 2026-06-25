package cl.municipalidad.restricciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class RestriccionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestriccionesApplication.class, args);
	}

}
