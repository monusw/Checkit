package xin.monus.checkit.data.entity

data class InboxItem(
        val id: Int = 0,
        val username: String,
        var content: String,
        var deadline: String,
        var complete: Boolean,
        var flag: Boolean,
        var status: Int = 0,
        var timestamp: String = ""
)