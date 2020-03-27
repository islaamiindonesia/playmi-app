package id.islaami.playmi.ui.setting.report

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.Observer
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.setting.SettingViewModel
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.setTextChangedListener
import id.islaami.playmi.util.ui.setupToolbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import id.islaami.playmi.util.ui.setVisibilityToGone
import id.islaami.playmi.util.ui.setVisibilityToVisible
import id.islaami.playmi.util.ui.showAlertDialog
import id.islaami.playmi.util.ui.showSnackbar
import kotlinx.android.synthetic.main.report_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportActivity : BaseActivity() {
    private val viewModel: SettingViewModel by viewModel()

    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference

    var selectedImage: Uri? = null
    var downloadUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_activity)

        setupToolbar(toolbar)

        storage = Firebase.storage

        // Create a storage reference from our app
        storageRef = storage.reference

        viewModel.initReportActivity()
        observeAddReport()
        observeReportValidation()

        btnSend.setOnClickListener {
            progressBar.setVisibilityToVisible()
            btnSend.setVisibilityToGone()
            val ref = storageRef.child("report_image/${System.currentTimeMillis()}")
            selectedImage?.let { imageUri ->
                ref.putFile(imageUri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }

                        return@continueWithTask ref.downloadUrl
                    }
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            downloadUri = task.result

                            viewModel.addReport(
                                desc = etReport.text.toString(),
                                imageUrl = downloadUri.toString()
                            )
                        }
                    }
            }
        }

        btnUpload.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, GALLERY_CODE)
        }

        etReport.setTextChangedListener(
            layoutEtReport,
            errorMessage = "Anda belum memberikan penjelasan masalah"
        ) {
            viewModel.report = it
            viewModel.updateReport()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GALLERY_CODE -> {
                    selectedImage = data?.data
                    uploadedImage.apply {
                        setVisibilityToVisible()
                        setImageURI(selectedImage)
                    }
                    viewModel.imageUrl = selectedImage.toString()
                    viewModel.updateReport()
                }
            }
        }
    }

    private fun observeReportValidation() {
        viewModel.reportValid.observe(this, Observer { result ->
            btnSend.isEnabled = result
        })
    }

    private fun observeAddReport() {
        viewModel.reportResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnSend.setVisibilityToGone()
                }
                SUCCESS -> {
                    btnSend.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()

                    showAlertDialog(
                        message = "Terima kasih atas laporan Anda untuk membantu Playmi menjadi lebih baik.",
                        btnText = "OK",
                        btnCallback = { dialog ->
                            dialog.dismiss()
                            onBackPressed()
                        }
                    )
                }
                ERROR -> {
                    btnSend.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()
                    handleApiError(result.message) { showSnackbar(it) }
                }
            }
        })
    }

    companion object {
        const val GALLERY_CODE = 1

        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, ReportActivity::class.java))
        }
    }
}
