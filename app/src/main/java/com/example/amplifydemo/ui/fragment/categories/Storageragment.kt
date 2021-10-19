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

class Storageragment : BaseFragment<CategoriesViewModel, FragmentRecyclerviewBinding>() {

    private val storageAdapter: StorageAdapter by lazy { StorageAdapter(arrayListOf()) }

    override fun layoutId(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.init(LinearLayoutManager(context), storageAdapter).let {
            it.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }


        storageAdapter.run {
            setOnItemClickListener { adapter, view, position ->

                ModelUtil.ItemClick(this@Storageragment,mViewModel,"")



            }
        }



    }

    override fun createObserver() {
    }


    override fun initData() {

        val dataId = arguments?.getString("dataId", null)
        val arrayName = arguments?.getString("arrayName", null)
        if (!TextUtils.isEmpty(dataId)) {
            Amplify.Storage.list("",
                { result ->
                    result.items.forEach { item ->
                        Log.i("Amplifydemo", "Item: ${item.key}")
                    }
                    Log.e("Amplifydemo", "size: ${ result.items.size}")
//                    storageAdapter.setNewInstance(result.items.toCollection(ArrayList()))
                },
                {
                    Log.e("Amplifydemo", "List failure", it)
                }
            )



            arrayName?.let {
                when(it){
                    "Storage"->{


                    }
                    else -> {

                    }
                }
            }
        }






    }
}