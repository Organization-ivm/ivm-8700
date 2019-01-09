package com.ivms.ivms8700.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ivms.ivms8700.R
import com.ivms.ivms8700.utils.PhotoVideoManager.adapter.ImageAdapter
import com.ivms.ivms8700.utils.PhotoVideoManager.bean.Bean
import com.ivms.ivms8700.utils.ShareUtils
import com.ivms.ivms8700.utils.UIUtil
import kotlinx.android.synthetic.main.fragment_screenshot.*
import java.io.File
import java.util.ArrayList

class ScreenshotFragment : Fragment() {


    lateinit var gridView: GridView
    //        private val path = Environment.getExternalStorageDirectory().toString() + "/HIKVISION/"
    private val path = Environment.getExternalStoragePublicDirectory("").toString() + "/HIKVISION/"
    lateinit var v: View
    lateinit var tvShare: TextView
    lateinit var tvDelete: TextView
    var dataList: MutableList<Bean>? = null
    var selectedList: MutableList<Bean>? = null
    private var myAdapter: ImageAdapter? = null
     var isShow: Boolean = false
    private var isSel = true
    private var file: File? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_screenshot, container, false)
        initView(v)
        setListener()
        return v
    }

    /**
     * 控制最里层fragment（视频截图与本地录像）点击切换时，确保刷新了最新的数据
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initDataList()

        }
    }

    private fun initView(v: View) {
        gridView = v.findViewById(R.id.gridview) as GridView
        tvShare = v.findViewById(R.id.tvshare) as TextView
        tvDelete = v.findViewById(R.id.tvdelete) as TextView
        selectedList = ArrayList()
        dataList = ArrayList()
        initDataList()
        myAdapter = ImageAdapter(context, dataList)
        gridView.setAdapter(myAdapter)
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
            delete()

        }
        tvShare.setOnClickListener {
            share()

        }
    }

    /**
     * 初始化加载数据
     */
    fun initDataList() {
        showLoadingProgress()
        Thread(Runnable {
            dataList!!.clear()
            getAllFiles(path)
            parentFragment!!.activity!!.runOnUiThread {
                cancelLoadingProgress()
                myAdapter!!.setItems(dataList)
                myAdapter!!.notifyDataSetChanged()
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
     * 清除数据
     */
    fun clearDate() {
        if (dataList != null) {
            dataList!!.clear()
        }
        if (selectedList != null) {
            selectedList!!.clear()
        }
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
     * 获取文件夹下的所有目录
     */
    private fun getAllFiles(path: String) {
        val file = File(path)
        val files = file.listFiles()
        var bean: Bean
        if (files != null) {
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    val path1 = files[i].path + "/"
                    getAllFiles(path1)
                } else {
                    val name = files[i].name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (name.size == 2 && (name[1] == "jpg" || name[1] == "png")) {
                        bean = Bean()
                        bean.filePath = files[i].path
                        bean.isChecked = false
                        bean.isShow = isShow
                        dataList!!.add(bean)
                    }
                }
            }
            return
        }
    }

    /**
     * 编辑按钮点击时间
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
     * item点击事件
     */
    fun selectChoose(position: Int) {
        //判断是否在选择状态选择状态就勾选，非选择状态就查看图片
        if (isShow) {
            val item = dataList!!.get(position)
            val isChecked = item.isChecked
            Log.i("tag", "===isChecked===$isChecked")
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
            Log.i("tag", "===isChecked=after==$isChecked")
            myAdapter!!.notifyDataSetChanged()
        } else {
            file = File(dataList!![position].filePath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            var uri = FileProvider.getUriForFile(context!!, "com.ivms.ivms8700.fileprovider", file!!)
            intent.setDataAndType(uri, "image/*")
            Log.i("URI", "uriPath" + uri.path.toString())
            Log.i("URI", "filePath" + file!!.path.toString())
            startActivity(intent)
        }
    }

    /**
     * 删除按钮点击事件
     */
    fun delete() {
        if (selectedList != null && selectedList!!.size > 0) {
            for (i in 0..selectedList!!.size - 1) {
                file = File(selectedList!![i].filePath)
                if (file!!.exists()) {
                    file!!.delete()
                } else {
                    Toast.makeText(context, "文件已删除", Toast.LENGTH_SHORT).show();
                }
            }
            dataList!!.removeAll(selectedList!!)
            selectedList!!.clear()
            dataList!!.clear()
            getAllFiles(path)
            changeShow()
            choose()
            myAdapter!!.setItems(dataList)
            gridView.setAdapter(myAdapter)
            myAdapter!!.notifyDataSetChanged()
        } else {
            Toast.makeText(context, "请选择条目", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 分享按钮点击事件
     */
    fun share() {
        if (selectedList != null && selectedList!!.size > 0) {
            file = File(selectedList!![0].filePath)
            if (file!!.exists()) {
                var uri = FileProvider.getUriForFile(context!!, "com.ivms.ivms8700.fileprovider", file!!)

                ShareUtils.shareImg(context, "123", "qwe", "图片", uri)
            } else {

                Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(context, "请选择条目", Toast.LENGTH_SHORT).show()
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
