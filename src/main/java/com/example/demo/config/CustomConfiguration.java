package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableMethodSecurity
public class CustomConfiguration {

	@Value("${aws.accessKeyId}")
	private String accessKey;
	
	@Value("${aws.secretAccessKey}")
	private String secretKey;
	
	@Value("${aws.bucketUrl}")
	private String bucketUrl;
	
	@Autowired
	private ServletContext application;
	
	@PostConstruct
	public void inita() {
		application.setAttribute("bucketUrl", bucketUrl);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		
//		http.formLogin(Customizer.withDefaults());
		http.formLogin().loginPage("/member/login");
		http.logout().logoutUrl("/member/logout");
		
//		http.authorizeHttpRequests().requestMatchers("/add").authenticated();
//		http.authorizeHttpRequests().requestMatchers("/member/signup").anonymous();
//		http.authorizeHttpRequests().requestMatchers("/**").permitAll();

//		http.authorizeHttpRequests()
//			.requestMatchers("/add")
//			.access(new WebExpressionAuthorizationManager("isAuthenticated()"));
//		
//		http.authorizeHttpRequests()
//			.requestMatchers("/member/signup")
//			.access(new WebExpressionAuthorizationManager("isAnonymous()"));
//		
//		http.authorizeHttpRequests()
//			.requestMatchers("/**")
//			.access(new WebExpressionAuthorizationManager("permitAll"));
		
		
		
		
		return http.build();
	}
	
	@Bean
	public S3Client s3client() {
		
		AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
		
		S3Client s3client = S3Client.builder()
				.credentialsProvider(provider)
				.region(Region.AP_NORTHEAST_2)
				.build();
		
		return s3client;
	}
}
