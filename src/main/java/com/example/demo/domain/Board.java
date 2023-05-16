package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Board {

	private Integer id;
	private String title;
	private String body;
	private LocalDateTime inserted;
	private String writer;
	private List<String> fileName;
	
	private Boolean liked;
	
	private Integer fileCount;
	private Integer likeCount;
}
