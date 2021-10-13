package com.example.amplifydemo.ui.fragment.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthResendSignUpCodeOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.blankj.utilcode.util.ToastUtils
import com.example.amplifydemo.BuildConfig
import com.example.amplifydemo.R
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentRegisteruserBinding
import kotlinx.android.synthetic.main.fragment_registeruser.*

class RegisterUserFragment : BaseFragment<AuthViewModel, FragmentRegisteruserBinding>(),
    View.OnClickListener {
    override fun layoutId(): Int {
        return R.layout.fragment_registeruser
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel =mViewModel

        tv_sendCode.setOnClickListener(this)
        tv_signUp.setOnClickListener(this)
        tv_signUpByCode.setOnClickListener(this)


        if (BuildConfig.DEBUG){
            ed_username.setText("Bryan")
            ed_password.setText("12345678")
            ed_email.setText("phantom33@qq.com")
        }

    }

    override fun createObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_signUp -> {
                check()
                showLoading("Loading...")

                val options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.email(), ed_email.text.toString())
                    .build()


                Amplify.Auth.signUp(ed_username.text.toString(),
                    ed_password.text.toString(),
                    options,
                    {
                        dismissLoading()
                        ToastUtils.showShort("注册成功")
                        Log.e("Auth signUp", "Sign up succeeded: $it")
                        mViewModel.message.set("注册成功")
                    },
                    {
                        dismissLoading()
                        ToastUtils.showLong("注册失败:"+it.message)
                        Log.e("Auth signUp", it.message.toString())
                        Log.e("Auth signUp", "" + it.localizedMessage)
                        Log.e("Auth signUp", "Sign up failed", it)
                        mViewModel.message.set("注册失败："+it)
                    }
                )
            }
            R.id.tv_sendCode -> {
                check()
                showLoading("Loading...")
                val options = AuthResendSignUpCodeOptions.defaults()

                Amplify.Auth.resendSignUpCode(ed_username.text.toString(),options,
                    {
                        dismissLoading()
                        ToastUtils.showShort("发送成功")
                        Log.e("Auth SignUpCode", "Sign up succeeded: $it")
                        mViewModel.message.set("发送成功")
                    },
                    {
                        dismissLoading()
                        ToastUtils.showLong("发送失败:"+it.message)
                        Log.e("Auth SignUpCode ", it.message.toString())
                        Log.e("Auth SignUpCode", "" + it.localizedMessage)
                        Log.e("Auth SignUpCode", "sendSignUpCode failed", it)
                        mViewModel.message.set("发送失败："+it)
                    })
            }
            R.id.tv_signUpByCode ->{
                check()
                if (TextUtils.isEmpty(ed_code.text)) {
                    ToastUtils.showShort("请输入验证码")
                    return
                }
                showLoading("Loading...")

                Amplify.Auth.confirmSignUp(
                    ed_username.text.toString(), ed_code.text.toString(),
                    { result ->
                        dismissLoading()
                        if (result.isSignUpComplete) {
                            ToastUtils.showShort("注册成功")
                            Log.e("Auth signUpByCode", "Confirm signUp succeeded")
                            mViewModel.message.set("注册成功")
                        } else {
                            ToastUtils.showShort("注册失败")
                            Log.e("Auth signUpByCode","Confirm sign up not complete")
                            mViewModel.message.set("注册失败："+"Confirm sign up not complete")
                        }
                    },
                    {
                        dismissLoading()
                        ToastUtils.showShort("注册失败:",it.message)
                        Log.e("Auth signUpByCode", "Failed to confirm sign up", it)
                        mViewModel.message.set("注册失败："+it)
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
        if (TextUtils.isEmpty(ed_email.text)) {
            ToastUtils.showShort("请输入邮箱")
            return
        }


    }
}