package com.example.amplifydemo.ui.fragment.adapter.categories

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.amplifydemo.R

class CategoriesAdapter(data:ArrayList<String>): BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_categories,data){
    override fun convert(holder: BaseViewHolder, item: String) {

        holder.setText(R.id.tv_name,item)
    }
}