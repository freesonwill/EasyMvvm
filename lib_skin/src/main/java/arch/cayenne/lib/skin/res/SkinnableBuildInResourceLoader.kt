package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import arch.cayenne.lib.skin.util.ResUtils
import arch.cayenne.lib.skin.widget.helper.SkinnableHelper
import androidx.core.graphics.drawable.toDrawable

class SkinnableBuildInResourceLoader(private val _skinName: String) : SkinnableResourceLoader {
    private var _secondarySkinName: String? = null
    private var _fixedSkinName: String? = null
    private val currentName: String
        get() = _fixedSkinName ?: (_secondarySkinName ?: _skinName)

    override fun getColor(context: Context, resId: Int): Int {
        val targetId = getTargetResourceId(context, resId)
        if (targetId != SkinnableHelper.INVALID_ID) {
            return ResourcesCompat.getColor(context.resources, targetId, context.theme)
        }
        return SkinnableHelper.INVALID_ID
    }

    override fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        val targetId = getTargetResourceId(context, resId)
        if (targetId != SkinnableHelper.INVALID_ID) {
            return AppCompatResources.getColorStateList(context, targetId)
        }
        return null
    }

    override fun getDrawable(context: Context, resId: Int): Drawable? {
        val targetId = getTargetResourceId(context, resId)
        if (targetId != SkinnableHelper.INVALID_ID) {
            val type = context.resources.getResourceTypeName(resId)
            if (type == "color") {
                return context.getColor(targetId).toDrawable()
            }

            return ResourcesCompat.getDrawable(context.resources, targetId, context.theme)
        }
        return null
    }

    override fun getTargetResourceId(context: Context, resId: Int): Int {
        return try {
            if (_skinName.isEmpty() && _secondarySkinName.isNullOrEmpty()) {
                return resId
            }
            val targetResId = getResId(context, currentName, resId)

            if (targetResId == 0) {
                return resId
            }
            return targetResId
        } catch (e: Exception) {
            e.printStackTrace()
            SkinnableHelper.INVALID_ID
        }
    }

    @AnyRes
    private fun getResId(context: Context, skinName: String, resId: Int): Int {
        val resName = context.resources.getResourceEntryName(resId) + "_" + skinName
        val type = context.resources.getResourceTypeName(resId)
        return ResUtils.getSuffixResourceId(context, resName, type)
    }

    override fun getSkinName(): String {
        return currentName
    }

    override fun getOriginResourceId(context: Context, resName: String, @AnyRes resId: Int): Int {
        val type = context.resources.getResourceTypeName(resId)
        val originId = ResUtils.getOriginalResourceId(context,resName,type)
         if(originId == 0){
             return resId
         }
        return originId
    }

    override fun setSecondarySkin(skinName: String) {
        this._secondarySkinName = skinName
    }

    override fun setFixedSkin(skin: String?) {
        this._fixedSkinName = skin
    }

    override fun getFixedSkin(): String? {
        return this._fixedSkinName
    }

    override fun getSecondarySkin(): String? {
        return this._secondarySkinName
    }
}