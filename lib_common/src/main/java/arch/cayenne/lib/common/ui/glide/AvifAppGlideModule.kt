package arch.cayenne.lib.common.ui.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.tencent.libavif.AvifSequenceDrawable
import com.tencent.qcloud.image.avif.glide.avif.ByteBufferAvifDecoder
import com.tencent.qcloud.image.avif.glide.avif.ByteBufferAvifSequenceDecoder
import com.tencent.qcloud.image.avif.glide.avif.StreamAvifDecoder
import com.tencent.qcloud.image.avif.glide.avif.StreamAvifSequenceDecoder
import java.io.InputStream
import java.nio.ByteBuffer


/**
 * @date: 2025/10/1 14:54
 * @description:
 */
@GlideModule
class AvifAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        /*------------------解码器 开始-------------------------*/
        //注册 AVIF 静态图片解码器
        registry.prepend<InputStream, Bitmap>(
            Registry.BUCKET_BITMAP,
            InputStream::class.java,
            Bitmap::class.java, StreamAvifDecoder(glide.bitmapPool, glide.arrayPool)
        )
        registry.prepend<ByteBuffer, Bitmap>(
            Registry.BUCKET_BITMAP,
            ByteBuffer::class.java,
            Bitmap::class.java, ByteBufferAvifDecoder(glide.bitmapPool)
        )
        //注册 AVIF 动图解码器
        registry.prepend(
            InputStream::class.java,
            AvifSequenceDrawable::class.java,
            StreamAvifSequenceDecoder(glide.bitmapPool, glide.arrayPool)
        )
        registry.prepend(
            ByteBuffer::class.java,
            AvifSequenceDrawable::class.java, ByteBufferAvifSequenceDecoder(glide.bitmapPool)
        )
        /*------------------解码器 结束-------------------------*/
    }
}