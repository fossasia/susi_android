package org.fossasia.susi.ai.profile

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_edit_profile.edit_profile_image
import kotlinx.android.synthetic.main.fragment_edit_profile.update_profile
import kotlinx.android.synthetic.main.fragment_edit_profile.edit_user_name
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class EditProfileFragment : Fragment() {

    private val GALLERY = 1
    private val CAMERA = 2
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getDatas()

        setupPermissions()

        edit_profile_image.setOnClickListener {
            showPictureDialog()
        }

        update_profile.setOnClickListener {
            val dialogClickListener = AlertDialog.Builder(requireContext())
            with(dialogClickListener) {
                setTitle(R.string.dialog_profile_update)
                setMessage(R.string.dialog_profile_message)
                setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                    loadDatas()
                })
                setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
                show()
                super.onViewCreated(view, savedInstanceState)
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), R.string.camera_permission, Toast.LENGTH_SHORT).show()
            edit_profile_image.isClickable = false
            makeRequest()
        } else {
            edit_profile_image.isClickable = true
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                RECORD_REQUEST_CODE)
    }

    private fun getDatas() {
        val user_name = PrefManager.getString(Constant.USER_NAME, Constant.SUSI)
        edit_user_name.setText(user_name)

        val profile_image_path = PrefManager.getString(Constant.PROFILE_IMAGE_PATH, Constant.SUSI)
        if (profile_image_path != Constant.SUSI) {
            edit_profile_image.setImageBitmap(BitmapFactory.decodeFile(profile_image_path))
        }
    }

    private fun loadDatas() {

        val user_name = edit_user_name.getText()
        PrefManager.putString(Constant.USER_NAME, user_name.toString())
        Toast.makeText(requireContext(), R.string.update_success, Toast.LENGTH_LONG).show()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        pictureDialog.setTitle(R.string.select_action)
        val pictureDialogItems = arrayOf(getString(R.string.photo_gallery), getString(R.string.photo_camera))
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    PrefManager.putString(Constant.PROFILE_IMAGE_PATH, path)
                    Toast.makeText(requireContext(), R.string.image_saved, Toast.LENGTH_SHORT).show()
                    edit_profile_image.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMERA) {
            val thumbnail = data?.extras?.get("data") as Bitmap
            val path = saveImage(thumbnail)
            edit_profile_image.setImageBitmap(BitmapFactory.decodeFile(path))
            Toast.makeText(context, path, Toast.LENGTH_LONG).show()
            PrefManager.putString(Constant.PROFILE_IMAGE_PATH, path)
            Log.d("Profile", "Saved image path is: " + path)
        }
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(requireContext(),
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/susi"
    }
}
