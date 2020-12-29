package com.google.gradient.red.fragments.update

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.viewmodel.JournalViewModel
import com.google.gradient.red.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class updateFragment : Fragment() {
    var bitmap: Bitmap? = null

    // Variables used later
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mJournalViewModel: JournalViewModel by viewModels()
    private val args by navArgs<updateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        // Set menu
        setHasOptionsMenu(true)

        // Get current bitmap if it exists and assign it to the bitmap var
        if (args.currentItem.image != null) {
            args.currentItem.image.also {
                if (it != null) {
                    bitmap = it
                }
            }
        }

        view.current_title_et.setText(args.currentItem.title)
        view.current_description_et.setText(args.currentItem.description)
        view.current_mood_spinner.setSelection(mSharedViewModel.parseMood(args.currentItem.mood))
        view.current_mood_spinner.onItemSelectedListener = mSharedViewModel.listener

        // Opens gallery when image button clicked, gets image
        view.current_image_et.setOnClickListener {
            readStorageTask()
            //Intent to pick image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
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
                            val bitmapUnprocessed = BitmapFactory.decodeFile(file.absolutePath)
                            bitmap = resizedBitmap(bitmapUnprocessed)
                            current_image_et.text = "Image picked!"
                        }
                    }
                }
            }
        }
    }

    // Resizes bitmap height to 1440 (2k res) and width based on dimensions
    fun resizedBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var bitmapResized: Bitmap? = null

        Log.v("Image size return", "width: {$width}, height: {$height}")

        val scale: Double = 1080 / bitmap.height.toDouble()
        val scaleWidth: Double = scale * width
        val scaleHeight: Double = scale * height

        bitmapResized = ThumbnailUtils.extractThumbnail(
            bitmap, scaleWidth.toInt(),
            scaleHeight.toInt()
        )

        Log.v("Value on the right should return 1080", bitmapResized.height.toString())
        return bitmapResized
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

    // This function checks whether the user has permissions using EasyPermissions
    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    // Creates check mark at the top of the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    // Controls buttons in menu, calls update and delete functions when needed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    // Called when updating save entry (called when check clicked)
    private fun updateItem() {
        val title = current_title_et.text.toString()
        val description = current_description_et.text.toString()
        val getMood = current_mood_spinner.selectedItem.toString()
        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        val editedDate = args.currentItem.date

        // Updates current item
        if (validation) {
            val updatedItem = JournalData(
                args.currentItem.id,
                title,
                mSharedViewModel.parseMood(getMood),
                description,
                editedDate,
                bitmap
            )
            mJournalViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(), "Entry successfully edited!", Toast.LENGTH_SHORT).show()

            // Navigates back to home/list fragment once finished
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            } else {
                Toast.makeText(requireContext(), "You have some empty fields.", Toast.LENGTH_SHORT).show()
            }
    }

    // Opens an alert dialog asking if they want deletion, deletes if positive
    private fun confirmItemRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mJournalViewModel.deleteItem(args.currentItem)
            Toast.makeText(
                requireContext(),
                "Your entry was successfully removed!",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Entry deletion")
        builder.setMessage("Are you sure you want to delete this journal entry?")
        builder.create().show()
    }
}