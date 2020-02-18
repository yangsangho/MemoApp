package kr.yangbob.memoapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_big_image.*
import kr.yangbob.memoapp.R

class BigImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_image)
        val idx = intent.getIntExtra("idx", -1)
        val imageList = intent.getStringArrayExtra("imageList") ?: arrayOf()

        val pagerAdapter = BigImagePagerAdapter(imageList)
        viewpager.adapter = pagerAdapter
        viewpager.setCurrentItem(idx, false)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("idx", viewpager.currentItem)
        })
        super.onBackPressed()
    }
}
