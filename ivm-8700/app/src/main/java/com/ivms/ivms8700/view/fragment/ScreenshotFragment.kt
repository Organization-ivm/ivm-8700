package com.ivms.ivms8700.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle


import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ivms.ivms8700.R
import com.ivms.ivms8700.utils.PhotoVideoManager.adapter.ImageAdapter
import com.ivms.ivms8700.utils.PhotoVideoManager.bean.Bean
import com.ivms.ivms8700.view.MainActivity
import java.io.File
import java.util.ArrayList

class ScreenshotFragment : Fragment(), ImageAdapter.OnShowItemClickListener {


    lateinit var gridView: GridView
    lateinit var iv_background: ImageView
    //    private val path = Environment.getExternalStorageDirectory().toString() + "/HIKVISION/"
    private val path = Environment.getExternalStoragePublicDirectory("").toString() + "/HIKVISION/"
    lateinit var v: View
    lateinit var tvShare: TextView
    lateinit var tvDelete: TextView
    var dataList: MutableList<Bean>? = null
    var selectedList: MutableList<Bean>? = null
    private val paths = ArrayList<String>()
    private val items = ArrayList<String>()
    private var myAdapter: ImageAdapter? = null
    public var isShow: Boolean = false
    private var isSel = true


    private var file: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_screenshot, container, false)
        initView(v)
        setListener()



        return v
    }

    fun clearDate() {
        if (dataList != null) {
            dataList!!.clear()

        }
        if (selectedList != null) {
            selectedList!!.clear()
        }
        if (paths != null) {

            paths!!.clear()
        }
        if (items != null) {
            items.clear()
        }

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
                val item = dataList!!.get(position)
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
                file = File(paths[position])
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse("file://$file"), "image/*")
                startActivity(intent)
            }

        }
        tvDelete.setOnClickListener {
            if (selectedList != null && selectedList!!.size > 0) {
                for (i in 0..selectedList!!.size - 1) {
                    file = File(selectedList!![i].filePath)
                    if (file!!.exists()) {
                        file!!.delete()
                    } else {
                        Toast.makeText(context, "文件已删除", Toast.LENGTH_SHORT).show();
                    }
                }
                for (i in 0..selectedList!!.size - 1) {
                    Log.i("tag", "==selectedList===" + selectedList!![i].getFilePath());
                }
                dataList!!.removeAll(selectedList!!)
                selectedList!!.clear()
                dataList!!.clear()
                myAdapter!!.clearData()
                getAllFiles(path)
                Log.i("tag", "==items.size===" + items!!.size)
                for (i in 0..paths.size - 1) {
                    var bean = Bean()
                    bean.setFilePath(paths[i])
                    bean.setChecked(false)
                    bean.setShow(isShow)
                    dataList!!.add(bean)
                }
                changeShow()
                choose()
                myAdapter!!.setItems(dataList)
                gridView.setAdapter(myAdapter)
                myAdapter!!.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "请选择条目", Toast.LENGTH_SHORT).show()
            }
            isshowImage();
        }
    }

    fun changeShow() {
        isShow = !isShow
    }

    //是否显示背景
    private fun isshowImage() {
        if (paths.size == 0 || dataList!!.size == 0) {
            iv_background.visibility = View.VISIBLE
        } else {
            iv_background.visibility = View.GONE
        }
    }

    private fun initView(v: View) {
        gridView = v.findViewById(R.id.gridview) as GridView
        tvShare = v.findViewById(R.id.tvshare) as TextView
        tvDelete = v.findViewById(R.id.tvdelete) as TextView
        selectedList = ArrayList<Bean>()
        iv_background = v.findViewById(R.id.background2) as ImageView
        dataList = ArrayList<Bean>()
        initDataList()
        myAdapter = ImageAdapter(context, paths, gridView, dataList)
        myAdapter!!.setOnShowItemClickListener(this)
        gridView.setAdapter(myAdapter)
    }

    override fun onShowItemClick(bean: Bean?) {
        if (bean!!.getFilePath().equals(paths[0])) {
            if (bean != null) {
                selectedList!!.add(bean)
            }
        }
        if (bean.isChecked()) {
            selectedList!!.add(bean)
        } else {
            selectedList!!.remove(bean)
        }

    }

    private fun initDataList() {
        getAllFiles(path)
        for (i in paths.indices) {
            val bean = Bean()
            bean.filePath = paths[i]
            bean.isChecked = false
            bean.isShow = isShow
            dataList!!.add(bean)
        }
    }

    private fun getAllFiles(path: String) {
        val file = File(path)
        val files = file.listFiles()
        if (files != null) {
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    val path1 = files[i].path + "/"
                    getAllFiles(path1)
                } else {
                    val name = files[i].name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (name.size == 2 && name[1] == "jpg" || name[1] == "png") {
                        items.add(files[i].name)
                        paths.add(files[i].path)
                    }
                }
            }
            return
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
