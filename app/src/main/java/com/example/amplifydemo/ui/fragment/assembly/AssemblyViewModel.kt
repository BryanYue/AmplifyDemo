package com.example.amplifydemo.ui.fragment.assembly

import android.content.Intent
import android.util.Log
import androidx.databinding.Observable
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions
import com.blankj.utilcode.util.FileUtils
import com.example.amplifydemo.app.databind.StringObservableField
import com.example.amplifydemo.app.viewmodel.BaseViewModel
import com.example.amplifydemo.ui.adapter.assembly.AssemblyAdapter
import java.io.File
import java.io.FileInputStream

class AssemblyViewModel : BaseViewModel() {

    var message = StringObservableField()
    var value = StringObservableField()
    val stringBuilder = StringBuilder()

    var assemblyAdapter: AssemblyAdapter? = null

    fun fetchUserAttributes() {
        showDialog()
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
                dismissDialog()
                Log.e("AmplifyDemo", "User attributes = $stringBuilder")
            },
            {
                stringBuilder.clear()
                stringBuilder.append("获取用户信息失败: $it")
                message.set(stringBuilder.toString())
                dismissDialog()
                Log.e("AmplifyDemo", "Failed to fetch user attributes", it)
            }
        )



    }


    fun updateUserAttributes(key: String) {
        val list: ArrayList<AuthUserAttribute> = arrayListOf()
        list.add(AuthUserAttribute(getAuthUserAttributeKey(key),value.get()))
        showDialog()
        Amplify.Auth.updateUserAttributes(
            list, // attributes is a list of AuthUserAttribute
            {

                stringBuilder.clear()
                stringBuilder.append("更新用户信息成功:$it")
                message.set(stringBuilder.toString())
                dismissDialog()
                Log.i("AmplifyDemo", "Updated user attributes = $it")
            },
            {
                stringBuilder.clear()
                stringBuilder.append("更新用户信息成功: $it")
                message.set(stringBuilder.toString())
                dismissDialog()
                Log.e("AmplifyDemo", "Failed to update user attributes", it)
            }
        )


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

    fun uploadInputStream(file: File){
        showDialog()


        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                when (session.identityId.type) {
                    AuthSessionResult.Type.SUCCESS ->{
                        Log.i("AmplifyDemo", "IdentityId = ${session.identityId.value}")

                        val options = StorageUploadInputStreamOptions.builder()
                            .accessLevel(StorageAccessLevel.PUBLIC)
                            .build()

                        if (file != null) {
                            Log.e("AmplifyDemo", "FileUtils = ${FileUtils.getFileName(file)}")
                            Log.e("AmplifyDemo", "FileUtils = ${FileUtils.getFileExtension(file)}")
                            Log.e("AmplifyDemo", "FileUtils = ${FileUtils.getSize(file)}")
                        }

                        val stream = FileInputStream(file)

                        Amplify.Storage.uploadInputStream(FileUtils.getFileName(file), stream,options,
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件流进度:${it.fractionCompleted}")
                                message.set(stringBuilder.toString())
                                Log.i("AmplifyDemo", "Fraction completed: ${it.fractionCompleted}")
                            },
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件流成功:$it")
                                message.set(stringBuilder.toString())
                                dismissDialog()
                                Log.i("AmplifyDemo", "Successfully uploaded: ${it.key}")
                            },
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件流失败:$it")
                                message.set(stringBuilder.toString())
                                dismissDialog()
                                Log.e("AmplifyDemo", "Upload failed", it)
                            }
                        )

                    }

                    AuthSessionResult.Type.FAILURE ->{
                        stringBuilder.clear()
                        stringBuilder.append("上传文件流失败 获取凭证失败:$it")
                        message.set(stringBuilder.toString())
                        dismissDialog()
                        Log.w("AmplifyDemo", "IdentityId not found", session.identityId.error)
                    }

                }
            },
            {
                stringBuilder.clear()
                stringBuilder.append("上传文件流失败 获取凭证失败:$it")
                message.set(stringBuilder.toString())
                dismissDialog()

                Log.e("AmplifyDemo", "Failed to fetch session", it)


            }
        )

    }

    fun uploadFile(file: File){
        showDialog()


        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                when (session.identityId.type) {
                    AuthSessionResult.Type.SUCCESS ->{
                        Log.i("AmplifyDemo", "IdentityId = ${session.identityId.value}")

                        val options = StorageUploadFileOptions.builder()
                            .accessLevel(StorageAccessLevel.PUBLIC)
                            .build()


                        Log.e("AmplifyDemo", "FileUtils = ${FileUtils.getFileName(file)}")
                        Log.e("AmplifyDemo", "FileUtils = ${FileUtils.getFileExtension(file)}")
                        Log.e("AmplifyDemo", "FileUtils = ${FileUtils.getSize(file)}")

                        Amplify.Storage.uploadFile(FileUtils.getFileName(file), file,options,
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件进度:${it.fractionCompleted}")
                                message.set(stringBuilder.toString())
                                Log.i("AmplifyDemo", "Fraction completed: ${it.fractionCompleted}")
                            },
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件成功:$it")
                                message.set(stringBuilder.toString())
                                dismissDialog()
                                Log.i("AmplifyDemo", "Successfully uploaded: ${it.key}")
                            },
                            {
                                stringBuilder.clear()
                                stringBuilder.append("上传文件失败:$it")
                                message.set(stringBuilder.toString())
                                dismissDialog()
                                Log.e("AmplifyDemo", "Upload failed", it)
                            }
                        )




                    }

                    AuthSessionResult.Type.FAILURE ->{
                        stringBuilder.clear()
                        stringBuilder.append("上传文件失败 获取凭证失败:$it")
                        message.set(stringBuilder.toString())
                        dismissDialog()
                        Log.w("AmplifyDemo", "IdentityId not found", session.identityId.error)
                    }

                }
            },
            {
                stringBuilder.clear()
                stringBuilder.append("上传文件失败 获取凭证失败:$it")
                message.set(stringBuilder.toString())
                dismissDialog()

                Log.e("AmplifyDemo", "Failed to fetch session", it)


            }
        )

    }




}