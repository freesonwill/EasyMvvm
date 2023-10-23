package com.xcjh.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 使用 ViewBinding 快速构建 Binder
 * @param T item数据类型
 * @param VB : ViewBinding
 */
abstract class BaseViewBindingQuickAdapter<T, VB:ViewBinding>(data: List<T> = emptyList()) : BaseQuickAdapter<T, BaseViewBindingQuickAdapter.VH<VB>>(data) {

    private var vbClass: Class<*>? = null

    init {
        initVBClass()
    }
    class VH<VB>(var binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH<VB> {
        return VH(getViewBinding(LayoutInflater.from(parent.context), parent)!!)
    }


    private fun initVBClass() {
        val superclass: Type = javaClass.genericSuperclass as Type
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

}