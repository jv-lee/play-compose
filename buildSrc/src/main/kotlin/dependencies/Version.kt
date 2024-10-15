package dependencies

object Version {

    //core
    const val ktxCore = "1.13.1"
    const val coroutines = "1.8.1"
    const val multidex = "2.0.1"
    const val startup = "1.1.1"
    const val appcompat = "1.7.0"
    const val material = "1.12.0"
    const val recyclerview = "1.3.2"
    const val constraintLayout = "2.1.4"
    const val viewpager2 = "1.1.0"
    const val swipeRefreshLayout = "1.1.0"
    const val webkit = "1.12.1"
    const val activity = "1.9.2"
    const val fragment = "1.8.4"
    const val navigation = "2.8.2"
    const val lifecycle = "2.8.6"
    const val room = "2.6.1"
    const val paging = "3.3.2"

    // compose
    const val composeCompiler = "1.5.15"
    const val compose = "1.7.3"

    const val protobuf = "3.10.0"
    const val autoService = "1.0"
    const val glide = "4.16.0"
    const val retrofit = "2.11.0"
    const val gson = "2.11.0"

    //Test
    const val junit = "4.13.2"
    const val junitAndroid = "1.1.3"
    const val espressoAndroid = "3.4.0"
    const val leakcanary = "2.9.1"
    const val blockCanary =
        "1.5.0" // compileSdk = 32 即android12 无法编译通过 (manifest <activity> has intent-filter exported = true )
}
