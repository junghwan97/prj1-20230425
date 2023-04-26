package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Board;
import com.example.demo.mapper.BoardMapper;

@Service
public class BoardService {

	@Autowired
	private BoardMapper mapper;
	
	public List<Board> listBoard(){
		List<Board> list = mapper.selectAll();
		return list;
	}

	public Board getBoard(Integer id) {
		return mapper.selectById(id);
	}

	public boolean modify(Board board) {
		int cnt = mapper.update(board);
		
		return cnt == 1;
		
	}

	public boolean remove(Integer id) {
		int cnt = mapper.deleteById(id);
		return cnt == 1;
	}
}
