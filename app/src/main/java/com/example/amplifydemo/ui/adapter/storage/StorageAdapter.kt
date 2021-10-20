package com.example.amplifydemo.ui.adapter.storage

import com.amplifyframework.storage.StorageItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.amplifydemo.R
import com.example.amplifydemo.ui.fragment.storage.StorageFragment

class StorageAdapter(data:ArrayList<StorageItem>): BaseQuickAdapter<StorageItem,BaseViewHolder>(R.layout.item_categories,data),DraggableModule{
    override fun convert(holder: BaseViewHolder, item: StorageItem) {

        holder.setText(R.id.tv_name,item.key)
    }


    override fun addDraggableModule(baseQuickAdapter: BaseQuickAdapter<*, *>): BaseDraggableModule {
        return StorageFragment.MyDraggableModule(baseQuickAdapter)
    }

}