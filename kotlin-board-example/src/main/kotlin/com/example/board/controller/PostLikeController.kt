package com.example.board.controller

import com.example.board.dto.PostLikeDto
import com.example.board.service.PostLikeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * PostLike Controller (Kotlin 학습용)
 *
 * [학습 포인트]
 * 1. RESTful API 설계
 *    - POST /api/posts/{postId}/likes : 좋아요 추가
 *    - DELETE /api/posts/{postId}/likes : 좋아요 취소
 *    - GET /api/posts/{postId}/likes : 좋아요 개수 조회
 *    - GET /api/posts/{postId}/likes/check : 좋아요 여부 확인
 *
 * 2. Path Variable vs Request Param
 *    - @PathVariable: URL 경로에 포함 (/posts/{postId})
 *    - @RequestParam: 쿼리 스트링 (?userId=1)
 *
 * 3. ResponseEntity
 *    - ResponseEntity.ok(): 200 OK
 *    - ResponseEntity.status(HttpStatus.CREATED): 201 Created
 *    - ResponseEntity.noContent(): 204 No Content
 *
 * 4. Data Class (Response DTO)
 *    - JSON 응답을 위한 DTO
 *    - data class는 자동으로 equals, hashCode, toString 생성
 */
@RestController
@RequestMapping("/api/posts")
class PostLikeController(
    // TODO: 학습 포인트 - Constructor Injection
    private val postLikeService: PostLikeService
) {

    /**
     * 좋아요 추가
     * POST /api/posts/{postId}/likes?userId={userId}
     *
     * [학습 포인트]
     * - @PostMapping: HTTP POST 메서드
     * - @PathVariable: URL 경로 변수 (postId)
     * - @RequestParam: 쿼리 파라미터 (userId)
     * - ResponseEntity.status(): HTTP 상태 코드 지정
     */
    @PostMapping("/{postId}/likes")
    fun addLike(
        @PathVariable postId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<PostLikeDto.LikeResponse> {
        // 1. Service 호출
        val like = postLikeService.addLike(postId, userId)

        // 2. Response DTO 생성
        val response = PostLikeDto.LikeResponse(
            postId = like.postId,
            userId = like.userId,
            message = "좋아요를 추가했습니다."
        )

        // 3. 201 Created 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(response)

    }

    /**
     * 좋아요 취소
     * DELETE /api/posts/{postId}/likes?userId={userId}
     *
     * [학습 포인트]
     * - @DeleteMapping: HTTP DELETE 메서드
     * - ResponseEntity<Unit>: 응답 본문 없음
     * - noContent(): 204 No Content
     */
    @DeleteMapping("/{postId}/likes")
    fun removeLike(
        @PathVariable postId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<Unit> {
        // 1. Service 호출
        postLikeService.removeLike(postId, userId)

        // 2. 204 No Content 응답
        return ResponseEntity.noContent().build()
    }

    /**
     * 특정 게시글의 좋아요 개수 조회
     * GET /api/posts/{postId}/likes/count
     *
     * [학습 포인트]
     * - @GetMapping: HTTP GET 메서드
     * - Data class로 응답
     */
    @GetMapping("/{postId}/likes/count")
    fun getLikeCount(@PathVariable postId: Long): ResponseEntity<PostLikeDto.LikeCountResponse> {
        // 1. Service 호출
        val count = postLikeService.getLikeCount(postId)

        // 2. Response DTO 생성
        val response = PostLikeDto.LikeCountResponse(
            postId = postId,
            count = count
        )

        // 3. 200 OK 응답
        return ResponseEntity.ok(response)
    }

    /**
     * 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     * GET /api/posts/{postId}/likes/check?userId={userId}
     *
     * [학습 포인트]
     * - Boolean 값 반환
     */
    @GetMapping("/{postId}/likes/check")
    fun checkLikeStatus(
        @PathVariable postId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<PostLikeDto.LikeCheckResponse> {
        // 1. Service 호출
        val isLiked = postLikeService.isLikedByUser(postId, userId)

        // 2. Response DTO 생성
        val response = PostLikeDto.LikeCheckResponse(
            postId = postId,
            userId = userId,
            isLiked = isLiked
        )

        // 3. 200 OK 응답
        return ResponseEntity.ok(response)
    }

    /**
     * 특정 게시글의 모든 좋아요 조회
     * GET /api/posts/{postId}/likes
     *
     * [학습 포인트]
     * - List 반환
     * - map 함수로 Entity -> DTO 변환
     */
    @GetMapping("/{postId}/likes")
    fun getLikesByPost(@PathVariable postId: Long): ResponseEntity<PostLikeDto.LikeListResponse> {
        // 1. Service 호출
        val likes = postLikeService.getLikesByPostId(postId)

        // 2. Entity -> DTO 변환 (map 함수)
        val likeDtos = likes.map { like ->
            PostLikeDto.LikeInfo(like.id, like.postId, like.userId, like.createdAt)
        }

        // 3. Response DTO 생성
        val response = PostLikeDto.LikeListResponse(
            postId = postId,
            count = likes.size,
            likes = likeDtos
        )

        // 4. 200 OK 응답
        return ResponseEntity.ok(response)

    }
}

