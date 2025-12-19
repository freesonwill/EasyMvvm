package com.walisport.module.misc.ui.viewmodel

import android.content.Context
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.misc.util.GallerySaveManager
import plugin.koin.KoinViewModel

@KoinViewModel
class InviteFriendsViewModel() : BaseViewModel() {

    override fun initViewModel() {
        super.initViewModel()
    }


    fun saveImage(base64: String,context: Context){
        GallerySaveManager.saveBase64ToGallery(context,base64)
    }


}