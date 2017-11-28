package xin.monus.checkit.data.entity


data class Action(
        val id: Int = 0,
        val projectId: Int,
        var content: String,
        var deadline: String,
        var complete: Boolean,
        var flag: Boolean,
        var subActionList: List<Action>
)