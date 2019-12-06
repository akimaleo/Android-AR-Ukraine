package com.letit0or1.kawa.uah_ar

open class AugmentedImage() {

}

class UAHImage(
    val fileName: String,
    val widthInMeters: Float,
    val type: Currency
) : AugmentedImage() {

    companion object {
        fun typeByFileName(name: String): Currency {
            return listOfCurrency.find { it.fileName == name }!!.type
        }
    }
}

enum class Currency {
    ONE_FOREGROUND,
    ONE_BACKGROUND,

    TWO_FOREGROUND,
    TWO_BACKGROUND,

    FIVE_FOREGROUND,
    FIVE_BACKGROUND,

    TEN_FOREGROUND,
    TEN_BACKGROUND
}

val listOfCurrency = listOf(
    UAHImage("one_foreground.jpg", 0.117f, Currency.ONE_FOREGROUND),
    UAHImage("one_background.jpg", 0.117f, Currency.ONE_BACKGROUND),
    UAHImage("two_foreground.jpg", 0.118f, Currency.TWO_FOREGROUND),
    UAHImage("two_background.jpg", 0.118f, Currency.TWO_BACKGROUND)
)
