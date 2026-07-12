package dev.haja.springtemplatesimplekotlin.repository

import dev.haja.springtemplatesimplekotlin.domain.Memo
import org.springframework.data.jpa.repository.JpaRepository

interface MemoRepository : JpaRepository<Memo, Long>
