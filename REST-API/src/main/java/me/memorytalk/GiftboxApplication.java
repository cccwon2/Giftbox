package me.memorytalk;

import com.google.common.base.Predicate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication
@EnableSwagger2
public class GiftboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftboxApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public Docket giftboxApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("0.api-full")
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("me.memorytalk.controller"))
				.paths(PathSelectors.any())
				.build();
	}

	@Bean
	public Docket adminApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("admin-api")
				.apiInfo(apiInfo())
				.select()
				.paths(adminPaths())
				.build();
	}

	@Bean
	public Docket bannerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("banner-api")
				.apiInfo(apiInfo())
				.select()
				.paths(bannerPaths())
				.build();
	}

	@Bean
	public Docket eventApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("event-api")
				.apiInfo(apiInfo())
				.select()
				.paths(eventPaths())
				.build();
	}

	@Bean
	public Docket popupApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("popup-api")
				.apiInfo(apiInfo())
				.select()
				.paths(popupPaths())
				.build();
	}

	private Predicate<String> adminPaths() {
		return regex("/admin.*");
	}

	private Predicate<String> bannerPaths() {
		return regex("/banner.*");
	}

	private Predicate<String> eventPaths() {
		return regex("/event.*");
	}

	private Predicate<String> popupPaths() {
		return regex("/popup.*");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("GiftBox REST API")
				.description("GiftBox REST API 시뮬레이터")
						//.termsOfServiceUrl("http://springfox.io")
						//.contact("springfox")
						//.license("Apache License Version 2.0")
						//.licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
				.version("1.0")
				.build();
	}
}
