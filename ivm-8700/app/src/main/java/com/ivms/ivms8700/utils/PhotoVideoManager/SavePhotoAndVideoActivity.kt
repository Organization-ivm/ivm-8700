//package com.photochoose.ph
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.BitmapFactory
//import android.os.Build
//import android.os.Bundle
//import android.support.v4.app.ActivityCompat
//import android.support.v4.app.ActivityCompat.startActivity
//import android.support.v4.content.ContextCompat
//import android.widget.Toast
//import com.ivms.ivms8700.R
//import com.ivms.ivms8700.utils.PhotoVideoManager.MainActivity
//import com.ivms.ivms8700.utils.PhotoVideoManager.showVidoActivity
//
//class SavePhotoAndVideoActivity : AppCompatActivity() {
//    private val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1001
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_save_photo_and_video)
//        requestPremission()
//
//        btnSavePhoto.setOnClickListener {
//            savePhoto()
//
//        }
//        btnSaveVideo.setOnClickListener {
//
//        }
//        btnJumpPhoto.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//
//        }
//        btnJumpVideo.setOnClickListener {
//        startActivity(Intent(this, showVidoActivity::class.java))
//
//        }
//    }
//
//    private fun savePhoto() {
//        Thread(Runnable {
//            for (i in 0..3) {
//                PhotoUtils.savePhoto(BitmapFactory.decodeResource(resources, R.mipmap.we))
////                runOnUiThread { Toast.makeText(MyApplication.getIns(), "成功", Toast.LENGTH_SHORT).show() }
//            }
//        }).start()
//    }
//
//    private fun requestPremission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                        MY_PERMISSIONS_REQUEST_CALL_PHONE)
//            } else {
////                savePhoto()
//
//                //                showToast("权限已申请");
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                Toast.makeText(MyApplication.getIns(), "权限已拒绝", Toast.LENGTH_SHORT).show()
//
//            }
//        }
//    }
//
//
//}
