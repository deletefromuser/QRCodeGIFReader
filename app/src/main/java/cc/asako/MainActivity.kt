package cc.asako

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cc.asako.ui.theme.QRCodeGIFReaderTheme
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject
import java.io.File

class MainActivity : ComponentActivity() {
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
        setContentView(R.layout.activity_main)
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // 处理扫描结果
//        if (resultCode == RESULT_OK) {
//            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//            if (result != null) {
//                // 获取二维码内容
//                val content = result.contents
//
//                // 解析二维码内容
//                val json = JSONObject(content)
//                val name = json.getString("name")
//                val count = json.getInt("count")
//                val index = json.getInt("index")
//                val data = json.getString("data")
//
//                // 判断是否已经扫描完成
//                if (index >= count - 1) {
//                    // 拼接所有数据
//                    var allData = ""
//                    for (i in 0 until count) {
//                        allData += data
//                    }
//
//                    // 将数据转换为二进制
//                    val bytes = Base64.decode(allData, Base64.DEFAULT)
//
//                    // 将数据写入文件
//                    val file = File(getExternalFilesDir(null), name)
//                    file.writeBytes(bytes)
//
//                    // 下载文件
//                    val uri = Uri.fromFile(file)
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.setDataAndType(uri, "application/octet-stream")
//                    startActivity(intent)
//
//                    // 停止扫描
////                    IntentIntegrator.clearScanner()
//                } else {
//                    // 继续扫描
//                    IntentIntegrator(this).initiateScan()
//                }
//            }
//        }
//    }

    fun startScanActivity(view: View) {
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QRCodeGIFReaderTheme {
        Greeting("Android")
    }
}