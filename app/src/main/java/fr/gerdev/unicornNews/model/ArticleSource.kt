package fr.gerdev.unicornNews.model

enum class ArticleSource(val url: String, val category: ArticleCategory, val device: ArticleDevice, val description: String) {

//    ZERO1NET("http://www.01net.com/rss/actualites/jeux/",
//            ArticleCategory.GENERAL,
//            ArticleDevice.ALL,
//            "01net.com"),

    AFJV("https://www.afjv.com/afjv_rss.xml", ArticleCategory.GENERAL, ArticleDevice.ALL, "afjv.com"),

    ACTUGAMING("https://www.actugaming.net/feed/", ArticleCategory.GENERAL, ArticleDevice.ALL, "actugaming.net"),

    CLUBIC_JEUXVIDEO("http://www.clubic.com/xml/articlejeuxvideo.xml",
            ArticleCategory.GENERAL,
            ArticleDevice.ALL,
            "clubic.com"),


    GAMEBLOG("http://www.gameblog.fr/rss.php",
            ArticleCategory.GENERAL,
            ArticleDevice.ALL,
            "gameblog.fr"),
    GAMEBLOG_TESTS_PC("http://www.gameblog.fr/rss.php?type=tests&plateforme=9",
            ArticleCategory.TEST,
            ArticleDevice.PC,
            "gameblog.fr"),
    GAMEBLOG_TESTS_XBOX_ONE("http://www.gameblog.fr/rss.php?type=tests&plateforme=77",
            ArticleCategory.TEST,
            ArticleDevice.XBOX_ONE,
            "gameblog.fr"),
    GAMEBLOG_TESTS_PS4("http://www.gameblog.fr/rss.php?type=tests&plateforme=78",
            ArticleCategory.TEST,
            ArticleDevice.PS4,
            "gameblog.fr"),
    GAMEBLOG_TESTS_ANDROID("http://www.gameblog.fr/rss.php?type=tests&plateforme=69",
            ArticleCategory.TEST,
            ArticleDevice.MOBILE,
            "gameblog.fr"),
    GAMEBLOG_TESTS_IPAD("http://www.gameblog.fr/rss.php?type=tests&plateforme=60",
            ArticleCategory.TEST,
            ArticleDevice.MOBILE,
            "gameblog.fr"),
    GAMEBLOG_TESTS_SWITCH("http://www.gameblog.fr/rss.php?type=tests&plateforme=87",
            ArticleCategory.TEST,
            ArticleDevice.NINTENDO_SWITCH,
            "gameblog.fr"),

    GAMEHOPE_TESTS("https://www.gamehope.com/rss/tests/tout.xml",
            ArticleCategory.TEST,
            ArticleDevice.ALL,
            "gamehope.com"),
    GAMEHOPE_PREVIEWS("https://www.gamehope.com/rss/previews/tout.xml",
            ArticleCategory.TEST,
            ArticleDevice.ALL,
            "gamehope.com"),
    GAMEHOPE_VIDEOS("https://www.gamehope.com/rss/videos/tout.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.ALL,
            "gamehope.com"),

    GAMERGEN_GENERAL_PC("https://gamergen.com/rss/pc", ArticleCategory.GENERAL, ArticleDevice.PC, "gamergen.com"),
    GAMERGEN_GENERAL_XBOX_ONE("https://gamergen.com/rss/xbox-one", ArticleCategory.GENERAL, ArticleDevice.XBOX_ONE, "gamergen.com"),
    GAMERGEN_GENERAL_PS4("https://gamergen.com/rss/ps4", ArticleCategory.GENERAL, ArticleDevice.PS4, "gamergen.com"),
    GAMERGEN_GENERAL_SWITCH("https://gamergen.com/rss/switch", ArticleCategory.GENERAL, ArticleDevice.NINTENDO_SWITCH, "gamergen.com"),
    GAMERGEN_GENERAL_ANDROID("https://gamergen.com/rss/android", ArticleCategory.GENERAL, ArticleDevice.MOBILE, "gamergen.com"),
    GAMERGEN_GENERAL_IOS("https://gamergen.com/rss/ios", ArticleCategory.GENERAL, ArticleDevice.MOBILE, "gamergen.com"),

    // A LOT OF OTHER CATEGORIES !!!!
    JEUX_ACTU_NEWS("http://www.jeuxactu.com/rss/news.rss",
            ArticleCategory.NEWS,
            ArticleDevice.ALL,
            "jeuxactu.com"
    ),
    JEUX_ACTU_ANDROID("http://www.jeuxactu.com/rss/android.rss",
            ArticleCategory.GENERAL,
            ArticleDevice.MOBILE,
            "jeuxactu.com"
    ),
    JEUX_ACTU_IOS("http://www.jeuxactu.com/rss/ios.rss",
            ArticleCategory.GENERAL,
            ArticleDevice.MOBILE,
            "jeuxactu.com"
    ),
    JEUX_ACTU_3DS("http://www.jeuxactu.com/rss/tests.rss",
            ArticleCategory.TEST,
            ArticleDevice.ALL,
            "jeuxactu.com"
    ),
    JEUX_ACTU_TESTS("http://www.jeuxactu.com/rss/tests.rss",
            ArticleCategory.TEST,
            ArticleDevice.ALL,
            "jeuxactu.com"
    ),
    JEUX_ACTU_PC("http://www.jeuxactu.com/rss/pc.rss",
            ArticleCategory.TEST,
            ArticleDevice.PC,
            "jeuxactu.com"
    ),
    JEUX_ACTU_PS4("http://www.jeuxactu.com/rss/ps4.rss",
            ArticleCategory.TEST,
            ArticleDevice.PC,
            "jeuxactu.com"
    ),
    JEUX_ACTU_SWITCH("http://www.jeuxactu.com/rss/switch.rss",
            ArticleCategory.TEST,
            ArticleDevice.NINTENDO_SWITCH,
            "jeuxactu.com"
    ),
    JEUX_ACTU_XBOX_ONE("http://www.jeuxactu.com/rss/xbox-one.rss",
            ArticleCategory.TEST,
            ArticleDevice.NINTENDO_SWITCH,
            "jeuxactu.com"
    ),

    JEUXONLINE_NEWS("http://www.jeuxonline.info/rss/actualites/rss.xml",
            ArticleCategory.NEWS,
            ArticleDevice.ALL,
            "jeuxonline.info"
    ),
    JEUXONLINE_TESTS("http://www.jeuxonline.info/rss/critiques/rss.xml",
            ArticleCategory.TEST,
            ArticleDevice.ALL,
            "jeuxonline.info"),
    JEUXONLINE_VIDEO("http://www.jeuxonline.info/rss/videos/rss.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.ALL,
            "jeuxonline.info"),

    JEUXVIDEO_FR("http://www.lemonde.fr/jeux-video/rss_full.xml",
            ArticleCategory.GENERAL,
            ArticleDevice.ALL,
            "jeuxonline.info"
    ),

    // don't include currentCategory : ALL not to flood too much app with this website...
    JEUXVIDEO_COM_NEWS_PC(
            "http://www.jeuxvideo.com/rss/rss-news-pc.xml",
            ArticleCategory.NEWS,
            ArticleDevice.PC,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_NEWS_PS4(
            "http://www.jeuxvideo.com/rss/rss-news-ps4.xml",
            ArticleCategory.NEWS,
            ArticleDevice.PS4,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_NEWS_XBOX_ONE(
            "http://www.jeuxvideo.com/rss/rss-news-xo.xml",
            ArticleCategory.NEWS,
            ArticleDevice.XBOX_ONE,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_NEWS_SWITCH(
            "http://www.jeuxvideo.com/rss/rss-news-switch.xml",
            ArticleCategory.NEWS,
            ArticleDevice.NINTENDO_SWITCH,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_NEWS_IPHONE(
            "http://www.jeuxvideo.com/rss/rss-news-iphone.xml",
            ArticleCategory.NEWS,
            ArticleDevice.MOBILE,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_NEWS_ANDROID(
            "http://www.jeuxvideo.com/rss/rss-news-android.xml",
            ArticleCategory.NEWS,
            ArticleDevice.MOBILE,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_VIDEO_PC(
            "http://www.jeuxvideo.com/rss/rss-videos-pc.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.PC,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_VIDEO_PS4(
            "http://www.jeuxvideo.com/rss/rss-videos-ps4.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.PS4,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_VIDEO_XBOX_ONE(
            "http://www.jeuxvideo.com/rss/rss-videos-xo.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.XBOX_ONE,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_VIDEO_SWITCH(
            "http://www.jeuxvideo.com/rss/rss-videos-switch.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.NINTENDO_SWITCH
            , "jeuxvideo.com"),
    JEUXVIDEO_COM_VIDEO_ANDROID(
            "http://www.jeuxvideo.com/rss/rss-videos-android.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.MOBILE,
            "jeuxvideo.com"),
    JEUXVIDEO_COM_VIDEO_IPHONE(
            "http://www.jeuxvideo.com/rss/rss-videos-iphone.xml",
            ArticleCategory.VIDEO,
            ArticleDevice.MOBILE,
            "jeuxvideo.com"),

    // ALSO EXISTS WII U, PS3, PS VITA, 3DS
    JEUXVIDEO_LIVE_PS4(
            "https://feeds.feedburner.com/jvl-ps4",
            ArticleCategory.NEWS,
            ArticleDevice.PS4,
            "jeuxvideo-live"
    ),
    JEUXVIDEO_LIVE_XBOX_ONE("https://feeds.feedburner.com/jvl-xboxone",
            ArticleCategory.NEWS,
            ArticleDevice.XBOX_ONE,
            "jeuxvideo-live"

    ),
    JEUXVIDEO_LIVE_SWITCH("https://feeds.feedburner.com/jvl-switch",
            ArticleCategory.NEWS,
            ArticleDevice.NINTENDO_SWITCH,
            "jeuxvideo-live"

    ),
    JEUXVIDEO_LIVE_PC("https://feeds.feedburner.com/jvl-pc",
            ArticleCategory.NEWS,
            ArticleDevice.PC,
            "jeuxvideo-live"

    ),

    LEMONDE_JEUX_VIDEO("http://www.lemonde.fr/jeux-video/rss_full.xml",
            ArticleCategory.NEWS,
            ArticleDevice.ALL,
            "lemonde.fr"
    ),

    PIXELSPRITE("https://www.pixelsprite.fr/feed/",
            ArticleCategory.GENERAL,
            ArticleDevice.ALL,
            "pixelsprite.fr"
    ),

    REALITE_VIRTUELLE("https://www.realite-virtuelle.com/feed",
            ArticleCategory.GENERAL,
            ArticleDevice.ALL,
            "realite-virtuelle.com"
    ),

    RETRO_GAMES("http://www.retro-games.fr/feed",
            ArticleCategory.GENERAL,
            ArticleDevice.ALL,
            "retro-games.fr"
    ),

    XBOX_GAME("http://www.xbox-gamer.net/rss.php",
            ArticleCategory.GENERAL,
            ArticleDevice.XBOX_ONE,
            "xbox-gamer.fr");


    companion object {

        fun byUrl(url: String): ArticleSource? {
            ArticleSource.values().forEach { if (it.url == url) return it }
            return null
        }
    }

    //little bonus ;)
//     PODCAST_PORNDIG("https://www.porndig.com/rss/top/videos.xml",
//            ArticleCategory.VIDEO,
//            ArticleDevice.MOBILE,
//            "bonus"),
//    PODCAST_XVIDEOS("https://www.xvideos.com/rss/rss.xml",
//            ArticleCategory.VIDEO,
//            ArticleDevice.MOBILE,
//            "bonus"),
}

enum class ArticleCategory {
    GENERAL,
    NEWS,
    TEST,
    VIDEO
}

enum class ArticleDevice {
    ALL,
    PC,
    XBOX_ONE,
    PS4,
    NINTENDO_SWITCH,
    MOBILE
}
