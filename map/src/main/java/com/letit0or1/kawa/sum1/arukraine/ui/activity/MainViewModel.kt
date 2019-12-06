package com.letit0or1.kawa.sum1.arukraine.ui.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.letit0or1.kawa.sum1.arukraine.R
import com.letit0or1.kawa.sum1.arukraine.data.Oblast

class MainViewModel : ViewModel() {

    fun getDataForOblast(oblast: Oblast, context: Context): String {
        return if (oblast.descriptionRes == 0) {
            context.getString(R.string.desc_empty)
        } else {
            context.getString(oblast.descriptionRes)
        }
    }
}