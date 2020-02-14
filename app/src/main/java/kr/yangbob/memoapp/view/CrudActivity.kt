package kr.yangbob.memoapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crud.*
import kr.yangbob.memoapp.R

class CrudActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)
        setSupportActionBar(toolbar)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            crudMotionLayout.loadLayoutDescription(R.xml.scene_crud_landscape)
            imageRecycler.background = resources.getDrawable(R.drawable.border_left, null)
        } else {
            imageRecycler.background = resources.getDrawable(R.drawable.border_top, null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_and_edit, menu)
        return true
    }
}
