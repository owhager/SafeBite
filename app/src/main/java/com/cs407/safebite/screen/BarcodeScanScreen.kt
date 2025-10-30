package com.cs407.safebite.screen

import android.Manifest
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.cs407.safebite.component.UnifiedTopBar

@Composable
fun BarcodeScanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToResults: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    var showManual by remember { mutableStateOf(false) }
    var barcode by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }

    var hasPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
        if (!granted) Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
    }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    fun submitManualIfValid() {
        val valid = barcode.isNotBlank()
        inputError = !valid
        if (valid) {
            showManual = false
            onNavigateToResults()
        }
    }

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Scan Barcode",
                onNavigateBack = onNavigateBack,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToRecents = onNavigateToRecents,
                onNavigateToInput = onNavigateToInput,
                onNavigateToScan = onNavigateToScan
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Camera Preview
            if (hasPermission) {
                CameraPreviewWithBarcode(
                    onBarcodeDetected = { code ->
                        if (!showConfirm) {
                            barcode = code
                            showConfirm = true
                        }
                    }
                )
            } else {
                Text(
                    text = "Requesting camera permission...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        if (showConfirm) {
            Dialog(onDismissRequest = { showConfirm = false }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 280.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Is this item correct?",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .heightIn(min = 120.dp)
                                .border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = barcode.ifEmpty { "No barcode detected" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = {
                                    showConfirm = false
                                    onNavigateToResults()
                                }
                            ) { Text("Yes") }

                            TextButton(
                                onClick = {
                                    showConfirm = false
                                    showManual = true
                                }
                            ) { Text("No") }
                        }
                    }
                }
            }
        }

        if (showManual) {
            Dialog(onDismissRequest = { showManual = false }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 280.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Scan Again or manually input number?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Scan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showManual = false
                                    barcode = ""
                                    inputError = false
                                }
                        )

                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Input",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = barcode,
                                onValueChange = { raw ->
                                    val digitsOnly = raw.filter { it.isDigit() }
                                    barcode = digitsOnly
                                    if (inputError && digitsOnly.isNotBlank()) inputError = false
                                },
                                placeholder = { Text("1234567890") },
                                singleLine = true,
                                isError = inputError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { submitManualIfValid() }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (inputError) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Please enter a number.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { submitManualIfValid() }) {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewWithBarcode(onBarcodeDetected: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply {
                        setAnalyzer(
                            ContextCompat.getMainExecutor(ctx),
                            BarcodeAnalyzer(onBarcodeDetected)
                        )
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

private class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var lastDetected = ""

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close(); return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { code ->
                        if (code != lastDetected) {
                            lastDetected = code
                            onBarcodeDetected(code)
                        }
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}
