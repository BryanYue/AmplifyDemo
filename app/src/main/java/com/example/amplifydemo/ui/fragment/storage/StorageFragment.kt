package com.example.amplifydemo.ui.fragment.storage

import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.amplifyframework.storage.options.StorageGetUrlOptions
import com.amplifyframework.storage.options.StorageRemoveOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.example.amplifydemo.R
import com.example.amplifydemo.app.ext.init
import com.example.amplifydemo.app.util.ModelUtil
import com.example.amplifydemo.base.BaseFragment
import com.example.amplifydemo.databinding.FragmentRecyclerviewBinding
import com.example.amplifydemo.ui.adapter.storage.StorageAdapter
import com.example.amplifydemo.ui.fragment.categories.CategoriesViewModel
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import java.io.File
import kotlin.collections.ArrayList

class StorageFragment : BaseFragment<CategoriesViewModel, FragmentRecyclerviewBinding>() {

    private val storageAdapter: StorageAdapter by lazy { StorageAdapter(arrayListOf()) }

    override fun layoutId(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.init(LinearLayoutManager(context), storageAdapter).let {
            it.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }


        storageAdapter.run {

            draggableModule.isSwipeEnabled = true
            draggableModule.itemTouchHelperCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)
            draggableModule.setOnItemSwipeListener(object : OnItemSwipeListener {
                override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    Log.e("onItemSwipe ", "Start")

                }

                override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                }

                override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    Log.e("onItemSwipe ", "End")
                    showLoading("数据正在删除。。。")
                    val options = StorageRemoveOptions.builder()
                        .accessLevel(StorageAccessLevel.PUBLIC)
                        .build()

                    Amplify.Storage.remove(getItem(pos).key,options,
                        {
                            recyclerView.post {
                                dismissLoading()
                                data.removeAt(pos)
                                notifyItemRemoved(pos)
                                ToastUtils.showShort("Successfully removed: ${it.key}")
                            }
                            Log.i("AmplifyDemo", "Successfully removed: ${it.key}")
                        },
                        {

                            recyclerView.post {
                                dismissLoading()
                                notifyItemChanged(pos)
                                ToastUtils.showShort("Remove failure", it)
                            }
                            Log.e("AmplifyDemo", "Remove failure", it)
                        }
                    )




                }

                override fun onItemSwipeMoving(
                    canvas: Canvas?,
                    viewHolder: RecyclerView.ViewHolder?,
                    dX: Float,
                    dY: Float,
                    isCurrentlyActive: Boolean,
                ) {
                }
            })



            setOnItemClickListener { adapter, view, position ->
                showLoading("正在生成文件下载地址。。。")
                val options = StorageGetUrlOptions.builder()
                    .accessLevel(StorageAccessLevel.PUBLIC)
                    .build()

                Amplify.Storage.getUrl(
                    getItem(position).key,options,
                    {
                        Log.i("AmplifyDemo", "Successfully generated: ${it.url}")

                        recyclerView.post {
                            dismissLoading()
                            ToastUtils.showShort("Successfully generated: ${it.url}")
                            val error: View = layoutInflater.inflate(R.layout.item_text_info, recyclerView, false)
                            val info :TextView=error.findViewById(R.id.tv_info)
                            info.text=getItem(position).key+"下载地址:"+it.url
                            storageAdapter.removeAllHeaderView()
                            storageAdapter.addHeaderView(error)
                            storageAdapter.notifyDataSetChanged()
                        }
                    },
                    {
                        Log.e("AmplifyDemo", "URL generation failure")

                        recyclerView.post {
                            dismissLoading()
                            ToastUtils.showShort("URL generation failure")
                        }
                    }
                )
            }

            setOnItemLongClickListener { adapter, view, position ->
                showLoading("文件正在下载。。。")

                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/"+getItem(position).key)

                val options = StorageDownloadFileOptions.builder()
                    .accessLevel(StorageAccessLevel.PUBLIC)
                    .build()

                Amplify.Storage.downloadFile(getItem(position).key, file, options,
                    {
                        Log.i("AmplifyDemo", "Fraction completed: ${it.fractionCompleted}")
                        showLoading("文件正在下载: ${it.fractionCompleted}")
                    },
                    {
                        Log.i("AmplifyDemo", "Successfully downloaded: ${it.file.name}")
                        recyclerView.post {
                            dismissLoading()
                            ToastUtils.showShort("Successfully downloaded: ${it.file.name}")
                        }
                    },
                    {
                        Log.e("AmplifyDemo", "Download Failure", it)
                        recyclerView.post {
                            dismissLoading()
                            ToastUtils.showShort("Successfully downloaded: $it")
                        }
                    }
                )
                return@setOnItemLongClickListener false
            }



        }



    }

    class MyDraggableModule(baseQuickAdapter: BaseQuickAdapter<*, *>):
        BaseDraggableModule(baseQuickAdapter) {
        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder) {
//            super.onItemSwiped(viewHolder)
            val pos = getViewHolderPosition(viewHolder)
            if (isSwipeEnabled) {
                    mOnItemSwipeListener?.onItemSwiped(viewHolder, pos)
                }
            }
    }

    override fun createObserver() {
    }


    override fun initData() {

        val dataId = arguments?.getString("dataId", null)
        val arrayName = arguments?.getString("arrayName", null)
        if (!TextUtils.isEmpty(dataId)) {

            showLoading("Loading...")
            Amplify.Storage.list("",
                { result ->
                    dismissLoading()
                    result.items.forEach { item ->
                        Log.i("AmplifyDemo", "Item: ${item.key}")
                    }
                    Log.e("AmplifyDemo", "size: ${ result.items.size}")
                    recyclerView.post {
                        storageAdapter.removeAllHeaderView()
                        storageAdapter.setNewInstance(result.items.toCollection(ArrayList()))

                        val error: View = layoutInflater.inflate(R.layout.item_text_info, recyclerView, false)
                        val info :TextView=error.findViewById(R.id.tv_info)
                        info.text="列表点击生成文件下载地址，长按下载文件，侧滑删除文件"
                        storageAdapter.removeAllHeaderView()
                        storageAdapter.addHeaderView(error)
                        storageAdapter.notifyDataSetChanged()
                    }

                },
                {
                    dismissLoading()
                    recyclerView.post {
                        val error: View = layoutInflater.inflate(R.layout.item_text_info, recyclerView, false)
                        val info :TextView=error.findViewById(R.id.tv_info)
                        info.text=it.toString()
                        storageAdapter.removeAllHeaderView()
                        storageAdapter.addHeaderView(error)
                        storageAdapter.notifyDataSetChanged()
                    }
                    Log.e("AmplifyDemo", "List failure", it)
                }
            )



            arrayName?.let {
                when(it){
                    "Storage"->{


                    }
                    else -> {

                    }
                }
            }
        }






    }
}