package id.islaami.playmi2021.ui.setting.report

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseSpecialActivity
import id.islaami.playmi2021.ui.setting.SettingViewModel
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.handleApiError
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.report_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportActivity : BaseSpecialActivity() {
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

        etReport.addTextChangedListener {
            viewModel.report = it.toString()
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

                    ReportDialogFragment.show(
                        fragmentManager = supportFragmentManager,
                        title = "Terima kasih atas laporan Anda untuk membantu islaami menjadi lebih baik.",
                        text = "Kami tidak dapat melihat dan menanggapi setiap laporan, namun beberapa laporan membantu kami meningkatkan layanan untuk semua orang.",
                        okCallback = {
                            uploadedImage.setVisibilityToGone()
                            btnUpload.setVisibilityToVisible()
                            etReport.setText("")
                        },
                        outsideTouchCallback = {
                            uploadedImage.setVisibilityToGone()
                            btnUpload.setVisibilityToVisible()
                            etReport.setText("")
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
