package configures.plugins

import build.BuildDebug
import build.BuildRelease
import build.BuildTypes
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * 项目公共参数配置依赖扩展
 * @author jv.lee
 * @date 2021/10/1
 */
fun Project.paramsConfigure() {
    extensions.configure<LibraryExtension> {
        buildTypes {
            getByName(BuildTypes.DEBUG) {
                BuildDebug.paramsMap.onEach {
                    buildConfigField("String", it.key, "\"" + it.value + "\"")
                }
            }
            getByName(BuildTypes.RELEASE) {
                BuildRelease.paramsMap.onEach {
                    buildConfigField("String", it.key, "\"" + it.value + "\"")
                }
            }
        }
    }
}