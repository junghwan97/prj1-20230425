package com.example.demo.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Board;
import com.example.demo.domain.Like;
import com.example.demo.mapper.BoardLikeMapper;
import com.example.demo.mapper.BoardMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Transactional(rollbackFor = Exception.class)
public class BoardService {

	@Autowired
	private S3Client s3;

	@Value("${aws.s3.bucketName}")
	private String bucketName;

	@Autowired
	private BoardMapper mapper;
	
	@Autowired
	private BoardLikeMapper likeMapper;

	public List<Board> listBoard() {
		List<Board> list = mapper.selectAll();
		return list;
	}

	public Board getBoard(Integer id, Authentication authentication) {
		Board board = mapper.selectById(id);
		
		// 현재 로그인한 사람이 이 게시물에 좋아요 했는지?
		if(authentication != null	) {
			Like like = likeMapper.select(id, authentication.getName());
			if(like != null) {
				board.setLiked(true);
			}
		}
		return board; 
	}

	public boolean modify(Board board, List<String> modifyFileNames, MultipartFile[] addFiles) throws Exception {

		// FileName 테이블 삭제
		if (modifyFileNames != null && !modifyFileNames.isEmpty()) {
			for (String fileName : modifyFileNames) {
				// s3에서 파일(객체) 삭제
				String objectKey = "board/" + board.getId() + "/" + fileName;
				DeleteObjectRequest dor = DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build();
				s3.deleteObject(dor);
				// 테이블에서 삭제
				mapper.deleteFileNameByBoardIdAndFileName(board.getId(), fileName);
			}
		}

		// 새 파일 추가
		for (MultipartFile newFile : addFiles) {
			if (newFile.getSize() > 0) {
				// 테이블에 파일명 추가
				mapper.insertFileName(board.getId(), newFile.getOriginalFilename());

				// s3에 파일(객체) 업로드
				String objectKey = "board/" + board.getId() + "/" + newFile.getOriginalFilename();
				PutObjectRequest por = PutObjectRequest.builder().acl(ObjectCannedACL.PUBLIC_READ).bucket(bucketName)
						.key(objectKey).build();

				RequestBody rb = RequestBody.fromInputStream(newFile.getInputStream(), newFile.getSize());
				s3.putObject(por, rb);
			}
		}

		// 게시물(board) 테이블 수정
		int cnt = mapper.update(board);
		return cnt == 1;

	}

	public boolean remove(Integer id) {

		// 좋아요 테이블 지우기
		likeMapper.deleteByBoardId(id);
		
		// 파일명 조회
		List<String> fileNames = mapper.selectFileNamesByBoardId(id);

		// FileName 테이블의 데이터 지우기
		mapper.deleteFileNameByBoardId(id);

		// s3 bucket의 파일 지우기
		for (String fileName : fileNames) {
			String objectKey = "board/" + id + "/" + fileName;
			DeleteObjectRequest dor = DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build();
			s3.deleteObject(dor);
		}

		// 게시물 테이블의 데이터 지우기
		int cnt = mapper.deleteById(id);
		return cnt == 1;
	}

	public boolean addBoard(Board board, MultipartFile[] files) throws Exception {
		// 게시물 insert
		int cnt = mapper.add(board);

		for (MultipartFile file : files) {
			if (file.getSize() > 0) {
				String objectKey = "board/" + board.getId() + "/" + file.getOriginalFilename();

				PutObjectRequest por = PutObjectRequest.builder().key(objectKey).acl(ObjectCannedACL.PUBLIC_READ)
						.bucket(bucketName).build();
				RequestBody rb = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

				s3.putObject(por, rb);
				// db에 관련 정보 저장(insert)
				mapper.insertFileName(board.getId(), file.getOriginalFilename());

				// 파일 저장(파일 시스템에)
				// 폴더 만들기
//				String folder = "C:\\sutdy\\upload\\" + board.getId();
//				File targetFolder = new File(folder);
//				if (!targetFolder.exists()) {
//					targetFolder.mkdirs();
//				}
//
//				String path = "C:\\sutdy\\upload\\" + board.getId() + "\\" + file.getOriginalFilename();
//				File target = new File(path);
//				file.transferTo(target);
//
			}
		}

		return cnt == 1;

	}

	public Map<String, Object> listBoard(Integer page, String search, String type) {
		// 페이지 당 행의 수
		Integer rowPerPage = 10;
		// 쿼리 LIMIT 절에 사용할 시작 인덱스
		Integer startIndex = (page - 1) * rowPerPage;

		// 페이지네이션이 필요한 정보
		// 전체 레코드 수
		Integer numOfRecords = mapper.countAll(search, type);

		// 맨처음 페이지
		Integer firstPageNum = 1;
		// 마지막 페이지 번호
		Integer lastPageNum = (numOfRecords - 1) / rowPerPage + 1;

		// 페이지네이션 왼쪽번호
		Integer leftPageNum = page - 5;
		// 1보다 작을 수 없음
		leftPageNum = Math.max(leftPageNum, 1);

		// 페이지네이션 오른쪽번호
		Integer rightPageNum = leftPageNum + 9;
		// 마지막페이지보다 클 수 없음
		rightPageNum = Math.min(rightPageNum, lastPageNum);

		// 현재 페이지
		Integer currentPageNum = page;

		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("rightPageNum", rightPageNum);
		pageInfo.put("leftPageNum", leftPageNum);
		pageInfo.put("currentPageNum", page);
		pageInfo.put("firstPageNum", firstPageNum);
		pageInfo.put("lastPageNum", lastPageNum);

		// 게시물 목록
		List<Board> list = mapper.selectAllPaging(startIndex, rowPerPage, search, type);
		return Map.of("pageInfo", pageInfo, "boardList", list);
	}

	public void removeByWriter(String writer) {
		List<Integer> idList = mapper.selectIdByWriter(writer);

		for (Integer id : idList) {
			remove(id);
		}
	}

	public Map<String, Object> like(Like like, Authentication authentication) {
		Map<String, Object> result = new HashMap<>();
		
		result.put("like", false);
		
		like.setMemberId(authentication.getName());
		Integer deleteCnt = likeMapper.delete(like);
		
		if (deleteCnt != 1) {
			Integer insertCnt = likeMapper.insert(like);
			result.put("like", true);
		}
		Integer count = likeMapper.countByBoardId(like.getBoardId());
		result.put("count", count);
		
		return result;
	}

	public Board getBoard(Integer id) {
		
		return getBoard(id, null);
	}
}

