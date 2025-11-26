package com.example.board.repository;

import com.example.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * QueryDSL을 사용한 커스텀 Repository 인터페이스
 * 복잡한 동적 쿼리를 위한 메서드 정의
 */
public interface PostRepositoryCustom {

    /**
     * 복잡한 검색 조건을 사용한 게시글 검색 (QueryDSL)
     * 모든 조건이 optional이며 null이 아닌 조건만 적용됨
     */
    Page<Post> searchPosts(
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
    );

    /**
     * 제목, 내용, 작성자로 동적 검색 (AND 조건)
     */
    Page<Post> searchByTitleContentAuthor(String title, String content, String author, Pageable pageable);

    /**
     * 날짜 범위로 검색
     */
    Page<Post> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 댓글이 있는 게시글만 조회
     */
    Page<Post> findPostsWithComments(Pageable pageable);

    /**
     * 댓글이 없는 게시글만 조회
     */
    Page<Post> findPostsWithoutComments(Pageable pageable);

    /**
     * 작성자별 게시글 수 통계
     */
    Map<String, Long> countPostsByAuthor();

    /**
     * 최근 N일 이내 작성된 게시글 조회
     */
    Page<Post> findRecentPosts(int days, Pageable pageable);

    /**
     * 인기 게시글 조회 (댓글 수 기준)
     */
    List<Post> findPopularPosts(int limit);

    /**
     * 제목 또는 내용에 키워드 포함 (OR 조건)
     */
    Page<Post> searchTitleOrContent(String keyword, Pageable pageable);

    /**
     * 특정 댓글 수 범위의 게시글 조회
     */
    Page<Post> findByCommentCountBetween(Integer minCount, Integer maxCount, Pageable pageable);

    /**
     * 여러 작성자의 게시글 조회 (IN 절)
     */
    Page<Post> findByAuthors(List<String> authors, Pageable pageable);
}
