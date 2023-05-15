<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>글쓰기</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>

	<my:navbar current="add" />
	<my:alert></my:alert>

	<div class="container-lg">
		<div class="row justify-content-center">
			<div class="col-12 col-md-8 col-lg-6">
				<h1>글쓰기</h1>
				<form action="" method="post" enctype="multipart/form-data">

					<div class="mb-3">
						<label for="titleInput" class="form-label">제목 : </label> <input id="titleInput" type="form-control" name="title" value="${board.title }" />
					</div>

<!-- 					<div class="mb-3"> -->
<!-- 									label.form-label -->
<%-- 						<label for="writerInput" class="form-label">작성자 : </label> <input id="writerInput" type="form-control" name="writer" value="<sec:authentication property="name"/>" readonly/> --%>
<!-- 					</div> -->

					<div class="mb-3">
						<label for="bodyInput" class="from-label">본문</label> <br />
						<textarea id="bodyInput" name="body" cols="80" rows="10"\>${board.body }</textarea>
					</div>

					<div class="mb-3">
						<label for="fileInput" class="form-label">그림 파일</label> 
						<input class="form-control" type="file" id="fileInput" name="files" multiple accept="image/*">
						<div class="form-text">
							총 10MB, 하나의 파일은 1MB 크기를 초과할 수 없습니다. 
						</div>
					</div>

					<div class="mb-3">
						<input class="btn btn-primary" type="submit" value="올리기" />
					</div>

				</form>
			</div>
		</div>
	</div>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.4/jquery.min.js" integrity="sha512-pumBsjNRGGqkPzKHndZMaAG+bir374sORyzM3uulLV14lN5LyykqNk8eEeUlUkB3U0M4FApyaHraT65ihJhDpQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
</body>
</html>