package com.sisapp.compose.presention.utils

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.min
import kotlin.math.pow


fun Bitmap.doGamma(): Bitmap {
    val bmOut = Bitmap.createBitmap(
        this.width, this.height,
        this.config
    )
    val width = this.width
    val height = this.height
    var A: Int
    var R: Int
    var G: Int
    var B: Int
    var pixel: Int
    val MAX_SIZE = 256
    val MAX_VALUE_DBL = 255.0
    val MAX_VALUE_INT = 255
    val REVERSE = 1.0
    val gammaR = IntArray(MAX_SIZE)
    val gammaG = IntArray(MAX_SIZE)
    val gammaB = IntArray(MAX_SIZE)
    for (i in 0 until MAX_SIZE) {
        gammaR[i] = min(
            MAX_VALUE_INT,
            (MAX_VALUE_DBL * (i / MAX_VALUE_DBL).pow(REVERSE / 12.0) + 0.5).toInt()
        )
        gammaG[i] = min(
            MAX_VALUE_INT,
            (MAX_VALUE_DBL * (i / MAX_VALUE_DBL).pow(REVERSE / 50.0) + 0.5).toInt()
        )
        gammaB[i] = min(
            MAX_VALUE_INT,
            (MAX_VALUE_DBL * (i / MAX_VALUE_DBL).pow(REVERSE / 5.0) + 0.5).toInt()
        )
    }
    for (x in 0 until width) {
        for (y in 0 until height) {
            pixel = this.getPixel(x, y)
            A = Color.alpha(pixel)
            R = gammaR[Color.red(pixel)]
            G = gammaG[Color.green(pixel)]
            B = gammaB[Color.blue(pixel)]
            bmOut.setPixel(x, y, Color.argb(A, R, G, B))
        }
    }
    return bmOut
}


fun Bitmap.doInvert(): Bitmap {
    val resultingBitmap = Bitmap.createBitmap(
        this.width, this.height,
        this.config
    )
    var a: Int
    var r: Int
    var g: Int
    var b: Int
    var pixelColor: Int
    val height = this.height
    val width = this.width
    for (y in 0 until height) {
        for (x in 0 until width) {
            pixelColor = this.getPixel(x, y)
            a = Color.alpha(pixelColor)
            r = 255 - Color.red(pixelColor)
            g = 255 - Color.green(pixelColor)
            b = 255 - Color.blue(pixelColor)
            resultingBitmap.setPixel(x, y, Color.argb(a, r, g, b))
        }
    }
    return resultingBitmap
}

fun Bitmap.doGreyScale(): Bitmap {
    val GS_RED = 0.299
    val GS_GREEN = 0.587
    val GS_BLUE = 0.114
    val resultingBitmap = Bitmap.createBitmap(
        this.width, this.height,
        this.config
    )
    var a: Int
    var r: Int
    var g: Int
    var b: Int
    var pixel: Int
    val width = this.width
    val height = this.height
    for (x in 0 until width) {
        for (y in 0 until height) {
            pixel = this.getPixel(x, y)
            a = Color.alpha(pixel)
            r = Color.red(pixel)
            g = Color.green(pixel)
            b = Color.blue(pixel)
            b = (GS_RED * r + GS_GREEN * g + GS_BLUE * b).toInt()
            g = b
            r = g
            resultingBitmap.setPixel(x, y, Color.argb(a, r, g, b))
        }
    }
    return resultingBitmap
}
