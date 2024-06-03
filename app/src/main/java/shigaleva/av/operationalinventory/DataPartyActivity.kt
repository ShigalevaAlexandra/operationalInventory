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
import shigaleva.av.operationalinventory.databinding.ActivityDataPartyBinding

import shigaleva.av.operationalinventory.requests.createSoapRequestGetPartyInfo

class DataPartyActivity : AppCompatActivity() {

    /*--declaration of global variables---------------------------------------------------------------------------------------*/

    // Search for elements and record their state in variables
    private lateinit var binding: ActivityDataPartyBinding

    private var codeWarehouse = ""

    private var codeParty = ""
    private var nameParty = ""
    private var quantityParty = ""
    private var quantityFreeParty = ""
    private var quantityReserveParty = ""

    /*--startup screen--------------------------------------------------------------------------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDataPartyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /// creating a new thread
        val runnable = Runnable {

            // receiving a scanned warehouse code and name warehouse to default
            codeWarehouse = intent.extras?.getString("CodeWarehouse").toString()

            codeParty = intent.extras?.getString("CodeParty").toString()
            nameParty = createSoapRequestGetPartyInfo(codeWarehouse, codeParty)[0]
            quantityParty = createSoapRequestGetPartyInfo(codeWarehouse, codeParty)[1]
            quantityFreeParty = createSoapRequestGetPartyInfo(codeWarehouse, codeParty)[2]
            quantityReserveParty = createSoapRequestGetPartyInfo(codeWarehouse, codeParty)[3]

            // recording the warehouse code and name warehouse in the form
            binding.textBoxCodeParty.post {
                binding.textBoxCodeParty.text = codeParty
            }

            binding.textBoxNameParty.post {
                binding.textBoxNameParty.text = nameParty
            }

            binding.textBoxQuantityParty.post {
                binding.textBoxQuantityParty.text = quantityParty
            }

            binding.textBoxQuantityFreeParty.post {
                binding.textBoxQuantityFreeParty.text = quantityFreeParty
            }

            binding.textBoxQuantityReserveParty.post {
                binding.textBoxQuantityReserveParty.text = quantityReserveParty
            }
        }

        val thread = Thread(runnable)
        thread.start()

        // press the "Scan QR Code Party" button
        binding.buttonActivityDataParty.setOnClickListener {
            scanCodeParty()
        }

        // press the "exit" button
        binding.buttonExit.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /*--launching the camera to read the QR code------------------------------------------------------------------------------*/

    // receiving party code using QR code
    private val scanCodePartyLauncher = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null) Toast.makeText(this, "Данный QR-код недействителен", Toast.LENGTH_SHORT).show()
        else {
            codeWarehouse = intent.extras?.getString("CodeWarehouse").toString()
            codeParty = getCodeParty(result)

            // saving scanned party code to default
            val intent = Intent(applicationContext, DataPartyActivity::class.java)
            intent.putExtra("CodeWarehouse", codeWarehouse)
            intent.putExtra("CodeParty", codeParty)
            startActivity(intent)
        }
    }

    // creating a function to customize the display scan launcher
    private fun scanCodeParty() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Разместите QR-код внутри рамки")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        scanCodePartyLauncher.launch(options)
    }

    // creating a function to cut the warehouse code
    // from the received string after reading the QR code
    private fun getCodeParty(result: ScanIntentResult): String {
        val codeWarehouse = result.contents.toString()
        val matchResult = Regex("""([A-Z]+[0-9]+)""").find(codeWarehouse)

        return matchResult?.value ?: "не распознан"
    }

    /*--saving the values of active variables--------------------------------------------------------------------------------*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("CODE_WAREHOUSE", codeWarehouse)

        outState.putString("CODE_PARTY", codeParty)
        outState.putString("NAME_PARTY", nameParty)
        outState.putString("QUANTITY_PARTY", quantityParty)
        outState.putString("QUANTITY_FREE_PARTY", quantityFreeParty)
        outState.putString("QUANTITY_RESERVE_PARTY", quantityReserveParty)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        codeWarehouse = savedInstanceState.getString("CODE_WAREHOUSE").toString()

        codeParty = savedInstanceState.getString("CODE_PARTY").toString()
        nameParty = savedInstanceState.getString("NAME_PARTY").toString()
        quantityParty = savedInstanceState.getString("QUANTITY_PARTY").toString()
        quantityFreeParty = savedInstanceState.getString("QUANTITY_FREE_PARTY").toString()
        quantityReserveParty = savedInstanceState.getString("QUANTITY_RESERVE_PARTY").toString()
    }
}