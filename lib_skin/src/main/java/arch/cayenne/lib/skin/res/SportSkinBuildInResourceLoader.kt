package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.Log
import androidx.annotation.AnyRes
import androidx.appcompat.graphics.drawable.StateListDrawableCompat
import androidx.core.content.res.ResourcesCompat
import arch.cayenne.lib.skin.util.ResUtils
import arch.cayenne.lib.skin.widget.helper.SportSkinHelper

class SportSkinBuildInResourceLoader(val _skinName: String) : SportSkinResourceLoader {
    private var _secondarySkinName: String = ""
    private var currentName = _skinName

    override fun getColor(context: Context, resId: Int): Int {
        val targetId = getTargetResourceId(context, resId)
        if (targetId != SportSkinHelper.INVALID_ID) {
            return ResourcesCompat.getColor(context.resources, targetId, context.theme)
        }
        return SportSkinHelper.INVALID_ID
    }

    override fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        val targetId = getTargetResourceId(context, resId)
        if (targetId != SportSkinHelper.INVALID_ID) {
            return ResourcesCompat.getColorStateList(context.resources, targetId, context.theme)
        }
        return null
    }

    override fun getDrawable(context: Context, resId: Int): Drawable? {
        val targetId = getTargetResourceId(context, resId)
        if (targetId != SportSkinHelper.INVALID_ID) {
            val type = context.resources.getResourceTypeName(resId)
            if (type == "color") {
                return ColorDrawable(context.getColor(targetId))
            }

            return ResourcesCompat.getDrawable(context.resources, targetId, context.theme)
        }
        return null
    }

    override fun getTargetResourceId(context: Context, resId: Int): Int {
        return try {
            if (_skinName.isEmpty() && _secondarySkinName.isEmpty()) {
                return resId
            }
            var targetResId = 0
            if (_secondarySkinName.isNotEmpty()) {
                currentName = _secondarySkinName
                targetResId = getResId(context, _secondarySkinName, resId)
            }
            if (targetResId == 0 && _skinName.isNotEmpty()) {
                currentName = _skinName
                targetResId = getResId(context, _skinName, resId)
            }

            if (targetResId == 0) {
                return resId
            }
            return targetResId
        } catch (e: Exception) {
            e.printStackTrace()
            SportSkinHelper.INVALID_ID
        }
    }

    @AnyRes
    private fun getResId(context: Context, skinName: String, resId: Int): Int {
        val resName = context.resources.getResourceEntryName(resId) + "_" + skinName
        val type = context.resources.getResourceTypeName(resId)
        return ResUtils.getResourceId(context, resName, type)
    }

    override fun getSkinName(): String {
        return currentName
    }

    override fun setSecondarySkin(skinName: String) {
        this._secondarySkinName = skinName
    }
}