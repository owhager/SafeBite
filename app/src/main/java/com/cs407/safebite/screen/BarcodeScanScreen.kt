package com.cs407.safebite.screen

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.viewmodel.BarcodeLookupViewModel

@Composable
fun BarcodeScanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToResults: () -> Unit,
    onLogout: () -> Unit,
    barcodeModel: BarcodeLookupViewModel
) {
    val barcodeState by barcodeModel.foodState.collectAsStateWithLifecycle()

    var showConfirm by remember { mutableStateOf(false) }
    var showManual by remember { mutableStateOf(false) }
    var barcode by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
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
                onNavigateToScan = onNavigateToScan,
                onLogout = onLogout
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
                            barcodeModel.getFoodData(code)
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
            val data = barcodeState.foodData
            Dialog(onDismissRequest = { showConfirm = false }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 300.dp, max = 400.dp)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Is this item correct?",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 140.dp)
                                .padding(horizontal = 4.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (barcodeState.isLoading) {
                                Text(
                                    text = "Loading...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            } else {
                                data?.food?.food_name?.let { foodName ->
                                    val brandName = data.food.brand_name ?: ""
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (brandName.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "Brand: ",
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = brandName,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                            Spacer(Modifier.height(12.dp))
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Item: ",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = foodName,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    showConfirm = false
                                    onNavigateToResults()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Yes",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    showConfirm = false
                                    showManual = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "No",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
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
    val context = LocalContext.current // Get context here

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
                            // Pass context to the analyzer
                            BarcodeAnalyzer(context, onBarcodeDetected)
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
    private val context: Context, // Add context to the constructor
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var lastDetected = ""

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val firstBarcodeValue = barcodes[0].rawValue ?: ""
                    // To avoid rapid-fire detections and vibrations
                    if (firstBarcodeValue.isNotEmpty() && firstBarcodeValue != lastDetected) {
                        lastDetected = firstBarcodeValue
                        onBarcodeDetected(firstBarcodeValue)
                        // VIBRATION ADDED HERE
                        triggerVibration()
                    }
                }
            }
            .addOnFailureListener {
                // You can add logging here if needed
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun triggerVibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Vibrate for 150 milliseconds with default intensity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150)
        }
    }
}
