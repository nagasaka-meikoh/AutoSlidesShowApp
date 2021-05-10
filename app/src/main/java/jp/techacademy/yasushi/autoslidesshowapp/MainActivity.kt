package jp.techacademy.yasushi.autoslidesshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    var cursor: Cursor? = null

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0


    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("aa", Build.VERSION.SDK_INT.toString())
        Log.d("bb", Build.VERSION_CODES.M.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //アクセス確認されている時の処理
                getContentsInfo()
            }
            else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }

        } else finish()

        start_Button.setOnClickListener() {
            if (mTimer == null){
                //再生実行
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (cursor!!.moveToNext()){
                            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor!!.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            mHandler.post {
                                imageView.setImageURI(imageUri)
                            }
                        }else {
                            cursor!!.moveToFirst()
                            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor!!.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            mHandler.post {
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
                start_Button.text = "停止"

                if (start_Button.text == "停止") {
                    next_Button.isEnabled = false
                    back_Button.isEnabled = false
                }

            } else {
                //再生停止
                mTimer!!.cancel()
                start_Button.text = "再生"
                next_Button.isEnabled = true
                back_Button.isEnabled = true
                mTimer = null
            }


        }


        next_Button.setOnClickListener() {
            if (cursor!!.moveToNext()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            } else {

                cursor!!.moveToFirst()
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }

            if (cursor != null) {
                finish()
            }

        }

        back_Button.setOnClickListener(){
            if (cursor!!.moveToPrevious()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            } else {

                cursor!!.moveToLast()
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }


        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //アクセス許可、戻る
                    getContentsInfo()
                } else {
                    //アクセス不許可、アプリ終了
                    finish()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）

        )

        if (cursor!!.moveToFirst()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cursor != null) {
            cursor!!.close()
        }
    }

}