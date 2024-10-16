import build.BuildModules
import dependencies.Dependencies
import dependencies.ProcessorsDependencies
import dependencies.TestDependencies
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.project

//app依赖扩展配置
fun DependencyHandlerScope.appDependencies() {
    baseService()
    commonProcessors()
    commonModules()
    commonTest()
}

//添加基础服务依赖
fun DependencyHandlerScope.baseService() {
    implementation(project(BuildModules.Library.service))
}

//注解处理器基础依赖
fun DependencyHandlerScope.commonProcessors() {
    kapt(ProcessorsDependencies.annotation)
    kapt(ProcessorsDependencies.room)
    kapt(ProcessorsDependencies.autoService)
}

//各业务组建基础依赖
fun DependencyHandlerScope.commonModules() {
    implementation(project(BuildModules.Module.home))
    implementation(project(BuildModules.Module.square))
    implementation(project(BuildModules.Module.system))
    implementation(project(BuildModules.Module.me))
    implementation(project(BuildModules.Module.official))
    implementation(project(BuildModules.Module.project))
    implementation(project(BuildModules.Module.details))
    implementation(project(BuildModules.Module.search))
    implementation(project(BuildModules.Module.account))
    implementation(project(BuildModules.Module.todo))
}

//基础测试依赖
fun DependencyHandlerScope.commonTest() {
    testImplementation(TestDependencies.junit)
    androidTestImplementation(TestDependencies.junitAndroid)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.composeUiTest)
    debugImplementation(TestDependencies.composeUiTooling)
    debugImplementation(TestDependencies.leakcanaryDebug)
}

//基础依赖配置
fun DependencyHandlerScope.commonDependencies() {
    api(Dependencies.coreKtx)
    api(Dependencies.coroutines)

    api(Dependencies.lifecycle)
    api(Dependencies.lifecycleLivedata)
    api(Dependencies.lifecycleViewModel)

    api(Dependencies.activity)
    api(Dependencies.fragment)

    api(Dependencies.multidex)
    api(Dependencies.startup)

    api(Dependencies.appcompat)
    api(Dependencies.webkit)

    api(Dependencies.navigationFragment)
    api(Dependencies.navigationUi)
    api(Dependencies.navigationCompose)

    api(Dependencies.composeActivity)
    api(Dependencies.composeConstraint)
    api(Dependencies.composeUi)
    api(Dependencies.composeIcon)
    api(Dependencies.composeMaterial)
    api(Dependencies.composePreview)
    api(Dependencies.composeUtil)
    api(Dependencies.composeLivedata)

    api(Dependencies.composeCoin)
    api(Dependencies.composeSystemUiController)

    api(Dependencies.pagingRuntime)
    api(Dependencies.pagingCompose)

    api(Dependencies.room)
    api(Dependencies.roomRuntime)
    api(Dependencies.roomPaging)

    api(Dependencies.retrofit)
    api(Dependencies.retrofitConverterGson) { exclude("com.google.code.gson") }
    api(Dependencies.retrofitConverterScalars)
    api(Dependencies.okhttp3Logging)

    api(Dependencies.gson)

    api(Dependencies.protobuf)

    api(Dependencies.autoService)

    api(Dependencies.agentWeb)
} 

