package com.devstories.anipointcompany.android.activities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.devstories.anipointcompany.android.R

class OperPolicyFragment : Fragment() {

    lateinit var myContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        this.myContext = container!!.context

        return inflater.inflate(R.layout.fragment_oper_policy, container, false)
    }
}
