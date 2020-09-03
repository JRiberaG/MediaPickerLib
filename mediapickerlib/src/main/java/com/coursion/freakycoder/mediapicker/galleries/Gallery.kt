package com.coursion.freakycoder.mediapicker.galleries

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coursion.freakycoder.mediapicker.fragments.ImageFragment
import com.coursion.freakycoder.mediapicker.fragments.VideoFragment
import com.coursion.freakycoder.mediapicker.helper.Util
import com.coursion.mediapickerlib.R
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.ArrayList

class Gallery : AppCompatActivity() {
    
    companion object {
        var maxSelection: Int = 0
        var mode: Int = 0
        var tabBarHidden = false
    }

    private lateinit var fab: FloatingActionButton
    private lateinit var tvTitle: TextView
    private lateinit var tvCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_gallery)

        fab = findViewById(R.id.fab)
        tvTitle = findViewById(R.id.title)
        tvCount = findViewById(R.id.count)

        // Set the toolbar
//        setSupportActionBar(toolbar)
//        toolbar.setNavigationIcon(R.drawable.arrow_back)
//        toolbar.setNavigationOnClickListener { onBackPressed() }
        btnBack.setOnClickListener { onBackPressed() }
        fab.setOnClickListener { returnResult() }

        Util.setButtonTint(fab, ContextCompat.getColorStateList(applicationContext, R.color.fabColor)!!)

        tvTitle.text = intent.extras!!.getString("title")
        maxSelection = intent.extras!!.getInt("maxSelection")
        if (maxSelection == 0) maxSelection = Integer.MAX_VALUE
        mode = intent.extras!!.getInt("mode")
        tabBarHidden = intent.extras!!.getBoolean("tabBarHidden")

        setTextViewCount()
        // Set the ViewPager and TabLayout
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)

        OpenGallery.selected.clear()
        OpenGallery.imagesSelected.clear()

    }

    override fun onResume() {
        super.onResume()
        setTextViewCount()
    }

    private fun setTextViewCount() {
        tvCount.text = if (OpenGallery.imagesSelected.size > 0) {
            OpenGallery.imagesSelected.size.toString()
        } else {
            ""
        }
    }

    //This method set up the tab view for images and videos
    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        if (mode == 0 || mode == 1) {
            adapter.addFragment(ImageFragment(), "Images")
        }
        if (mode == 0 || mode == 2)
            adapter.addFragment(VideoFragment(), "Videos")
        viewPager!!.adapter = adapter

        if (tabBarHidden) {
            tabLayout.visibility = View.GONE
        } else {
            tabLayout.visibility = View.VISIBLE
        }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    private fun returnResult() {
        val returnIntent = Intent()
        returnIntent.putStringArrayListExtra("result", OpenGallery.imagesSelected)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
