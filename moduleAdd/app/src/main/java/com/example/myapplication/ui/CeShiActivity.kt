package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.xcjh.base_lib.utils.getXXPermissions

class CeShiActivity : AppCompatActivity() {
    var imnageShow: AppCompatImageView?=null
    var glShow: GLSurfaceView?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ce_shi)
        var btnInage=findViewById<Button>(R.id.btnInage)
          imnageShow=findViewById<AppCompatImageView>(R.id.imnageShow)
        glShow=findViewById<GLSurfaceView>(R.id.glShow)


        btnInage.setOnClickListener {

            getXXPermissions(this){
                // 启动相册
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickIntent, 10002)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10002 && resultCode == Activity.RESULT_OK && data != null) {
            // 处理从相册返回的图片GLSurfaceView.Renderer
            val selectedImageUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            Glide.with(this).load(bitmap).into(imnageShow!!)
//            glShow!!.setRenderer(ImageRenderer(this,bitmap))
            loadTextureFromBitmap(bitmap)


            // 这里可以对选择的图片进行操作，比如显示在 ImageView 中
        }
    }

    private fun loadTextureFromBitmap(bitmap: Bitmap): Int {
        val textureId = IntArray(1)
        GLES20.glGenTextures(1, textureId, 0)
        val texture = textureId[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

        // 设置纹理参数
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        // 将Bitmap转换为OpenGL纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        return texture
    }
}