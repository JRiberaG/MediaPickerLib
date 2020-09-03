# MediaPickerLib
### Kotlin based media picker library, to pick multiple images and/or vidoes from built-in gallery. Easy to implement and use :) 
<p align="center">
<img src="https://github.com/WrathChaos/MediaPickerLib/blob/master/MediaPickerLibAsset.jpg">
</p>

<p align="center">
<a href="https://github.com/WrathChaos/MediaPickerLib">
<img src="https://kotlin.link/awesome-kotlin.svg"
alt="awesome_kotlin">
</a>
<a href="https://opensource.org/licenses/MIT">
<img src="https://img.shields.io/badge/License-MIT-yellow.svg"
alt="licence">
</a>
<a href="https://jitpack.io/#JRiberaG/MediaPickerLib">
<img src="https://jitpack.io/v/JRiberaG/MediaPickerLib.svg"
alt="Version">
<a href="https://android-arsenal.com/api?level=19">
<img src="https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)"
alt="API">
               
</a>
<a href="https://github.com/WrathChaos/MediaPickerLib">
<img src="https://img.shields.io/badge/Maintained%3F-yes-green.svg"
alt="Maintenance">
</a>
</p>

### Improvements - 03/09/20
- Added the functionality to save and discard changes:
  * Save changes: tap the `Floating Action Button`
  * Discard changes: tab back button on `Appbar` or press back
- Added the name of the folder on the `OpenGallery` and moved the media file counter to the end
- Layouts replaced for `ConstraintLayout`
- Added a `Dialog` to choose the mode in the Demo app

## Setup
Add this on your module:app build.gradle
```
allprojects {
  repositories {
  	...
  	maven { url 'https://jitpack.io' }
  }
}
```
## Gradle

```kotlin
implementation 'com.github.JRiberaG:MediaPickerLib:0.2.1'
```

## Usage


##### This is the request code to handle and get the picked images/videos
```kotlin
private val OPEN_MEDIA_PICKER = 1  // Request code
```


##### You need to import Gallery from MediaPickerLib and send mandatory intents to work
```kotlin
val intent = Intent(this, Gallery::class.java)
// Set the title for toolbar
intent.putExtra("title", "Select media")
// Mode 0 for both images and videos selection, 1 for images only and 2 for videos!
intent.putExtra("mode", 0)
intent.putExtra("maxSelection", 3) // Optional
startActivityForResult(intent, OPEN_MEDIA_PICKER)
```


##### Receive what you picked here: This is an example from sample project, you can handle whatever you want with the path :)

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    // Check which request we're responding to
    if (requestCode == OPEN_MEDIA_PICKER) {
        // Make sure the request was successful
        if (resultCode == Activity.RESULT_OK && data != null) {
            val selectionResult = data.getStringArrayListExtra("result")
            selectionResult.forEach {
                try {
                    Log.d("MyApp", "Image Path : " + it)
                    val uriFromPath = Uri.fromFile(File(it))
                    Log.d("MyApp", "Image URI : " + uriFromPath)
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
```


## Full Working Example 
If you need more spesific example, please look at the [Example](https://github.com/WrathChaos/MediaPickerLib/tree/master/app)

```kotlin
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
```

## Customization

#### You can customize the color of the library (More customization features is coming soon)

Title and back's button color
```xml
 <color name="titleTextColor">#ffffff</color>  
```
Unselected image and video's tab title
```xml
 <color name="titleTabColor">#afafaf</color>   
```
Selected image and video's tab title
```xml
<color name="titleSelectedTabColor">#ffffff</color>
``` 
Gallery's fab button color
```xml
<color name="fabColor">#931931</color>
``` 
## Fork
This is original a fork from [multiple-media-picker](https://github.com/erikagtierrez/multiple-media-picker) | Currently not working 
* Re-written on Kotlin
* Added new features 
* Tablet support
* Fixed bugs

#### Thanks for inspiration [Erikagtierrez](https://github.com/erikagtierrez) :)

## Author

FreakyCoder, kurayogun@gmail.com

## License

MediaPickerLib is available under the MIT license. See the LICENSE file for more info.
