package com.example.amplifydemo.ui.fragment.assembly

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.databinding.Observable
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.blankj.utilcode.util.FileUtils
import com.example.amplifydemo.app.App
import com.example.amplifydemo.app.databind.StringObservableField
import com.example.amplifydemo.app.viewmodel.BaseViewModel
import com.example.amplifydemo.ui.fragment.adapter.assembly.AssemblyAdapter
import com.google.android.play.core.internal.al
import java.io.File

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
                Log.i("Amplifydemo", "Updated user attributes = $it")
            },
            {
                stringBuilder.clear()
                stringBuilder.append("更新用户信息成功: $it")
                message.set(stringBuilder.toString())
                fragment.dismissLoading()
                Log.e("Amplifydemo", "Failed to update user attributes", it)
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

    fun openFileManage(fragment: AssemblyFragment){
        val intent:Intent =Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        fragment.startActivityForResult(intent,fragment.RC_EXTERNAL_STORAGE)
    }

    fun uploadFile(fragment: AssemblyFragment,path: String){
        fragment.showLoading("Loading...")


        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                when (session.identityId.type) {
                    AuthSessionResult.Type.SUCCESS ->{
                        Log.i("Amplifydemo", "IdentityId = ${session.identityId.value}")

                        val options = StorageUploadFileOptions.builder()
                            .accessLevel(StorageAccessLevel.PUBLIC)
                            .build()

                        val file:File =FileUtils.getFileByPath(path)

                        Log.e("Amplifydemo", "FileUtils = ${FileUtils.getFileName(file)}")
                        Log.e("Amplifydemo", "FileUtils = ${FileUtils.getFileExtension(file)}")
                        Log.e("Amplifydemo", "FileUtils = ${FileUtils.getSize(file)}")

                        Amplify.Storage.uploadFile(FileUtils.getFileName(file), file,options,
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件进度:${it.fractionCompleted}")
                                message.set(stringBuilder.toString())
                                Log.i("Amplifydemo", "Fraction completed: ${it.fractionCompleted}")
                            },
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件成功:$it")
                                message.set(stringBuilder.toString())
                                fragment.dismissLoading()
                                Log.i("Amplifydemo", "Successfully uploaded: ${it.key}")
                            },
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件失败:$it")
                                message.set(stringBuilder.toString())
                                fragment.dismissLoading()
                                Log.e("Amplifydemo", "Upload failed", it)
                            }
                        )




                    }

                    AuthSessionResult.Type.FAILURE ->{
                        stringBuilder.clear()
                        stringBuilder.append("上传文件失败 获取凭证失败:$it")
                        message.set(stringBuilder.toString())
                        fragment.dismissLoading()
                        Log.w("Amplifydemo", "IdentityId not found", session.identityId.error)
                    }

                }
            },
            {
                stringBuilder.clear()
                stringBuilder.append("上传文件失败 获取凭证失败:$it")
                message.set(stringBuilder.toString())
                fragment.dismissLoading()

                Log.e("Amplifydemo", "Failed to fetch session", it)


            }
        )








        message.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                assemblyAdapter?.let {
                    it.data[1].content = message.get()
                    it.notifyItemChanged(1)
                }
            }
        })
    }




}