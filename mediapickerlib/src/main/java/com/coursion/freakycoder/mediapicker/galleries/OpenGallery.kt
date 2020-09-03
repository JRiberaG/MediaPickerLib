package com.coursion.freakycoder.mediapicker.galleries

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.coursion.freakycoder.mediapicker.adapters.MediaAdapter
import com.coursion.freakycoder.mediapicker.fragments.ImageFragment
import com.coursion.freakycoder.mediapicker.fragments.VideoFragment
import com.coursion.freakycoder.mediapicker.helper.Util
import com.coursion.mediapickerlib.R
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_open_gallery.*
import kotlinx.android.synthetic.main.activity_open_gallery.btnBack
import kotlinx.android.synthetic.main.content_open_gallery.*
import java.util.ArrayList

class OpenGallery : AppCompatActivity() {

    companion object {
        var selected: MutableList<Boolean> = ArrayList()
        var imagesSelected = ArrayList<String>()
        private var listAux = mutableListOf<String>()
    }

    var parent: String? = null
    private var mAdapter: MediaAdapter? = null
    private val mediaList = ArrayList<String>()

    private lateinit var fab: FloatingActionButton
    private lateinit var tvTitle: TextView
    private lateinit var tvCount: TextView
    private lateinit var recyclerView: RecyclerView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_open_gallery)

        fab = findViewById(R.id.fab)
        tvTitle = findViewById(R.id.title)
        tvCount = findViewById(R.id.count)
        recyclerView = findViewById(R.id.recyclerView)

        Util.setButtonTint(fab, ContextCompat.getColorStateList(applicationContext, R.color.fabColor)!!)
        fab.setOnClickListener {
            // Saves changes
            imagesSelected = listAux as ArrayList<String>
            finish()
        }

        btnBack.setOnClickListener { onBackPressed() }

        parent = intent.extras!!.getString("FROM")
        tvTitle.text = intent.extras!!.getString("BUCKETNAME")
        if (imagesSelected.size > 0) {
            tvCount.text = imagesSelected.size.toString()
        }

        mediaList.clear()
        selected.clear()
        if (parent == "Images") {
            mediaList.addAll(ImageFragment.imagesList)
            selected.addAll(ImageFragment.selected)
        } else {
            mediaList.addAll(VideoFragment.videosList)
            selected.addAll(VideoFragment.selected)
        }

        listAux = imagesSelected.toMutableList()

        populateRecyclerView()
    }

    private fun populateRecyclerView() {
        for (i in selected.indices) {
            selected[i] = imagesSelected.contains(mediaList[i])
        }
        mAdapter = MediaAdapter(mediaList, selected, applicationContext)
        val mLayoutManager = GridLayoutManager(applicationContext, 5)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator?.changeDuration = 0
        recyclerView.adapter = mAdapter
        recyclerView.addOnItemTouchListener(RecyclerTouchListener(this, recyclerView, object : ClickListener {
            override fun onClick(view: View, position: Int) {
//                if (!selected[position] && imagesSelected.size < Gallery.maxSelection) {
                if (!selected[position] && listAux.size < Gallery.maxSelection) {
//                    imagesSelected.add(mediaList[position])
                    listAux.add(mediaList[position])

                    selected[position] = !selected[position]
                    mAdapter!!.notifyItemChanged(position)
                } else if (selected[position]) {
                    if (listAux.indexOf(mediaList[position]) != -1) {
//                        imagesSelected.removeAt(imagesSelected.indexOf(mediaList[position]))
                        listAux.removeAt(listAux.indexOf(mediaList[position]))

                        selected[position] = !selected[position]
                        mAdapter!!.notifyItemChanged(position)
                    }
                }

                tvCount.text = if (listAux.size != 0) {
                    listAux.size.toString()
                } else {
                    ""
                }
            }

            override fun onLongClick(view: View?, position: Int) {
            }
        }))
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }

    class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {
        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

}

