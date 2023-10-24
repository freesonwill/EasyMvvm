package com.xcjh.app.ui.home.my.operate

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils
import com.xcjh.app.R
import com.xcjh.app.adapter.GridImageAdapter
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.ContactBean
import com.xcjh.app.databinding.ActivityContactUsBinding
import com.xcjh.app.utils.FullyGridLayoutManager
import com.xcjh.app.utils.GlideEngine
import com.xcjh.app.utils.nice.Utils
import com.xcjh.app.utils.picture.ImageFileCompressEngine
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.bindadapter.CustomBindAdapter.afterTextChanged
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.io.File

/**
 * 联系我们
 */
class ContactUsActivity : BaseActivity<ContactUsVm, ActivityContactUsBinding>() {
    //问题类型
    var questionList=ArrayList<ContactBean>()
    private var mAdapter: GridImageAdapter? = null
    private var maxSelectNum:Int = 3
    private var mData=ArrayList<LocalMedia>()
    private var language = LanguageConfig.UNKNOWN_LANGUAGE
    //选择的问题类型
    var selectType:ContactBean?=null


    override fun initView(savedInstanceState: Bundle?) {

        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.contact_us)
        var contactBean=ContactBean(1,resources.getString(R.string.txt_feedtype1))
        questionList.add(contactBean)
        contactBean=ContactBean(2,resources.getString(R.string.txt_feedtype2))
        questionList.add(contactBean)
        contactBean=ContactBean(3,resources.getString(R.string.txt_feedtype3))
        questionList.add(contactBean)
        contactBean=ContactBean(4,resources.getString(R.string.txt_feedtype4))
        questionList.add(contactBean)
        contactBean=ContactBean(5,resources.getString(R.string.txt_feedtype5))
        questionList.add(contactBean)
        contactBean=ContactBean(6,resources.getString(R.string.txt_feedtype6))
        questionList.add(contactBean)
        var gridViewAdapter = GridViewAdapter(this, questionList)
        mDatabind.gvContactGrid.adapter = gridViewAdapter

        //监听输入的文字
        mDatabind.editContactInput.afterTextChanged{
            if(it.isNotEmpty()){
                mDatabind.txtContactNum.text="${it.length}/200"
            }else{
                mDatabind.txtContactNum.text="0/200"
            }
            isSelect()

        }

        val manager = FullyGridLayoutManager(
            this,
            3, GridLayoutManager.VERTICAL, false
        )
        mDatabind.recycler.layoutManager = manager
        val itemAnimator= mDatabind.recycler.getItemAnimator()
        if (itemAnimator != null) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        mDatabind.recycler.addItemDecoration(
            GridSpacingItemDecoration(
                3, DensityUtil.dip2px(this, 8f), false
            )
        )
        mAdapter = GridImageAdapter(this, mData)
        mAdapter!!.selectMax = maxSelectNum
        mDatabind.recycler.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : GridImageAdapter.OnItemClickListener {
            //预览
            override fun onItemClick(v: View?, position: Int) {
                PictureSelector.create(this@ContactUsActivity)
                    .openPreview()
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setLanguage(language)
                    .setExternalPreviewEventListener(  MyExternalPreviewEventListener())
                    .startActivityPreview(position, true, mAdapter!!.getData())

            }

            override fun openPicture() {
                PictureSelector.create(this@ContactUsActivity)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setCompressEngine(ImageFileCompressEngine())
                    .setSelectionMode(SelectModeConfig.MULTIPLE)
                    .setLanguage(language)
                    .isPageStrategy(true)
                    .setMaxSelectNum(maxSelectNum)
                    .isGif(false)
                    .isPageSyncAlbumCount(true)
                    .setSelectedData(mAdapter!!.data)
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {
                            if(result!=null&&result!!.size>0){
                                analyticalSelectResults(result)
                            }



                        }

                        override fun onCancel() {

                        }

                    })
            }

            override fun onDelete() {

                isSelect()
            }

        })

        //点击提交
        mDatabind.txtContactSub.clickNoRepeat {
            if(selectType!=null&&mAdapter!!.data.size>0&& mDatabind.editContactInput.text.toString().trim().isNotEmpty()){
                mViewModel.showLoading()
                mViewModel.imageList.clear()
                mViewModel.upLoadPic(0,mAdapter!!.data)

            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.uploading.observe(this){
            if(mViewModel.imageList.size==mAdapter!!.data.size){
                    mViewModel.submitFeedback(selectType!!.id,mDatabind.editContactInput.text.toString().trim(),mViewModel.imageList)
            }
        }

        mViewModel.submit.observe(this){
            myToast(resources.getString(R.string.contact_txt_submit_succeed))
            finish()
        }
    }


    inner  class GridViewAdapter(context: Context?, girdItem: ArrayList<ContactBean>) : BaseAdapter() {
        var context: Context? = null
        var list: ArrayList<ContactBean>? = null
        init {
            this.context = context
            this.list = girdItem
        }

        override fun getCount(): Int {
            return this.list!!.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var holder: Holder? = null
            var view: View? = null
            if (holder == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_contact_type, null)
                holder = Holder()

                holder.txtContactName = view.findViewById(R.id.txtContactName)
                holder.rlClick=view.findViewById(R.id.rlClick)
            } else {
                holder = view!!.tag as Holder
            }
            holder.txtContactName.text= list!![position].name
            if(list!![position].select){
                holder.rlClick.background=ContextCompat.getDrawable(context!!,R.drawable.shape_r8_6d48fe)
            }else{
                holder.rlClick.background=ContextCompat.getDrawable(context!!,R.drawable.shape_r8_18152b)
            }

            holder.rlClick.clickNoRepeat {

                list!!.forEach {
                    it.select=false
                }
                list!![position].select=true
                selectType=list!![position]
                isSelect()
                notifyDataSetChanged()
            }


            return view!!
        }


    }


    class Holder {

        lateinit var txtContactName: AppCompatTextView
        lateinit var rlClick: RelativeLayout


    }


    /**
     * 处理选择结果
     *
     * @param result
     */
    private fun analyticalSelectResults(result: java.util.ArrayList<LocalMedia>) {
        for (media in result) {
            if (media.width == 0 || media.height == 0) {
                if (PictureMimeType.isHasImage(media.mimeType)) {
                    val imageExtraInfo = MediaUtils.getImageSize(this, media.path)
                    media.width = imageExtraInfo.width
                    media.height = imageExtraInfo.height
                } else if (PictureMimeType.isHasVideo(media.mimeType)) {
                    val videoExtraInfo = MediaUtils.getVideoSize(this, media.path)
                    media.width = videoExtraInfo.width
                    media.height = videoExtraInfo.height
                }
            }

//            Log.i("DDDDD", "文件名: " + media.fileName)
//            Log.i("DDDDD", "是否压缩:" + media.isCompressed)
//            Log.i("DDDDD", "压缩:" + media.compressPath)
//            Log.i("DDDDD", "初始路径:" + media.path)
//            Log.i("DDDDD", "绝对路径:" + media.realPath)
//            Log.i("DDDDD", "是否裁剪:" + media.isCut)
//            Log.i("DDDDD", "裁剪路径:" + media.cutPath)
//            Log.i("DDDDD", "是否开启原图:" + media.isOriginal)
//            Log.i("DDDDD", "原图路径:" + media.originalPath)
//            Log.i("DDDDD", "沙盒路径:" + media.sandboxPath)
//            Log.i("DDDDD", "水印路径:" + media.watermarkPath)
//            Log.i(MainActivity.TAG, "视频缩略图:" + media.videoThumbnailPath)
//            Log.i(MainActivity.TAG, "原始宽高: " + media.width + "x" + media.height)
//            Log.i(MainActivity.TAG, "裁剪宽高: " + media.cropImageWidth + "x" + media.cropImageHeight)
//            Log.i(
//                MainActivity.TAG,
//                "文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.size)
//            )
//            Log.i(MainActivity.TAG, "文件时长: " + media.duration)
        }
        runOnUiThread {
            val isMaxSize = result.size == mAdapter!!.selectMax
            val oldSize: Int = mAdapter!!.data.size
            mAdapter!!.notifyItemRangeRemoved(0, if (isMaxSize) oldSize + 1 else oldSize)
            mAdapter!!.data.clear()
            mAdapter!!.data.addAll(result)
            mAdapter!!.notifyItemRangeInserted(0, result.size)
        }
        isSelect()
    }

    /**
     * 外部预览监听事件
     */
    inner  class MyExternalPreviewEventListener : OnExternalPreviewEventListener {
        override fun onPreviewDelete(position: Int) {
            mAdapter!!.remove(position)
            mAdapter!!.notifyItemRemoved(position)
            isSelect()
        }

        override fun onLongPressDownload(context: Context, media: LocalMedia): Boolean {
            return false
        }
    }

    fun isSelect(){
        if(selectType!=null&&mAdapter!!.data.size>0&& mDatabind.editContactInput.text.toString().trim().isNotEmpty()){
            mDatabind.txtContactSub.background=ContextCompat.getDrawable(this@ContactUsActivity,R.drawable.shape_r8_6d48fe)
            mDatabind.txtContactSub.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
        }else{
            mDatabind.txtContactSub.background=ContextCompat.getDrawable(this@ContactUsActivity,R.drawable.shape_r8_2b2b2b)
            mDatabind.txtContactSub.setTextColor(ContextCompat.getColor(this,R.color.c_4dfffff))
        }
    }

}