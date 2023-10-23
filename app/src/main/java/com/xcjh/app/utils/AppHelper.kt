package com.xcjh.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseDifferAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.google.android.material.snackbar.Snackbar
import com.just.agentweb.WebViewClient
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xcjh.app.R
import com.xcjh.app.adapter.ViewPager2Adapter
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.ui.login.LoginActivity
import com.xcjh.app.view.callback.EmptyCallback
import com.xcjh.app.view.callback.LoadingCallback
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.layoutInflater
import com.xcjh.base_lib.utils.setLm
import com.xcjh.base_lib.utils.startNewActivity
import java.text.DecimalFormat


//顶层方法类 全是静态方法 并存在在HelperKt类中
fun doSomething() {
    println("======")
}

//除法
fun myDivide(a: Int, b: Int): Float {
    //“0.00000000”确定精度
    if (a == 0) {
        return 0f
    }
    val dF = DecimalFormat("0.00000000")
    return dF.format((a.toFloat() / b).toDouble()).toFloat()
}

/**
 * 找最大值
 */
fun <T : Comparable<T>> max(vararg nums: T): T {
    if (nums.isEmpty()) throw RuntimeException("Params can not be empty")

    var maxNum = nums[0]
    for (num in nums) {
        if (num > maxNum) {
            maxNum = num
        }
    }
    return maxNum
}

/**
 * 找最小值
 */
fun <T : Comparable<T>> min(vararg nums: T): T {
    if (nums.isEmpty()) throw RuntimeException("Params can not be empty")

    var minNum = nums[0]
    for (num in nums) {
        if (num < minNum) {
            minNum = num
        }
    }
    return minNum
}

/**
 * "".showToast(context,Toast.LENGTH_LONG)
 */
fun String.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).show()
}

fun Int.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).show()
}

/**
 * view.showSnackbar("",""){
 *
 * }
 */
fun View.showSnackbar(
    text: String,
    actionText: String? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    block: (() -> Unit)? = null
) {

    val snackbar = Snackbar.make(this, text, duration)
    if (actionText != null && block != null) {
        snackbar.setAction(actionText) {
            block()
        }
    }
    snackbar.show()
}

fun View.showSnackbar(
    resId: Int,
    actionResId: Int? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    block: (() -> Unit)? = null
) {

    val snackbar = Snackbar.make(this, resId, duration)
    if (actionResId != null && block != null) {
        snackbar.setAction(actionResId) {
            block()
        }
    }
    snackbar.show()
}

fun ViewPager2.initChangeActivity(
    acivity: FragmentActivity,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true//是否可滑动
): ViewPager2 {
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = ViewPager2Adapter(acivity,fragments)
    setLm(this)
    isSaveEnabled = false
    return this
}

//绑定普通的Recyclerview
fun RecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this
}


/**
 * 普通加载列表数据
 */
fun <T> smartListData(
    activity: Context,
    data: ListDataUiState<T>,
    baseQuickAdapter: BaseQuickAdapter<T, *>,
    smartRefresh: SmartRefreshLayout,
    imgEmptyId: Int = R.drawable.tp_empty,//图片
    notice: String = appContext.getString(R.string.no_data),//提示
    block: (() -> Unit)? = null
) {
    val empty = setTpEmpty(activity, imgEmptyId, notice)
    if (data.isSuccess) {
        //成功
        when {
            //第一页并没有数据 显示空布局界面
            data.isFirstEmpty -> {
                smartRefresh.finishRefresh()
                baseQuickAdapter.submitList(null)
                baseQuickAdapter.emptyView = empty
            }
            //是第一页
            data.isRefresh -> {
                smartRefresh.finishRefresh()
                smartRefresh.resetNoMoreData()
                baseQuickAdapter.submitList(data.listData)
            }
            //不是第一页
            else -> {
                if (data.listData.isEmpty()) {
                    smartRefresh.finishLoadMoreWithNoMoreData()
                } else {
                    smartRefresh.finishLoadMore()
                    baseQuickAdapter.addAll(data.listData)
                }
            }
        }
    } else {
        //失败
        if (data.isRefresh) {
            smartRefresh.finishRefresh()
            //如果是第一页，则显示错误界面，并提示错误信息
            baseQuickAdapter.submitList(null)
            // emptyHint.text = data.errMessage
            baseQuickAdapter.emptyView = empty
            /* val layoutParams = baseQuickAdapter.emptyLayout?.layoutParams
              layoutParams?.height=DisplayUtils.dp2px(300f)
              baseQuickAdapter.emptyLayout?.layoutParams=layoutParams*/
        } else {
            smartRefresh.finishLoadMore(false)
        }
    }
}
/**
 * 普通加载Differ列表数据
 */
fun <T> smartDifferListData(
    activity: Context,
    data: ListDataUiState<T>,
    baseQuickAdapter: BaseDifferAdapter<T, *>,
    smartRefresh: SmartRefreshLayout,
    imgEmptyId: Int = R.drawable.tp_empty,//图片
    notice: String = appContext.getString(R.string.no_data),//提示
    block: (() -> Unit)? = null
) {
    val empty = setTpEmpty(activity, imgEmptyId, notice)
    if (data.isSuccess) {
        //成功
        when {
            //第一页并没有数据 显示空布局界面
            data.isFirstEmpty -> {
                smartRefresh.finishRefresh()
                baseQuickAdapter.submitList(null)
                baseQuickAdapter.emptyView = empty
            }
            //是第一页
            data.isRefresh -> {
                smartRefresh.finishRefresh()
                smartRefresh.resetNoMoreData()
                baseQuickAdapter.submitList(data.listData)
            }
            //不是第一页
            else -> {
                if (data.listData.isEmpty()) {
                    smartRefresh.finishLoadMoreWithNoMoreData()
                } else {
                    smartRefresh.finishLoadMore()
                    baseQuickAdapter.addAll(data.listData)
                }
            }
        }
    } else {
        //失败
        if (data.isRefresh) {
            smartRefresh.finishRefresh()
            //如果是第一页，则显示错误界面，并提示错误信息
            baseQuickAdapter.submitList(null)
            // emptyHint.text = data.errMessage
            baseQuickAdapter.emptyView = empty
            /* val layoutParams = baseQuickAdapter.emptyLayout?.layoutParams
              layoutParams?.height=DisplayUtils.dp2px(300f)
              baseQuickAdapter.emptyLayout?.layoutParams=layoutParams*/
        } else {
            smartRefresh.finishLoadMore(false)
        }
    }
}


fun setTpEmpty(
    activity: Context,
    imgEmptyId: Int = R.drawable.tp_empty,//图片
    notice: String = appContext.getString(R.string.no_data),//提示
    block: (() -> Unit)? = null
): View {
    val empty = activity.layoutInflater!!.inflate(R.layout.empty_view, null)
    val emptyImg = empty.findViewById<ImageView>(R.id.emptyImg)
    val emptyHint = empty.findViewById<TextView>(R.id.emptyHint)
    emptyImg.setImageResource(imgEmptyId)
    emptyHint.text = notice
    return empty
}


/**
 * 普通加载列表数据
 */
fun <T> smartListData(
    data: ListDataUiState<T>,
    rcv: RecyclerView,
    smartRefresh: SmartRefreshLayout,
) {
    if (data.isSuccess) {
        //成功
        when {
            //第一页并没有数据 显示空布局界面
            data.isFirstEmpty -> {
                smartRefresh.finishRefresh()
                rcv.addModels(null)
            }
            //是第一页
            data.isRefresh -> {
                smartRefresh.finishRefresh()
                smartRefresh.resetNoMoreData()
                rcv.addModels(data.listData)
            }
            //不是第一页
            else -> {
                if (data.listData.isEmpty()) {
                    smartRefresh.finishLoadMoreWithNoMoreData()
                } else {
                    smartRefresh.finishLoadMore()
                    rcv.addModels(data.listData)
                }
            }
        }
    } else {
        //失败
        if (data.isRefresh) {
            smartRefresh.finishRefresh()
            rcv.addModels(null)
        } else {
            smartRefresh.finishLoadMore(false)
        }
    }
}

/**
 * 普通加载列表数据
 */
fun <T> smartPageListData(
    data: ListDataUiState<T>,
    rcv: RecyclerView,
    pageRefreshLayout: PageRefreshLayout,
    imgEmptyId: Int = R.drawable.tp_empty,//图片
    notice: String = appContext.getString(R.string.no_data),//提示
) {
    pageRefreshLayout.emptyLayout=R.layout.layout_empty
    pageRefreshLayout.stateLayout?.onEmpty {
        val emptyImg = findViewById<ImageView>(R.id.ivEmptyIcon)
        val emptyHint = findViewById<TextView>(R.id.txtEmptyName)
        emptyImg.setImageResource(imgEmptyId)
        emptyHint.text = notice
    }
    if (data.isSuccess) {
        //成功
        when {
            //第一页并没有数据 显示空布局界面
            data.isFirstEmpty -> {
                pageRefreshLayout.finishRefresh()
                rcv.models=(null)
                pageRefreshLayout.showEmpty()
            }
            //是第一页
            data.isRefresh -> {
                pageRefreshLayout.finishRefresh()
                pageRefreshLayout.resetNoMoreData()
                pageRefreshLayout.showContent()
                rcv.models=(data.listData)
            }
            //不是第一页
            else -> {
                pageRefreshLayout.showContent()
                if (data.listData.isEmpty()) {
                    pageRefreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    pageRefreshLayout.finishLoadMore()
                    rcv.addModels(data.listData)
                }
            }
        }
    } else {
        //失败
        if (data.isRefresh) {
            pageRefreshLayout.finishRefresh()
            rcv.addModels(null)
            pageRefreshLayout.showEmpty()
        } else {
            pageRefreshLayout.finishLoadMore(false)
        }
    }
}
/**
 * 加载 LoadService+列表数据
 */
fun <T> loadListData(
    data: ListDataUiState<T>,
    baseQuickAdapter: BaseQuickAdapter<T, *>,
    loadService: LoadService<*>,
    smartRefresh: SmartRefreshLayout
) {
    if (data.isSuccess) {
        //成功
        when {
            //第一页并没有数据 显示空布局界面
            data.isFirstEmpty -> {
                smartRefresh.finishRefresh()
                loadService.showEmpty()
            }
            //是第一页
            data.isRefresh -> {
                smartRefresh.finishRefresh()
                smartRefresh.resetNoMoreData()
                baseQuickAdapter.submitList(data.listData)
                loadService.showSuccess()
            }
            //不是第一页
            else -> {
                loadService.showSuccess()
                if (data.listData.isEmpty()) {
                    smartRefresh.finishLoadMoreWithNoMoreData()
                } else {
                    smartRefresh.finishLoadMore()
                    baseQuickAdapter.addAll(data.listData)
                }
            }
        }
    } else {
        //失败
        if (data.isRefresh) {
            //如果是第一页，则显示错误界面，并提示错误信息
            smartRefresh.finishRefresh()
            //如果是第一页，则显示错误界面，并提示错误信息
            baseQuickAdapter.submitList(null)
            loadService.showEmpty()
        } else {
            smartRefresh.finishLoadMore(false)
        }
    }
}

/**
 * 加载列表空布局
 */
@SuppressLint("MissingInflatedId")
fun <T> setEmptyOrError(
    activity: Activity,
    baseQuickAdapter: BaseQuickAdapter<T, *>,
    imgId: Int = R.drawable.shape_r8,//图片
    notice: String = appContext.getString(R.string.no_data_hint),//提示
) {
    val empty = activity.layoutInflater.inflate(R.layout.empty_view, null)
    val emptyImg = empty.findViewById<ImageView>(R.id.emptyImg)
    val emptyHint = empty.findViewById<TextView>(R.id.emptyHint)
    emptyImg.setImageResource(imgId)
    emptyHint.text = notice
    baseQuickAdapter.submitList(null)
    baseQuickAdapter.emptyView = empty
}


/**
 * 获取版本号名称
 *
 * @param context 上下文
 * @return
 */
fun getVerName(context: Context): String {
    var verName = ""
    try {
        verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return "V$verName"
}
/**
 * 获取版本号名称
 *
 * @param context 上下文
 * @return
 */
fun getVerCode(context: Context):  Long{
    var versionCode:Long=0
    try {
       var  packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

          versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionCode
}

fun judgeLogin(action: () -> Unit = {}) {
    if (CacheUtil.isLogin()) {
        action.invoke()
    } else {
        startNewActivity<LoginActivity>()
    }
}


/**
 * 获取 h5 标签中的内容
 */
fun getH5Content(htmlStr: String): String {
    val regFormat = "\\s*|\t|\r|\n"
    val regTag = "<[^>]*>"
    return htmlStr.replace(regFormat.toRegex(), "").replace(regTag.toRegex(), "")
}
/**
 * 获取 h5 标签中的内容
 * 保留原始 空格\t、回车\r、换行符\n、制表符\t
 */
fun getH5Content2(htmlStr: String): String {
    val regTag = "<[^>]*>"
    return htmlStr.replace(regTag.toRegex(), "")
}

/**
 * 处理loadDataWithBaseURL 加载换行
 */
fun getH5Content3(htmlStr: String): String {
    val regFormat = "\n"
    return htmlStr.replace(regFormat.toRegex(), "<br>")
}
fun setWeb(mWebView :WebView) {
    mWebView.webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.e("TAG", "--------onPageStarted------ $url")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            Log.e("TAG", "--------onReceivedError------ ")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.e("TAG", "--------onPageFinished------ ")
        }
    }
    mWebView.isVerticalScrollBarEnabled = false
    mWebView.isHorizontalScrollBarEnabled = false
    val settings = mWebView.settings
    settings.javaScriptEnabled = true
    settings.defaultTextEncodingName = "UTF-8"
}
fun clearWebView(webView: WebView?) {
    val m = webView ?: return
    if (Looper.myLooper() != Looper.getMainLooper()) {
        return
    }
    m.loadUrl("about:blank")
    m.stopLoading()
    if (m.handler != null) {
        m.handler.removeCallbacksAndMessages(null)
    }
    m.removeAllViews()
    var mViewGroup: ViewGroup? = null
    if ((m.parent as ViewGroup).also { mViewGroup = it } != null) {
        mViewGroup!!.removeView(m)
    }
    m.webChromeClient = null
    m.tag = null
    m.clearHistory()
    m.destroy()
}


/**
 * 设置空布局
 */
fun LoadService<*>.showEmpty() {
    this.showCallback(EmptyCallback::class.java)
}

/**
 * 设置加载中
 */
fun LoadService<*>.showLoading() {
    this.showCallback(LoadingCallback::class.java)
}

fun loadServiceInit(view: View, callback: () -> Unit): LoadService<Any> {
    val loadsir = LoadSir.getDefault().register(view) {
        //点击重试时触发的操作
        callback.invoke()
    }
    loadsir.showSuccess()
    return loadsir
}
