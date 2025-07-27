package cc.asako;

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cc.asako.ui.theme.QRCodeGIFReaderTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

class ScanActivity : ComponentActivity(), SurfaceHolder.Callback, Camera.PreviewCallback {

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val TAG = "ScanActivity"

    private var camera: Camera? = null
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var multiFormatReader: MultiFormatReader
    private var isScanning = false
//    private var fileName: String = ""
//    private var totalCount: Int = 0
    private var currentIndex: Int = 0
    private var fileData: StringBuilder = StringBuilder()

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRCodeGIFReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setContentView(R.layout.scan_activity)

        val surfaceView: SurfaceView = findViewById(R.id.camera_preview)
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        multiFormatReader = MultiFormatReader()

        // 设置解码器支持的编码格式
        val hints: MutableMap<DecodeHintType, Any> = HashMap()
        hints[DecodeHintType.POSSIBLE_FORMATS] = listOf(BarcodeFormat.QR_CODE)
        multiFormatReader.setHints(hints)

//        fileName = "input.txt"
//        totalCount = 2
        currentIndex = 0
        fileData = StringBuilder()
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startPreview()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        // 不需要处理
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        stopPreview()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPreview()
            } else {
                // 没有相机权限，处理逻辑
            }
        }
    }

    private fun startPreview() {
        try {
            camera = Camera.open()
            camera?.setDisplayOrientation(90)
            camera?.setPreviewDisplay(surfaceHolder)
            camera?.setPreviewCallback(this)
            camera?.startPreview()
            isScanning = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start preview: ${e.message}")
        }
    }

    private fun stopPreview() {
        camera?.let {
            isScanning = false
            it.setPreviewCallback(null)
            it.stopPreview()
            it.release()
            camera = null
        }
    }

    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        if (isScanning) {
            processFrame(bytes, camera)
        }
    }

    private fun processFrame(bytes: ByteArray, camera: Camera) {
        val size: Camera.Size = camera.parameters.previewSize
        val width: Int = size.width
        val height: Int = size.height

        // 创建二进制位图对象
        val bitmap = BinaryBitmap(
            HybridBinarizer(
                PlanarYUVLuminanceSource(
                    bytes,
                    width,
                    height,
                    0,
                    0,
                    width,
                    height,
                    false
                )
            )
        )

        try {
            // 解码二进制位图，获取QR码数据
            val result: Result = multiFormatReader.decodeWithState(bitmap)
            val qrData: String = result.text

            // 解析JSON数据
            try {
                Log.e(TAG, "qr data : ${qrData}")
                val json = JSONObject(qrData)
                val name: String = String(
                    Base64.decode(
                        json.getString("name").toByteArray(Charsets.UTF_8),
                        Base64.DEFAULT
                    ), StandardCharsets.UTF_8
                )
                val count: Int = json.getInt("count")
                val index: Int = json.getInt("index")
                val data: String = json.getString("data")

                // 检查解码后的QR码数据
                checkQRCodeData(name, count, index, data)
            } catch (e: JSONException) {
                Log.e(TAG, "Failed to parse JSON data: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode QR code: ${e.message}")
        }
    }

    private fun checkQRCodeData(name: String, count: Int, index: Int, data: String) {
        Log.e(TAG, "currentIndex=${currentIndex}, count=${count}")
        Log.e(TAG, "name=${name}, count=${count}, index=${index}, data=${data}")
        if (name.isNotEmpty() && count > 0 && index >= 0 && data.isNotEmpty()) {
            if (index == currentIndex) {
                fileData.append(data)
                Toast.makeText(this.applicationContext, "[${name}] - progress: ${index + 1}/${count}", Toast.LENGTH_SHORT).show();

                currentIndex++
                Log.e(TAG, "currentIndex=${currentIndex}, count=${count}")
                if (currentIndex == count) {
                    saveFile(name)
                    Log.i(TAG, "File saved successfully")
                    Toast.makeText(this.applicationContext, "${name} downloaded", Toast.LENGTH_LONG).show();
                    stopPreview()
                    finish()
                }
            } else {
                Log.e(TAG, "Invalid QR code data")
            }
        } else {
            Log.e(TAG, "Invalid QR code data")
        }
    }

    private fun saveFile(name: String) {
        try {
            val decodedData: ByteArray = Base64.decode(fileData.toString(), Base64.DEFAULT)

            // 保存文件
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, name)
            try {
                FileOutputStream(file).use { fos ->
                    fos.write(decodedData)
                    fos.flush()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save file: ${e.message}")
            }

            Log.i(TAG, "File saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save file: ${e.message}")
        }
    }
}
