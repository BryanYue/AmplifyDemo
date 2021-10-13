package com.example.amplifydemo.ui.fragment.categories

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.blankj.utilcode.util.ToastUtils
import com.example.amplifydemo.R
import com.example.amplifydemo.app.ext.init
import com.example.amplifydemo.app.ext.nav
import com.example.amplifydemo.app.ext.navigateAction
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentRecyclerviewBinding
import com.example.amplifydemo.ui.fragment.adapter.CategoriesAdapter
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlin.collections.ArrayList

class CategoriesFragment : BaseFragment<CategoriesViewModel, FragmentRecyclerviewBinding>() {

    private val categoriesAdapter: CategoriesAdapter by lazy { CategoriesAdapter(arrayListOf()) }

    override fun layoutId(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.init(LinearLayoutManager(context), categoriesAdapter).let {
            it.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }


        categoriesAdapter.run {
            setOnItemClickListener { adapter, view, position ->
                when(categoriesAdapter.getItem(position)){
                    "Authentication"->{
                        nav().navigateAction(
                            R.id.action_categoriesFragment_to_categoriesFragment,
                            Bundle().apply {
                                putString("arrayId", R.array.auth.toString())
                                putString("arrayName", "Authentication")
                            })
                    }
                    "register-a-user"->{
                        nav().navigateAction(
                            R.id.action_categoriesFragment_to_registerUserFragment)

                    }

                    "sign-in-a-user"->{
                        nav().navigateAction(
                            R.id.action_categoriesFragment_to_signUserfFragment)
                    }

                    "devices"->{
                        nav().navigateAction(
                            R.id.action_categoriesFragment_to_devicesFragment)
                    }
                    "password-management"->{
                        nav().navigateAction(
                            R.id.action_categoriesFragment_to_passwordManagementFragment)
                    }

                    "sign-out"->{
                        nav().navigateAction(
                            R.id.action_categoriesFragment_to_signOutFragment)
                    }
                    else->{
                        ToastUtils.showShort("该功能开发中")
                    }
                }

            }
        }



    }

    override fun createObserver() {
    }


    override fun initData() {

        val arrayId = arguments?.getString("arrayId", null)
        val arrayName = arguments?.getString("arrayName", null)
        if (TextUtils.isEmpty(arrayId)) {
            val data: ArrayList<String> = resources.getStringArray(R.array.categories).toCollection(ArrayList())
            categoriesAdapter.setNewInstance(data)
        } else {
            val data: ArrayList<String> = resources.getStringArray(arrayId!!.toInt()).toCollection(ArrayList())
            categoriesAdapter.setNewInstance(data)

            arrayName?.let {
                when(it){
                    "Authentication"->{
                        Amplify.Hub.subscribe(HubChannel.AUTH) { event ->
                            when (event.name) {
                                InitializationStatus.SUCCEEDED.toString() ->
                                    Log.i("AuthQuickstart", "Auth successfully initialized")
                                InitializationStatus.FAILED.toString() ->
                                    Log.i("AuthQuickstart", "Auth failed to succeed")
                                else -> when (AuthChannelEventName.valueOf(event.name)) {
                                    AuthChannelEventName.SIGNED_IN ->
                                        Log.i("AuthQuickstart", "Auth just became signed in")
                                    AuthChannelEventName.SIGNED_OUT ->
                                        Log.i("AuthQuickstart", "Auth just became signed out")
                                    AuthChannelEventName.SESSION_EXPIRED ->
                                        Log.i("AuthQuickstart", "Auth session just expired")
                                    else ->
                                        Log.w("AuthQuickstart", "Unhandled Auth Event: ${event.name}")
                                }
                            }
                        }
                    }

                    else -> {

                    }
                }
            }

        }



    }
}