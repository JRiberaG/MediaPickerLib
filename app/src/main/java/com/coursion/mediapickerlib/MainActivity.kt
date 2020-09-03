package com.coursion.mediapickerlib

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.coursion.freakycoder.mediapicker.galleries.Gallery
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val OPEN_MEDIA_PICKER = 1  // Request code
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100 // Request code for read external storage
    }

    private var modeSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.arrow_back)

        setButtonTint(fab, ContextCompat.getColorStateList(applicationContext, R.color.fabColor)!!)
        fab.setOnClickListener {
            if (!permissionIfNeeded()) {
                openDialog()
            }
        }
    }

    private fun openDialog() {
        val items = arrayOf("Both", "Image", "Video")

        AlertDialog.Builder(this@MainActivity)
                .setTitle("Select media option")
                .setSingleChoiceItems(items, modeSelected, null)
                .setPositiveButton("Ok") { d, _ ->
                    modeSelected = (d as AlertDialog).listView.checkedItemPosition
                    d.dismiss()
                    toGallery()
                }
                .setCancelable(false)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private fun toGallery() {
        val intent = Intent(this, Gallery::class.java)
        // Set the title
        intent.putExtra("title", "Select media files")
        intent.putExtra("mode", modeSelected)
        //intent.putExtra("maxSelection", 3) // Optional
        intent.putExtra("tabBarHidden", false) //Optional - default value is false
        startActivityForResult(intent, OPEN_MEDIA_PICKER)
    }

    private fun setButtonTint(button: FloatingActionButton, tint: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.backgroundTintList = tint
        } else {
            ViewCompat.setBackgroundTintList(button, tint)
        }
    }

    private fun permissionIfNeeded(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            openDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK && data != null) {
                val selectionResult = data.getStringArrayListExtra("result")
                selectionResult.forEach {
                    try {
                        Log.d(TAG, "Image Path : $it")
                        val uriFromPath = Uri.fromFile(File(it))
                        Log.d(TAG, "Image URI : $uriFromPath")
                        // Convert URI to Bitmap
                        val bm = BitmapFactory.decodeStream(
                                contentResolver.openInputStream(uriFromPath))
                        image.setImageBitmap(bm)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
