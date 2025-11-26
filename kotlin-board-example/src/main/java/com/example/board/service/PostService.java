package com.example.board.service;

import com.example.board.dto.PostDto.*;
import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public PostListResponse getPosts(Pageable pageable) {
        Page<Post> page = postRepository.findAll(pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 게시글 상세 조회
     */
    public PostDetailResponse getPost(Long id) {
        Post post = postRepository.findByIdWithComments(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id: " + id));

        return PostDetailResponse.from(post);
    }

    /**
     * 게시글 검색
     */
    public PostListResponse searchPosts(String keyword, Pageable pageable) {
        Page<Post> page = postRepository.searchByKeyword(keyword, pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        Post post = request.toEntity();
        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse updatePost(Long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id: " + id));

        // Dirty Checking을 통한 업데이트
        post.update(request.getTitle(), request.getContent());

        return PostResponse.from(post);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다. id: " + id);
        }

        postRepository.deleteById(id);
    }

    /**
     * 복잡한 검색 조건을 사용한 게시글 검색
     * QueryDSL 활용 - 모든 파라미터가 optional
     */
    public PostListResponse searchPosts(
            String titleContains,
            String contentContains,
            String authorEquals,
            List<String> authorsIn,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            LocalDateTime updatedAfter,
            LocalDateTime updatedBefore,
            Integer minCommentCount,
            Integer maxCommentCount,
            String keyword,
            Pageable pageable
    ) {
        // QueryDSL을 사용한 동적 쿼리 실행
        Page<Post> page = postRepository.searchPosts(
                titleContains, contentContains, authorEquals, authorsIn,
                createdAfter, createdBefore, updatedAfter, updatedBefore,
                minCommentCount, maxCommentCount, keyword, pageable
        );

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 고급 검색 - QueryDSL 동적 쿼리
     * 제목, 내용, 작성자를 개별적으로 검색하고 AND 조건으로 결합
     */
    public PostListResponse advancedSearch(String title, String content, String author, Pageable pageable) {
        // QueryDSL을 사용한 동적 쿼리 실행
        Page<Post> page = postRepository.searchByTitleContentAuthor(title, content, author, pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 날짜 범위로 검색
     * QueryDSL을 사용한 날짜 범위 쿼리
     */
    public PostListResponse searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // QueryDSL을 사용한 날짜 범위 검색
        Page<Post> page = postRepository.searchByDateRange(startDate, endDate, pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 댓글이 있는 게시글만 조회
     * QueryDSL EXISTS 서브쿼리 사용
     */
    public PostListResponse getPostsWithComments(Pageable pageable) {
        Page<Post> page = postRepository.findPostsWithComments(pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 댓글이 없는 게시글만 조회
     * QueryDSL NOT EXISTS 서브쿼리 사용
     */
    public PostListResponse getPostsWithoutComments(Pageable pageable) {
        Page<Post> page = postRepository.findPostsWithoutComments(pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 작성자별 게시글 통계
     * QueryDSL GROUP BY 또는 Stream API 활용
     */
    public Map<String, Long> getPostCountByAuthor() {
        return postRepository.countPostsByAuthor();
    }

    /**
     * 최근 N일 이내 작성된 게시글 조회
     * QueryDSL 날짜 계산 활용
     */
    public PostListResponse getRecentPosts(int days, Pageable pageable) {
        Page<Post> page = postRepository.findRecentPosts(days, pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /**
     * 인기 게시글 조회 (댓글 수 기준)
     * QueryDSL LEFT JOIN과 GROUP BY 활용
     */
    public List<PostResponse> getPopularPosts(int limit) {
        List<Post> posts = postRepository.findPopularPosts(limit);

        return posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 복잡한 OR 조건 검색
     * QueryDSL OR 조건 활용
     */
    public PostListResponse searchTitleOrContent(String keyword, Pageable pageable) {
        Page<Post> page = postRepository.searchTitleOrContent(keyword, pageable);

        return new PostListResponse(
                page.getContent().stream()
                        .map(PostResponse::from)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}
