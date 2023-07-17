package com.xcjh.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.jetbrains.annotations.NotNull
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 使用 ViewBinding 快速构建 Binder
 * @param T item数据类型
 * @param VB : ViewBinding
 */
abstract class QuickViewBindingItemBinder<T, VB : ViewBinding> :
    BaseItemBinder<T, QuickViewBindingItemBinder.BinderVBHolder<VB>>() {

    /**
     * 此 Holder 不适用于其他 BaseAdapter，仅针对[BaseBinderAdapter]
     */
    class BinderVBHolder<VB : ViewBinding>(viewBinding: VB) : BaseViewHolder(viewBinding.root)

    lateinit var mViewBind: VB
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinderVBHolder<VB> {

        /*  try {
              val superclass: Type = javaClass.genericSuperclass as Type
              val aClass = (superclass as ParameterizedType).actualTypeArguments[1]
              val method =aClass.javaClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
              binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
              return BinderVBHolder(binding)
          }catch (e:Exception){
              return BinderVBHolder(null)
          }*/
        val aClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VB>
        val method = aClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        mViewBind = binding
        return BinderVBHolder(binding)
        // return newBindingViewHolder<VB>(parent)
    }

    /*  fun <T : ViewBinding> newBindingViewHolder(parent: ViewGroup): BinderVBHolder<T> {
          val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
          val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as T
          return BinderVBHolder(binding)
      }*/
}

abstract class QuickViewBindingAdapter<T, VB : ViewBinding> :
    BaseQuickAdapter<T, QuickViewBindingAdapter.BinderVBHolder<VB>>(0) {

    lateinit var mViewBind: VB

    abstract fun getViewBinding(): VB

    /**
     * 此 Holder 不适用于其他 BaseAdapter，仅针对[BaseBinderAdapter]
     */
    class BinderVBHolder<VB : ViewBinding>(viewBinding: VB) : BaseViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinderVBHolder<VB> {
        mViewBind = getViewBinding()
        /*  try {
              val superclass: Type = javaClass.genericSuperclass as Type
              val aClass = (superclass as ParameterizedType).actualTypeArguments[1]
              val method =aClass.javaClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
              binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
              return BinderVBHolder(binding)
          }catch (e:Exception){
              return BinderVBHolder(null)
          }*/
        /*val aClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VB>
        val method = aClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        mViewBind=binding*/
        return BinderVBHolder(mViewBind)
        // return newBindingViewHolder<VB>(parent)
    }

    /*  fun <T : ViewBinding> newBindingViewHolder(parent: ViewGroup): BinderVBHolder<T> {
          val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
          val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as T
          return BinderVBHolder(binding)
      }*/
}

/**
 * ViewBinding
 * adapter
 */
class BaseCustomViewHolder<VB : ViewBinding> : BaseViewHolder {

    constructor(view: View):super(view)

    private lateinit var binding: VB

    fun getViewBinding(): VB {
        return binding
    }

    fun setViewBinding(bind: VB) {
        this.binding = bind
    }
}

abstract class BaseVBindingQuickAdapter<T : Any, VB : ViewBinding> :
    BaseQuickAdapter<T, BaseVBindingQuickAdapter.BaseVBViewHolder<VB>>(0, arrayListOf()) {
    private var vbClass: Class<*>? = null

    init {
        initVBClass()
    }

    private fun initVBClass() {
        val superclass: Type = javaClass.genericSuperclass
        val typeArr: Array<Type> = (superclass as ParameterizedType).actualTypeArguments
        for (type in typeArr) {
            val aClass = type as Class<*>
            if (ViewBinding::class.java.isAssignableFrom(aClass)) {
                vbClass = aClass
                return
            }
        }
        throw RuntimeException("你的适配器需要提供一个ViewBinding的泛型才能进行视图绑定")
    }

    override fun onCreateDefViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseVBViewHolder<VB> {
        return BaseVBViewHolder(getViewBinding(LayoutInflater.from(parent.context), parent))
    }

    private fun getViewBinding(from: LayoutInflater?, parent: ViewGroup?): VB? {
        var binding: VB? = null
        try {
            val method: Method = vbClass!!.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            binding = method.invoke(null, from, parent, false) as VB?
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return binding
    }

    class BaseVBViewHolder<VB:ViewBinding>(bind: VB?) : BaseViewHolder(bind?.root!!) {
        var binding: VB

        init {
            this.binding = bind!!
        }
    }
}