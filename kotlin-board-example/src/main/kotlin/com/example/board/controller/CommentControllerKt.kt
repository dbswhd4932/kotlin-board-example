package com.example.board.controller

import com.example.board.dto.CommentDtoKt
import com.example.board.service.CommentServiceKt
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Java의 CommentController를 Kotlin으로 변환
 *
 * 학습 목표:
 * 1. @RestController, @RequestMapping 어노테이션 사용법 (Java와 동일)
 * 2. constructor injection 패턴 (Kotlin은 생성자에서 바로 프로퍼티 선언 가능)
 * 3. @PathVariable, @RequestBody, @Valid 어노테이션 활용
 * 4. ResponseEntity 사용법 (Kotlin에서도 동일하게 사용)
 *
 * Kotlin 변환 포인트:
 * - Java의 생성자 주입 → Kotlin의 primary constructor로 간결하게 표현
 *   Java: private final CommentService commentService;
 *         public CommentController(CommentService commentService) {
 *             this.commentService = commentService;
 *         }
 *   Kotlin: class CommentControllerKt(private val commentService: CommentServiceKt)
 *
 * - ResponseEntity 생성
 *   Java: ResponseEntity.ok(response)
 *   Kotlin: ResponseEntity.ok(response) (동일)
 *
 * - HTTP Status 지정
 *   Java: ResponseEntity.status(HttpStatus.CREATED).body(response)
 *   Kotlin: ResponseEntity.status(HttpStatus.CREATED).body(response) (동일)
 */
@RestController
@RequestMapping("/api/kt/posts/{postId}/comments")
class CommentControllerKt(
    private val commentService: CommentServiceKt
) {

    /**
     * 특정 게시글의 댓글 목록 조회
     * GET /api/kt/posts/{postId}/comments
     *
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    @GetMapping
    fun getComments(@PathVariable postId: Long): ResponseEntity<List<CommentDtoKt.CommentResponse>> {
        val response = commentService.getCommentsByPostId(postId)
        return ResponseEntity.ok(response)
    }

    /**
     * 댓글 생성
     * POST /api/kt/posts/{postId}/comments
     *
     * @param postId 게시글 ID
     * @param request 댓글 생성 요청
     * @return 생성된 댓글 정보
     */
    @PostMapping
    fun createComment(
        @PathVariable postId: Long,
        @Valid @RequestBody request: CommentDtoKt.CreateCommentRequest
    ): ResponseEntity<CommentDtoKt.CommentResponse> {
        val response = commentService.createComment(postId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }


    /**
     * 댓글 수정
     * PUT /api/kt/posts/{postId}/comments/{commentId}
     *
     * @param postId 게시글 ID
     * @param commentId 댓글 ID
     * @param request 댓글 수정 요청
     * @return 수정된 댓글 정보
     */
    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody request: CommentDtoKt.UpdateCommentRequest
    ): ResponseEntity<CommentDtoKt.CommentResponse> {
        val response = commentService.updateComment(commentId, request)
        return ResponseEntity.ok(response)
    }


    /**
     * 댓글 삭제
     * DELETE /api/kt/posts/{postId}/comments/{commentId}
     *
     * @param postId 게시글 ID
     * @param commentId 댓글 ID
     * @return 응답 없음 (204 No Content)
     */
    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long
    ): ResponseEntity<Void> {
        commentService.deleteComment(commentId)
        return ResponseEntity.noContent().build()
    }

}
