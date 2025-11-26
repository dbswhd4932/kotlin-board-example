package com.example.board.service

import com.example.board.dto.CommentDtoKt
import com.example.board.repository.CommentRepositoryKt
import com.example.board.repository.PostRepositoryKt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * TODO: Java의 CommentService를 Kotlin으로 변환
 *
 * 학습 목표:
 * 1. @Service, @Transactional 어노테이션 사용법
 * 2. Repository 주입 및 사용
 * 3. Optional 대신 Kotlin의 nullable 타입 활용
 * 4. Stream API 대신 Kotlin의 Collection 확장 함수 사용
 * 5. Elvis 연산자(?:)를 활용한 예외 처리
 *
 * 구현해야 할 기능:
 * - getCommentsByPostId: 특정 게시글의 댓글 목록 조회
 * - createComment: 댓글 생성
 * - updateComment: 댓글 수정
 * - deleteComment: 댓글 삭제
 *
 * Kotlin 변환 포인트:
 * 1. Optional 처리
 *    Java: postRepository.findById(postId)
 *          .orElseThrow(() -> new IllegalArgumentException("..."))
 *    Kotlin: postRepository.findById(postId)
 *            ?: throw IllegalArgumentException("...")
 *
 * 2. Stream API → Collection 함수
 *    Java: list.stream()
 *          .map(CommentResponse::from)
 *          .collect(Collectors.toList())
 *    Kotlin: list.map { CommentResponse.from(it) }
 *    또는: list.map(CommentResponse::from)
 *
 * 3. Null 체크
 *    Java: if (comment.getPost() != null) { ... }
 *    Kotlin: comment.post?.let { ... }
 *
 * 4. 생성자 주입
 *    Java: private final CommentRepository commentRepository;
 *          public CommentService(CommentRepository commentRepository, ...) {
 *              this.commentRepository = commentRepository;
 *          }
 *    Kotlin: class CommentServiceKt(
 *              private val commentRepository: CommentRepositoryKt,
 *              private val postRepository: PostRepositoryKt
 *            )
 */
@Service
@Transactional(readOnly = true)
class CommentServiceKt(
    // TODO: Repository 의존성 주입
    // private val commentRepository: CommentRepositoryKt,
    // private val postRepository: PostRepositoryKt
) {

    /**
     * TODO: 특정 게시글의 댓글 목록 조회
     *
     * 구현 힌트:
     * 1. commentRepository.findByPostId(postId) 호출
     * 2. 결과를 map을 사용하여 CommentResponse로 변환
     * 3. Java의 .stream().map().collect() 대신 Kotlin의 .map() 사용
     */
    // fun getCommentsByPostId(postId: Long): List<CommentDtoKt.CommentResponse> {
    //     TODO("댓글 목록을 조회하고 DTO로 변환하세요")
    // }

    /**
     * TODO: 댓글 생성
     *
     * 구현 힌트:
     * 1. postRepository.findById(postId)로 게시글 조회
     * 2. Elvis 연산자(?:)를 사용하여 없으면 예외 발생
     * 3. request.toEntity()로 엔티티 생성
     * 4. comment.post = post로 연관관계 설정
     * 5. commentRepository.save(comment)로 저장
     * 6. 양방향 연관관계 설정: post.addComment(savedComment)
     * 7. CommentResponse.from(savedComment) 반환
     */
    // @Transactional
    // fun createComment(postId: Long, request: CommentDtoKt.CreateCommentRequest): CommentDtoKt.CommentResponse {
    //     TODO("댓글을 생성하고 저장하세요")
    // }

    /**
     * TODO: 댓글 수정
     *
     * 구현 힌트:
     * 1. commentRepository.findById(commentId)로 댓글 조회
     * 2. Elvis 연산자로 없으면 예외 발생
     * 3. comment.update(request.content)로 업데이트 (dirty checking)
     * 4. CommentResponse.from(comment) 반환
     */
    // @Transactional
    // fun updateComment(commentId: Long, request: CommentDtoKt.UpdateCommentRequest): CommentDtoKt.CommentResponse {
    //     TODO("댓글을 수정하세요")
    // }

    /**
     * TODO: 댓글 삭제
     *
     * 구현 힌트:
     * 1. commentRepository.findById(commentId)로 댓글 조회
     * 2. Elvis 연산자로 없으면 예외 발생
     * 3. comment.post?.removeComment(comment)로 양방향 연관관계 제거 (?.let 사용)
     * 4. commentRepository.delete(comment)로 삭제
     */
    // @Transactional
    // fun deleteComment(commentId: Long) {
    //     TODO("댓글을 삭제하세요")
    // }
}
