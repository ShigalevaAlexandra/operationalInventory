package shigaleva.av.operationalinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import shigaleva.av.operationalinventory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /*--declaration of global variables---------------------------------------------------------------------------------------*/

    // Search for elements and record their state in variables
    private lateinit var binding: ActivityMainBinding

    private var codeWarehouse = ""

    /*--startup screen--------------------------------------------------------------------------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // press the "Scan QR Code Warehouse" button
        binding.buttonActivityMain.setOnClickListener {
            scanCodeWarehouse()
        }
    }

    /*--launching the camera to read the QR code------------------------------------------------------------------------------*/

    // receiving warehouse code using QR code
    private val scanCodeWarehouseLauncher = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null) Toast.makeText(this, "Данный QR-код недействителен", Toast.LENGTH_SHORT).show()
        else {
            codeWarehouse = getCodeWarehouse(result)

            // saving scanned warehouse code to default
            val intent = Intent(applicationContext, DataWarehouseActivity::class.java)
            intent.putExtra("CodeWarehouse", codeWarehouse)

            startActivity(intent)
        }
    }

    // creating a function to customize the display scan launcher
    private fun scanCodeWarehouse() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Разместите QR-код внутри рамки")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        scanCodeWarehouseLauncher.launch(options)
    }

    // creating a function to cut the warehouse code
    // from the received string after reading the QR code
    private fun getCodeWarehouse(result: ScanIntentResult): String {
        val codeWarehouse = result.contents.toString()
        val matchResult = Regex("""[0-9]+""").find(codeWarehouse)

        return matchResult?.value ?: "не распознан"
    }

    /*--saving the values of active variables-------------------------------------------------------------------------------*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("CODE_WAREHOUSE", codeWarehouse)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        codeWarehouse = savedInstanceState.getString("CODE_WAREHOUSE").toString()
    }
}