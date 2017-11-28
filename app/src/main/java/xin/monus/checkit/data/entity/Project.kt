package xin.monus.checkit.data.entity


data class Project(
        val id: Int = 0,
        val username: String,
        var content: String,
        var type: ProjectType,
        var deadline: String,
        var complete: Boolean,
        var flag: Boolean,
        var actionList: List<Action>
)

enum class ProjectType {
    PARALLEL,           //平行项目 对应数据库存储 0
    SEQUENCE,           //顺序项目 对应数据库存储 1
    SINGLE              //单个动作列表 对应数据库存储 2
}