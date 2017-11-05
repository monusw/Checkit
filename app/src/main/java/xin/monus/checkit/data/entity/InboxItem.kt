package xin.monus.checkit.data.entity

import java.util.*

data class InboxItem (
        val id: String = UUID.randomUUID().toString(),
        val title: String = ""
)