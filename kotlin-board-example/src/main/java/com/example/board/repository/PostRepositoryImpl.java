package com.example.board.repository;

import com.example.board.entity.Post;
import com.example.board.entity.QPost;
import com.example.board.entity.QComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QueryDSL을 사용한 복잡한 동적 쿼리 구현
 * JPAQueryFactory를 활용한 타입 안전한 쿼리 빌더
 */
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPost post = QPost.post;
    private final QComment comment = QComment.comment;

    public PostRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 복잡한 검색 조건을 사용한 게시글 검색
     * BooleanBuilder를 활용한 동적 쿼리 생성
     * null인 파라미터는 조건에서 제외됨
     */
    @Override
    public Page<Post> searchPosts(
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
        // 동적 조건 빌더
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 제목 검색 (LIKE)
        if (titleContains != null && !titleContains.trim().isEmpty()) {
            builder.and(post.title.containsIgnoreCase(titleContains));
        }

        // 2. 내용 검색 (LIKE)
        if (contentContains != null && !contentContains.trim().isEmpty()) {
            builder.and(post.content.containsIgnoreCase(contentContains));
        }

        // 3. 작성자 정확히 일치
        if (authorEquals != null && !authorEquals.trim().isEmpty()) {
            builder.and(post.author.eq(authorEquals));
        }

        // 4. 작성자 목록 중 하나 (IN 절)
        if (authorsIn != null && !authorsIn.isEmpty()) {
            builder.and(post.author.in(authorsIn));
        }

        // 5. 생성일 범위
        if (createdAfter != null) {
            builder.and(post.createdAt.goe(createdAfter));
        }

        if (createdBefore != null) {
            builder.and(post.createdAt.loe(createdBefore));
        }

        // 6. 수정일 범위
        if (updatedAfter != null) {
            builder.and(post.updatedAt.goe(updatedAfter));
        }

        if (updatedBefore != null) {
            builder.and(post.updatedAt.loe(updatedBefore));
        }

        // 7. 키워드 검색 (제목 OR 내용)
        if (keyword != null && !keyword.trim().isEmpty()) {
            builder.and(
                post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword))
            );
        }

        // 8. 댓글 수 범위 (Subquery 사용)
        if (minCommentCount != null || maxCommentCount != null) {
            // 댓글 수를 계산하는 서브쿼리
            JPQLQuery<Long> commentCountSubquery = JPAExpressions
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(post.id));

            if (minCommentCount != null) {
                builder.and(commentCountSubquery.goe(minCommentCount.longValue()));
            }

            if (maxCommentCount != null) {
                builder.and(commentCountSubquery.loe(maxCommentCount.longValue()));
            }
        }

        // 쿼리 실행 (기본 정렬: 최신순)
        List<Post> content = queryFactory
            .selectFrom(post)
            .where(builder)
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 전체 개수 조회
        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 제목, 내용, 작성자로 동적 검색
     * BooleanExpression을 활용한 null-safe 조건 생성
     */
    @Override
    public Page<Post> searchByTitleContentAuthor(String title, String content, String author, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // null이 아닌 경우에만 조건 추가
        if (title != null && !title.trim().isEmpty()) {
            builder.and(titleContains(title));
        }

        if (content != null && !content.trim().isEmpty()) {
            builder.and(contentContains(content));
        }

        if (author != null && !author.trim().isEmpty()) {
            builder.and(authorEquals(author));
        }

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(builder)
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    /**
     * 날짜 범위로 검색
     */
    @Override
    public Page<Post> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (startDate != null) {
            builder.and(post.createdAt.goe(startDate));
        }

        if (endDate != null) {
            builder.and(post.createdAt.loe(endDate));
        }

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(builder)
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    /**
     * 댓글이 있는 게시글만 조회
     * EXISTS 서브쿼리 사용
     */
    @Override
    public Page<Post> findPostsWithComments(Pageable pageable) {
        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(
                JPAExpressions
                    .selectFrom(comment)
                    .where(comment.post.id.eq(post.id))
                    .exists()
            )
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(
                JPAExpressions
                    .selectFrom(comment)
                    .where(comment.post.id.eq(post.id))
                    .exists()
            )
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    /**
     * 댓글이 없는 게시글만 조회
     * NOT EXISTS 서브쿼리 사용
     */
    @Override
    public Page<Post> findPostsWithoutComments(Pageable pageable) {
        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(
                JPAExpressions
                    .selectFrom(comment)
                    .where(comment.post.id.eq(post.id))
                    .notExists()
            )
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(
                JPAExpressions
                    .selectFrom(comment)
                    .where(comment.post.id.eq(post.id))
                    .notExists()
            )
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    /**
     * 작성자별 게시글 수 통계
     * GROUP BY와 Stream API 조합
     */
    @Override
    public Map<String, Long> countPostsByAuthor() {
        List<Post> posts = queryFactory
            .selectFrom(post)
            .fetch();

        // Stream API로 그룹핑
        return posts.stream()
            .collect(Collectors.groupingBy(
                Post::getAuthor,
                Collectors.counting()
            ));
    }

    /**
     * 최근 N일 이내 작성된 게시글 조회
     */
    @Override
    public Page<Post> findRecentPosts(int days, Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.createdAt.goe(startDate))
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(post.createdAt.goe(startDate))
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    /**
     * 인기 게시글 조회 (댓글 수 기준)
     * LEFT JOIN과 GROUP BY, HAVING, ORDER BY 사용
     */
    @Override
    public List<Post> findPopularPosts(int limit) {
        return queryFactory
            .selectFrom(post)
            .leftJoin(post.comments, comment)
            .groupBy(post.id)
            .orderBy(comment.count().desc(), post.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    /**
     * 제목 또는 내용에 키워드 포함 (OR 조건)
     */
    @Override
    public Page<Post> searchTitleOrContent(String keyword, Pageable pageable) {
        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(
                post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword))
            )
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(
                post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword))
            )
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    /**
     * 특정 댓글 수 범위의 게시글 조회
     * HAVING 절 사용
     */
    @Override
    public Page<Post> findByCommentCountBetween(Integer minCount, Integer maxCount, Pageable pageable) {
        JPAQuery<Post> query = queryFactory
            .selectFrom(post)
            .leftJoin(post.comments, comment)
            .groupBy(post.id);

        if (minCount != null) {
            query.having(comment.count().goe(minCount.longValue()));
        }

        if (maxCount != null) {
            query.having(comment.count().loe(maxCount.longValue()));
        }

        List<Post> posts = query
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // Total count 계산
        JPAQuery<Long> countQuery = queryFactory
            .select(post.count())
            .from(post)
            .leftJoin(post.comments, comment)
            .groupBy(post.id);

        if (minCount != null) {
            countQuery.having(comment.count().goe(minCount.longValue()));
        }

        if (maxCount != null) {
            countQuery.having(comment.count().loe(maxCount.longValue()));
        }

        Long total = (long) countQuery.fetch().size();

        return new PageImpl<>(posts, pageable, total);
    }

    /**
     * 여러 작성자의 게시글 조회 (IN 절)
     */
    @Override
    public Page<Post> findByAuthors(List<String> authors, Pageable pageable) {
        if (authors == null || authors.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0L);
        }

        List<Post> posts = queryFactory
            .selectFrom(post)
            .where(post.author.in(authors))
            .orderBy(post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(post.count())
            .from(post)
            .where(post.author.in(authors))
            .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }

    // ========== Private Helper Methods ==========

    /**
     * 제목 포함 조건
     */
    private BooleanExpression titleContains(String title) {
        return title != null ? post.title.containsIgnoreCase(title) : null;
    }

    /**
     * 내용 포함 조건
     */
    private BooleanExpression contentContains(String content) {
        return content != null ? post.content.containsIgnoreCase(content) : null;
    }

    /**
     * 작성자 일치 조건
     */
    private BooleanExpression authorEquals(String author) {
        return author != null ? post.author.eq(author) : null;
    }
}
