package com.example.amplifydemo.ui.fragment.assembly

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amplifydemo.R
import com.example.amplifydemo.app.App
import com.example.amplifydemo.app.ext.init
import com.example.amplifydemo.app.util.FileUtil
import com.example.amplifydemo.app.util.ModelUtil
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentRecyclerviewBinding
import com.example.amplifydemo.ui.adapter.assembly.AssemblyAdapter
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class AssemblyFragment : BaseFragment<AssemblyViewModel, FragmentRecyclerviewBinding>(),
    EasyPermissions.PermissionCallbacks {

    var arrayName: String? = null
    var dataId: String? = null

    val perms = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private val assemblyAdapter: AssemblyAdapter by lazy {
        AssemblyAdapter()
    }


    override fun layoutId(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun initView(savedInstanceState: Bundle?) {
        recyclerView.init(LinearLayoutManager(context), assemblyAdapter).let {
            it.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        mViewModel.assemblyAdapter = assemblyAdapter
        assemblyAdapter.run {
            setOnItemClickListener { adapter, view, position ->

                if (assemblyAdapter.getItem(position).content?.contains("updateUserAttributes") == true) {
                    val editText: EditText =
                        assemblyAdapter.getViewByPosition(1, R.id.ed_item) as EditText
                    if (!TextUtils.isEmpty(editText.text)) {
                        mViewModel.value.set(editText.text.toString())
                    }
                }

                if (view is Button) ModelUtil.ItemClick(
                    this@AssemblyFragment,
                    mViewModel,
                    assemblyAdapter.getItem(position).content
                )


            }
        }

        mViewModel.message.addOnPropertyChangedCallback(messageCallback())
    }

    override fun createObserver() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.message.removeOnPropertyChangedCallback(messageCallback())
    }

    inner class messageCallback : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            assemblyAdapter.notifyAction(dataId, mViewModel.message.get())
        }

    }

    override fun initData() {
        dataId = arguments?.getString("dataId", null)
        arrayName = arguments?.getString("arrayName", null)

//        val data = arguments?.getStringArray("data")?.toCollection(ArrayList())
        if (!TextUtils.isEmpty(dataId)) {
            assemblyAdapter.setNewInstance(ModelUtil.getData(dataId!!))

        }

        when (dataId) {
            "获取当前用户属性" -> {
                mViewModel.fetchUserAttributes()
            }
        }

        when (arrayName) {
            "Storage" -> {
                context?.let {

                    var hasPermission = EasyPermissions.hasPermissions(it, *perms)
                    Log.e("EasyPermissions", "hasPermission = $hasPermission")
                    if (hasPermission) {

                    } else {
                        EasyPermissions.requestPermissions(
                            this,
                            "请允许访问手机读写存储权限",
                            RC_EXTERNAL_STORAGE,
                            *perms
                        )
                    }
                }

            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("EasyPermissions", "requestCode = $requestCode")
        Log.e("EasyPermissions", "permissions = $permissions")
        Log.e("EasyPermissions", "grantResults = $grantResults")

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.e("EasyPermissions", "requestCode = $requestCode")
        Log.e("EasyPermissions", "perms = $perms")
        Log.e("EasyPermissions VERSION", Build.VERSION.SDK_INT.toString())
        when (requestCode) {
            RC_EXTERNAL_STORAGE -> {
                if (EasyPermissions.somePermissionDenied(this, perms.toString())) {
                    AppSettingsDialog.Builder(this)
                        .setNegativeButton("取消")
                        .setPositiveButton("确定")
                        .setTitle("权限申请")
                        .setRationale("请允许访问手机读写存储权限")
                        .build().show()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("onActivityResult", "requestCode = $requestCode")


        if (data != null && data.data != null) {
            context?.let {
                val file: File =
                    File(FileUtil.getFileAbsolutePath(App.instance.applicationContext, data.data!!))

                dataId?.let {
                    when (it) {
                        "uploadFile" -> {
                            if (file != null) {
                                mViewModel.uploadFile(file)
                            }
                        }
                        "uploadInputStream" -> {
                            if (file != null) {
                                mViewModel.uploadInputStream(file)
                            }
                        }
                    }
                }


            }


        }
    }


}


