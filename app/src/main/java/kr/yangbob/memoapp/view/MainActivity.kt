package kr.yangbob.memoapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.ActivityMainBinding
import kr.yangbob.memoapp.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.cntMemo = 0
        binding.model = model

        setSupportActionBar(toolbar)

        val memoAdapter = MemoListAdapter()
        model.getMemoList().observe(this, Observer {
            memoAdapter.updateList(it)
            binding.cntMemo = it.size
            model.setIsNoItem(it.isEmpty())
        })
        memoRecycler.adapter = memoAdapter

        addBtn.setOnClickListener {
            startActivity(Intent(this, CrudActivity::class.java))
        }
    }
}
