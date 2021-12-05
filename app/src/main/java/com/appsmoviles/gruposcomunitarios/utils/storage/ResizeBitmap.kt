package com.appsmoviles.gruposcomunitarios.utils.storage

import android.graphics.Bitmap


fun resize(image: Bitmap, maxWidth: Int = 768, maxHeight: Int = 768): Bitmap {
    var image = image
    return if (maxHeight > 0 && maxWidth > 0) {
        val width = image.width
        val height = image.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > 1) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
        image
    } else {
        image
    }
}