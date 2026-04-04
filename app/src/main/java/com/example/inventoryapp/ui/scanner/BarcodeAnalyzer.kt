package com.example.inventoryapp.ui.scanner

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
class BarcodeAnalyzer(
    private val barcodeScanner: BarcodeScanner,
    private val isActiveProvider: () -> Boolean,
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        if (!isActiveProvider()) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes
                    .firstOrNull { it.format != Barcode.FORMAT_UNKNOWN }
                    ?.rawValue
                    ?.let { onBarcodeDetected(it) }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
