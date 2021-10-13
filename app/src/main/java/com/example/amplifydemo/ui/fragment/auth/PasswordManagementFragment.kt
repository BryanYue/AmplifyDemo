package com.example.amplifydemo.ui.fragment.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.amplifyframework.core.Amplify
import com.blankj.utilcode.util.ToastUtils
import com.example.amplifydemo.BuildConfig
import com.example.amplifydemo.R
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentPasswordManagementBinding
import kotlinx.android.synthetic.main.fragment_password_management.*

class PasswordManagementFragment :BaseFragment<AuthViewModel,FragmentPasswordManagementBinding>(){
    override fun layoutId(): Int {
        return  R.layout.fragment_password_management
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel =mViewModel
        tv_updatePassword.setOnClickListener {
            check()
           showLoading("Loading...")

            Amplify.Auth.updatePassword(ed_existingPassword.text.toString(), ed_newPassword.text.toString(),
                {
                    dismissLoading()
                    mViewModel.message.set("更新成功")
                    Log.i("AuthQuickstart", "Updated password successfully")
                },
                {
                    dismissLoading()
                    mViewModel.message.set("更新失败："+it)
                    Log.i("AuthQuickstart", "更新失败："+it)
                }
            )
        }

        tv_sendCode.setOnClickListener {
            if (TextUtils.isEmpty(ed_username.text)) {
                ToastUtils.showShort("请输入用户名")
                return@setOnClickListener
            }
            showLoading("Loading...")
            Amplify.Auth.resetPassword(ed_username.text.toString(),
                {
                    Log.i("AuthQuickstart", "Password reset OK: $it")
                    dismissLoading()
                    mViewModel.message.set("发送成功")
                },
                {
                    Log.e("AuthQuickstart", "Password reset failed", it)
                    dismissLoading()
                    mViewModel.message.set("发送失败: $it")
                }
            )
        }


        tv_resetPassword.setOnClickListener {
            if (TextUtils.isEmpty(ed_newPassword.text)) {
                ToastUtils.showShort("请输入新密码")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(ed_code.text)) {
                ToastUtils.showShort("请输入验证码")
                return@setOnClickListener
            }
            showLoading("Loading...")
            Amplify.Auth.confirmResetPassword(ed_newPassword.text.toString(), ed_code.text.toString(),
                {
                    Log.i("AuthQuickstart", "New password confirmed")
                    dismissLoading()
                    mViewModel.message.set("重置成功")
                },
                {
                    Log.e("AuthQuickstart", "Failed to confirm password reset", it)
                    dismissLoading()
                    mViewModel.message.set("重置失败: $it")
                }
            )
        }
        if (BuildConfig.DEBUG) {
            ed_username.setText("Bryan")
        }
    }

    override fun createObserver() {
    }





    fun check() {
        if (TextUtils.isEmpty(ed_username.text)) {
            ToastUtils.showShort("请输入用户名")
            return
        }
        if (TextUtils.isEmpty(ed_existingPassword.text)) {
            ToastUtils.showShort("请输入旧密码")
            return
        }
        if (TextUtils.isEmpty(ed_newPassword.text)) {
            ToastUtils.showShort("请输入新密码")
            return
        }


    }
}