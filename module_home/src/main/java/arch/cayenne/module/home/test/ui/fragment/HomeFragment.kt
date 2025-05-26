package arch.cayenne.module.home.test.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.ActivityNavigatorExtras
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.ui.view.BasePopup
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.databinding.PopupCalendarViewBinding
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.extractDate
import arch.cayenne.lib.common.utils.ext.toChineseMonth
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentHomeBinding
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import kotlinx.coroutines.delay
import org.koin.android.ext.android.getKoin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as Rc

class HomeFragment : BaseFragment<EmptyViewModel, FragmentHomeBinding>() {
    override val vbClass: KClass<FragmentHomeBinding> = FragmentHomeBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        /**
         * navigation:
         * fragment --> fragment(Internal)
         * fragment --> fragment(External)
         * activity -> fragment
         * fragment -> activity
         */
        mBinding.tv1.setOnClickListener {

            navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("Tom"))
            //navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity(null))
        }
        //mBinding.tv2.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.LoginActivity,bundleOf("userId" to "David")))
        mBinding.tv2.setOnClickListener {
            //navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity())
            navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity("actionHomeFragmentToLoginActivity"))

            //navigate(R.id.LoginActivity, bundleOf("userId" to "David"))
            //navigate(R.id.loginSecondFragment)
            //apply plugin: 'androidx.navigation.safeargs.kotlin'
            //navigate(R.id.action_homeFragment_to_secondFragment)
            //navigate(R.id.secondFragment)

        }
        mBinding.tv3.setOnClickListener {
            //deep link
            navigate(Uri.parse("walisport://login_activity?userId=lucy"))
            //navigate(R.id.action_homeFragment_to_LoginActivity, bundleOf("userId" to "lili"))
            //navigate(Uri.parse("walisport://login_activity?userId=lucy"))
        }
        mBinding.tv4.setOnClickListener {
            //navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("toFragmentInner"))
            //navigate(Uri.parse("walisport://module_login/loginSecondFragment"))
            var selectedDate = "20250423"
            val popup = BasePopup.Builder(requireContext(), PopupCalendarViewBinding::inflate)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocusable(true)
                .setOutsideTouchable(true)
                //.setAnimationStyle(R.style.PopupAnimation)// 使用默认的动画即可，可以自定义
                .setOnShowListener { vb ->
                    // 通过 ViewBinding 初始化视图
                    vb.ivRightClick.clickNoRepeat {
                        vb.calendarView.scrollToNext(true)
                    }
                    vb.ivLeftClick.clickNoRepeat {
                        vb.calendarView.scrollToPre(true)
                    }
                    val result = selectedDate.extractDate()
                    result?.let {
                        val (year, month, day) = it
                        vb.calendarView.scrollToCalendar(year, month, day)
                        vb.tvCurrentMonth.text = "${month.toChineseMonth()} $year"
                    } ?: run {
                        val currentYear = vb.calendarView.curYear
                        val currentMonth = vb.calendarView.curMonth
                        vb.calendarView.scrollToCurrent(true)
                        vb.tvCurrentMonth.text = "${currentMonth.toChineseMonth()} $currentYear"
                    }
                    vb.calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
                        override fun onCalendarOutOfRange(calendar: Calendar?) {

                        }

                        override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                            if (calendar == null) return
                            selectedDate = "$calendar"
                            vb.tvCurrentMonth.text =
                                "${calendar.month.toChineseMonth()} ${calendar.year}"
                        }
                    })
                }
                .setOnDismissListener {
                    // 弹窗消失时的操作，刷新数据等
                    println("弹窗已销毁")
                }
                .build()
            popup.show(mBinding.tv4, Gravity.BOTTOM, 0, 0)
        }
        mBinding.tv5.setOnClickListener {
            /*val request = NavDeepLinkRequest.Builder
                .fromUri("walisport://module_login/loginSecondFragment".toUri())
                .build()
           navigate(request)*/
            navigate(Uri.parse("walisport://module_login/loginSecondFragment"))
            //startActivity(Intent().apply { component  = ComponentName(requireActivity().packageName, "com.walisport.module_login.ui.LoginActivity") })
            //navigate(R.id.loginFragment)
        }

        mBinding.tv6.setOnClickListener {
            navigate(HomeFragmentDirections.actionHomeFragmentToNewHomeFragment())
        }

        mBinding.tv7.clickNoRepeat {
            navigate(Uri.parse("walisport://module_setting/settingFragment"))
        }

        mBinding.tv11.clickNoRepeat {
            navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink())
        }

        mBinding.tv12.clickNoRepeat {
            navigate(Uri.parse("walisport://module_handicap/HandicapFragment"))
        }
        mBinding.tvWebFragment.clickNoRepeat {
            navigate(
                Rc.string.deeplink_single_web_fragment.deeplink(
                    "title" to "baidu",
                    "url" to "https://www.baidu.com/"
                )
            )
            /*navigate(Rc.string.deeplink_single_web_fragment.deeplink(
                "title=baidu",
                "url=https://www.baidu.com/")
            )*/
        }
        mBinding.http.clickNoRepeat {
            val httpClient = getKoin().get<HttpClient>()
            launch {
                val api = httpClient.create(ITestApi::class.java)
                //Get
                var progressDialog: ProgressDialog? = null
                httpClient.safeRequest(
                    request = {
                        api.getVacations(
                            token = "A2E0C3CDEA081D3BFC34F8FE23A15886",
                            type = 1,
                            timestamp = "1462377600",
                            client = "ceshi"
                        )
                    },
                    onStart = {
                        progressDialog = ProgressDialog(requireContext()).apply {
                            setMessage("测试Get...")
                            setCancelable(false) // 不可取消
                            show()
                        }
                    },
                    onSuccess = {
                        "response------>$it".logd(TAG)
                        showToast(it.toString())
                        progressDialog?.dismiss()
                    },
                    onFailure = { code, msg, throwable ->
                        "response------>$code,$msg,$throwable".loge(TAG)
                        showToast(msg)
                        progressDialog?.dismiss()
                    }
                )
                delay(1000)
                //POST
                httpClient.safeRequest(
                    request = {
                        api.postTest(
                            mapOf(
                                "name" to "ChatGPT",
                                "message" to "Hello World"
                            )
                        )
                    },
                    onStart = {
                        progressDialog = ProgressDialog(requireContext()).apply {
                            setMessage("测试Post...")
                            setCancelable(false) // 不可取消
                            show()
                        }
                    },
                    onSuccess = {
                        "response------>$it".logd(TAG)
                        showToast(it.toString())
                        progressDialog?.dismiss()
                    },
                    onFailure = { code, msg, throwable ->
                        "response------>$code,$msg,$throwable".loge(TAG)
                        showToast(msg)
                        progressDialog?.dismiss()
                    }
                )

                /*api.getVacations2(
                    token = "A2E0C3CDEA081D3BFC34F8FE23A15886",
                    type = 1,
                    timestamp = "1462377600",
                    client = "ceshi"
                )*/
                /*api.getVacations2(
                    token = "A2E0C3CDEA081D3BFC34F8FE23A15886",
                    type = 1,
                    timestamp = "1462377600",
                    client = "ceshi"
                ).let {
                    "response------>$it".logd(TAG)
                }

                api.getVacations3(
                    mapOf(
                        "token" to "A2E0C3CDEA081D3BFC34F8FE23A15886",
                        "type" to "1",
                        "timestamp" to "1462377600",
                        "client" to "ceshi"
                    )
                ).let {
                    "response------>$it".logd(TAG)
                }
                api.postTest(mapOf(
                    "name" to "ChatGPT",
                    "message" to "Hello World"
                )).let {
                    "response------>$it,body:${it.body()}".logd(TAG)
                }
                api.postTest2(mapOf(
                    "name" to "ChatGPT",
                    "message" to "Hello World"
                )).let {
                    "response------>$it}".logd(TAG)
                    Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                }*/
            }
        }
    }

    data class HolidayResponse(
        val status: Int,
        val data: Map<String, String>,
        val errMsg: String
    )

    data class HttpBinResponse(
        val args: Map<String, String>?,
        val data: String?,
        val files: Map<String, String>?,
        val form: Map<String, String>?,
        val headers: HttpBinHeaders?,
        val json: Map<String, String>?,
        val origin: String?,
        val url: String?
    )

    data class HttpBinHeaders(
        val Accept: String?,
        val `Content-Length`: String?,
        val `Content-Type`: String?,
        val Host: String?,
        val `User-Agent`: String?,
        val `X-Amzn-Trace-Id`: String?
    )


    interface ITestApi : IApi {
        @GET("calendar/vacations")
        suspend fun getVacations(
            @Query("token") token: String,
            @Query("type") type: Int,
            @Query("timestamp") timestamp: String,
            @Query("client") client: String
        ): Response<HolidayResponse>

        @GET("calendar/vacations")
        suspend fun getVacations2(
            @Query("token") token: String,
            @Query("type") type: Int,
            @Query("timestamp") timestamp: String,
            @Query("client") client: String
        ): HolidayResponse

        @GET("calendar/vacations")
        suspend fun getVacations3(
            @QueryMap params: Map<String, String>
        ): HolidayResponse

        /************** 测试切换baseUrl *********/
        @Headers("baseUrl:https://httpbin.org/")
        @POST("post")
        suspend fun postTest(@Body params: Map<String, String>): Response<HttpBinResponse>

        @Headers("BASEURL:https://httpbin.org/")
        @POST("post")
        suspend fun postTest2(@Body params: Map<String, String>): HttpBinResponse
    }

    private fun toFragmentInner() {

    }

    private fun toActivityByIdBundle() {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        val extras = ActivityNavigatorExtras(
            activityOptions = options,
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        // 使用 Bundle 传递参数（如果 Safe Args 不支持 Activity 参数）
        val bundle = Bundle().apply { putString("userId", "userId") }
        navigate(R.id.LoginActivity, bundle, null, extras)
    }

    override fun createObserver() {

    }

}