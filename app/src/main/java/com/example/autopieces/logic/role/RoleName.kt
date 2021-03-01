package com.example.autopieces.logic.role

class RoleName {
    companion object{

        /**
         * 1费 13张
         */
        const val MING_REN = "鸣人"
        const val XIAO_YIN = "春野樱"
        const val CHU_TIAN = "日向雏田"
        const val DING_CI = "秋道丁次"
        const val ZHONG_WU = "天秤重吾"
        const val XIE = "赤砂之蝎"
        const val KAN_JIU_LANG = "勘九郎"
        const val CHI_TU = "赤土"
        const val LI = "李"
        const val YOU_LI = "林檎雨由利"
        const val ER_REN = "通草野饵人"
        const val TIAN_TIAN = "天天"
        const val ZHI_NAI = "油女志乃"

        /**
         * 2费 13张
         */
        const val ZUO_ZU = "宇智波佐助"
        const val NING_CI = "日向宁次"
        const val JING_YE = "山中井野"
        const val HE_TUN_GUI = "西瓜山河豚鬼"
        const val SHEN_BA = "无梨甚八"
        const val CHUAN_WAN = "栗霰串丸"
        const val CHANG_SHI_LANG = "长十郎"
        const val JIAO_DU = "角都"
        const val DOU = "兜"
        const val XIAO_NAN = "小南"
        const val SHOU_JU = "手鞠"
        const val YIN_JIAO = "银角"
        const val BAI = "白"

        /**
         * 3费 13张
         */
        const val JIN_JIAO = "金角"
        const val ZAI_BU_ZHAN = "桃地再不斩"
        const val ZUO_JIN = "佐井"
        const val SHUI_YUE = "鬼灯水月"
        const val JUN_MA_LV = "君麻吕"
        const val DI_DA_LA = "迪达拉"
        const val JUE = "绝"
        const val FEI_DUAN = "飞段"
        const val DA_HE = "大和"
        const val LU_WAN = "奈良鹿丸"
        const val DA_LU_YI = "达鲁伊"
        const val XIANG_LIN = "香磷"
        const val QI_LA_BI = "奇拉比"

        /**
         * 4费 11张
         */
        const val GUI_JIAO = "干柿鬼鲛"
        const val DA_YE_MU = "两天秤大野木"
        const val SI_DAI_LEI_YING = "四代雷影"
        const val KAKA_XI = "卡卡西"
        const val TUAN_ZANG = "团藏"
        const val GUI_DENG_MAN_YUE = "鬼灯满月"
        const val ZHAO_MEI_MIN = "照美冥"
        const val SHI_CANG = "矢仓"
        const val WO_AI_LUO = "我爱罗"
        const val PAIN = "佩恩"
        const val FEI_JIAN = "千手扉间"

        /**
         * 5费 8张
         */
        const val BAN = "宇智波斑"
        const val ZI_LAI_YE = "自来也"
        const val GANG_SHOU = "纲手"
        const val DA_SHE_WAN = "大蛇丸"
        const val YOU = "宇智波鼬"
        const val SAN_DAI_LEI_YING = "三代雷影"
        const val KAI = "凯"
        const val ZHU_JIAN = "千手柱间"

        val cost1RolesArray = arrayOf(
                RoleName.MING_REN,
                RoleName.XIAO_YIN,
                RoleName.CHU_TIAN,
                RoleName.DING_CI,
                RoleName.ZHONG_WU,
                RoleName.XIE,
                RoleName.KAN_JIU_LANG,
                RoleName.CHI_TU,
                RoleName.LI,
                RoleName.YOU_LI,
                RoleName.ER_REN,
                RoleName.TIAN_TIAN,
                RoleName.ZHI_NAI
        )

        val cost2RolesArray = arrayOf(
                RoleName.ZUO_ZU,
                RoleName.NING_CI,
                RoleName.JING_YE,
                RoleName.HE_TUN_GUI,
                RoleName.SHEN_BA,
                RoleName.CHUAN_WAN,
                RoleName.CHANG_SHI_LANG,
                RoleName.JIAO_DU,
                RoleName.DOU,
                RoleName.XIAO_NAN,
                RoleName.SHOU_JU,
                RoleName.YIN_JIAO,
                RoleName.BAI
        )

        val cost3RolesArray = arrayOf(
                RoleName.JIN_JIAO,
                RoleName.ZAI_BU_ZHAN,
                RoleName.ZUO_JIN,
                RoleName.SHUI_YUE,
                RoleName.JUN_MA_LV,
                RoleName.DI_DA_LA,
                RoleName.JUE,
                RoleName.FEI_DUAN,
                RoleName.DA_HE,
                RoleName.LU_WAN,
                RoleName.DA_LU_YI,
                RoleName.XIANG_LIN,
                RoleName.QI_LA_BI
        )

        val cost4RolesArray = arrayOf(
                RoleName.GUI_JIAO,
                RoleName.DA_YE_MU,
                RoleName.SI_DAI_LEI_YING,
                RoleName.KAKA_XI,
                RoleName.TUAN_ZANG,
                RoleName.GUI_DENG_MAN_YUE,
                RoleName.ZHAO_MEI_MIN,
                RoleName.SHI_CANG,
                RoleName.WO_AI_LUO,
                RoleName.PAIN,
                RoleName.FEI_JIAN
        )

        val cost5RolesArray = arrayOf(
                RoleName.BAN,
                RoleName.ZI_LAI_YE,
                RoleName.GANG_SHOU,
                RoleName.DA_SHE_WAN,
                RoleName.YOU,
                RoleName.SAN_DAI_LEI_YING,
                RoleName.KAI,
                RoleName.ZHU_JIAN
        )

        val rolesArray = arrayOf(
                cost1RolesArray,
                cost2RolesArray,
                cost3RolesArray,
                cost4RolesArray,
                cost5RolesArray
        )
    }
}

fun roleCost(roleName:String):Int{
    RoleName.rolesArray.forEachIndexed { index, roleNames ->
        if (roleNames.contains(roleName))
            return index+1
    }
    return 1
}