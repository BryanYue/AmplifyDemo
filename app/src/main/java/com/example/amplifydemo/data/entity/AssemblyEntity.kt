package com.example.amplifydemo.data.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

class AssemblyEntity(override val itemType: Int, var content: String?) : MultiItemEntity {
    companion object {
        val TEXT_TITLE = 0
        val TEXT_BUTTON = 1
        val TEXT_INFO = 2
        val EditText = 3
    }


    var hint: String? = null
    var title: String? = null

}