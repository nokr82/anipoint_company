package com.devstories.anipointcompany.android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.devstories.aninuriandroid.adapter.UserVisitAdapter
import com.devstories.anipointcompany.android.Actions.MemberAction
import com.devstories.anipointcompany.android.R
import com.devstories.anipointcompany.android.base.PrefUtils
import com.devstories.anipointcompany.android.base.Utils
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class User_List_Fragment : Fragment() {
    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null
    lateinit var userLL: LinearLayout

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)


        return inflater.inflate(R.layout.fra_userlist,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userLL = view.findViewById(R.id.userLL)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainData()




    }

    fun mainData() {
        val params = RequestParams()
        params.put("company_id", 1)

        MemberAction.user_list(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    adapterData.clear()

                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        var data = response.getJSONArray("member")

                        Log.d("메인리스트",data.toString())

                        for (i in 0..(data.length() - 1)) {
                            Log.d("갯수", i.toString())
                            adapterData.add(data[i] as JSONObject)
                            var json=data[i] as JSONObject
                            val member = json.getJSONObject("Member")
                            var point_o  = json.getJSONObject("Point")

                            var point =   Utils.getString(point_o, "balance")



                            val userView = View.inflate(myContext, R.layout.item_user, null)
                            var dateTV : TextView = userView.findViewById(R.id.dateTV)
                            var nameTV : TextView = userView.findViewById(R.id.nameTV)
                            var pointTV : TextView = userView.findViewById(R.id.pointTV)
                            var acc_pointTV : TextView = userView.findViewById(R.id.acc_pointTV)
                            var visitTV : TextView = userView.findViewById(R.id.visitTV)
                            var name2TV : TextView = userView.findViewById(R.id.name2TV)
                            var genderTV : TextView = userView.findViewById(R.id.genderTV)
                            var ageTV : TextView = userView.findViewById(R.id.ageTV)
                            var birthTV : TextView = userView.findViewById(R.id.birthTV)
                            var use_pointTV : TextView = userView.findViewById(R.id.use_pointTV)
                            var couponTV: TextView = userView.findViewById(R.id.couponTV)
                            var visit_recordTV: TextView = userView.findViewById(R.id.visit_recordTV)
                            var stack_pointTV: TextView = userView.findViewById(R.id.stack_pointTV)
                            var memoTV: TextView = userView.findViewById(R.id.memoTV)
                            var phoneTV: TextView = userView.findViewById(R.id.phoneTV)



                            var id = Utils.getString(member, "id")
                            var age =   Utils.getString(member, "age")
                            var name = Utils.getString(member, "name")
                            var gender =   Utils.getString(member, "gender")
                            var memo = Utils.getString(member, "memo")
                            var phone =   Utils.getString(member, "phone")
                            var coupon = Utils.getString(member, "coupon")
                         var stack_point =   Utils.getString(member, "point")
                            var use_point =   Utils.getString(member, "use_point")
                            var company_id = Utils.getString(member, "company_id")
                            var birth =   Utils.getString(member, "birth")
                            var created = Utils.getString(member, "created")
                            var visit =   Utils.getString(member, "visit_cnt")
                            val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                            val updated = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utils.getString(member, "updated"))
                            val updated_date = sdf.format(updated)

                            pointTV.text = point+"P"
                            use_pointTV.text = use_point+"P"
                            acc_pointTV.text = stack_point+"P"
                            stack_pointTV.text = "누적:"+stack_point+"P"
                            dateTV.text = updated_date+" 방문"
                            ageTV.text = age+"세"
                            nameTV.text = name
                            name2TV.text = name
                            genderTV.text = gender
                            memoTV.text = memo
                            couponTV.text = coupon+"장"
                            birthTV.text = birth
                            visitTV.text = visit+"회"
                            phoneTV.text = phone

                            userLL.addView(userView)
                        }

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
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONArray?
            ) {
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
