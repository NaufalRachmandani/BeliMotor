package com.naufal.belimotor.data.common

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object GetFile {
    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))

        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!, destinationFilename
                )
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }

        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = cursor.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }
}