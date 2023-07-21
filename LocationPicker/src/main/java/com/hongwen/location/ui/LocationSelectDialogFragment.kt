package com.hongwen.location.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hongwen.location.R
import com.hongwen.location.adapter.LocationSelectAdapter
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
class LocationSelectDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentLocationSelectBinding
    private lateinit var adapter: LocationSelectAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)// 隐藏标题栏（如果有）
        binding = FragmentLocationSelectBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Carlos-DialogFragment", "onViewCreated~~~~~")

        iniRecyclerView()
        initListener()


    }

    private fun setWindow(dialog: Dialog) {
        val window = dialog.window
        //显示系统状态栏
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window?.statusBarColor = Color.TRANSPARENT

        // 获取Dialog的Window对象并配置属性
        window?.apply {
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            //设置导航栏颜
            window.navigationBarColor = Color.WHITE
            //window.navigationBarColor = Color.RED

        }

        window?.apply {
            //设置Dialog的宽高充满屏幕
            val lp = window.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.TOP
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            // 设置透明背景
            window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            window.attributes = lp
        }


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("Carlos-DialogFragment", "onAttach~~~~~")

        setStyle(STYLE_NORMAL, R.style.CityPickerStyle)

    }

    override fun onStart() {
        super.onStart()
        Log.d("Carlos-DialogFragment", "onStart~~~~~")


        //如果是Dialog模式
        dialog?.let { setWindow(it) }
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

            items.add(0,LocatedLocation("正在定位", "未知", "未知"))
            items.add(1,HotLocation("热门城市", "未知", "未知"))

            allItems = items
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = LocationSelectAdapter(allItems, hotItems).also {
                adapter = it
                adapter.setLayoutManager(binding.recyclerView.layoutManager as LinearLayoutManager)
            }
            binding.recyclerView.addItemDecoration(
                SectionItemDecoration(
                    requireContext(),
                    allItems
                )
            )
            binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        }
    }

    private fun initListener() {
        binding.searchLl.editText.addTextChangedListener(
            afterTextChanged = {
                val keyWord = it?.toString()?.trim()
                if (keyWord.isNullOrEmpty()) {
                    resetData()
                } else {
                    searchData(keyWord)
                }

            }
        )

        binding.cpSideIndexBar.setNavigationBarHeight(
            ScreenUtil.getNavigationBarHeight(
                requireContext()
            )
        )
        binding.cpSideIndexBar.setOverlayTextView(binding.cpOverlay)
        binding.cpSideIndexBar.setOnIndexChangedListener { index, position ->
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
        binding.emptyView.root.visibility = if (show) View.VISIBLE else View.GONE
    }

}