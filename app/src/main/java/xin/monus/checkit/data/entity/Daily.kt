package xin.monus.checkit.data.entity

data class Daily(
        val id: Int = 0,
        val username: String,
        var content: String,
        var remindTime: String,
        var complete: Boolean,
        var flag: Boolean,
        var status: Int = 0,
        var timestamp: String = ""
)