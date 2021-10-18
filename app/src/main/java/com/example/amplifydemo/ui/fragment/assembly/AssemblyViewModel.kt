package com.example.amplifydemo.ui.fragment.assembly

import android.util.Log
import androidx.databinding.Observable
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.example.amplifydemo.app.databind.StringObservableField
import com.example.amplifydemo.app.viewmodel.BaseViewModel
import com.example.amplifydemo.ui.fragment.adapter.assembly.AssemblyAdapter

class AssemblyViewModel : BaseViewModel() {

    var message = StringObservableField()
    var value = StringObservableField()
    val stringBuilder = StringBuilder()

    var assemblyAdapter: AssemblyAdapter? = null

    fun fetchUserAttributes(fragment: AssemblyFragment) {
        fragment.showLoading("Loading...")
        Amplify.Auth.fetchUserAttributes(
            {
                stringBuilder.clear()
                stringBuilder.append("获取用户信息成功:")
                it.forEach {
                    stringBuilder.append("\n")
                    stringBuilder.append("key:" + it.key.keyString)
                    stringBuilder.append("\n")
                    stringBuilder.append("value:" + it.value)
                    stringBuilder.append("\n")
                }
                message.set(stringBuilder.toString())
                fragment.dismissLoading()

                Log.e("Amplifydemo", "User attributes = $stringBuilder")
            },
            {
                stringBuilder.clear()
                stringBuilder.append("获取用户信息失败: $it")
                message.set(stringBuilder.toString())
                fragment.dismissLoading()

                Log.e("Amplifydemo", "Failed to fetch user attributes", it)
            }
        )


        message.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                assemblyAdapter?.let {
                    it.data[0].content = message.get()
                    it.notifyItemChanged(0)
                }
            }
        })
    }


    fun updateUserAttributes(fragment: AssemblyFragment, key: String) {
        val list: ArrayList<AuthUserAttribute> = arrayListOf()
        list.add(AuthUserAttribute(getAuthUserAttributeKey(key),value.get()))
        fragment.showLoading("Loading...")
        Amplify.Auth.updateUserAttributes(
            list, // attributes is a list of AuthUserAttribute
            {

                stringBuilder.clear()
                stringBuilder.append("更新用户信息成功:$it")
                message.set(stringBuilder.toString())
                fragment.dismissLoading()
                Log.i("AuthDemo", "Updated user attributes = $it")
            },
            {
                stringBuilder.clear()
                stringBuilder.append("更新用户信息成功: $it")
                message.set(stringBuilder.toString())
                fragment.dismissLoading()
                Log.e("AuthDemo", "Failed to update user attributes", it)
            }
        )

        message.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                assemblyAdapter?.let {
                    it.data[3].content = message.get()
                    it.notifyItemChanged(3)
                }
            }
        })
    }


    fun getAuthUserAttributeKey(key: String):AuthUserAttributeKey{
        when(key){
            "updateUserAttributes ADDRESS" ->{
                return AuthUserAttributeKey.address()
            }
            "updateUserAttributes BIRTHDATE"->{
                return AuthUserAttributeKey.birthdate()
            }
            else ->{
                return AuthUserAttributeKey.address()
            }
        }
    }

}