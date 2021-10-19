package com.example.amplifydemo.app.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

import androidx.core.content.FileProvider

import android.os.Build
import com.example.amplifydemo.BuildConfig

import java.io.File
import android.provider.MediaStore

import android.provider.DocumentsContract

import android.content.ContentUris

import androidx.annotation.RequiresApi
import android.database.Cursor
import android.os.Environment
import android.text.TextUtils


object FileUtil {

    fun getFileFromUri(uri: Uri?, context: Context): File? {
        return if (uri == null) {
            null
        } else when (uri.scheme) {
            "content" -> getFileFromContentUri(uri, context)
            "file" -> File(uri.path)
            else -> null
        }
    }

    /**
     * 通过内容解析中查询uri中的文件路径
     */
    private fun getFileFromContentUri(contentUri: Uri?, context: Context): File? {
        if (contentUri == null) {
            return null
        }
        var file: File? = null
        val filePath: String
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            contentUri, filePathColumn, null,
            null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
            cursor.close()
            if (!TextUtils.isEmpty(filePath)) {
                file = File(filePath)
            }
        }
        return file
    }


    fun getUriFromFile(context: Context?, file: File?): Uri? {
        if (context == null || file == null) {
            throw NullPointerException()
        }
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context.getApplicationContext(),
                BuildConfig.APPLICATION_ID.toString() + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
        return uri
    }


    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            //一些三方的文件浏览器会进入到这个方法中，例如ES
            //QQ文件管理器不在此列
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.lastPathSegment
            if (isQQMediaDocument(uri)) {
                val path = uri.path
                val fileDir: File = Environment.getExternalStorageDirectory()
                val file = File(fileDir, path!!.substring("/QQBrowser".length, path.length))
                return if (file.exists()) file.toString() else null
            }
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     * The context.
     * @param uri
     * The Uri to query.
     * @param selection
     * (Optional) Filter used in the query.
     * @param selectionArgs
     * (Optional) Selection arguments used in the query.
     *
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.MediaColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getRealPathFromURI(context: Context?, contentUri: Uri?): String? {
        return getDataColumn(context!!, contentUri, null, null)
    }

    /**
     * @param uri
     * The Uri to check.
     *
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri
     * The Uri to check.
     *
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri
     * The Uri to check.
     *
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    /**
     * 使用第三方qq文件管理器打开
     *
     * @param uri
     *
     * @return
     */
    fun isQQMediaDocument(uri: Uri): Boolean {
        return "com.tencent.mtt.fileprovider" == uri.authority
    }

    /**
     * @param uri
     * The Uri to check.
     *
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}