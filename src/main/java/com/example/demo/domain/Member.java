package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Member {
	private String id;
	private String password;
	private String email;
	private String nickName;
	private LocalDateTime inserted;
	private List<String> authority;
}
