package com.letit0or1.kawa.sum1.arukraine.data

import android.content.ClipDescription
import android.content.Context
import androidx.annotation.StringRes
import com.google.ar.sceneform.math.Vector3
import com.letit0or1.kawa.sum1.arukraine.R
import java.text.FieldPosition

class OblastRepository(val context: Context) {

    fun s(@StringRes id: Int): String {
        return context.getString(id)
    }
}

enum class Oblast(
    val stringRes: Int, val modelRes: Int,
    val localScale: Vector3 = Vector3(1f, 1f, 1f),
    val localPosition: Vector3 = Vector3(-0.12f, 0f, 0f),
    val descriptionRes: Int = 0
) {
    Cherkasy(R.string.cherkasy, R.raw.cherkasy, localPosition = Vector3(0.002f, 0f, -0.018f)),
    Chernihiv(R.string.chernihiv, R.raw.chernihiv, localPosition = Vector3(0.01f, 0f, -0.062f)),
    Crimea(
        R.string.crimea, R.raw.crimea, localPosition = Vector3(0.044f, 0f, 0.06f),
        descriptionRes = R.string.desc_crimea
    ),
    Dnipropetrovsk(
        R.string.dnipropetrovsk,
        R.raw.dnipropetrovsk,
        Vector3(1.2f, 0.1f, 1f),
        Vector3(0.046f, 0f, -0.002f)
    ),
    Kherson(R.string.kherson, R.raw.kherson, localPosition = Vector3(0.024f, 0f, 0.032f)),
    Kirovograd(R.string.kirovograd, R.raw.kirovograd, localPosition = Vector3(0.002f, 0f, 0.0f)),
    Kyiv(R.string.kyiv, R.raw.kyiv, localPosition = Vector3(-0.006f, 0f, -0.036f)),
    Mykolaiv(
        R.string.mykolaiv,
        R.raw.mykolaiv,
        Vector3(0.9f, 1f, 0.9f),
        Vector3(0.002f, 0f, 0.02f)
    ),
    Odessa(R.string.odessa, R.raw.odessa, localPosition = Vector3(-0.018f, 0f, 0.038f)),
    Poltava(R.string.poltava, R.raw.poltava, localPosition = Vector3(0.038f, 0f, -0.026f)),
    Zaporizhia(R.string.zaporizhia, R.raw.zaporizhia, localPosition = Vector3(0.054f, 0f, 0.018f)),
    Sumy(R.string.sumy, R.raw.sumy, localPosition = Vector3(0.046f, 0f, -0.062f)),
    Kharkiv(R.string.kharkiv, R.raw.kharkiv, localPosition = Vector3(0.076f, 0f, -0.026f)),
    Luhansk(R.string.luhansk, R.raw.luhansk, localPosition = Vector3(0.101f, 0f, -0.014f)),
    Donetsk(R.string.donetsk, R.raw.donetsk, localPosition = Vector3(0.086f, 0f, 0.002f)),
    Chernivtsi(
        R.string.chernivtsi,
        R.raw.chernivtsi,
        Vector3(0.7f, 0.1f, 0.7f),
        Vector3(-0.07f, 0f, 0.01f)
    ),
    Khmelnitskyi(
        R.string.khmelnitskyi,
        R.raw.khmelnitskyi,
        localPosition = Vector3(-0.062f, 0f, -0.016f)
    ),
    Lviv(R.string.lviv, R.raw.lviv, localPosition = Vector3(-0.1125f, 0f, -0.0225f)),
    InavoFrankisvk(
        R.string.ivanofrankivsk,
        R.raw.ivanofrankivsk,
        localPosition = Vector3(-0.095f, 0f, -0.0025f)
    ),
    Ternopil(
        R.string.ternopil,
        R.raw.ternopil,
        Vector3(0.7f, 1f, 0.7f),
        Vector3(-0.084f, 0f, -0.0175f)
    ),
    Rivne(R.string.rivne, R.raw.rivne, localPosition = Vector3(-0.07f, 0f, -0.052f)),
    Vinnytsa(R.string.vinnytsa, R.raw.vinnytsia, localPosition = Vector3(-0.034f, 0f, 0f)),
    Volyn(R.string.volyn, R.raw.volyn, localPosition = Vector3(-0.096f, 0f, -0.056f)),
    Zakarpatia(
        R.string.zakarpatia,
        R.raw.zakarpatia,
        localPosition = Vector3(-0.12f, 0f, -0.0075f)
    ),
    Zhytomyr(R.string.zhytomyr, R.raw.zhytomyr, localPosition = Vector3(-0.038f, 0f, -0.042f))
}

