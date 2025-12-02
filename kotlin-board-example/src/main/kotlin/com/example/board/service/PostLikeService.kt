package com.example.board.service

import com.example.board.entity.PostLike
import com.example.board.repository.PostLikeRepository
import com.example.board.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * PostLike Service (Kotlin 학습용)
 *
 * [학습 포인트]
 * 1. Constructor Injection (생성자 주입)
 *    - Java: @Autowired private final PostLikeRepository repository;
 *    - Kotlin: class PostLikeService(private val repository: PostLikeRepository)
 *
 * 2. @Transactional
 *    - readOnly = false (기본값): 쓰기 작업 (INSERT, UPDATE, DELETE)
 *    - readOnly = true: 읽기 전용 (SELECT만)
 *
 * 3. Elvis Operator (?:)
 *    - Java: if (obj == null) throw new Exception()
 *    - Kotlin: obj ?: throw Exception()
 *
 * 4. Let 함수
 *    - nullable 객체 처리
 *    - obj?.let { ... } : obj가 null이 아니면 실행
 */
@Service
class PostLikeService(
    // 학습 포인트 - Constructor Injection
    private val postLikeRepository: PostLikeRepository,
    private val postRepository: PostRepository
) {

    /**
     * 좋아요 추가
     *
     * [학습 포인트]
     * - @Transactional: DB 변경 작업이므로 필요
     * - Elvis operator: ?: throw
     * - let 함수: nullable 처리
     */
    @Transactional
    fun addLike(postId: Long, userId: Long): PostLike {
        // 1. 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw IllegalArgumentException("게시글을 찾을 수 없습니다. id: $postId")
        }

        // 2. 이미 좋아요 했는지 확인
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw IllegalStateException("이미 좋아요를 눌렀습니다.")
        }

        // 3. 좋아요 생성
        val like = PostLike(postId = postId, userId = userId)

        // 4. 저장 및 반환
        return postLikeRepository.save(like)
    }

    /**
     * 좋아요 취소
     *
     * [학습 포인트]
     * - @Transactional: 삭제 작업이므로 필요
     * - deleteByPostIdAndUserId: Query Method 활용
     * - if 표현식: Kotlin에서는 if도 값을 반환할 수 있음
     */
    @Transactional
    fun removeLike(postId: Long, userId: Long) {
        // 1. 좋아요 존재 확인
        // existsByPostIdAndUserId로 체크
        // 없으면 IllegalArgumentException("좋아요를 찾을 수 없습니다") 던지기
        if(!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw IllegalArgumentException("좋아요를 찾을 수 없습니다.")
        }

        // 2. 좋아요 삭제
        // 반환값(삭제된 개수)을 변수에 저장해도 되고, 무시해도 됨
        postLikeRepository.deleteByPostIdAndUserId(postId, userId)
    }

    /**
     * 특정 게시글의 좋아요 개수 조회
     *
     * [학습 포인트]
     * - Transactional(readOnly = true): 읽기 전용
     * - 간단한 메서드는 = 로 바로 반환 가능 (Expression Body)
     */
    @Transactional(readOnly = true)
    fun getLikeCount(postId: Long): Long {
        return postLikeRepository.countByPostId(postId)
    }

    /**
     * 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     *
     * [학습 포인트]
     * - Expression Body: fun xxx() = yyy (return 생략)
     * - Boolean 반환
     */
    @Transactional(readOnly = true)
    fun isLikedByUser(postId: Long, userId: Long): Boolean {
        // TODO: Expression Body로 작성
        return postLikeRepository.existsByPostIdAndUserId(postId, userId)
    }

    /**
     * 특정 게시글의 모든 좋아요 조회
     *
     * [학습 포인트]
     * - List 반환
     * - findAllByPostId 사용
     */
    @Transactional(readOnly = true)
    fun getLikesByPostId(postId: Long): List<PostLike> {
        return postLikeRepository.findAllByPostId(postId)
    }

    /**
     * 특정 사용자가 누른 모든 좋아요 조회
     *
     * [학습 포인트]
     * - Expression Body
     * - List 반환
     */
    @Transactional(readOnly = true)
    fun getLikesByUserId(userId: Long): List<PostLike> {
        return postLikeRepository.findAllByUserId(userId)
    }
}
