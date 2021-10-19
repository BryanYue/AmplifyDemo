package com.example.amplifydemo.ui.fragment.adapter.assembly

import android.widget.EditText
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.amplifydemo.R
import com.example.amplifydemo.data.entity.AssemblyEntity

class AssemblyAdapter : BaseMultiItemQuickAdapter<AssemblyEntity,BaseViewHolder>() {


    init {
        addItemType(AssemblyEntity.TEXT_BUTTON, R.layout.item_button)
        addItemType(AssemblyEntity.EditText, R.layout.item_edittext)
        addItemType(AssemblyEntity.TEXT_INFO, R.layout.item_text_info)
        addItemType(AssemblyEntity.TEXT_TITLE, R.layout.item_categories)
    }


    override fun convert(holder: BaseViewHolder, item: AssemblyEntity) {
      when(holder.itemViewType){
          AssemblyEntity.TEXT_TITLE ->{
              item.title?.let {
                  holder.setText(R.id.tv_name,it)
              }
          }
          AssemblyEntity.TEXT_BUTTON ->{

              item.content?.let {
                  holder.setText(R.id.button,it)
              }

              addChildClickViewIds(R.id.button)
          }
          AssemblyEntity.EditText ->{
              item.hint?.let {
                  val view:EditText=holder.getView(R.id.ed_item)
                  view.hint =it
              }

          }
          AssemblyEntity.TEXT_INFO ->{
              item.content?.let {
                  holder.setText(R.id.tv_info,it)
              }
          }
          else ->{

          }
      }
    }


}