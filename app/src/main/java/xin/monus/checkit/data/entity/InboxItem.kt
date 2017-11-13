package xin.monus.checkit.data.entity

data class InboxItem(
        val id: Int = 0,
        val username: String,
        val content: String,
        val deadline: String,
        var complete: Boolean,
        var flag: Boolean
)