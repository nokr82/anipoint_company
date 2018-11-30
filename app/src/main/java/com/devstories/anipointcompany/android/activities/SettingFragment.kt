package com.devstories.anipointcompany.android.activities

import android.content.Context
import android.os.Bundle
import android.app.ProgressDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.devstories.anipointcompany.android.R
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : android.support.v4.app.Fragment() {

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null
    lateinit var myInfoTV: TextView
    lateinit var operPolicyTV: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context


        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myInfoTV = view.findViewById(R.id.myInfoTV)
        operPolicyTV= view.findViewById(R.id.operPolicyTV)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val SettingMyInfoFragment : SettingMyInfoFragment = SettingMyInfoFragment()
        val OperPolicyFragment : OperPolicyFragment = OperPolicyFragment()

        myInfoTV.setOnClickListener {
            //프레그먼트에서 프래그먼트선언하기
            setmenu()
            myInfoTV.setTextColor(Color.parseColor("#ffffff"))
            childFragmentManager.beginTransaction().replace(R.id.settingFL, SettingMyInfoFragment).commit()
        }
        myInfoTV.callOnClick()

        operPolicyTV.setOnClickListener {
            //프레그먼트에서 프래그먼트선언하기
            setmenu()
            operPolicyTV.setTextColor(Color.parseColor("#ffffff"))
            childFragmentManager.beginTransaction().replace(R.id.settingFL, OperPolicyFragment).commit()
        }


    }

    fun setmenu(){
        myInfoTV.setTextColor(Color.parseColor("#80ffffff"))
        operPolicyTV.setTextColor(Color.parseColor("#80ffffff"))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

}