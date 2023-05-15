package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.domain.Board;
import com.example.demo.domain.Like;

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
			SELECT 
				b.id,
				b.title,
				b.body,
				b.inserted,
				b.writer,
				f.fileName
			FROM Board b LEFT JOIN FileNames f ON b.id = f.boardId
			WHERE b.id = #{id}
			""")
	@ResultMap("boardResultMap")
	Board selectById(Integer id);

	
	@Update("""
			UPDATE Board
			SET
			 	title = #{title},
			 	body = #{body}
			WHERE
				id = #{id}
			""")
	int update(Board board);
	
	@Delete("""
			DELETE FROM Board
			WHERE id = #{id}
			""")
	int deleteById(Integer id);

	@Insert("""
			INSERT INTO Board(title, body, writer)
			VALUES(#{title}, #{body}, #{writer})
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
//	 @Options 어노테이션은 MyBatis에서 제공하는 옵션을 설정할 수 있도록 도와줍니다. 
//	 useGeneratedKeys 옵션을 true로 설정하면, 자동 생성된 키 값을 사용하여 레코드를 추가할 수 있습니다. 
// 	 keyProperty 옵션은 자동 생성된 키 값을 Board 객체의 id 프로퍼티에 저장합니다.
	int add(Board board);
	
	
	@Select("""
			<script>
			<bind name="pattern" value="'%' + search + '%'" />
			SELECT
				b.id,
				b.title,
				b.writer,
				b.inserted,
				COUNT(f.id) fileCount
			FROM Board b LEFT JOIN FileNames f ON b.id = f.boardId
			<where>
				<if test="(type eq 'all') or (type eq 'title')">
				   title  LIKE #{pattern}
				</if>
				<if test="(type eq 'all') or (type eq 'body')">
				OR body   LIKE #{pattern}
				</if>
				<if test="(type eq 'all') or (type eq 'writer')">
				OR writer LIKE #{pattern}
				</if>
			</where>
		   	GROUP BY b.id
			ORDER BY b.id DESC
			LIMIT #{startIndex}, #{rowPerPage}
			</script>
			""")
	List<Board> selectAllPaging(Integer startIndex, Integer rowPerPage, String search, String type);

	
	@Select("""
			<script>
			<bind name="pattern" value="'%' + search + '%'" />
			SELECT COUNT(*)
			FROM Board
			<where>
			
			<if test="type == 'all'">
				title LIKE #{pattern}	
			 OR body LIKE #{pattern}
		   	 OR writer LIKE #{pattern}
			</if>
			
			<if test="type == 'title'">
				title LIKE #{pattern}	
			</if>						
			
			<if test="type == 'body'">
			 OR body LIKE #{pattern}
			</if>
			
			<if test="type == 'writer'">
		   	 OR writer LIKE #{pattern}			
			</if>
		   	</where> 
		   	 </script>
			""")
	Integer countAll(String search, String type);

	@Insert("""
			INSERT INTO FileNames(boardId, fileName)
			VALUES(#{boardId}, #{fileName})
			""")
	Integer insertFileName(Integer boardId, String fileName);


	@Select("""
			SELECT fileName
			FROM Board b LEFT JOIN FileNames f ON f.boardId = b.id
			WHERE b.id = #{id}
			""")
	List<String> selectFileNamesByBoardId(Integer id);
	
	@Delete("""
			DELETE FROM FileNames 
			WHERE boardId = #{boardId}
			""")
	int deleteFileNameByBoardId(Integer boardId);

	
	@Delete("""
			DELETE FROM FileNames
			WHERE boardId = #{boardId}
				AND fileName = #{fileName}
			""")
	void deleteFileNameByBoardIdAndFileName(Integer boardId, String fileName);

	
	@Select("""
			SELECT id
			FROM Board
			WHERE writer = #{writer}
			""")
	List<Integer> selectIdByWriter(String writer);

	
}
