package com.devstories.aninuriandroid.adapter

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.devstories.anipointcompany.android.R
import com.devstories.anipointcompany.android.R.id.dateTV
import com.devstories.anipointcompany.android.R.id.updateTV
import com.devstories.anipointcompany.android.base.Utils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


open class MemberListAdapter (context:Context, view:Int, data: ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

    private lateinit var item: ViewHolder
    var view:Int = view
    var data: ArrayList<JSONObject> = data


    override fun getView(position: Int, convertView: View?, parent : ViewGroup?): View {

        lateinit var retView: View

        if (convertView == null) {
            retView = View.inflate(context, view, null)
            item = ViewHolder(retView)
            retView.tag = item
        } else {
            retView = convertView
            item = convertView.tag as ViewHolder
            if (item == null) {
                retView = View.inflate(context, view, null)
                item = ViewHolder(retView)
                retView.tag = item
            }
        }

        var json = data.get(position)
        val member = json.getJSONObject("Member")
        var phone = Utils.getString(member, "phone")
        var name = Utils.getString(member, "name")
        Log.d("제이슨",json.toString())

        item.nameTV.text = name
        item.phoneTV.text = phone

        val point_o = json.getJSONObject("Point")
        var point = Utils.getString(point_o, "point")
        var type = Utils.getInt(point_o, "type")
        var member_coupon_id = Utils.getInt(point_o, "member_coupon_id")
        var updated = Utils.getString(point_o, "created")
        var membership_per = Utils.getInt(point_o, "membership_per")
        var cate = Utils.getInt(point_o, "cate")





        return retView
    }

    override fun getItem(position: Int): JSONObject {
        return data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.count()
    }
    class ViewHolder(v: View) {
        var nameTV = v.findViewById(R.id.nameTV) as TextView
        var phoneTV = v.findViewById(R.id.phoneTV) as TextView
        init {

        }
    }
}
