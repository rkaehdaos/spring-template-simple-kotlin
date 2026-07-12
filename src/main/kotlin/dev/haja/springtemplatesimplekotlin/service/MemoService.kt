package dev.haja.springtemplatesimplekotlin.service

import dev.haja.springtemplatesimplekotlin.domain.Memo
import dev.haja.springtemplatesimplekotlin.repository.MemoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** 생성자 주입만 사용 (필드 @Autowired 금지 — KonsistTest 에서 검증) */
@Service
@Transactional(readOnly = true)
class MemoService(
    private val memoRepository: MemoRepository,
) {
    fun findAll(): List<Memo> = memoRepository.findAll()

    fun findById(id: Long): Memo =
        memoRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Memo not found: id=$id")

    @Transactional
    fun create(title: String, content: String): Memo =
        memoRepository.save(Memo(title = title, content = content))
}
