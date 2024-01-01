package com.sisapp.compose.data.mediaStore

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.sisapp.compose.data.model.MediaModel
import com.sisapp.compose.data.model.MediaType

class MediaStoreHealper(var context: Application) {

    fun getMixedMediaItems(page: Int, pageSize: Int): List<MediaModel> {
        val contentResolver: ContentResolver = context.contentResolver
        val offset = (page - 1) * pageSize
        Log.i("TAGabs", " page: $page  pageSize: $pageSize  offset: $offset")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE)
        val queryUri: Uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}, ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO})"
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC LIMIT $pageSize OFFSET $offset"
        val mixedList = mutableListOf<MediaModel>()
        val cursor = if (Build.VERSION.SDK_INT >= 30) {
            contentResolver.query(
                queryUri,
                projection,
                Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putStringArray(
                        ContentResolver.QUERY_ARG_SORT_COLUMNS,
                        arrayOf(MediaStore.Images.Media.DATE_ADDED)
                    )
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                    putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                }, null)
        } else {
            contentResolver.query(queryUri, projection, selection, null, sortOrder)
        }

        cursor?.use {

            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val mediaTypeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (it.moveToNext()) {


                val dataId = it.getLong(idColumn)
                val mediaType = it.getInt(mediaTypeColumn)


                val mediaUri = when (mediaType) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, dataId)
                    }
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataId)
                    }
                    else -> null
                }

                val thumbnail = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.contentResolver.loadThumbnail(mediaUri!!, Size(150, 150), null)
                    } else {
                        when (mediaType) {
                            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                                MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, dataId, MediaStore.Images.Thumbnails.MINI_KIND, null)
                            }

                            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                                MediaStore.Video.Thumbnails.getThumbnail(context.contentResolver, dataId, MediaStore.Images.Thumbnails.MINI_KIND, null)
                            }
                            else -> Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888)
                        }
                    }
                }catch (ex:Exception){
                    Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888)
                }

                mediaUri?.let { uri ->
                    val dateAdded = it.getLong(dateAddedColumn)
                    mixedList.add(MediaModel(uri,thumbnail,dataId.toString(), getMediaType(mediaType), dateAdded))
                }
            }
            it.close()
        }
        mixedList.sortByDescending { it.dateAdded }

        return mixedList
    }


    private fun getMediaType(mediaType: Int): MediaType {
        return when (mediaType) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.IMAGE
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.VIDEO
            else -> throw IllegalArgumentException("Unsupported media type: $mediaType")
        }
    }

}