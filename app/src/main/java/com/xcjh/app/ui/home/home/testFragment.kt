package com.xcjh.app.ui.home.home

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DefaultItemAnimator
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.adapter.*
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.DiffEntity
import com.xcjh.app.bean.SwitchVideoModel
import com.xcjh.app.databinding.HeadViewBinding
import com.xcjh.app.databinding.ItemDifferBinding
import com.xcjh.app.databinding.TtComRefreshListBinding
import com.xcjh.app.utils.smartListData
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.bean.ListDataUiState


class testFragment : BaseFragment<MainVm, TtComRefreshListBinding>() {

    private val mAdapter = DiffUtilAdapter()
    // private val mAdapter = TestAdapter()
    // private val mAdapter = Test1Adapter()
    lateinit var entities:MutableList<DiffEntity>
    //  private val mAdapter = Test2Adapter()
    //  private val mAdapter = TestMultiAdapter()
    override fun initView(savedInstanceState: Bundle?) {

        entities = getDiffUtilDemoEntities()
        setAdapter()
        //initEvent()

    }

    private fun setAdapter() {
        mDatabind.smartListView.rcvCommon.linear().setup {
            addType<DiffEntity>(R.layout.item_differ)
            addType<SwitchVideoModel>(R.layout.head_view)
            itemDifferCallback = object : ItemDifferCallback {
                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return if (oldItem is DiffEntity && newItem is DiffEntity) {
                        oldItem.id == newItem.id
                    } else super.areContentsTheSame(oldItem, newItem)

                }

                override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                    return true
                }
            }
            onCreate {
                //getBinding<ItemDifferBinding>().tvTitle.text = getModel<DiffEntity>().title//"文本内容"
            }
            onBind {
                /* when (getBinding<ViewBinding>()) {
                    is ItemDifferBinding -> {
                        //viewBinding.tvName.text = "文本内容"
                    }
                    is HeadViewBinding -> {
                        //viewBinding.tvName.text = "类型2-文本内容"
                    }
                }*/
                when (val model = getModel<Any>()) {
                    is DiffEntity -> {
                        /*model.input = "消息内容"
                        model.notifyChange()*/
                        // val model = getModel<DiffEntity>()
                        getBinding<ItemDifferBinding>().tvTitle.text = model.title
                    }
                    is SwitchVideoModel -> {
                        /* model.input = "评论内容"
                        model.notifyChange()*/
                        // val model = getModel<DiffEntity>()
                        getBinding<HeadViewBinding>().tvTitle.text = model.name
                    }
                }
                /* when(itemViewType) {
                    R.layout.item_differ -> {
                        val model = getModel<DiffEntity>()
                        getBinding<ItemDifferBinding>().tvTitle.text = model.title
                    }
                    R.layout.head_view -> {

                    }
                   *//* is ItemDifferBinding -> {
                            val model = getModel<DiffEntity>()
                            binding.tvTitle.text = model.title
                        }
                        is HeadViewBinding -> {
                            binding.tvTitle.text = "类型2-文本内容"
                        }*//*
                        // findView<TextView>(R.id.tvTitle).text = getModel<DiffEntity>().title
                    }*/
            }
        }.models = entities
        mDatabind.smartListView.smartCommon.setOnLoadMoreListener {
            mDatabind.smartListView.smartCommon.finishLoadMore()
            mDatabind.smartListView.rcvCommon.bindingAdapter.addModels(getDiffUtilMore())
        }
        mDatabind.smartListView.smartCommon.setOnRefreshListener {
            mDatabind.smartListView.smartCommon.finishRefresh()
            val models = mDatabind.smartListView.rcvCommon.bindingAdapter.models
            if (models == null) {
                mDatabind.smartListView.rcvCommon.bindingAdapter.models = getDiffUtilDemoEntities()
            } else {
                if (models is MutableList) {
                    models.clear()
                    mDatabind.smartListView.rcvCommon.bindingAdapter.checkedPosition.clear()
                    mDatabind.smartListView.rcvCommon.bindingAdapter.addModels(
                        getDiffUtilDemoEntities()
                    )
                }
            }
        }
        mDatabind.change.setOnClickListener {

            mDatabind.smartListView.rcvCommon.setDifferModels(newList, true)
        }
        mDatabind.refresh.setOnClickListener {
            entities.add(
                DiffEntity(
                    1111,
                    "===========1=== $1",
                    "This item $1 content",
                    "06-12"
                )
            )

            mDatabind.smartListView.rcvCommon.bindingAdapter.notifyItemInserted(entities.size)
            // mDatabind.smartListView.rcvCommon.setDifferModels(getDiffUtilDemoEntities(), false)
        }
    }


    private fun initEvent() {
        mDatabind.smartListView.rcvCommon.layoutManager =
            QuickGridLayoutManager(requireContext(), 3)
        // mDatabind.smartListView.rcvCommon.addItemDecoration(GlmSpaceItemDecoration(3, 20, 20))
        val helper = QuickAdapterHelper.Builder(mAdapter)
            .build().addBeforeAdapter(HeaderAdapter())
        val itemAnimator = DefaultItemAnimator()
        /*itemAnimator.addDuration = 3000

        itemAnimator.changeDuration = 1000*/
        itemAnimator.removeDuration = 3000
        //itemAnimator.moveDuration = 1000
        mDatabind.smartListView.rcvCommon.itemAnimator = itemAnimator
        mDatabind.smartListView.rcvCommon.adapter = helper.adapter
        // 设置diff数据（默认就为异步Diff，不需要担心卡顿）
        mAdapter.submitList(getDiffUtilDemoEntities())

        mDatabind.change.setOnClickListener {
            // 第二次改变数据
            mAdapter.submitList(newList)
            Log.e("===", "initEvent: ===" + mAdapter.items[3].title)
        }
        mDatabind.refresh.setOnClickListener {
            mAdapter.submitList(getDiffUtilDemoEntities())
           // mAdapter.notifyItemChanged(2, 3)
        }
        // 监听 ViewHolder 附加到窗口的状态
        /* mAdapter.addOnViewAttachStateChangeListener(object :
             BaseQuickAdapter.OnViewAttachStateChangeListener {
             override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {

                 Log.e("TAG", "onViewAttachedToWindow: ====" + holder.position)
             }

             override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
                 Log.e("TAG", "onViewDetachedFromWindow: ====" + holder.position)
             }
         })*/
        mDatabind.smartListView.smartCommon.setOnLoadMoreListener {
            smartListData(
                requireContext(),
                ListDataUiState(isSuccess = true, isRefresh = false, listData = getDiffUtilMore()),
                mAdapter,
                mDatabind.smartListView.smartCommon,
                imgEmptyId = R.drawable.tp_empty
            )
        }
        mDatabind.smartListView.smartCommon.setOnRefreshListener {
            smartListData(
                requireContext(),
                ListDataUiState(isSuccess = true, isRefresh = true, listData = getDiffUtilMore()),
                mAdapter,
                mDatabind.smartListView.smartCommon,
                imgEmptyId = R.drawable.tp_empty
            )
        }
    }

    private fun getDiffUtilDemoEntities(): MutableList<DiffEntity> {
        val list: MutableList<DiffEntity> = arrayListOf()
        for (i in 0..9) {
            list.add(
                DiffEntity(
                    i,
                    "Item $i",
                    "This item $i content",
                    "06-12"
                )
            )
        }
        return list
    }

    private fun getDiffUtilMore(): MutableList<DiffEntity> {
        val list: MutableList<DiffEntity> = arrayListOf()
        var n = mAdapter.items.size
        for (i in n until n + 5) {
            list.add(
                DiffEntity(
                    i,
                    "Item $i",
                    "This item $i content",
                    "06-12"
                )
            )
        }
        return list
    }

    private val newList: MutableList<DiffEntity>
        get() {
            val list: MutableList<DiffEntity> = ArrayList()
            for (i in 0..9) {

                if (i == 1) {
                    list.add(
                        DiffEntity(
                            3,
                            "Item " + 3,
                            "This item " + 3 + " content",
                            "06-12"
                        )
                    )
                }/* else if (i == 2) {
                    list.add(
                        DiffEntity(
                            2,
                            "Item " + 22,
                            "This item " + 22 + " content",
                            "06-12"
                        )
                    )
                }*/ else if (i == 3) {
                    list.add(
                        DiffEntity(
                            1,
                            "Item " + 1,
                            "This item " + 1 + " content",
                            "06-12"
                        )
                    )
                } else {
                    //val i = i+n
                    list.add(
                        DiffEntity(
                            i,
                            "Item " + i,
                            "This item " + i + " content",
                            "06-12"
                        )
                    )
                }
            }
            return list
        }

    override fun createObserver() {
        /* appViewModel.updateLoginEvent.observe(this) {
             mViewModel.getUserBaseInfo()
         }*/
    }


}