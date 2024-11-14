package build

/**
 * 项目编译类型配置信息
 * @author jv.lee
 * @date 2021/10/1
 */

interface BuildTypes {

    companion object {
        const val DEBUG = "debug"
        const val RELEASE = "release"
    }

    val isMinifyEnabled: Boolean
    val isShrinkResources: Boolean
    val paramsMap: Map<String, String>
}

object BuildDebug : BuildTypes {
    override val isMinifyEnabled = false
    override val isShrinkResources = false
    override val paramsMap = mapOf(Pair("BASE_URI", "https://www.wanandroid.com"))

    object SigningConfig {
        const val STORE_FILE = "pioneer.jks"
        const val STORE_PASSWORD = "123456"
        const val KEY_ALIAS = "pioneer"
        const val KEY_PASSWORD = "123456"
    }
}

object BuildRelease : BuildTypes {
    override val isMinifyEnabled = true
    override val isShrinkResources = true
    override val paramsMap = mapOf(Pair("BASE_URI", "https://www.wanandroid.com"))

    object SigningConfig {
        const val STORE_FILE = "pioneer.jks"
        const val STORE_PASSWORD = "123456"
        const val KEY_ALIAS = "pioneer"
        const val KEY_PASSWORD = "123456"
    }
}
