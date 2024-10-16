package dependencies

object Dependencies {
    // kotlin androidx核心库
    const val coreKtx = "androidx.core:core-ktx:${Version.ktxCore}"

    // 协程
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"

    // 生命周期
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecycle}"
    const val lifecycleLivedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Version.lifecycle}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle}"

    // 核心基础
    const val activity = "androidx.activity:activity-ktx:${Version.activity}"
    const val fragment = "androidx.fragment:fragment-ktx:${Version.fragment}"

    // 分包库
    const val multidex = "androidx.multidex:multidex:${Version.multidex}"

    // startup
    const val startup = "androidx.startup:startup-runtime:${Version.startup}"

    // androidUi
    const val appcompat = "androidx.appcompat:appcompat:${Version.appcompat}"
    const val material = "com.google.android.material:material:${Version.material}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Version.recyclerview}"
    const val constraint = "androidx.constraintlayout:constraintlayout:${Version.constraintLayout}"
    const val viewpager2 = "androidx.viewpager2:viewpager2:${Version.viewpager2}"
    const val swipeRefreshLayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Version.swipeRefreshLayout}"
    const val webkit = "androidx.webkit:webkit:${Version.webkit}"

    // fragment导航
    const val navigationFragment =
        "androidx.navigation:navigation-fragment-ktx:${Version.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Version.navigation}"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Version.navigation}"
    const val navigationDynamic =
        "androidx.navigation:navigation-dynamic-features-fragment:${Version.navigation}"

    // compose
    const val composeActivity = "androidx.activity:activity-compose:${Version.activity}"
    const val composeUi = "androidx.compose.ui:ui:${Version.compose}"
    const val composeIcon = "androidx.compose.material:material-icons-extended:${Version.compose}"
    const val composeMaterial = "androidx.compose.material:material:${Version.compose}"
    const val composePreview = "androidx.compose.ui:ui-tooling-preview:${Version.compose}"
    const val composeLivedata = "androidx.compose.runtime:runtime-livedata:${Version.compose}"
    const val composeUtil = "androidx.compose.ui:ui-util:${Version.compose}"
    const val composeConstraint = "androidx.constraintlayout:constraintlayout-compose:1.0.0"

    // compose accompanist
    const val composeSystemUiController =
        "com.google.accompanist:accompanist-systemuicontroller:0.36.0"
    const val composeCoin = "com.google.accompanist:accompanist-coil:0.15.0"

    // paging 分页
    const val pagingRuntime = "androidx.paging:paging-runtime:${Version.paging}"
    const val pagingCompose = "androidx.paging:paging-compose:${Version.paging}"

    // room数据库
    const val room = "androidx.room:room-ktx:${Version.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Version.room}"
    const val roomPaging = "androidx.room:room-paging:${Version.room}"

    // 图片加载
    const val glide = "com.github.bumptech.glide:glide:${Version.glide}"
    const val glideOkhttp3 = "com.github.bumptech.glide:okhttp3-integration:${Version.glide}"
    const val glideAnnotations = "com.github.bumptech.glide:annotations:${Version.glide}"

    // 网络加载
    const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofit}"
    const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Version.retrofit}"
    const val retrofitConverterScalars =
        "com.squareup.retrofit2:converter-scalars:${Version.retrofit}"
    const val okhttp3Logging = "com.squareup.okhttp3:logging-interceptor:3.11.0"

    // gson解析
    const val gson = "com.google.code.gson:gson:${Version.gson}"

    // protobuf
    const val protobuf = "com.google.protobuf:protobuf-javalite:${Version.protobuf}"

    // 组件化依赖
    const val autoService = "com.google.auto.service:auto-service:${Version.autoService}"

    // 图片选择器
    const val imageTools = "com.github.jv-lee.imagetools:library:1.4.4"

    // AgentWebView
    const val agentWeb = "com.github.Justson.AgentWeb:agentweb-core:v5.0.6-androidx"
}
