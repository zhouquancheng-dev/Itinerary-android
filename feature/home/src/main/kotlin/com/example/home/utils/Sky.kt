package com.example.home.utils

import androidx.annotation.DrawableRes
import com.example.home.R

/**
 * @param info 天气现象描述
 * @param icon 对应的图标
 * @param iconFill 对应的填充图标
 */
class Sky(private val info: String, @DrawableRes val icon: Int, @DrawableRes val iconFill: Int)

fun getSky(skyCon: String): Sky {
    return (sky[skyCon] ?: sky["999"])!!
}

private val sky: Map<String, Sky> = mapOf(
    "100" to Sky("晴", R.drawable.ic_qweather_100, R.drawable.ic_qweather_100_fill),
    "101" to Sky("多云", R.drawable.ic_qweather_101, R.drawable.ic_qweather_101_fill),
    "102" to Sky("少云", R.drawable.ic_qweather_102, R.drawable.ic_qweather_102_fill),
    "103" to Sky("晴间多云", R.drawable.ic_qweather_103, R.drawable.ic_qweather_103_fill),
    "104" to Sky("阴", R.drawable.ic_qweather_104, R.drawable.ic_qweather_104_fill),
    "150" to Sky("晴", R.drawable.ic_qweather_150, R.drawable.ic_qweather_150_fill),
    "151" to Sky("多云", R.drawable.ic_qweather_151, R.drawable.ic_qweather_151_fill),
    "152" to Sky("少云", R.drawable.ic_qweather_152, R.drawable.ic_qweather_152_fill),
    "153" to Sky("晴间多云", R.drawable.ic_qweather_153, R.drawable.ic_qweather_153_fill),
    "300" to Sky("阵雨", R.drawable.ic_qweather_300, R.drawable.ic_qweather_300_fill),
    "301" to Sky("强阵雨", R.drawable.ic_qweather_301, R.drawable.ic_qweather_301_fill),
    "302" to Sky("雷阵雨", R.drawable.ic_qweather_302, R.drawable.ic_qweather_302_fill),
    "303" to Sky("强雷阵雨", R.drawable.ic_qweather_303, R.drawable.ic_qweather_303_fill),
    "304" to Sky("雷阵雨伴有冰雹", R.drawable.ic_qweather_304, R.drawable.ic_qweather_304_fill),
    "305" to Sky("小雨", R.drawable.ic_qweather_305, R.drawable.ic_qweather_305_fill),
    "306" to Sky("中雨", R.drawable.ic_qweather_306, R.drawable.ic_qweather_306_fill),
    "307" to Sky("大雨", R.drawable.ic_qweather_307, R.drawable.ic_qweather_307_fill),
    "308" to Sky("极端降雨", R.drawable.ic_qweather_308, R.drawable.ic_qweather_308_fill),
    "309" to Sky("毛毛雨", R.drawable.ic_qweather_309, R.drawable.ic_qweather_309_fill),
    "310" to Sky("暴雨", R.drawable.ic_qweather_310, R.drawable.ic_qweather_310_fill),
    "311" to Sky("大暴雨", R.drawable.ic_qweather_311, R.drawable.ic_qweather_311_fill),
    "312" to Sky("特大暴雨", R.drawable.ic_qweather_312, R.drawable.ic_qweather_312_fill),
    "313" to Sky("冻雨", R.drawable.ic_qweather_313, R.drawable.ic_qweather_313_fill),
    "314" to Sky("小到中雨", R.drawable.ic_qweather_314, R.drawable.ic_qweather_314_fill),
    "315" to Sky("中到大雨", R.drawable.ic_qweather_315, R.drawable.ic_qweather_315_fill),
    "316" to Sky("大到暴雨", R.drawable.ic_qweather_316, R.drawable.ic_qweather_316_fill),
    "317" to Sky("暴雨到大暴雨", R.drawable.ic_qweather_317, R.drawable.ic_qweather_317_fill),
    "318" to Sky("大暴雨到特大暴雨", R.drawable.ic_qweather_318, R.drawable.ic_qweather_318_fill),
    "350" to Sky("阵雨", R.drawable.ic_qweather_350, R.drawable.ic_qweather_350_fill),
    "351" to Sky("强阵雨", R.drawable.ic_qweather_351, R.drawable.ic_qweather_351_fill),
    "399" to Sky("雨", R.drawable.ic_qweather_399, R.drawable.ic_qweather_399_fill),
    "400" to Sky("小雪", R.drawable.ic_qweather_400, R.drawable.ic_qweather_400_fill),
    "401" to Sky("中雪", R.drawable.ic_qweather_401, R.drawable.ic_qweather_401_fill),
    "402" to Sky("大雪", R.drawable.ic_qweather_405, R.drawable.ic_qweather_405_fill),
    "403" to Sky("暴雪", R.drawable.ic_qweather_403, R.drawable.ic_qweather_403_fill),
    "404" to Sky("雨夹雪", R.drawable.ic_qweather_404, R.drawable.ic_qweather_404_fill),
    "405" to Sky("雨雪天气", R.drawable.ic_qweather_405, R.drawable.ic_qweather_405_fill),
    "406" to Sky("阵雨夹雪", R.drawable.ic_qweather_406, R.drawable.ic_qweather_406_fill),
    "407" to Sky("阵雪", R.drawable.ic_qweather_407, R.drawable.ic_qweather_407_fill),
    "408" to Sky("小到中雪", R.drawable.ic_qweather_408, R.drawable.ic_qweather_408_fill),
    "409" to Sky("中到大雪", R.drawable.ic_qweather_409, R.drawable.ic_qweather_409_fill),
    "410" to Sky("大到暴雪", R.drawable.ic_qweather_410, R.drawable.ic_qweather_410_fill),
    "456" to Sky("阵雨夹雪", R.drawable.ic_qweather_456, R.drawable.ic_qweather_456_fill),
    "457" to Sky("阵雪", R.drawable.ic_qweather_457, R.drawable.ic_qweather_457_fill),
    "499" to Sky("雪", R.drawable.ic_qweather_499, R.drawable.ic_qweather_499_fill),
    "500" to Sky("薄雾", R.drawable.ic_qweather_500, R.drawable.ic_qweather_500_fill),
    "501" to Sky("雾", R.drawable.ic_qweather_501, R.drawable.ic_qweather_501_fill),
    "502" to Sky("霾", R.drawable.ic_qweather_502, R.drawable.ic_qweather_502_fill),
    "503" to Sky("扬沙", R.drawable.ic_qweather_503, R.drawable.ic_qweather_503_fill),
    "504" to Sky("浮尘", R.drawable.ic_qweather_504, R.drawable.ic_qweather_504_fill),
    "507" to Sky("沙尘暴", R.drawable.ic_qweather_507, R.drawable.ic_qweather_507_fill),
    "508" to Sky("强沙尘暴", R.drawable.ic_qweather_508, R.drawable.ic_qweather_508_fill),
    "509" to Sky("浓雾", R.drawable.ic_qweather_509, R.drawable.ic_qweather_509_fill),
    "510" to Sky("强浓雾", R.drawable.ic_qweather_510, R.drawable.ic_qweather_510_fill),
    "511" to Sky("中度霾", R.drawable.ic_qweather_511, R.drawable.ic_qweather_511_fill),
    "512" to Sky("重度霾", R.drawable.ic_qweather_512, R.drawable.ic_qweather_512_fill),
    "513" to Sky("严重霾", R.drawable.ic_qweather_513, R.drawable.ic_qweather_513_fill),
    "514" to Sky("大雾", R.drawable.ic_qweather_514, R.drawable.ic_qweather_514_fill),
    "515" to Sky("特强浓雾", R.drawable.ic_qweather_515, R.drawable.ic_qweather_515_fill),
    "900" to Sky("热", R.drawable.ic_qweather_900, R.drawable.ic_qweather_900_fill),
    "901" to Sky("冷", R.drawable.ic_qweather_901, R.drawable.ic_qweather_901_fill),
    "999" to Sky("未知", R.drawable.ic_qweather_999, R.drawable.ic_qweather_999_fill)
)