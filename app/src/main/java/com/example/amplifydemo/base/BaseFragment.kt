package com.example.amplifydemo.base

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.medsci.app.digitalhealthcare_patient.app.ext.dismissLoadingExt
import cn.medsci.app.digitalhealthcare_patient.app.ext.showLoadingExt
import com.example.amplifydemo.app.ext.getVmClazz
import com.example.amplifydemo.app.viewmodel.BaseViewModel

abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> :Fragment(){
    lateinit var mViewModel: VM
    lateinit var mDatabind: DB

    public val RC_READ_PHONE_STATE = 100
    public val RC_EXTERNAL_STORAGE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabind = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        mDatabind.lifecycleOwner = this
        return mDatabind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = createViewModel()
        initView(savedInstanceState)
        registorDefUIChange()
        createObserver()
        initData()
    }




    abstract fun layoutId(): Int

    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun createObserver()

    open fun initData() {}



    /**
     * 注册 UI 事件
     */
    private fun registorDefUIChange() {
        mViewModel.loadingChange.showDialog.observeInFragment(this, Observer {
            showLoading(it)
        })
        mViewModel.loadingChange.dismissDialog.observeInFragment(this, Observer {
            dismissLoading()
        })
    }

    /**
     * 打开等待框
     */
     fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
     fun dismissLoading() {
        dismissLoadingExt()
    }





}