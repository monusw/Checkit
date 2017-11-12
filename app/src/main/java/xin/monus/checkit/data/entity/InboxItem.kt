package xin.monus.checkit.data.entity

data class InboxItem (
        val id: Int,
        val username: String,
        val content: String,
        val deadline: String,
        val complete: Boolean,
        val flag: Boolean
)