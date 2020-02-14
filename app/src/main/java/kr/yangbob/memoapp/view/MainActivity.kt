package kr.yangbob.memoapp.view

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memoapp.R
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        titleTextView = toolbar.javaClass.getDeclaredField("mTitleTextView").let {
            it.isAccessible = true
            it.get(toolbar) as TextView
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            startTitleLayout.visibility = View.GONE

            val collapsingToolbarLayout: AppBarLayout.LayoutParams =
                collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
            collapsingToolbarLayout.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP

            val appbarLayoutParams = appBar.layoutParams
            appbarLayoutParams.height = AppBarLayout.LayoutParams.WRAP_CONTENT
            appBar.layoutParams = appbarLayoutParams

            noteRecycler.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        } else {
            setScrollEvent()
            noteRecycler.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        noteRecycler.adapter = NoteListAdapter(listOf())

        addBtn.setOnClickListener {
            startActivity(Intent(this, CrudActivity::class.java))
        }
    }

    private fun setScrollEvent() {
        val contentLayoutParams: ViewGroup.LayoutParams = contentLayout.layoutParams
        val appHeight: Int = Point().let {
            windowManager.defaultDisplay.getSize(it)
            it.y
        }
        var scrollCriteria = 0f
        var doubleScrollCriteria = 0f

        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollCriteria == 0f) {
                scrollCriteria = appBarLayout.totalScrollRange / 4f
                doubleScrollCriteria = scrollCriteria * 2
            }

            contentLayoutParams.height = appHeight - (appBarLayout.height + verticalOffset)
            contentLayout.layoutParams = contentLayoutParams

            val offsetAbs = verticalOffset.absoluteValue

            var titleAlpha =
                if (offsetAbs < scrollCriteria) 0f
                else (offsetAbs - scrollCriteria) / doubleScrollCriteria
            if (titleAlpha > 1f) titleAlpha = 1f

            val startTitleAlpha =
                if (offsetAbs > doubleScrollCriteria) 0f
                else 1f - offsetAbs / doubleScrollCriteria

            titleTextView.alpha = titleAlpha
            startTitleLayout.alpha = startTitleAlpha
        })
    }
}
