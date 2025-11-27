package com.example.board.controller;

import com.example.board.dto.PostDto.*;
import com.example.board.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 게시글 목록 조회
     * GET /api/posts?page=0&size=10&sortBy=createdAt&direction=DESC
     */
    @GetMapping
    public ResponseEntity<PostListResponse> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);
        PostListResponse response = postService.getPosts(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {
        PostDetailResponse response = postService.getPost(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 검색
     * GET /api/posts/search?keyword=검색어&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<PostListResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostListResponse response = postService.searchPosts(keyword, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 생성
     * POST /api/posts
     * Body: { "title": "제목", "content": "내용", "author": "작성자" }
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정
     * PUT /api/posts/{id}
     * Body: { "title": "제목", "content": "내용" }
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        PostResponse response = postService.updatePost(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제
     * DELETE /api/posts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // ========== 복잡한 검색 API 시작 ==========

    /**
     * 고급 검색 - 복잡한 조건을 사용한 검색 (QueryDSL)
     * GET /api/posts/advanced-search?titleContains=제목&contentContains=내용&author=작성자&createdAfter=2024-01-01T00:00:00&createdBefore=2024-12-31T23:59:59&minCommentCount=5
     *
     * 모든 파라미터는 optional이며, null이 아닌 조건만 검색에 적용됨
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<PostListResponse> advancedSearch(
            @RequestParam(required = false) String titleContains,
            @RequestParam(required = false) String contentContains,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) List<String> authors,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedBefore,
            @RequestParam(required = false) Integer minCommentCount,
            @RequestParam(required = false) Integer maxCommentCount,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);

        // QueryDSL을 사용한 동적 쿼리 실행
        PostListResponse response = postService.searchPosts(
                titleContains, contentContains, author, authors,
                createdAfter, createdBefore, updatedAfter, updatedBefore,
                minCommentCount, maxCommentCount, keyword, pageable
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 간단한 고급 검색 - 제목, 내용, 작성자 조합
     * GET /api/posts/simple-advanced-search?title=제목&content=내용&author=작성자
     */
    @GetMapping("/simple-advanced-search")
    public ResponseEntity<PostListResponse> simpleAdvancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostListResponse response = postService.advancedSearch(title, content, author, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 날짜 범위로 검색
     * GET /api/posts/search-by-date?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
     */
    @GetMapping("/search-by-date")
    public ResponseEntity<PostListResponse> searchByDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostListResponse response = postService.searchByDateRange(startDate, endDate, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글이 있는 게시글만 조회
     * GET /api/posts/with-comments
     */
    @GetMapping("/with-comments")
    public ResponseEntity<PostListResponse> getPostsWithComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostListResponse response = postService.getPostsWithComments(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글이 없는 게시글만 조회
     * GET /api/posts/without-comments
     */
    @GetMapping("/without-comments")
    public ResponseEntity<PostListResponse> getPostsWithoutComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostListResponse response = postService.getPostsWithoutComments(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 작성자별 게시글 통계
     * GET /api/posts/statistics/by-author
     *
     * Response: { "작성자1": 10, "작성자2": 5, ... }
     */
    @GetMapping("/statistics/by-author")
    public ResponseEntity<Map<String, Long>> getPostCountByAuthor() {
        Map<String, Long> statistics = postService.getPostCountByAuthor();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 최근 N일 이내 작성된 게시글 조회
     * GET /api/posts/recent?days=7
     */
    @GetMapping("/recent")
    public ResponseEntity<PostListResponse> getRecentPosts(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        PostListResponse response = postService.getRecentPosts(days, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 인기 게시글 조회 (댓글 수 기준)
     * GET /api/posts/popular?limit=10
     */
    @GetMapping("/popular")
    public ResponseEntity<List<PostResponse>> getPopularPosts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PostResponse> response = postService.getPopularPosts(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * 제목 또는 내용 검색 (OR 조건)
     * GET /api/posts/search-title-or-content?keyword=검색어
     */
    @GetMapping("/search-title-or-content")
    public ResponseEntity<PostListResponse> searchTitleOrContent(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PostListResponse response = postService.searchTitleOrContent(keyword, pageable);

        return ResponseEntity.ok(response);
    }
}
