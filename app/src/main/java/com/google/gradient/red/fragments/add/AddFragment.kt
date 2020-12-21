package com.google.gradient.red.fragments.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.models.Mood
import com.google.gradient.red.data.viewmodel.JournalViewModel
import com.google.gradient.red.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

class addFragment : Fragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private val mJournalViewModel: JournalViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    var currentDate: String? = null

    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // set menu
        setHasOptionsMenu(true)

        view.mood_spinner.onItemSelectedListener = mSharedViewModel.listener

        // Set date and time for currentDate
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm a")
        currentDate = sdf.format(Date())

        // Opens gallery when image button clicked, gets image
        view.image_et.setOnClickListener {
            readStorageTask()
        }

        return view
    }

    // Creates check mark at the top of the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    // If the check mark is clicked, entry gets added
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add) {
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    // uses below function to check if text is empty, and gets values from add fragment
    private fun insertDataToDb() {
        val mTitle = title_et.text.toString()
        val mMood = mood_spinner.selectedItem.toString()
        val mDescription = description_et.text.toString()
        val mDate = currentDate.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)
        if(validation) {
            val newData = JournalData(
                0,
                mTitle,
                parseMood(mMood),
                mDescription,
                mDate
            )
            mJournalViewModel.insertData(newData)
            Toast.makeText(requireContext(), "New entry added!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment) // Jetpack Navigation
        } else {
            Toast.makeText(requireContext(), "You have some empty fields.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gets title and description as parameters and checks if either is empty
    private fun verifyDataFromUser(title: String, description: String): Boolean {
        return if(TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            false
        } else !(title.isEmpty() || description.isEmpty())
    }

    // Reads the mood spinner and assigns a mood value.
    private fun parseMood(mood: String): Mood {
        return when(mood) {
            "Happy" -> {Mood.HAPPY}
            "Okay" -> {Mood.OKAY}
            "Upset" -> {Mood.UPSET}
            else -> {Mood.HAPPY}
        }
    }

    // SECTION FOR PERMISSIONS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, requireActivity())
    }

    // These two functions check whether the user has permissions using EasyPermissions
    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // Checks if the user has permissions and asks for them if not
    private fun readStorageTask() {
        if (hasReadStoragePerm()) {

        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                "This app needs access to your storage to be able to pick pictures.",
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    // Uses intent to pick image from gallery
    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && requestCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
                        var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        preview_image.setImageBitmap(bitmap)
                        preview_image.visibility = View.VISIBLE
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRationaleDenied(requestCode: Int) {
        // Doesn't do anything, closes rationale
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // Continues
    }

    override fun onRationaleAccepted(requestCode: Int) {
        // Continues
    }
}