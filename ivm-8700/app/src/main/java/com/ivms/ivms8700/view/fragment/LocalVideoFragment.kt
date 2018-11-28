package com.ivms.ivms8700.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ivms.ivms8700.R
import com.ivms.ivms8700.utils.PhotoVideoManager.VideoInfo
import com.ivms.ivms8700.utils.PhotoVideoManager.adapter.VideoAdapter
import com.ivms.ivms8700.utils.PhotoVideoManager.utils.VideoUtils
import com.ivms.ivms8700.utils.UIUtil
import java.io.File
import java.util.ArrayList


@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class LocalVideoFragment : Fragment(), VideoAdapter.OnShowItemClickListener {


    lateinit var v: View


    lateinit var gridView: GridView
    lateinit var iv_background: ImageView
    //    private val path = Environment.getExternalStorageDirectory().toString() + "/HIKVISION/"
    private val path = Environment.getExternalStoragePublicDirectory("").toString() + "/HIKVISION/"
    lateinit var tvShare: TextView
    lateinit var tvDelete: TextView
    var dataList: MutableList<VideoInfo>? = null
    var selectedList:MutableList<VideoInfo>? = null
    //    private List<String> paths = new ArrayList<String>();
    //    private List<String> items = new ArrayList<String>();
    private val videoList = ArrayList<VideoInfo>()
//    private val paths = ArrayList<String>()
//    private val items = ArrayList<String>()
    private var myAdapter: VideoAdapter? = null
    public var isShow: Boolean = false
    private var isSel = true


    private var file: File? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_video, container, false)

        // Inflate the layout for this fragment
        initView(v)
        setListener()
        return v
    }


    private fun setListener() {
        gridView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            isShow = !isShow
            choose()
            true
        }
        gridView.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

            //判断CheckBox是否显示，如果显示说明在选择状态，否则是浏览状态用图片浏览器打开
            if (isShow) {
                val item = dataList!![position]
                val isChecked = item.isChecked
                Log.i("tag", "===isChecked===$isChecked")
                if (isChecked) {
                    item.isChecked = false
                } else {
                    item.isChecked = true
                }
                Log.i("tag", "===isChecked=after==$isChecked")
                myAdapter!!.notifyDataSetChanged()
            } else {
                file = File(videoList[position].path)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse("file://$file"), "image/*")
                startActivity(intent)
            }

        }
        tvDelete.setOnClickListener {
            if (selectedList != null && selectedList!!.size > 0) {
                for (i in selectedList!!.indices) {
                    file = File(selectedList!![i].path)
                    if (file!!.exists()) {
                        file!!.delete()
                    } else {
                        Toast.makeText(context, "文件已删除", Toast.LENGTH_SHORT).show()
                    }
                }
//                for (i in selectedList!!.indices) {
//                    Log.i("tag", "==selectedList===" + selectedList!![i].path)
//                }
                dataList!!.removeAll(selectedList!!)
                selectedList!!.clear()
//                dataList!!.clear()
//                myAdapter!!.clearData()
//                videoList!!.clear()
//                getAllFiles(path)
//                Log.i("tag", "==video.size===" + videoList.size)
//                for (i in videoList.indices) {
//                    val bean = VideoInfo()
//                    bean.path = videoList[i].path
//                    bean.isChecked = false
//                    bean.isShow = isShow
//                    dataList!!.add(bean)
//                }
                changeShow()
                choose()
                myAdapter!!.setItems(dataList!!)
//                gridView.adapter = myAdapter
                myAdapter!!.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "请选择条目", Toast.LENGTH_SHORT).show()
            }
            isshowImage()
        }
    }

    fun changeShow() {
        isShow = !isShow

    }

    //是否显示背景
    private fun isshowImage() {
        if (videoList.size == 0 || dataList!!.size == 0) {
            iv_background.setVisibility(View.VISIBLE)
        } else {
            iv_background.setVisibility(View.GONE)
        }
    }

    private fun initView(v: View) {
        gridView = v.findViewById(R.id.gridview) as GridView
        tvShare = v.findViewById(R.id.tvshare) as TextView
        tvDelete = v.findViewById(R.id.tvdelete) as TextView
        selectedList = ArrayList<VideoInfo>()
        iv_background = v.findViewById(R.id.background3) as ImageView
        dataList = ArrayList<VideoInfo>()

        initDataList()
        myAdapter = VideoAdapter(context, videoList, gridView, dataList)
        myAdapter!!.setOnShowItemClickListener(this)
        gridView.setAdapter(myAdapter)
    }


    private fun initDataList() {
        showLoadingProgress()
        Thread(Runnable {
            getAllFiles(path)
            dataList!!.clear()
            for (i in videoList.indices) {
                val bean = VideoInfo()
                bean.path = videoList[i].path
                bean.lastModifed
                bean.bitmap = videoList[i].bitmap
                bean.lastModifed = videoList[i].lastModifed
                bean.time = videoList[i].time
                bean.isChecked = false
                bean.isShow = isShow
                dataList!!.add(bean)
            }
            parentFragment!!.activity!!.runOnUiThread {
                cancelLoadingProgress()
                myAdapter!!.setItems(dataList)
                myAdapter!!.notifyDataSetChanged()

            }
        }).start()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            initDataList()

        }
    }


    fun choose() {
        isSel = isShow
        if (isSel) {


//            tv_sel.setText("取消")
        } else {
//            tv_sel.setText("选择")

            for (item in dataList!!) {
                isShow = false
                item.setChecked(false)
            }
            selectedList!!.clear()
            myAdapter!!.notifyDataSetChanged()
        }
        for (item in dataList!!) {
            item.setShow(isShow)
        }
        myAdapter!!.notifyDataSetChanged()
        isSel = !isSel


    }

    /**
     * 加载进度条
     */
    private fun showLoadingProgress() {
        UIUtil.showProgressDialog(activity, R.string.loading_process_tip)
    }

    /**
     * 取消进度条
     */
    private fun cancelLoadingProgress() {
        UIUtil.cancelProgressDialog()
    }


    private fun getAllFiles(path: String) {

            val file = File(path)
            videoList!!.clear()
            val files = file.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    if (files[i].isDirectory) {
                        val path1 = files[i].path + "/"
                        getAllFiles(path1)
                    } else {
                        val name = files[i].name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (name.size == 2 && name[1] == "mp4") {
                            val videoInfo = VideoInfo()
                            videoInfo.lastModifed = UIUtil.timeStamp2Date(files[i].lastModified().toString())
                            videoInfo.name = files[i].name
                            videoInfo.path = files[i].path
                            videoInfo.bitmap = VideoUtils.getBitmapFromFile(files[i].path)
                            videoInfo.time = VideoUtils.getVideoDuration(files[i].path)
                            videoList.add(videoInfo)
                        }
                    }
                }
            }


    }

    override fun onShowItemClick(bean: VideoInfo?) {
        if (bean!!.path.equals(videoList[0].path)) {
            selectedList!!.add(bean)
        }
        if (bean.isChecked()) {
            selectedList!!.add(bean)
        } else {
            selectedList!!.remove(bean)
        }
    }

    //监听返回键
    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isSel) {
//                tv_sel.setText("选择")
                selectedList!!.clear()
                for (item in dataList!!) {
                    isShow = false
                    item.setChecked(false)
                }
                myAdapter!!.notifyDataSetChanged()
            } else {
                parentFragment!!.activity!!.finish()
            }
            for (item in dataList!!) {
                item.setShow(isShow)
            }
            myAdapter!!.notifyDataSetChanged()
            isSel = !isSel
        }
        return true
    }

}
