package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.appcompat.graphics.drawable.StateListDrawableCompat
import androidx.core.content.res.ResourcesCompat
import arch.cayenne.lib.skin.util.ResUtils
import arch.cayenne.lib.skin.widget.helper.SportSkinHelper

class SportSkinBuildInResourceLoader(val skinName: String) : SportSkinResourceLoader {


    override fun getColor(context: Context, resId: Int): Int {
        val targetId = getTargetResourceId(context, resId,)
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
            if(type == "color"){
                return ColorDrawable(context.getColor(targetId))
            }

            return ResourcesCompat.getDrawable(context.resources, targetId, context.theme)
        }
        return null
    }

    override fun getTargetResourceId(context: Context, resId: Int): Int {
        return try {
            if(skinName.isEmpty()){
                return resId
            }
            val resName =  context.resources.getResourceEntryName(resId) + "_" + skinName
            val type = context.resources.getResourceTypeName(resId)
            val targetResId = ResUtils.getResourceId(context,resName,type)
            if(targetResId == 0){
                return resId
            }
            return targetResId
        } catch (e: Exception) {
            e.printStackTrace()
            SportSkinHelper.INVALID_ID
        }
    }
}