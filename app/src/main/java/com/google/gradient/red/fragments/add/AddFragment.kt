package com.google.gradient.red.fragments.add

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.OpenableColumns
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class addFragment : Fragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    lateinit var bitmap: Bitmap

    private val mJournalViewModel: JournalViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    var currentDate: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var bitmap: Bitmap? = null
    }

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
            //Intent to pick image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        return view
    }

    // Handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {

            // Converts image URI to bitmap
            if (data != null && data.data != null) {
                val uri = data.data!!
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
                cursor?.use { c ->
                    val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (c.moveToFirst()) {
                        val name = c.getString(nameIndex)
                        inputStream?.let { inputStream ->
                            // create same file with same name
                            val file = File(requireContext().cacheDir, name)
                            val os = file.outputStream()
                            os.use {
                                inputStream.copyTo(it)
                            }
                            bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            preview_image.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
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
                mDate,
                bitmap
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
            "Happy" -> {
                Mood.HAPPY
            }
            "Okay" -> {
                Mood.OKAY
            }
            "Upset" -> {
                Mood.UPSET
            }
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

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireActivity()
        )
    }

    // This function checks whether the user has permissions using EasyPermissions
    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    // Checks if the user has permissions and asks for them if not
    private fun readStorageTask() {
        if (hasReadStoragePerm()) {

        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                "This app needs access to your storage to be able to pick pictures.",
                1001,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
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