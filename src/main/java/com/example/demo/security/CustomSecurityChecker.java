package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.demo.domain.Board;
import com.example.demo.mapper.BoardMapper;

@Component
public class CustomSecurityChecker {

	@Autowired
	private BoardMapper mapper;
	
	public boolean checkBoardWriter(Authentication authentication, Integer boardId) {
		Board board = mapper.selectById(boardId);
		
		String username = authentication.getName();
		String writer = board.getWriter();
		
		return username.equals(writer);
	}
}
