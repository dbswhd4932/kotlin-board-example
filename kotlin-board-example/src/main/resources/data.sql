-- 테스트 데이터 초기화 스크립트
-- H2 데이터베이스 시작 시 자동 실행됨

-- 게시글 데이터 (5개)
INSERT INTO posts (id, title, content, author, created_at, updated_at) VALUES
    (1, 'Kotlin 학습 가이드', 'Kotlin을 배우는 방법에 대한 포스트입니다.', '김개발', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Spring Boot와 Kotlin', 'Spring Boot에서 Kotlin을 사용하는 방법', '이코틀린', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Coroutines 완벽 가이드', 'Kotlin Coroutines를 마스터하는 방법', '박비동기', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'QueryDSL 활용법', 'QueryDSL로 타입 안전한 쿼리 작성하기', '최쿼리', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'JPA Entity 설계', 'JPA Entity를 Kotlin으로 설계하는 방법', '정엔티티', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 댓글 데이터 (게시글 1번에 10개)
INSERT INTO comments (id, content, author, created_at, updated_at, post_id) VALUES
    (1, '유익한 글 감사합니다!', '댓글러1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (2, '도움이 많이 되었어요', '댓글러2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (3, '궁금한 점이 있습니다', '댓글러3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (4, '잘 읽었습니다', '댓글러4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (5, '좋은 정보 감사합니다', '댓글러5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (6, '추가 질문이 있어요', '댓글러6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (7, '완벽한 설명이네요', '댓글러7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (8, '다음 편도 기대됩니다', '댓글러8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (9, '공유 감사합니다', '댓글러9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    (10, '북마크 했어요', '댓글러10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- 다른 게시글에도 댓글 추가
INSERT INTO comments (id, content, author, created_at, updated_at, post_id) VALUES
    (11, 'Spring Boot 짱!', '스프링러', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
    (12, 'Coroutines 어렵네요', '초보자', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3),
    (13, 'QueryDSL 좋아요', 'DSL팬', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4);

-- 좋아요 데이터 (게시글 1번에 5개)
INSERT INTO post_likes (id, post_id, user_id, created_at) VALUES
    (1, 1, 1, CURRENT_TIMESTAMP),
    (2, 1, 2, CURRENT_TIMESTAMP),
    (3, 1, 3, CURRENT_TIMESTAMP),
    (4, 1, 4, CURRENT_TIMESTAMP),
    (5, 1, 5, CURRENT_TIMESTAMP);

-- 다른 게시글에도 좋아요 추가
INSERT INTO post_likes (id, post_id, user_id, created_at) VALUES
    (6, 2, 1, CURRENT_TIMESTAMP),
    (7, 2, 2, CURRENT_TIMESTAMP),
    (8, 3, 1, CURRENT_TIMESTAMP),
    (9, 4, 1, CURRENT_TIMESTAMP),
    (10, 5, 1, CURRENT_TIMESTAMP);
