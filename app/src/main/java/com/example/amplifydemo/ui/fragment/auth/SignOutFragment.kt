package com.example.amplifydemo.ui.fragment.auth

import android.os.Bundle
import android.util.Log
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.example.amplifydemo.R
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentSignoutBinding
import kotlinx.android.synthetic.main.fragment_signout.*

class SignOutFragment :BaseFragment<AuthViewModel,FragmentSignoutBinding>(){
    override fun layoutId(): Int {
       return  R.layout.fragment_signout
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel =mViewModel

        tv_signOut.setOnClickListener {
            showLoading("Loading...")
            Amplify.Auth.signOut(
                {
                    Log.i("AuthQuickstart", "Signed out successfully")
                    dismissLoading()
                    mViewModel.message.set("退出成功")
                },
                {
                    Log.e("AuthQuickstart", "Sign out failed", it)
                    dismissLoading()
                    mViewModel.message.set("退出失败"+ it)
                }
            )
        }


        tv_signOutAllDevices.setOnClickListener {
            showLoading("Loading...")

            val options = AuthSignOutOptions.builder()
                .globalSignOut(true)
                .build()
            Amplify.Auth.signOut(options,
                {
                    Log.i("AuthQuickstart", "Signed out globally")
                    dismissLoading()
                    mViewModel.message.set("所有设备退出成功")
                },
                {
                    Log.e("AuthQuickstart", "Sign out failed", it)
                    dismissLoading()
                    mViewModel.message.set("所有设备退出失败"+ it)
                }
            )
        }
    }

    override fun createObserver() {
    }
}