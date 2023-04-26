package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.domain.Board;

@Mapper
public interface BoardMapper {

	@Select("""
			SELECT 
				id,
				title,
				writer,
				inserted
			FROM Board
			ORDER BY id DESC
			""")
	List<Board> selectAll();

	@Select("""
			SELECT *
			FROM Board
			WHERE ID = #{id}
			""")
	Board selectById(Integer id);

	
	@Update("""
			UPDATE Board
			SET
			 	title = #{title},
			 	body = #{body},
			 	writer = #{writer}
			WHERE
				id = #{id}
			""")
	int update(Board board);

	
	@Delete("""
			DELETE FROM Board
			WHERE id = #{id}
			""")
	int deleteById(Integer id);
}
