package com.example.demo.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.Like;

@Mapper
public interface BoardLikeMapper {

	@Insert("""
			INSERT INTO BoardLike
			VALUES(#{boardId}, #{memberId})
			""")
	Integer insert(Like like);

	@Delete("""
			DELETE FROM BoardLike
			WHERE boardId = #{boardId}
			  AND memberId = #{memberId}
			""")
	Integer delete(Like like);
}
