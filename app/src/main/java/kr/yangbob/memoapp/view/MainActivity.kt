package kr.yangbob.memoapp.view

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.ActivityMainBinding
import kr.yangbob.memoapp.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.cntNotes = 0

        setSupportActionBar(toolbar)

        val spanCount: Int
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3
        } else {
            spanCount = 2
//            setScrollEvent()
        }
        val memoAdapter = MemoListAdapter()

        memoRecycler.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        memoRecycler.adapter = memoAdapter

        addBtn.setOnClickListener {
            startActivity(Intent(this, CrudActivity::class.java))
        }

        model.getMemoList().observe(this, Observer {
            memoAdapter.updateList(it)
            binding.cntNotes = it.size
            if(noItemMsgLayout.visibility == View.GONE && it.isEmpty()){
                noItemMsgLayout.visibility = View.VISIBLE
            } else if(noItemMsgLayout.visibility != View.GONE && it.isNotEmpty() ){
                noItemMsgLayout.visibility = View.GONE
            }
        })
    }

//    private fun setScrollEvent() {
//        val titleTextView = toolbar.javaClass.getDeclaredField("mTitleTextView").let {
//            it.isAccessible = true
//            it.get(toolbar) as TextView
//        }
//
//        val contentLayoutParams: ViewGroup.LayoutParams = contentLayout.layoutParams
//        val appHeight: Int = Point().let {
//            windowManager.defaultDisplay.getSize(it)
//            it.y
//        }
//        var scrollCriteria = 0f
//        var doubleScrollCriteria = 0f
//
//        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            if (scrollCriteria == 0f) {
//                scrollCriteria = appBarLayout.totalScrollRange / 4f
//                doubleScrollCriteria = scrollCriteria * 2
//            }
//
//            contentLayoutParams.height = appHeight - (appBarLayout.height + verticalOffset)
//            contentLayout.layoutParams = contentLayoutParams
//
//            val offsetAbs = verticalOffset.absoluteValue
//
//            var titleAlpha =
//                    if (offsetAbs < scrollCriteria) 0f
//                    else (offsetAbs - scrollCriteria) / doubleScrollCriteria
//            if (titleAlpha > 1f) titleAlpha = 1f
//
//            val startTitleAlpha =
//                    if (offsetAbs > doubleScrollCriteria) 0f
//                    else 1f - offsetAbs / doubleScrollCriteria
//
//            titleTextView.alpha = titleAlpha
//            startTitleLayout.alpha = startTitleAlpha
//        })
//    }
}
