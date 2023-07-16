package com.hongwen.location.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hongwen.location.adapter.LocationSelectAdapter
import com.hongwen.location.databinding.ActivityLocationSelectBinding
import com.hongwen.location.databinding.FragmentLocationSelectBinding
import com.hongwen.location.db.DBManager
import com.hongwen.location.decoration.DividerItemDecoration
import com.hongwen.location.decoration.SectionItemDecoration
import com.hongwen.location.model.HotLocation
import com.hongwen.location.model.LocatedLocation
import com.hongwen.location.model.Location
import com.hongwen.location.utils.ScreenUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by chenlu at 2023/7/16 16:47
 */
class LocationSelectDialogFragment: DialogFragment() {
    private lateinit var bind:FragmentLocationSelectBinding
    private lateinit var adapter: LocationSelectAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentLocationSelectBinding.inflate(inflater,container,false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Carlos-DialogFragment","onViewCreated~~~~~")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor()
            setSystemUiVisibility()

        }

        iniRecyclerView()
        initListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("Carlos-DialogFragment","onAttach~~~~~")

    }

    override fun onStart() {
        super.onStart()
        Log.d("Carlos-DialogFragment","onStart~~~~~")

    }

    private fun resetData() {
        adapter.updateData(allItems)

        setEmptyViewVisibility(allItems.isEmpty())
    }

    private fun searchData(keyWord: String?) {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                val dbManager = DBManager(requireContext())
                dbManager.searchLocation(keyWord)
            }
            adapter.updateData(items)
            setEmptyViewVisibility(items.isEmpty())

        }
    }



    private lateinit var allItems: MutableList<Location>
    private fun iniRecyclerView() {
        lifecycleScope.launch {

            val items = withContext(Dispatchers.IO) {
                val dbManager = DBManager(requireContext())
                dbManager.allCities
            }
            val hotItems = withContext(Dispatchers.IO) {
                getHotLocations()
            }
            items[0] = LocatedLocation("正在定位", "未知", "未知")
            items[1] = HotLocation("热门城市", "未知", "未知")

            allItems = items
            bind.recyclerView.setHasFixedSize(true)
            bind.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            bind.recyclerView.adapter = LocationSelectAdapter(allItems, hotItems).also {
                adapter = it
                adapter.setLayoutManager(bind.recyclerView.layoutManager as LinearLayoutManager)
            }
            bind.recyclerView.addItemDecoration(
                SectionItemDecoration(
                    requireContext(),
                    allItems
                )
            )
            bind.recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        }
    }

    private fun initListener() {
        bind.searchLl.editText.addTextChangedListener(
            afterTextChanged = {
                val keyWord = it?.toString()?.trim()
                if (keyWord.isNullOrEmpty()) {
                    resetData()
                } else {
                    searchData(keyWord)
                }

            }
        )

        bind.cpSideIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(requireContext()))
        bind.cpSideIndexBar.setOverlayTextView(bind.cpOverlay)
        bind.cpSideIndexBar.setOnIndexChangedListener { index, position ->
            //滚动RecyclerView到索引位置
            adapter.scrollToSection(index)
        }
    }

    private fun getHotLocations(): List<Location> {
        //初始化热门城市
        val mHotCities = ArrayList<HotLocation>()
        mHotCities.add(HotLocation("北京", "北京", ""))
        mHotCities.add(HotLocation("上海", "上海", ""))
        mHotCities.add(HotLocation("广州", "广东", ""))
        mHotCities.add(HotLocation("深圳", "广东", ""))
        mHotCities.add(HotLocation("成都", "四川", ""))
        mHotCities.add(HotLocation("天津", "天津", ""))
        mHotCities.add(HotLocation("杭州", "浙江", ""))
        mHotCities.add(HotLocation("南京", "江苏", ""))
        mHotCities.add(HotLocation("武汉", "湖北", ""))

        return mHotCities
    }


    private fun setEmptyViewVisibility(show: Boolean) {
        bind.emptyView.root.visibility = if (show) View.VISIBLE else View.GONE
    }

    // 设置状态栏颜色为透明
    private fun setStatusBarColor() {
        dialog?.window?.statusBarColor = Color.TRANSPARENT
    }

    // 设置系统UI可见性以实现沉浸式状态栏效果
    private fun setSystemUiVisibility() {
        val view = dialog?.window?.decorView ?: return
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            WindowInsetsCompat.Builder(insets)
                .setInsets(
                    WindowInsetsCompat.Type.systemBars(),
                    Insets.of(0, 0, 0, systemBarsInsets.bottom)
                )
                .build()
        }
    }
}