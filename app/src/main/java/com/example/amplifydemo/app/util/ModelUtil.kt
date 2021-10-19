package com.example.amplifydemo.app.util

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageItem
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.amplifydemo.R
import com.example.amplifydemo.app.ext.nav
import com.example.amplifydemo.app.ext.navigateAction
import com.example.amplifydemo.data.entity.AssemblyEntity
import com.example.amplifydemo.ui.fragment.assembly.AssemblyFragment
import com.example.amplifydemo.ui.fragment.assembly.AssemblyViewModel
import java.lang.reflect.Field
import java.util.*
import kotlin.arrayOf as arrayOf1

object ModelUtil {

    fun getCategories_UserAttributes(): ArrayList<String> {
        val list: ArrayList<String> = arrayListOf()
        list.addAll(StringUtils.getStringArray(R.array.UserAttributes))
        return list
    }
    fun getAssembly_UpdateUserAttributesWithOutConfirm(dataId:String): ArrayList<AssemblyEntity> {
        val list: ArrayList<AssemblyEntity> = arrayListOf()
        list.add(AssemblyEntity(AssemblyEntity.TEXT_TITLE, dataId))
        list.add(AssemblyEntity(AssemblyEntity.EditText, ""))
        list.add(AssemblyEntity(AssemblyEntity.TEXT_BUTTON, "updateUserAttributes $dataId"))
        list.add(AssemblyEntity(AssemblyEntity.TEXT_INFO, ""))
        return list
    }
    fun getAssembly_FetchUserAttributes(): ArrayList<AssemblyEntity> {
        val list: ArrayList<AssemblyEntity> = arrayListOf()
        list.add(AssemblyEntity(AssemblyEntity.TEXT_INFO, "正在获取用户信息。。。"))

        return list
    }

    fun getAssembly_storage(dataId:String): ArrayList<AssemblyEntity> {
        val list: ArrayList<AssemblyEntity> = arrayListOf()
        list.add(AssemblyEntity(AssemblyEntity.TEXT_BUTTON,"Storage $dataId"))
        list.add(AssemblyEntity(AssemblyEntity.TEXT_INFO, ""))
        return list
    }





    fun getData(dataId: String) :ArrayList<AssemblyEntity>{
        val list: ArrayList<AssemblyEntity> = arrayListOf()
        when(dataId){
            "获取当前用户属性" ->{
                list.addAll(getAssembly_FetchUserAttributes())
            }
            "uploadInputStream","uploadFile","downloadFile","storage-list"->{
                list.addAll(getAssembly_storage(dataId))
            }

            else->{
                list.addAll(getAssembly_UpdateUserAttributesWithOutConfirm(dataId))

            }
        }


        return list
    }

    fun ItemClick(fragment: Fragment,viewModel: ViewModel, action: String?) {
        Log.e("ItemClick","action $action")
        when (action) {
            "Authentication" -> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_categoriesFragment,
                    Bundle().apply {
                        putString("arrayId", R.array.auth.toString())
                        putString("arrayName", "Authentication")
                    })
            }
            "Storage"-> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_categoriesFragment,
                    Bundle().apply {
                        putString("arrayId", R.array.Storage.toString())
                        putString("arrayName", "Storage")
                    })
            }
            "register-a-user" -> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_registerUserFragment
                )

            }

            "sign-in-a-user" -> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_signUserfFragment
                )
            }

            "devices" -> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_devicesFragment
                )
            }
            "password-management" -> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_passwordManagementFragment
                )
            }

            "sign-out" -> {
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_signOutFragment
                )
            }
            "user-attributes" ->{
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_categoriesFragment,
                    Bundle().apply {
                        putString("arrayId", R.array.UserAttributes.toString())
                        putString("arrayName", "Authentication")
                    })
            }



            "获取当前用户属性" ->{
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_assemblyFragment,
                    Bundle().apply {
                        putString("dataId", "获取当前用户属性")
                    })
            }
            "更新用户属性"->{
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_categoriesFragment,
                    Bundle().apply {
                        putStringArray("arrayData", getAuthUserAttributeKey())
                        putString("arrayName", "Authentication")
                    })
            }
            "ADDRESS","BIRTHDATE"->{
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_assemblyFragment,
                    Bundle().apply {
                        putString("dataId", action)
                    })
            }

            "uploadInputStream","uploadFile","downloadFile","track-download-progress"->{
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_assemblyFragment,
                    Bundle().apply {
                        putString("dataId", action)
                        putString("arrayName", "Storage")
                    })
            }

            "storage-list","storage-remove"->{
                fragment.nav().navigateAction(
                    R.id.action_categoriesFragment_to_storageragment,
                    Bundle().apply {
                        putString("dataId", action)
                        putString("arrayName", "Storage")
                    })
            }
            "Storage uploadInputStream"->{

            }
            "Storage uploadFile"->{
                val model: AssemblyViewModel = viewModel as AssemblyViewModel
                val assemblyFragment: AssemblyFragment = fragment as AssemblyFragment
                model.openFileManage(assemblyFragment)
            }
            "updateUserAttributes ADDRESS","updateUserAttributes BIRTHDATE"->{
                val model: AssemblyViewModel = viewModel as AssemblyViewModel
                val assemblyFragment: AssemblyFragment = fragment as AssemblyFragment
                model.updateUserAttributes(assemblyFragment,action)
            }
            else -> {
                ToastUtils.showShort("该功能开发中")
            }
        }

    }








    fun  getAuthUserAttributeKey(): Array<String>{
        val field = AuthUserAttributeKey::class.java.declaredFields
        val date: Array<String> = Array(field.size) { i -> field[i].name }
        return date
    }
}