package com.example.amplifydemo.ui.fragment.assembly

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amplifydemo.R
import com.example.amplifydemo.app.ext.init
import com.example.amplifydemo.app.util.ModelUtil
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentRecyclerviewBinding
import com.example.amplifydemo.ui.fragment.adapter.assembly.AssemblyAdapter
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class AssemblyFragment :BaseFragment<AssemblyViewModel,FragmentRecyclerviewBinding>(){
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

        mViewModel.assemblyAdapter=assemblyAdapter
        assemblyAdapter.run {
            setOnItemClickListener { adapter, view, position ->

                if (assemblyAdapter.getItem(position).content?.contains("updateUserAttributes")==true){
                    val editText: EditText = assemblyAdapter.getViewByPosition(1,R.id.ed_item) as EditText
                    if (!TextUtils.isEmpty(editText.text)){
                        mViewModel.value.set(editText.text.toString())
                    }
                }
                ModelUtil.ItemClick(this@AssemblyFragment,mViewModel,assemblyAdapter.getItem(position).content)

            }
        }
    }

    override fun createObserver() {

    }

    override fun initData() {
        val dataId = arguments?.getString("dataId", null)
//        val data = arguments?.getStringArray("data")?.toCollection(ArrayList())
        if (!TextUtils.isEmpty(dataId)) {
            assemblyAdapter.setNewInstance(ModelUtil.getData(dataId!!))

        }

        when(dataId){
            "获取当前用户属性"->{
                mViewModel.fetchUserAttributes(this)
            }
        }
    }
}