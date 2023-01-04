package com.runcard


object AndroidXDepVersion {
    const val core_version = "1.5.0-beta01"
    const val core_splashscreen_version = "1.0.0-alpha02"
    const val appcompat_version = "1.2.0"
    const val material_version = "1.3.0"
    const val activity_version  = "1.3.0-alpha03"
    const val recyclerview_version  = "1.2.0"
    const val constraint_layout_version = "2.0.4"
    const val viewpager_version = "1.0.0"
    const val window_version = "1.0.0"


    const val junit_version = "4.+"
    const val ext_junit_version = "1.1.2"
    const val espresso_core_version = "3.3.0"

    const val navigation_fragment_ktx = "2.3.5"
    const val lifecycle_version = "2.6.0-alpha01"



}


object AndroidXDep {
    const val core_ktx = "androidx.core:core-ktx:${AndroidXDepVersion.core_version}"
    const val core_splashscreen = "androidx.core:core-splashscreen:${AndroidXDepVersion.core_splashscreen_version}"
    const val appcompat = "androidx.appcompat:appcompat:${AndroidXDepVersion.appcompat_version}"
    const val material = "com.google.android.material:material:${AndroidXDepVersion.material_version}"
    const val window = "androidx.window:window::${AndroidXDepVersion.window_version}"
    private const val activity = "androidx.activity:activity-ktx:${AndroidXDepVersion.activity_version}"
    private const val recyclerview = "androidx.recyclerview:recyclerview:${AndroidXDepVersion.recyclerview_version}"
    private const val constraintLayout = "androidx.constraintlayout:constraintlayout:${AndroidXDepVersion.constraint_layout_version}"
    private const val viewpager = "androidx.viewpager:viewpager:${AndroidXDepVersion.viewpager_version}"

    private const val lifecycle = "androidx.lifecycle:lifecycle-viewmodel-ktx:${AndroidXDepVersion.lifecycle_version}"

    //页面导航
    private const val navigation_fragment = "androidx.navigation:navigation-fragment-ktx:${AndroidXDepVersion.navigation_fragment_ktx}"
    private const val navigation_ui = "androidx.navigation:navigation-ui-ktx:${AndroidXDepVersion.navigation_fragment_ktx}"
    private const val navigation_runtime = "androidx.navigation:navigation-runtime-ktx:${AndroidXDepVersion.navigation_fragment_ktx}"

    //测试
    const val junit = "junit:junit:${AndroidXDepVersion.junit_version}"
    const val ext_junit = "androidx.test.ext:junit:${AndroidXDepVersion.ext_junit_version}"
    const val espresso_core = "androidx.test.espresso:espresso-core:${AndroidXDepVersion.espresso_core_version}"

    val values = arrayListOf(
        core_ktx,
        appcompat,
        material,
        constraintLayout,
        activity,
        recyclerview,
        navigation_fragment,
        navigation_ui,
        navigation_runtime,
        viewpager
//        lifecycle,
    )
}



object NetWorkDepVersion {
    const val retrofit_version = "2.9.0"
    const val logging_interceptor_version = "4.9.0"
    const val gson_version = "2.8.6"
    const val aria_version = "3.8.16"
}

object NetWorkDep {
    const val retrofit = "com.squareup.retrofit2:retrofit:${NetWorkDepVersion.retrofit_version}"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:${NetWorkDepVersion.retrofit_version}"
    const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${NetWorkDepVersion.logging_interceptor_version}"
    const val gson = "com.google.code.gson:gson:${NetWorkDepVersion.gson_version}"

    const val aria_core = "me.laoyuyu.aria:core:3.8.16"
    const val aria_compiler = "me.laoyuyu.aria:compiler:3.8.16"
    const val aria_ftp = "me.laoyuyu.aria:ftp:3.8.16"

    val values = arrayListOf(
        retrofit,
        converter_gson,
        logging_interceptor,
        gson
    )
}



object ThirdDepVersion {
    const val jiaozivideoplayer = "7.7.0"
    const val glide = "4.12.0"
    const val pager2Banner = "1.0.5"
    const val hhl = "1.0.0"
    const val cardview = "1.0.1"
    const val permissionx = "1.4.0"
    const val agentweb_filechooser = "v5.0.0-alpha.1-androidx"
    const val downloader = "v5.0.0"


    const val zxing_android_embedded = "3.5.0"

    const val android_pickerView = "4.1.9"
    const val wheelview = "4.1.0"
    const val coil = "1.1.1"
    const val magicIndicator = "1.5.0"
    const val smartRefreshLayout = "1.1.0"
    const val recyclerViewAdapterHelper = "2.9.30"

    const val consecutiveScroller = "2.6.2"
    const val videocache = "2.7.0"
    const val banner = "1.0.0"
    const val badgeview = "1.0.5"
    const val switchbutton = "2.1.0"
}

object ThirdDep {

    //添加饺子视频播放器
    private const val jiaozivideoplayer = "cn.jzvd:jiaozivideoplayer:${ThirdDepVersion.jiaozivideoplayer}"
    //图片加载库
    const val glide = "com.github.bumptech.glide:glide:${ThirdDepVersion.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${ThirdDepVersion.glide}"
    private const val pager2Banner = "io.github.zguop:pager2Banner:${ThirdDepVersion.pager2Banner}"


    private const val gridpagersnaphelper = "com.hhl:gridpagersnaphelper:${ThirdDepVersion.hhl}"
    private const val recyclerviewindicator = "com.hhl:recyclerviewindicator:${ThirdDepVersion.hhl}"
    private const val cardview = "com.zyp.cardview:cardview:${ThirdDepVersion.cardview}"

    //权限检查
    private const val permissionx = "com.permissionx.guolindev:permissionx:${ThirdDepVersion.permissionx}"

    //自定义webView
    private const val agentweb_filechooser = "com.github.Justson.AgentWeb:agentweb-filechooser:${ThirdDepVersion.agentweb_filechooser}"


    // (可选)
    private const val downloader =  "com.github.Justson:Downloader:${ThirdDepVersion.downloader}"

    //识别二维码
    private const val zxing_android_embedded =  "com.journeyapps:zxing-android-embedded:${ThirdDepVersion.zxing_android_embedded}"

    //日期选择器
    //private const val android_pickerView = "com.contrarywind:Android-PickerView:${ThirdDepVersion.android_pickerView}"
     //private const val wheel_view = "com.contrarywind:wheelview:${ThirdDepVersion.wheelview}"

    //kotlin的图片加载框架  简单方便
    private const val coil = "io.coil-kt:coil:${ThirdDepVersion.coil}"

    //指示器
    private const val magicIndicator = "com.github.hackware1993:MagicIndicator:${ThirdDepVersion.magicIndicator}"

    //上拉加载 下拉刷新框架
    private const val smartRefreshLayout = "com.scwang.smartrefresh:SmartRefreshLayout:${ThirdDepVersion.smartRefreshLayout}"

    //RecyclerView适配器
    const val recyclerViewAdapterHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:${ThirdDepVersion.recyclerViewAdapterHelper}"

    //设置滚动布局吸顶
    const val consecutiveScroller = "com.github.donkingliang:ConsecutiveScroller:${ThirdDepVersion.consecutiveScroller}"

    const val videocache = "com.danikula:videocache:${ThirdDepVersion.videocache}"

    //banner
    const val banner = "com.youth.banner:banner:${ThirdDepVersion.banner}"


    //红点
    const val badgeview = "com.itingchunyu.badgeview:badgeview:${ThirdDepVersion.badgeview}"

    //switch
    const val switchbutton = "com.kyleduo.switchbutton:library:${ThirdDepVersion.switchbutton}"




    val values = arrayListOf(
        glide,
        pager2Banner,
        zxing_android_embedded,
        gridpagersnaphelper,
        recyclerviewindicator,
        cardview,
        permissionx,
        agentweb_filechooser,
        downloader,
        jiaozivideoplayer,
//        android_pickerView ,
//        wheel_view,
        coil,
        magicIndicator,
        smartRefreshLayout
    )

}

object GoogleDepVersion {
    const val play_services_maps_version = "16.1.0"
    const val android_map_utils = "2.4.0"
    const val flexbox = "1.0.0"
}

object GoogleDep {
    private const val play_services_maps = "com.google.android.gms:play-services-maps:${GoogleDepVersion.play_services_maps_version}"
    private const val android_maps_utils = "com.google.maps.android:android-maps-utils:${GoogleDepVersion.android_map_utils}"
    private const val flexbox = "com.google.android:flexbox:${GoogleDepVersion.flexbox}"

    val values = arrayListOf(
        play_services_maps,
        android_maps_utils,
        flexbox
    )
}

object HiltDepVersion {
    const val hilt_version = "2.31.2-alpha"
//    const val hilt_version = "2.42"
}

object HiltDep {
    const val hilt_android = "com.google.dagger:hilt-android:${HiltDepVersion.hilt_version}"
    const val hilt_compiler = "com.google.dagger:hilt-android-compiler:${HiltDepVersion.hilt_version}"
//    const val hilt_compiler = "com.google.dagger:hilt-compiler:${HiltDepVersion.hilt_version}"
}


object TencentDepVersion {
    const val mmkv_static_version = "1.2.7"
    const val tbssdk_version = "43939"
}

object TencentDep {
    // bug 收集插件
    private const val mmkv_static = "com.tencent:mmkv-static:${TencentDepVersion.mmkv_static_version}"
    // tbs 内核
    private const val tbssdk = "com.tencent.tbs.tbssdk:sdk:${TencentDepVersion.tbssdk_version}"

    val values = arrayListOf(
        mmkv_static,
        tbssdk
    )
}