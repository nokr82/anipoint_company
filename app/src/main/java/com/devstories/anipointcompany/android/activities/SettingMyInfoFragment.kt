package com.devstories.anipointcompany.android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.devstories.anipointcompany.android.Actions.CompanyAction
import com.devstories.anipointcompany.android.Actions.CompanyAction.company_info
import com.devstories.anipointcompany.android.Actions.CompanyAction.edit_image
import com.devstories.anipointcompany.android.Actions.CompanyAction.edit_info
import com.devstories.anipointcompany.android.R
import com.devstories.anipointcompany.android.R.id.*
import com.devstories.anipointcompany.android.base.Config
import com.devstories.anipointcompany.android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException

class SettingMyInfoFragment : Fragment() {

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    lateinit var compNameET: EditText
    lateinit var phoneNum1ET: EditText
    lateinit var phoneNum2ET: EditText
    lateinit var phoneNum3ET: EditText
    lateinit var termET: EditText
    lateinit var compIdET: EditText
    lateinit var addImage1RL: RelativeLayout
    lateinit var tempPasswordET: EditText
    lateinit var newPasswordET: EditText
    lateinit var newPassCheckET: EditText
    lateinit var checkTV: TextView
    lateinit var infocheckTV: TextView
    lateinit var imgcheckTV: TextView
    lateinit var userLL: LinearLayout


    private val GALLERY = 1

    //비트맵 이미지 배열
    //이걸로 api배열에 이미지를 넣는다.
    var addImages = ArrayList<Bitmap>()
    var delids = ArrayList<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)

        return inflater.inflate(R.layout.fragment_setting_my_info, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compNameET = view.findViewById(R.id.compNameET)
        phoneNum1ET = view.findViewById(R.id.phoneNum1ET)
        phoneNum2ET = view.findViewById(R.id.phoneNum2ET)
        phoneNum3ET = view.findViewById(R.id.phoneNum3ET)
        compIdET = view.findViewById(R.id.compIdET)
        addImage1RL = view.findViewById(R.id.addImage1RL)
        termET = view.findViewById(R.id.termET)
        tempPasswordET = view.findViewById(R.id.tempPasswordET)
        newPasswordET = view.findViewById(R.id.newPasswordET)
        newPassCheckET = view.findViewById(R.id.newPassCheckET)
        checkTV = view.findViewById(R.id.checkTV)
        infocheckTV= view.findViewById(R.id.infocheckTV)
        imgcheckTV= view.findViewById(R.id.imgcheckTV)
        userLL =  view.findViewById(R.id.userLL)



    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        company_info(1)

        //정보수정
        infocheckTV.setOnClickListener {
            edit_info()
            company_info(1)
        }
        addImage1RL.setOnClickListener {

            if (userLL.getChildCount() > 9) {
                Toast.makeText(myContext, "10개만등록불가.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            choosePhotoFromGallary()
        }

        imgcheckTV.setOnClickListener {

            if (userLL.getChildCount() > 9) {
                Toast.makeText(myContext, "10개만등록불가.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            edit_image()
        }


    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }


    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                Log.d("uri",contentURI.toString())
                try
                {
                    var thumbnail = MediaStore.Images.Media.getBitmap(myContext.contentResolver, contentURI)
                    thumbnail = Utils.rotate(myContext.contentResolver, thumbnail, contentURI)

                    //비트맵배열에 비트맵추가
                    addImages.add(thumbnail)


                    Log.d("이미지 추가",addImages.toString())
                    val userView = View.inflate(myContext, R.layout.item_company_img, null)
                    val c_imgIV :ImageView = userView.findViewById(R.id.c_imgIV)
                    val delIV :ImageView = userView.findViewById(R.id.delIV)
                    c_imgIV.setImageBitmap(thumbnail)
                    userLL.addView(userView)
                    //배열사이즈값 -해줘서
                    userView.tag = addImages.size -1
                    Log.d("태그",userView.tag.toString())

                    delIV.setOnClickListener {
                        //그사이즈값으로 인덱스를 구해서 삭제!!!
                          addImages.removeAt(userView.tag as Int)
                            userLL.removeView(userView)
                    }

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(myContext, "바꾸기실패", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }




    //사업체 정보뽑기
    fun company_info(company_id: Int) {
        val params = RequestParams()
        params.put("company_id",company_id)

        CompanyAction.company_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {
                        val company = response.getJSONObject("company")
                        val company_name = Utils.getString(company,"company_name")
                        val phone1 = Utils.getString(company,"phone1")
                        val phone2 = Utils.getString(company,"phone2")
                        val phone3 = Utils.getString(company,"phone3")
                        val login_id = Utils.getString(company,"login_id")

                        compNameET.setText(company_name)
                        phoneNum1ET.setText(phone1)
                        phoneNum2ET.setText(phone2)
                        phoneNum3ET.setText(phone3)
                        compIdET.setText(login_id)

                        val images = response.getJSONArray("images")
                        Log.d("이미지",images.toString())

                        userLL.removeAllViews()

                        for (i in 0..images.length()-1){
                            //새로운뷰를 이미지의 길이만큼생성
                            val userView = View.inflate(myContext, R.layout.item_company_img, null)
                            var json=images[i] as JSONObject
                            Log.d("제이슨",json.toString())
                            val CompanyImage = json.getJSONObject("CompanyImage")
                            Log.d("제이슨이미지",json.toString())
                            val image_uri = Utils.getString(CompanyImage,"image_uri")
                            val c_imgIV :ImageView = userView.findViewById(R.id.c_imgIV)
                            val delIV :ImageView = userView.findViewById(R.id.delIV)
                            var image = Config.url + image_uri
                            Log.d("이미지1",image)
                            userView.tag = Utils.getInt(CompanyImage, "id")
                            delIV.setOnClickListener {
                                userView.visibility = View.GONE
                                Toast.makeText(myContext,userView.tag.toString(),Toast.LENGTH_SHORT).show()
                                delids.add(userView.tag as Int)
                                Log.d("아이디값",delids.toString())

                            }
                            ImageLoader.getInstance().displayImage(image,c_imgIV, Utils.UILoptionsUserProfile)
                            userLL.addView(userView)

                        }


                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

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


    //사업체 정보수정
    fun edit_info() {
        val company_name = Utils.getString(compNameET)
        var phone1:String =  Utils.getString(phoneNum1ET)
        var phone2:String =  Utils.getString(phoneNum2ET)
        var phone3:String =  Utils.getString(phoneNum3ET)
        var login_id:String =  Utils.getString(compIdET)

        val params = RequestParams()
        params.put("company_id",1)
        params.put("company_name",company_name)
        params.put("phone1",phone1)
        params.put("phone2",phone2)
        params.put("phone3",phone3)
        params.put("login_id",login_id)


        CompanyAction.edit_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {
                        Toast.makeText(myContext,"수정완료", Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(myContext,"수정실패", Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

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

    //사업체 이미지 업데이트및 삭제
    fun edit_image() {

        val params = RequestParams()
        params.put("company_id",1)
        if (delids.size>0){

            for (i in 0..(delids.size -1)){
                val delimg = delids[i]
                //배열로 입력저장은 [] 이걸 넣어준다
                params.put("del_ids["+i+"]",delimg)
                Log.d("삭제번호",delimg.toString())

            }

        }


            //비트맵배열의 크기만큼
        if (addImages.size > 0){
            for(i in 0..(addImages.size - 1)) {
                val byteArrayInputStream = ByteArrayInputStream(Utils.getByteArray(addImages[i]))
                params.put("upload[" + i + "]", byteArrayInputStream)
                Log.d("바이트썸네",byteArrayInputStream.toString())
            }
        }

        CompanyAction.edit_image(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {
                        addImages.clear()
                        company_info(1)

                    }else{
                        Toast.makeText(myContext,"업데이트실패", Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

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