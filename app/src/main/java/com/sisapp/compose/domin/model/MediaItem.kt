package com.sisapp.compose.domin.model

import android.graphics.Bitmap
import android.net.Uri
import com.sisapp.compose.data.model.MediaType
import com.sisapp.compose.presention.utils.doGamma
import com.sisapp.compose.presention.utils.doGreyScale
import com.sisapp.compose.presention.utils.doInvert

class MediaItem(
    val uri: Uri,
    val thumbnail: Bitmap,
    val id: String,
    val type: MediaType
) {
    var indexFilter: Int = 0

    fun changeFilter(): Bitmap {
        indexFilter++
        indexFilter %= 4
        return getThumbnailFiltered()
    }

    fun getThumbnailFiltered(): Bitmap {
        return when (indexFilter) {
            1 -> thumbnail.doGamma()
            2 -> thumbnail.doInvert()
            3 -> thumbnail.doGreyScale()
            else -> thumbnail
        }
    }

}
