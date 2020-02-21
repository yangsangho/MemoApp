package kr.yangbob.memoapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_big_image.*
import kr.yangbob.memoapp.R

class BigImageActivity : AppCompatActivity() {
    private val toggleUiOption = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
    private var isDetailMode = false
    private lateinit var imageList: MutableList<String>
    private val deleteList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_image)

        val idx = intent.getIntExtra("idx", 0)
        imageList = (intent.getStringArrayExtra("imageList") ?: arrayOf()).toMutableList()
        isDetailMode = intent.getBooleanExtra("isDetailMode", false)

        val pagerAdapter = BigImagePagerAdapter(imageList.toList(), this)
        viewpager.adapter = pagerAdapter
        viewpager.setCurrentItem(idx, false)

        if (isDetailMode) bottomBar.visibility = View.GONE
        else {
            deleteBtn.setOnClickListener {
                deleteList.add(imageList.removeAt(viewpager.currentItem))
                if(imageList.isEmpty()) onBackPressed()
                else pagerAdapter.updateList(imageList.toList())
            }
        }

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().apply {
            if(imageList.isNotEmpty()) putExtra("idx", viewpager.currentItem)
            if(deleteList.isNotEmpty()) putExtra("deleteList", deleteList.toTypedArray())
        })
        super.onBackPressed()
    }


    fun toggleSystemUi() {
        val newUiOption: Int
        val uiOption = window.decorView.systemUiVisibility
        val isImmersiveModeEnabled = (uiOption and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0
        if (isImmersiveModeEnabled) {
            newUiOption = uiOption xor toggleUiOption
            if (!isDetailMode) bottomBar.visibility = View.VISIBLE
        } else {
            newUiOption = uiOption or toggleUiOption
            if (!isDetailMode) bottomBar.visibility = View.GONE
        }
        window.decorView.systemUiVisibility = newUiOption
    }
}
