package com.phoint.facebooksimulation.ui.showImagePhoneApp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentShowImageBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ShowImageFragment : BaseFragment<FragmentShowImageBinding, ShowImageViewModel>() {
    private var adapter: ImageAdapter? = null

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Bạn đã cấp quyền", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Bạn từ chối cấp quyền", Toast.LENGTH_SHORT).show()
            }
        }

    private val startForCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl = result.data?.data
                handleCameraImageResult(imageUrl)
            }
        }

    private fun handleCameraImageResult(imageUrl: Uri?) {
        try {
            findNavController().navigate(
                R.id.action_showImageFragment_to_photoPreviewFragment, bundleOf(
                    Pair("imageUrl", imageUrl.toString())
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val startActivityForImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl = result.data?.data
                handleImageResult(imageUrl)
            }
        }

    private fun handleImageResult(imageUrl: Uri?) {
        try {
            findNavController().navigate(
                R.id.action_showImageFragment_to_photoPreviewFragment, bundleOf(
                    Pair("imageUrl", imageUrl.toString())
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_show_image
    }

    override fun viewModelClass(): Class<ShowImageViewModel> {
        return ShowImageViewModel::class.java
    }

    override fun initView() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Không làm gì khi nút Back được nhấn
        }

        binding.svImage.setQuery("Văn bản cần hiển thị", false)
        binding.svImage.queryHint = "Gợi ý tìm kiếm"

        adapter = ImageAdapter(requireContext())
        binding.rcvImagePhone.adapter = adapter

        if (checkPermission()) {
            val imageList = getImagesFromMediaStore()
            adapter?.setImageList(imageList)
        } else {
            requestPermission()
        }

        adapter?.itemOnClick = {
            findNavController().navigate(
                R.id.action_showImageFragment_to_photoPreviewFragment, bundleOf(
                    Pair("imageUrl", it.toString())
                )
            )
        }

        binding.btnImagePhone.setOnSingClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForImage.launch(intent)
        }

        binding.abShowImage.clickAccessToCamera {
            val permission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startForCameraResult.launch(intent)
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        binding.abShowImage.onSingClickListenerLeft {
            findNavController().popBackStack()
        }
    }

    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission),
            READ_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val imageList = getImagesFromMediaStore()
                adapter?.setImageList(imageList)
            }
        }
    }

    private fun getImagesFromMediaStore(): List<Uri> {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                imageList.add(contentUri)
            }
        }
        return imageList
    }
}
