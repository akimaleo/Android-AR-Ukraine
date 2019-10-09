package com.letit0or1.kawa.sum1.arukraine.ui.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.letit0or1.kawa.sum1.arukraine.data.Oblast

class MainViewModel : ViewModel() {


    fun getDataForOblast(oblast: Oblast, context: Context): String {
        return context.getString(oblast.descriptionRes)
    }
}