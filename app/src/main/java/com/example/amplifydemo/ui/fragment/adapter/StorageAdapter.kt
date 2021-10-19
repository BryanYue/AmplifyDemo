package com.example.amplifydemo.ui.fragment.adapter

import com.amplifyframework.storage.StorageItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.amplifydemo.R

class StorageAdapter(data:ArrayList<StorageItem>): BaseQuickAdapter<StorageItem,BaseViewHolder>(R.layout.item_categories,data){
    override fun convert(holder: BaseViewHolder, item: StorageItem) {

        holder.setText(R.id.tv_name,item.toString())
    }
}