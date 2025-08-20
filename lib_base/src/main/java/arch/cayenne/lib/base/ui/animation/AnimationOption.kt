package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * @date: 2025/8/20 15:24
 * @description:
 */
abstract class IAnimationOption(
    val type: Type,
    @Transient open val duration: Long,
    @Transient open val interpolator: IInterpolatorOption
) {
    abstract fun toAnimation(): Animation

    enum class Type {
        TranslateAnimation,
        SimpleAnimation,
    }

    companion object {
        fun fromJson(json: String): IAnimationOption {
            val type: Type = when {
                json.contains("TranslateAnimation") -> Type.TranslateAnimation
                json.contains("SimpleAnimation") -> Type.SimpleAnimation
                else -> throw IllegalStateException("cannot find type for $json")
            }
            return when (type) {
                Type.TranslateAnimation ->
                    gson.fromJson(json, TranslateAnimationOption::class.java)

                Type.SimpleAnimation ->
                    gson.fromJson(json, SimpleAnimationOption::class.java)
            }
        }

        fun toJson(option: IAnimationOption): String {
            return gson.toJson(option)
        }
    }

    fun toJson(): String {
        return gson.toJson(this)
    }
}
private val gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(IInterpolatorOption::class.java, IInterpolatorOptionDeserializer())
        .create()
}

class IInterpolatorOptionDeserializer : JsonDeserializer<IInterpolatorOption> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): IInterpolatorOption {
        val jsonObj = json.asJsonObject
        val typeElement = jsonObj.get("type")
        val typeStr = typeElement?.asString ?: throw IllegalArgumentException(
            "Missing 'type' in interpolator JSON: $jsonObj")
        return when(typeStr) {
            IInterpolatorOption.Type.LinearInterpolator.toString() ->
                context!!.deserialize(json, LinearInterpolatorOption::class.java)
            IInterpolatorOption.Type.PathInterpolator.toString() ->
                context!!.deserialize(json, PathInterpolatorOption::class.java)
            else -> throw IllegalArgumentException("Unknown interpolator type: $typeStr")
        }
    }
}
data class SimpleAnimationOption(
    override val duration: Long,
    override val interpolator: IInterpolatorOption
) : IAnimationOption(Type.SimpleAnimation, duration,interpolator) {

    override fun toAnimation(): Animation {
        return AnimationSet(false).also {
            it.duration = duration
            it.interpolator = interpolator.toInterpolator()
        }
    }
}

data class TranslateAnimationOption(
    val fromXType: Int,
    val fromXValue: Float,
    val toXType: Int,
    val toXValue: Float,
    val fromYType: Int,
    val fromYValue: Float,
    val toYType: Int,
    val toYValue: Float,
    override val duration: Long,
    override val interpolator: IInterpolatorOption,
) : IAnimationOption(Type.TranslateAnimation, duration,interpolator) {
    override fun toAnimation(): TranslateAnimation {
        return TranslateAnimation(
            fromXType, fromXValue,
            toXType, toXValue,
            fromYType, fromYValue,
            toYType, toYValue,
        ).also {
            it.duration = duration;
            it.interpolator = interpolator.toInterpolator()
        }
    }
}

abstract class IInterpolatorOption(val type: Type) {
    abstract fun toInterpolator(): Interpolator
    enum class Type {
        PathInterpolator, LinearInterpolator
    }

    companion object {
        fun fromJson(type: Type, json: String): Interpolator {
            return when (type) {
                Type.PathInterpolator ->
                    gson.fromJson(json, PathInterpolatorOption::class.java).toInterpolator()

                Type.LinearInterpolator ->
                    gson.fromJson(json, LinearInterpolatorOption::class.java).toInterpolator()
            }
        }

        fun toJson(option: IInterpolatorOption): String {
            return gson.toJson(option)
        }
    }
}

data class PathInterpolatorOption(
    val controlX1: Float,
    val controlY1: Float,
    val controlX2: Float,
    val controlY2: Float,
) : IInterpolatorOption(Type.PathInterpolator) {
    override fun toInterpolator(): Interpolator {
        return PathInterpolator(controlX1, controlY1, controlX2, controlY2)
    }
}

class LinearInterpolatorOption : IInterpolatorOption(Type.LinearInterpolator) {
    override fun toInterpolator(): Interpolator {
        return LinearInterpolator()
    }
}