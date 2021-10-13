package com.example.amplifydemo.ui.fragment.auth

import android.Manifest
import android.os.Bundle
import android.util.Log
import com.amplifyframework.core.Amplify
import com.example.amplifydemo.R
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentDevicesBinding
import kotlinx.android.synthetic.main.fragment_devices.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class DevicesFragment:BaseFragment<AuthViewModel,FragmentDevicesBinding>(), EasyPermissions.PermissionCallbacks  {
    override fun layoutId(): Int {
       return R.layout.fragment_devices
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel =mViewModel


        tv_remember_device.setOnClickListener {
            showLoading("Loading...")

            Amplify.Auth.rememberDevice(
                {
                    dismissLoading()
                    mViewModel.message.set("绑定设备成功：" + it)
                    Log.i("AuthQuickStart", "Remember device succeeded")
                },
                {
                    dismissLoading()
                    mViewModel.message.set("绑定设备失败：" + it)
                    Log.e("AuthQuickStart", "Remember device failed with error", it)
                }
            )
        }

        tv_forget_device.setOnClickListener {
            showLoading("Loading...")
            Amplify.Auth.forgetDevice(
                {
                    dismissLoading()
                    Log.i("AuthQuickStart", "Forget device succeeded")
                    mViewModel.message.set("移除设备成功：" + it)
                },
                {
                    dismissLoading()
                    Log.e("AuthQuickStart", "Forget device failed with error", it)
                    mViewModel.message.set("移除设备失败：" + it)
                }
            )
        }
        tv_fetch_devices.setOnClickListener {
            showLoading("Loading...")
            Amplify.Auth.fetchDevices(
                { devices ->
                    dismissLoading()
                   val stringBuilder=StringBuilder()
                    stringBuilder.append("获取设备列表:")
                    devices.forEach {
                        stringBuilder.append("\n")
                        stringBuilder.append("deviceName:"+it.deviceName)
                        stringBuilder.append("deviceId:"+it.deviceId)
                        Log.i("AuthQuickStart", "Device: " + it)
                    }

                    mViewModel.message.set(stringBuilder.toString())
                },
                {
                    dismissLoading()
                    mViewModel.message.set("获取设备失败：" + it)
                    Log.e("AuthQuickStart", "Fetch devices failed with error", it)
                }
            )
        }

        activity?.let {
            if (!EasyPermissions.hasPermissions(it, Manifest.permission.READ_PHONE_STATE)) {
                EasyPermissions.requestPermissions(
                    this,
                    "请允许获取手机信息权限",
                    RC_READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_STATE
                )
            }
        }




    }

    override fun createObserver() {
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        when(requestCode){
            RC_READ_PHONE_STATE->{
                if (EasyPermissions.somePermissionDenied(this, Manifest.permission.READ_PHONE_STATE)){
                    AppSettingsDialog.Builder(this)
                        .setNegativeButton("取消")
                        .setPositiveButton("确定")
                        .setTitle("权限申请")
                        .setRationale("请允许手机信息权限,以保证推送功能的正常使用")
                        .build().show()
                }
            }

        }
    }
}