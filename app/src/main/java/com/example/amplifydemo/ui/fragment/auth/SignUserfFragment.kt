package com.example.amplifydemo.ui.fragment.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.amplifyframework.auth.options.AuthSignInOptions
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.blankj.utilcode.util.ToastUtils
import com.example.amplifydemo.BuildConfig
import com.example.amplifydemo.R
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentSignuserBinding
import kotlinx.android.synthetic.main.fragment_registeruser.*
import kotlinx.android.synthetic.main.fragment_signuser.*
import kotlinx.android.synthetic.main.fragment_signuser.ed_password
import kotlinx.android.synthetic.main.fragment_signuser.ed_username

class SignUserfFragment : BaseFragment<AuthViewModel, FragmentSignuserBinding>(),
    View.OnClickListener {
    override fun layoutId(): Int {
        return R.layout.fragment_signuser
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel =mViewModel


        tv_signIn.setOnClickListener(this)

        if (BuildConfig.DEBUG) {
            ed_username.setText("Bryan")
            ed_password.setText("12345678")
        }
    }

    override fun createObserver() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_signIn -> {
                check()

                showLoading("Loading...")

                val options = AuthSignInOptions.defaults()


                Amplify.Auth.signIn(ed_username.text.toString(), ed_password.text.toString(),options,
                    { result ->
                        dismissLoading()

                        if (result.isSignInComplete) {
                            Log.i("Auth SignIn", "Sign in succeeded")
                            mViewModel.message.set("登陆成功")
                        } else {
                            Log.i("Auth SignIn", "Sign in not complete")
                            mViewModel.message.set("登陆失败：" + "Sign in not complete")
                        }
                    },
                    {
                        dismissLoading()
                        Log.e("Auth SignIn", "Failed to sign in", it)
                        mViewModel.message.set("登陆失败：" + it)
                    }
                )
            }
        }
    }


    fun check() {
        if (TextUtils.isEmpty(ed_username.text)) {
            ToastUtils.showShort("请输入用户名")
            return
        }
        if (TextUtils.isEmpty(ed_password.text)) {
            ToastUtils.showShort("请输入密码")
            return
        }


    }
}