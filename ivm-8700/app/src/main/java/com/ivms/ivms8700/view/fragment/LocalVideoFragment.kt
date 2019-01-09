package com.ivms.ivms8700.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ivms.ivms8700.R
import com.ivms.ivms8700.utils.MediaFile
import com.ivms.ivms8700.utils.PhotoVideoManager.VideoInfo
import com.ivms.ivms8700.utils.PhotoVideoManager.adapter.VideoAdapter
import com.ivms.ivms8700.utils.PhotoVideoManager.utils.VideoUtils
import com.ivms.ivms8700.utils.ShareUtils
import com.ivms.ivms8700.utils.UIUtil
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.File
import java.util.ArrayList


@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class LocalVideoFragment : Fragment() {


    lateinit var v: View


    lateinit var gridView: GridView
    private val path = Environment.getExternalStorageDirectory().toString() + "/HIKVISION/"
    //    private val path = Environment.getExternalStoragePublicDirectory("").toString() + "/HIKVISION/"
    lateinit var tvShare: TextView
    lateinit var tvDelete: TextView
    var dataList: MutableList<VideoInfo>? = null
    var selectedList: MutableList<VideoInfo>? = null
    private val videoList = ArrayList<VideoInfo>()
    private var myAdapter: VideoAdapter? = null
    var isShow: Boolean = false
    private var isSel = true
    private var file: File? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_video, container, false)
        initView(v)
        setListener()
        return v
    }

    /**
     * fragment显示出来时刷新数据
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initDataList()
        }
    }

    /**
     * 初始化
     */
    private fun initView(v: View) {
        gridView = v.findViewById(R.id.gridview) as GridView
        tvShare = v.findViewById(R.id.tvshare) as TextView
        tvDelete = v.findViewById(R.id.tvdelete) as TextView
        selectedList = ArrayList()
        dataList = ArrayList()

        initDataList()
        myAdapter = VideoAdapter(context, dataList)
        gridView.adapter = myAdapter
    }


    private fun setListener() {
        gridView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            isShow = !isShow
            choose()
            true
        }
        gridView.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            //判断CheckBox是否显示，如果显示说明在选择状态，否则是浏览状态用图片浏览器打开
            selectChoose(position)
        }
        tvDelete.setOnClickListener {
            deleteItem()

//            isshowImage()
        }
        tvShare.setOnClickListener {
            shareItem()
        }
    }

    /**
     * 初始化视屏列表资源
     */
    fun initDataList() {
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
            if (null != activity) {
                parentFragment!!.activity!!.runOnUiThread {
                    cancelLoadingProgress()
                    myAdapter!!.setItems(dataList)
                    myAdapter!!.notifyDataSetChanged()

                }
            }
        }).start()

    }


    /**
     * 改变是否选择状态
     */

    fun changeShow() {
        isShow = !isShow
    }

    /**
     * 编辑按钮点击事件
     */
    fun choose() {
        isSel = isShow
        if (isSel) {
            rlbottom.visibility = View.VISIBLE
        } else {
            rlbottom.visibility = View.GONE
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

    /**
    获取文件夹下的所有视频生成对象装进list里
     */
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

    /**
     * 点击item事件
     */
    fun selectChoose(position: Int) {
        if (isShow) {
            val item = dataList!![position]
            val isChecked = item.isChecked
            if (isChecked) {
                item.isChecked = false
                selectedList!!.remove(item)
            } else {
                if (selectedList != null && selectedList!!.size > 0) {
                    Toast.makeText(context, getString(R.string.toast_choose_one), Toast.LENGTH_SHORT).show()
                    return
                }
                item.isChecked = true
                selectedList!!.add(item)
            }
            myAdapter!!.notifyDataSetChanged()
        } else {
            file = File(videoList[position].path)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            var uri = FileProvider.getUriForFile(context!!, "com.ivms.ivms8700.fileprovider", file!!)
            intent.setDataAndType(uri, "video/mp4")
            try {
                startActivity(intent)
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 删除按钮点击事件
     */
    fun deleteItem() {
        if (selectedList != null && selectedList!!.size > 0) {
            for (i in selectedList!!.indices) {
                file = File(selectedList!![i].path)
                if (file!!.exists()) {
                    if (file!!.delete()) {
                        delete(selectedList!![i].path)
                    }
                } else {
                    Toast.makeText(context, "文件已删除", Toast.LENGTH_SHORT).show()
                }
            }
            dataList!!.removeAll(selectedList!!)
            selectedList!!.clear()
            changeShow()
            choose()
            myAdapter!!.setItems(dataList!!)
//                gridView.adapter = myAdapter
            myAdapter!!.notifyDataSetChanged()
        } else {
            Toast.makeText(context, "请选择条目", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 分享按钮
     */
    fun shareItem() {
        if (selectedList != null && selectedList!!.size > 0) {
            file = File(selectedList!![0].path)
            if (file!!.exists()) {
                var uri = FileProvider.getUriForFile(context!!, "com.ivms.ivms8700.fileprovider", file!!)
                ShareUtils.shareVideo(context, "分享", "qwe", "视频", uri)
            } else {
                Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "请选择条目", Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * 防止删除视频时删除不干净，二次删除确保删掉
     */
    fun delete(filePath: String) {
        var where = ""
        var uri: Uri
        if (MediaFile.isVideoFileType(filePath)) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Video.Media.DATA;
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Images.Media.DATA;
        }
        where += "='" + filePath + "'";
        var mContentResolver = activity!!.contentResolver;
        mContentResolver.delete(uri, where, null);
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
