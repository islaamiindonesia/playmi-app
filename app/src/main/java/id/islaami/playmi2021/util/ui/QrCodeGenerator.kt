package id.islaami.playmi2021.util.ui

/**
 * Created by Kemal Amru Ramadhan on 24/04/2019.
 */
/*
fun generateQrCodeImage(text: String, width: Int = 300, height: Int = 300): Bitmap? {
    val bitMatrix: BitMatrix
    try {
        bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, null)
    } catch (Illegalargumentexception: IllegalArgumentException) {
        return null
    }

    val bitMatrixWidth = bitMatrix.width
    val bitMatrixHeight = bitMatrix.height
    val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

    val colorWhite = -0x1
    val colorBlack = -0x1000000

    for (y in 0 until bitMatrixHeight) {
        val offset = y * bitMatrixWidth
        for (x in 0 until bitMatrixWidth) {
            pixels[offset + x] = if (bitMatrix.get(x, y)) colorBlack else colorWhite
        }
    }

    return Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444).apply {
        setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
    }
}*/
