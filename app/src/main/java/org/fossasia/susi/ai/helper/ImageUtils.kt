package org.fossasia.susi.ai.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * Created by chiragw15 on 14/7/17.
 */
class ImageUtils {

    companion object {
        fun writeToTempImage(inContext: Context, inImage: Bitmap): Uri {
            val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
            return Uri.parse(path)
        }

        fun getImageUrl(context: Context, uri: Uri): Uri? {
            var inputStream: InputStream? = null
            if (uri.authority != null) {
                try {
                    inputStream = context.contentResolver.openInputStream(uri)
                    val bmp = BitmapFactory.decodeStream(inputStream)
                    return writeToTempImage(context, bmp)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            return null
        }

        fun encodeImage(context: Context, selectedImageUri: Uri): String {
            val imageStream: InputStream = context.contentResolver.openInputStream(selectedImageUri)
            val selectedImage: Bitmap
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(getImageUrl(context.applicationContext, selectedImageUri), filePathColumn, null, null, null)
            cursor?.moveToFirst()
            selectedImage = BitmapFactory.decodeStream(imageStream)
            val baos = ByteArrayOutputStream()
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
            cursor.close()
            return encodedImage
        }

        fun decodeImage(context: Context, previouslyChatImage: String): Drawable {
            val b = Base64.decode(previouslyChatImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
            return BitmapDrawable(context.resources, bitmap)
        }

        fun cropImage(thePic: Bitmap): String {
            val baos = ByteArrayOutputStream()
            thePic.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

    }

}