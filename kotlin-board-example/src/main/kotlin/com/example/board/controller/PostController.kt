package com.example.board.controller

import com.example.board.dto.PostDto
import com.example.board.service.PostService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts/kt")
class PostController(

    private val postServiceKt: PostService

) {
    // 게시글 목록 조회
    @GetMapping
    fun getPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PostDto.PostListResponse> {

        val sort = if (direction.equals("DESC", ignoreCase = true)) // true -> 대소문자 무시
            Sort.by(sortBy).descending()
        else Sort.by(sortBy).ascending()

        val pageable = PageRequest.of(page, size, sort)
        val response = postServiceKt.getPosts(pageable)

        return ResponseEntity.ok(response)
    }

    // 게시글 상세조회
    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): ResponseEntity<PostDto.PostDetailResponse> {
        val response = postServiceKt.getPost(id)
        return ResponseEntity.ok(response)
    }

    // 게시글 검색
    @GetMapping("/search")
    fun searchPost(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<PostDto.PostListResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val response = postServiceKt.searchPost(keyword, pageable)

        return ResponseEntity.ok(response)
    }

    // 게시글 생성
    @PostMapping
    fun createPost(@Valid @RequestBody request: PostDto.CreatePostRequest): ResponseEntity<PostDto.PostResponse> {
        val response = postServiceKt.createPost(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // 게시글 수정
    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @Valid @RequestBody request: PostDto.UpdatePostRequest
    ): ResponseEntity<PostDto.PostResponse> {
        val response = postServiceKt.updatePost(id, request)
        return ResponseEntity.ok(response)
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Unit> {
        postServiceKt.deletePost(id)
        return ResponseEntity.noContent().build()
    }

    // ==================== 성능 비교용 엔드포인트 ====================

    /**
     * 동기 방식 - 게시글 상세 조회 (순차 실행)
     * GET /api/posts/kt/{id}/sync
     */
    @GetMapping("/{id}/sync")
    fun getPostSync(@PathVariable id: Long): ResponseEntity<PostDto.PostDetailResponse> {
        val response = postServiceKt.getPostSync(id)
        return ResponseEntity.ok(response)
    }

    /**
     * Coroutines 방식 - 게시글 상세 조회 (병렬 실행)
     * GET /api/posts/kt/{id}/async
     */
    @GetMapping("/{id}/async")
    suspend fun getPostAsync(@PathVariable id: Long): ResponseEntity<PostDto.PostDetailResponse> {
        val response = postServiceKt.getPostWithCoroutines(id)
        return ResponseEntity.ok(response)
    }

}