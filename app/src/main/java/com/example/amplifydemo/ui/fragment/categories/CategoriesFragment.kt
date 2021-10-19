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
import com.example.amplifydemo.R
import com.example.amplifydemo.app.ext.init
import com.example.amplifydemo.app.util.ModelUtil
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentRecyclerviewBinding
import com.example.amplifydemo.ui.fragment.adapter.StorageAdapter
import com.example.amplifydemo.ui.fragment.adapter.categories.CategoriesAdapter
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlin.collections.ArrayList

class CategoriesFragment : BaseFragment<CategoriesViewModel, FragmentRecyclerviewBinding>() {

    private val categoriesAdapter: CategoriesAdapter by lazy { CategoriesAdapter(arrayListOf()) }
    private val storageAdapter: StorageAdapter by lazy { StorageAdapter(arrayListOf()) }

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

                ModelUtil.ItemClick(this@CategoriesFragment,mViewModel,categoriesAdapter.getItem(position))



            }
        }



    }

    override fun createObserver() {
    }


    override fun initData() {

        val arrayId = arguments?.getString("arrayId", null)
        val dataId = arguments?.getString("dataId", null)
        var data = arguments?.getStringArray("arrayData")?.toCollection(ArrayList())
        val arrayName = arguments?.getString("arrayName", null)
        if (TextUtils.isEmpty(arrayId)) {
            if (data==null||data.isEmpty()){
                 data = resources.getStringArray(R.array.categories).toCollection(ArrayList())
            }
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
                    "Storage"->{


                    }
                    else -> {

                    }
                }
            }

        }



    }
}