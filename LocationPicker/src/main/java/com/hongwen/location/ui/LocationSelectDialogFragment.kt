package com.hongwen.location.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hongwen.location.R
import com.hongwen.location.adapter.LocationSelectAdapter
import com.hongwen.location.callback.OnPickerListener
import com.hongwen.location.databinding.FragmentLocationSelectBinding
import com.hongwen.location.db.DBManager
import com.hongwen.location.decoration.DividerItemDecoration
import com.hongwen.location.decoration.SectionItemDecoration
import com.hongwen.location.model.HotLocation
import com.hongwen.location.model.IModel
import com.hongwen.location.model.LocateState
import com.hongwen.location.model.LocatedLocation
import com.hongwen.location.utils.ScreenUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by chenlu at 2023/7/16 16:47
 */
class LocationSelectDialogFragment : DialogFragment(), OnPickerListener.OnItemClickListener<IModel>,
    OnPickerListener.OnLocateListener {
    private lateinit var binding: FragmentLocationSelectBinding
    private lateinit var adapter: LocationSelectAdapter
    private val allItems: MutableList<IModel> = mutableListOf()

    private var hintText: String? = null
    private var title: String? = null
    private var autoLocate: Boolean = false
    private var onShowListener: OnPickerListener.OnShowListener? = null
    private var onDismissListener: OnPickerListener.OnDismissListener? = null
    private var onCancelListener: OnPickerListener.OnCancelListener? = null
    private var onLocateListener: OnPickerListener.OnLocateListener? = null
    private var onItemClickListener: OnPickerListener.OnItemClickListener<IModel>? = null
    private lateinit var iModelLoader: OnPickerListener.IModelLoader<IModel>


    fun setTitle(title: String?) {
        this.title = title
    }
    fun setSearchHintText(hint: String?) {
        this.hintText = hint
    }


    fun setAutoLocate(autoLocate: Boolean) {
        this.autoLocate = autoLocate
    }

    fun setOnShowListener(onShowListener: OnPickerListener.OnShowListener?) {
        this.onShowListener = onShowListener
    }

    fun setOnCancelListener(onCancelListener: OnPickerListener.OnCancelListener?) {
        this.onCancelListener = onCancelListener
    }

    fun setOnDismissListener(onDismissListener: OnPickerListener.OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    fun setOnLocateListener(onLocateListener: OnPickerListener.OnLocateListener?) {
        this.onLocateListener = onLocateListener
    }

    fun setOnItemClickListener(onItemClickListener: OnPickerListener.OnItemClickListener<IModel>?) {
        this.onItemClickListener = onItemClickListener
    }


    fun setIModelLoader(iModelLoader: OnPickerListener.IModelLoader<IModel>) {
        this.iModelLoader = iModelLoader
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)// 隐藏标题栏（如果有）
        binding = FragmentLocationSelectBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRecyclerView()
        initListener()

        binding.searchLl.setHintText(hintText ?: "")
        binding.cToolBar.setCenterText(title?:"选择城市")

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
        setStyle(STYLE_NORMAL, R.style.CityPickerStyle)

    }

    override fun onStart() {
        super.onStart()

        //如果是Dialog模式
        dialog?.let { setWindow(it) }
        dialog?.setOnShowListener {
            onShowListener?.onShow(it)
        }
        dialog?.window?.let {
            view?.fitsSystemWindows = true
            setImmersiveSystemUiVisibility(it.decorView)
        }
    }

    private fun setImmersiveSystemUiVisibility(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            Log.d(
                "Carlos",
                "systemBarsInsets.bottom:" + systemBarsInsets.bottom + "  rect:" + systemBarsInsets
            )
            WindowInsetsCompat.Builder(insets).setInsets(
                WindowInsetsCompat.Type.systemBars(),
                // android:fitsSystemWindows="true" 可自动为根布局添加导航栏高度的paddingBottom,避免被导航栏覆盖
                Insets.of(0, 0, 0, systemBarsInsets.bottom)
            ).build()
        }
    }

    override fun onPause() {
        super.onPause()

        val windowInsetsController = ViewCompat.getWindowInsetsController(binding.root)
        windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelListener?.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)

    }


    private fun resetData() {
        adapter.updateData(allItems)

        setEmptyViewVisibility(allItems.isEmpty())
    }

    private fun searchData(keyWord: String?) {
        lifecycleScope.launch {
            val items: MutableList<IModel> = withContext(Dispatchers.IO) {
                val dbManager = DBManager(requireContext())
                dbManager.searchLocation(keyWord).toMutableList()
            }
            adapter.updateData(items)
            setEmptyViewVisibility(items.isEmpty())

        }
    }


    private fun iniRecyclerView() {
        lifecycleScope.launch {

            val items: List<IModel> = withContext(Dispatchers.IO) {
                iModelLoader.getAllItems()
            }
            val hotItems = withContext(Dispatchers.IO) {
                iModelLoader.getHotItems()
            }


            val locateState = withContext(Dispatchers.IO) {
                allItems.clear()

                val locateState: LocateState
                val lastLocation = findLastLocated()

                if (lastLocation == null || lastLocation.name.isBlank()) {
                    if (autoLocate) {
                        locateState = LocateState.LOCATING
                        allItems.add(0, LocatedLocation("正在定位"))

                        onLocateListener?.onLocate(adapter)

                    } else {
                        locateState = LocateState.INIT
                        allItems.add(0, LocatedLocation("点击定位"))
                    }
                } else {
                    locateState = LocateState.SUCCESS
                    allItems.add(0, lastLocation)
                }


                allItems.add(1, HotLocation("热门城市"))
                allItems.addAll(items)

                locateState
            }


            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = LocationSelectAdapter(
                allItems = allItems, hotItems = hotItems, locateState = locateState
            ).also {
                adapter = it
                adapter.setLayoutManager(binding.recyclerView.layoutManager as LinearLayoutManager)
            }
            binding.recyclerView.addItemDecoration(
                SectionItemDecoration(
                    requireContext(), allItems
                )
            )
            binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))

            //设置定位回调
            adapter.setOnLocateListener(this@LocationSelectDialogFragment)
            //设置点击事件
            adapter.setOnItemClickListener(this@LocationSelectDialogFragment)
        }
    }

    private fun findLastLocated(): LocatedLocation? {
        val location = preferences.getString("last_location", null)
        return location?.let { LocatedLocation(it) }
    }

    private fun initListener() {
        binding.cToolBar.tv_left_back.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.searchLl.editText.addTextChangedListener(afterTextChanged = {
            val keyWord = it?.toString()?.trim()
            if (keyWord.isNullOrEmpty()) {
                resetData()
            } else {
                searchData(keyWord)
            }

        })

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


    private fun setEmptyViewVisibility(show: Boolean) {
        binding.emptyView.root.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onItemClick(item: IModel) {
        onItemClickListener?.onItemClick(item)

        dismissAllowingStateLoss()

    }

    override fun onLocate(callback: OnPickerListener.OnLocationStateChangeListener) {
        //代理
        val listener = object : OnPickerListener.OnLocationStateChangeListener {
            override fun onSuccess(locate: LocatedLocation) {
                callback.onSuccess(locate)

                saveLastLocation(locate)
            }

            override fun onFailed(msg: String) {
                callback.onFailed(msg)
            }

        }
        onLocateListener?.onLocate(listener)
    }


    private val preferences by lazy {
        requireContext().getSharedPreferences(
            "location_picker_preferences", Context.MODE_PRIVATE
        )
    }

    private fun saveLastLocation(locate: LocatedLocation) {
        preferences.edit().apply {
            putString("last_location", locate.name)
        }.apply()
    }
}