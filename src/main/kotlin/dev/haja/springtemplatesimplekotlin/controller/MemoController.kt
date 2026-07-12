package dev.haja.springtemplatesimplekotlin.controller

import dev.haja.springtemplatesimplekotlin.domain.Memo
import dev.haja.springtemplatesimplekotlin.service.MemoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/memos")
class MemoController(
    private val memoService: MemoService,
) {
    @GetMapping
    fun list(): List<MemoResponse> = memoService.findAll().map(MemoResponse::from)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): MemoResponse =
        MemoResponse.from(memoService.findById(id))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: MemoCreateRequest): MemoResponse =
        MemoResponse.from(memoService.create(request.title, request.content))
}

/** DTO 는 data class 로 작성 (KonsistTest 에서 검증) */
data class MemoCreateRequest(val title: String, val content: String)

data class MemoResponse(val id: Long, val title: String, val content: String) {
    companion object {
        fun from(memo: Memo) = MemoResponse(
            id = requireNotNull(memo.id),
            title = memo.title,
            content = memo.content,
        )
    }
}
