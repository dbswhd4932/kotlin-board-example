package com.example.board.service

import com.example.board.dto.PostDto
import com.example.board.repository.PostRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepositoryKt: PostRepository
) {
    // 게시글 목족 조회 (페이징)
    fun getPosts(pageable: Pageable): PostDto.PostListResponse {
        val page = postRepositoryKt.findAll(pageable)

        return PostDto.PostListResponse(
            posts = page.content.map {
                PostDto.PostResponse.from(it) // it -> 현재요소
            },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            size = page.size
        )
    }

    // 게시글 상세 조회
    fun getPost(id: Long): PostDto.PostDetailResponse {
        val post = postRepositoryKt.findByIdWithComments(id)
            ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다. Id: $id")

        return PostDto.PostDetailResponse.from(post)
    }

    // 게시글 검색
    fun searchPost(keyword: String, pageable: Pageable): PostDto.PostListResponse {
        val page = postRepositoryKt.searchByKeyword(keyword, pageable)

        return PostDto.PostListResponse(
            posts = page.content.map {
                PostDto.PostResponse.from(it)
            },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            size = page.size
        )
    }

    // 게시글 생성
    @Transactional
    fun createPost(request: PostDto.CreatePostRequest): PostDto.PostResponse {
        val post = request.toEntity()
        val savedPost = postRepositoryKt.save(post)

        return PostDto.PostResponse.from(savedPost)
    }

    // 게시글 수정
    @Transactional
    fun updatePost(id: Long, request: PostDto.UpdatePostRequest): PostDto.PostResponse {
        val post = postRepositoryKt.findByIdOrNull(id)
            ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다. id: $id")

        // 더티체킹 발생
        post.update(request.title, request.content)
        return PostDto.PostResponse.from(post)

    }

    // 게시글 삭제
    @Transactional
    fun deletePost(id: Long) {
        if (!postRepositoryKt.existsById(id)) {
            throw IllegalArgumentException("게시글을 찾을 수 없습니다. id: $id")
        }

        postRepositoryKt.deleteById(id)
    }
}