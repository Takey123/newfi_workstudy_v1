package com.newfi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@MapperScan("com.newfi.mapper")
@SpringBootApplication
public class NewfiWorkstudyV1Application  extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NewfiWorkstudyV1Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(NewfiWorkstudyV1Application.class, args);
	}
}
