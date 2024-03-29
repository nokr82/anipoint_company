package com.devstories.anipointcompany.android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.devstories.anipointcompany.android.Actions.PointAction
import com.devstories.anipointcompany.android.R
import com.devstories.anipointcompany.android.base.CustomProgressDialog
import com.devstories.anipointcompany.android.base.PrefUtils
import com.devstories.anipointcompany.android.base.Utils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fra_user_visit_select3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class User_visit_Select3_Fragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: CustomProgressDialog? = null

    lateinit var ageBarChart:BarChart
    lateinit var todayRL: RelativeLayout
    lateinit var weekRL: RelativeLayout
    lateinit var monthRL: RelativeLayout
    lateinit var three_mRL: RelativeLayout
    lateinit var todayTV: TextView
    lateinit var weekTV: TextView
    lateinit var monthTV: TextView
    lateinit var three_mTV: TextView
    lateinit var max_memberTV: TextView
    lateinit var min_memberTV: TextView
    lateinit var dateTV: TextView
    var day_type = 1 //1-오늘 2-이번주 3-이번달 4-3개월
    var company_id = -1

    //오늘날짜구하기
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    val date = Date()
    val currentDate = formatter.format(date)

    var type = -1

    var totalMemberCnt = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        progressDialog = CustomProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        return inflater.inflate(R.layout.fra_user_visit_select3, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ageLL = view.findViewById(R.id.ageLL)
        ageBarChart = view.findViewById(R.id.ageBarChart)
        todayTV = view.findViewById(R.id.todayTV)
        monthTV = view.findViewById(R.id.monthTV)
        weekTV = view.findViewById(R.id.weekTV)
        three_mTV = view.findViewById(R.id.three_mTV)
        todayRL = view.findViewById(R.id.todayRL)
        weekRL = view.findViewById(R.id.weekRL)
        monthRL = view.findViewById(R.id.monthRL)
        three_mRL = view.findViewById(R.id.three_mRL)
        dateTV = view.findViewById(R.id.dateTV)
        max_memberTV= view.findViewById(R.id.max_memberTV)
        min_memberTV= view.findViewById(R.id.min_memberTV)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        graph()
        click()
        time_detail()
    }
    fun graph(){
        company_id = PrefUtils.getIntPreference(context, "company_id")



        ageBarChart.setDrawBarShadow(false)
        ageBarChart.setDrawValueAboveBar(true)
        ageBarChart.description.isEnabled = false
        ageBarChart.legend.isEnabled = false
        ageBarChart.setMaxVisibleValueCount(100)
        ageBarChart.setPinchZoom(false)
        ageBarChart.setDrawGridBackground(false)
        ageBarChart.setScaleEnabled(false)
        ageBarChart.setTouchEnabled(false)
        ageBarChart.getAxisLeft().setAxisMinimum(0f)
        ageBarChart.getAxisRight().setEnabled(false)
        var val_:Float? = null
        ageBarChart.getAxisLeft().setValueFormatter(IAxisValueFormatter { value, axis ->
            //                return Double.parseDouble(String.format("%.2f", value)) + "h";
            val_ = (value / totalMemberCnt) * 100f
            String.format("%.1f", val_)+ "%"
        })




    }

    fun click(){
        todayRL.setOnClickListener {
            setmenu()
            day_type = 1
            todayTV.setTextColor(Color.parseColor("#606060"))
            dateTV.text = currentDate + "~" + currentDate
            time_detail()
        }

        todayRL.callOnClick()

        weekRL.setOnClickListener {
            setmenu()
            day_type = 2
            weekTV.setTextColor(Color.parseColor("#606060"))
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val df = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            var startDate = df.format(calendar.getTime())
            calendar.add(Calendar.DATE, 6)
            var endDate = df.format(calendar.getTime())
            Log.d("현재", startDate)
            Log.d("미래", endDate)
            dateTV.text = startDate + " ~ " + endDate
            time_detail()
        }
        monthRL.setOnClickListener {
            setmenu()
            day_type = 3
            monthTV.setTextColor(Color.parseColor("#606060"))
            val beforemonth = SimpleDateFormat("yyyy.MM.01", Locale.KOREA)
            val aftermonth = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            val cal = Calendar.getInstance()
            //그달의 마지막일 구하기
            val endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val date = Date()
            val currentDate = beforemonth.format(date)
            val lastmonth = aftermonth.format(date).toString().substring(0, 8) + endDay

            dateTV.text = currentDate + " ~ " + lastmonth
            time_detail()
        }
        three_mRL.setOnClickListener {
            setmenu()
            day_type = 4
            three_mTV.setTextColor(Color.parseColor("#606060"))
            val aftermonth = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            val cal = Calendar.getInstance()
            //그달의 마지막일 구하기
            val startday = cal.getActualMaximum(Calendar.MONTH) - 3
            val beforemonth = SimpleDateFormat("yyyy." + startday + ".dd", Locale.KOREA)
            val date = Date()
            val currentDate = beforemonth.format(date).toString()
            val lastmonth = aftermonth.format(date).toString()

            dateTV.text = currentDate + " ~ " + lastmonth
            time_detail()
        }
    }

    fun setmenu() {
        todayTV.setTextColor(Color.parseColor("#c5c5c5"))
        monthTV.setTextColor(Color.parseColor("#c5c5c5"))
        weekTV.setTextColor(Color.parseColor("#c5c5c5"))
        three_mTV.setTextColor(Color.parseColor("#c5c5c5"))
    }


    fun time_detail(){
        val params = RequestParams()
        params.put("company_id", company_id)
        params.put("day_type", day_type)

        PointAction.time_detail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        val age = response.getJSONArray("age")
                        val ages = arrayOf("00", "01", "02", "03", "04", "05","06", "07", "08", "09", "10", "11","12", "13", "14", "15", "16","17", "18", "19", "20", "21", "22","23")
//                        val ages = arrayOf("01", "03", "05", "07", "09", "11","13", "15","17", "19", "21","23")


                        val min = response.getJSONObject("min")
                        val max = response.getJSONObject("max")
                        var min_time = Utils.getString(min,"age")
                        var min_cnt = Utils.getInt(min,"count")
                        var max_time = Utils.getString(max,"age")
                        var max_cnt = Utils.getInt(max,"count")
                        max_memberTV.text = max_cnt.toString()+"명"+"\n"+max_time+"시"
                        min_memberTV.text =min_cnt.toString()+"명"+"\n"+min_time+"시"




                        var xAxis = ageBarChart.getXAxis()
                        xAxis.setTextColor(Color.parseColor("#a0a0a0"))
//                        xAxis.setDrawLabels(true)
                        xAxis.setDrawGridLines(false)
                        xAxis.setGranularity(1f) // minimum axis-step (interval) is 1
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
                        xAxis.setAvoidFirstLastClipping(true)
                        xAxis.setLabelCount(24, false)
//                        xAxis.setValueFormatter(IndexAxisValueFormatter(ages))
//                        xAxis.setCenterAxisLabels(true)
       /*                 xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                            // System.out.println("va 0 : " + value);
                            if (value < 0) {
                                return@IAxisValueFormatter ""
                            }
                            if (ages.size > value) {
                                ages[value.toInt()]
                            } else {
                                ""
                            }
                        }*/


                        var ageData: MutableList<BarEntry> = ArrayList()

                        var arr = ArrayList<Int>()

                        for (i in 0 until age.length()) {
                            var data = age[i] as JSONObject
                            var data0 = age[0] as JSONObject
                            var data1 = age[1] as JSONObject
                            var data2 = age[2] as JSONObject
                            var data3 = age[3] as JSONObject
                            var data4 = age[4] as JSONObject
                            var data5 = age[5] as JSONObject
                            var data6 = age[6] as JSONObject
                            var data7 = age[7] as JSONObject
                            var data8 = age[8] as JSONObject
                            var data9 = age[9] as JSONObject
                            var data10 = age[10] as JSONObject
                            var data11 = age[11] as JSONObject
                            var data12 = age[12] as JSONObject
                            var data13 = age[13] as JSONObject
                            var data14 = age[14] as JSONObject
                            var data15 = age[15] as JSONObject
                            var data16 = age[16] as JSONObject
                            var data17 = age[17] as JSONObject
                            var data18 = age[18] as JSONObject
                            var data19 = age[19] as JSONObject
                            var data20 = age[20] as JSONObject
                            var data21 = age[21] as JSONObject
                            var data22 = age[22] as JSONObject
                            var data23 = age[23] as JSONObject

                            var time0 = Utils.getInt(data0, "count")
                            var time1 = Utils.getInt(data1, "count")
                            var time2 = Utils.getInt(data2, "count")
                            var time3 = Utils.getInt(data3, "count")
                            var time4 = Utils.getInt(data4, "count")
                            var time5 = Utils.getInt(data5, "count")
                            var time6 = Utils.getInt(data6, "count")
                            var time7 = Utils.getInt(data7, "count")
                            var time8 = Utils.getInt(data8, "count")
                            var time9 = Utils.getInt(data9, "count")
                            var time10 = Utils.getInt(data10, "count")
                            var time11 = Utils.getInt(data11, "count")
                            var time12 = Utils.getInt(data12, "count")
                            var time13 = Utils.getInt(data13, "count")
                            var time14 = Utils.getInt(data14, "count")
                            var time15 = Utils.getInt(data15, "count")
                            var time16 = Utils.getInt(data16, "count")
                            var time17 = Utils.getInt(data17, "count")
                            var time18 = Utils.getInt(data18, "count")
                            var time19 = Utils.getInt(data19, "count")
                            var time20 = Utils.getInt(data20, "count")
                            var time21 = Utils.getInt(data21, "count")
                            var time22 = Utils.getInt(data22, "count")
                            var time23 = Utils.getInt(data23, "count")
                            totalMemberCnt = time0+time1+time2+time3+time4+time5+time6+time7+time8+time9+time10+time11+
                                    time12+time13+time14+time15+time16+time17+time18+time19+time20+time21+time22+time23

                            ageData.add(BarEntry(i.toFloat(), Utils.getInt(data, "count").toFloat()))
                        }
                        if (totalMemberCnt==0){
                            totalMemberCnt =1
                        }









                        var barDataSet = BarDataSet(ageData, "12")
                        barDataSet.setColors(*intArrayOf(Color.parseColor("#4b8bc8"), Color.parseColor("#4b8bc8"), Color.parseColor("#4b8bc8"), Color.parseColor("#4b8bc8"), Color.parseColor("#4b8bc8"), Color.parseColor("#4b8bc8")))
                        barDataSet.setDrawValues(false)
                        var barData = BarData(barDataSet)
                        barData.setBarWidth(0.3f)
                        ageBarChart.getXAxis().setAxisMinimum(0f);
                        ageBarChart.setData(barData)
                        ageBarChart.invalidate() // refresh


                        val entries = ArrayList<PieEntry>()


                        val colors = ArrayList<Int>()
                        colors.add(Color.parseColor("#6799FF"))
                        colors.add(Color.parseColor("#FF00DD"))

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }
}


