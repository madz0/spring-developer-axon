package com.github.madz0.springdeveloperaxon;

import org.axonframework.test.server.AxonServerContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpringDeveloperAxonApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringDeveloperAxonApplication::main).with(TestSpringDeveloperAxonApplication.class).run(args);
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}

	@Bean
	@ServiceConnection
	AxonServerContainer axonServerContainer() {
		return new AxonServerContainer(DockerImageName.parse("axoniq/axonserver:latest-dev"));
	}

}
