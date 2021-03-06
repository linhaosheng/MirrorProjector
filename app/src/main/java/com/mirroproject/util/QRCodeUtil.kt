package com.mirroproject.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap

/**
 * Created by reeman on 2017/10/31.
 */
class QRCodeUtil {

    companion object {
        /**
         * 生成二维码Bitmap
         *
         * @param content   内容
         * @param widthPix  图片宽度
         * @param heightPix 图片高度
         * @param logoBm    二维码中心的Logo图标（可以为null）
         * @param filePath  用于存储二维码图片的文件路径
         * @return 生成二维码及保存文件是否成功
         */
        fun createQRImage(content: String?, widthPix: Int, heightPix: Int, logoBm: Bitmap?, filePath: String): Boolean {
            var fos: FileOutputStream? = null
            try {
                if (content == null || "" == content) {
                    return false
                }

                // 配置参数
                var hints = HashMap<EncodeHintType, Any>()
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
                // 容错级别
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                // 设置空白边距的宽度
                // hints.put(EncodeHintType.MARGIN, 2); //default is 4

                // 图像数据转换，使用了矩阵转换
                val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints)
                val pixels = IntArray(widthPix * heightPix)
                // 下面这里按照二维码的算法，逐个生成二维码的图片，
                // 两个for循环是图片横列扫描的结果
                for (y in 0 until heightPix) {
                    for (x in 0 until widthPix) {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * widthPix + x] = -0x1000000
                        } else {
                            pixels[y * widthPix + x] = -0x1
                        }
                    }
                }

                // 生成二维码图片的格式，使用ARGB_8888
                var bitmap: Bitmap? = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888)
                bitmap!!.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix)

                if (logoBm != null) {
                    bitmap = addLogo(bitmap, logoBm)
                }
                // 必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
                val path = File(filePath)
                if (!path.exists()) {
                    path.createNewFile()
                }
                fos = FileOutputStream(filePath)
                return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } catch (e: WriterException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }

            return false
        }

        /**
         * 在二维码中间添加Logo图案
         */
        private fun addLogo(src: Bitmap?, logo: Bitmap?): Bitmap? {
            if (src == null) {
                return null
            }

            if (logo == null) {
                return src
            }

            // 获取图片的宽高
            val srcWidth = src.width
            val srcHeight = src.height
            val logoWidth = logo.width
            val logoHeight = logo.height

            if (srcWidth == 0 || srcHeight == 0) {
                return null
            }

            if (logoWidth == 0 || logoHeight == 0) {
                return src
            }

            // logo大小为二维码整体大小的1/5
            val scaleFactor = srcWidth * 1.0f / 5f / logoWidth.toFloat()
            var bitmap: Bitmap? = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
            try {
                val canvas = Canvas(bitmap!!)
                canvas.drawBitmap(src, 0f, 0f, null)
                canvas.scale(scaleFactor, scaleFactor, (srcWidth / 2).toFloat(), (srcHeight / 2).toFloat())
                canvas.drawBitmap(logo, ((srcWidth - logoWidth) / 2).toFloat(), ((srcHeight - logoHeight) / 2).toFloat(), null)
                canvas.save(Canvas.ALL_SAVE_FLAG)
                canvas.restore()
            } catch (e: Exception) {
                bitmap = null
                e.stackTrace
            }

            return bitmap
        }

        //生成二维码图片
        fun createErCode(device: String, path: String, size: Int) {
            val downDir = path.substring(0, path.lastIndexOf("/")).trim { it <= ' ' }
            Log.i("downDir===", downDir)
            val file = File(downDir)
            if (!file.exists()) {
                val mkdirs = file.mkdirs()
                Log.i("create==downDir===", mkdirs.toString() + "")
            }
            Thread(Runnable {
                val qrImage = createQRImage(device, size, size, null, path)
                Log.i("create ER ", "" + qrImage)
            }).start()
        }
    }
}