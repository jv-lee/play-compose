package build

/**
 * 项目模块配置信息
 * @author jv.lee
 * @date 2021/10/1
 */
object BuildModules {
    const val APP = ":app"

    object Library {
        const val BASE = ":library:base"
        const val COMMON = ":library:common"
        const val ROUTE = ":library:router"
        const val SERVICE = ":library:service"
    }

    object Module {
        const val HOME = ":module:home"
        const val SQUARE = ":module:square"
        const val SYSTEM = ":module:system"
        const val ME = ":module:me"
        const val OFFICIAL = ":module:official"
        const val PROJECT = ":module:project"
        const val DETAILS = ":module:details"
        const val SEARCH = ":module:search"
        const val ACCOUNT = ":module:account"
        const val TODO = ":module:todo"
    }

    fun String.name(): String {
        return this.substring(this.lastIndexOf(":") + 1, this.length)
    }

}


