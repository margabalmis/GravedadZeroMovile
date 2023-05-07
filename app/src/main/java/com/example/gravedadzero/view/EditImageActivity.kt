package com.example.gravedadzero.view


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.gravedadzero.R
import com.example.gravedadzero.adapter.BaseActivity
import com.example.gravedadzero.utils.FileSaveHelper
import com.example.gravedadzero.utils.PropertiesBSFragment
import com.example.gravedadzero.utils.ShapeBSFragment
import com.example.gravedadzero.view.AddBulderFragment.Companion.fotoEditada
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.ViewType
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import kotlinx.coroutines.launch
import java.io.IOException


@Suppress("DEPRECATION")
class EditImageActivity : BaseActivity(), OnPhotoEditorListener, View.OnClickListener,
    PropertiesBSFragment.Properties, ShapeBSFragment.Properties {

    lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mPhotoEditorView: PhotoEditorView
    private lateinit var mPropertiesBSFragment: PropertiesBSFragment
    private lateinit var mShapeBSFragment: ShapeBSFragment
    private lateinit var mShapeBuilder: ShapeBuilder
    private lateinit var mRvTools: ImageView
    private lateinit var mRootView: ConstraintLayout


    private lateinit var mSaveFileHelper: FileSaveHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()
        handleIntentImage(mPhotoEditorView.source)

        mPropertiesBSFragment = PropertiesBSFragment()
        mShapeBSFragment = ShapeBSFragment()
        mPropertiesBSFragment.setPropertiesChangeListener(this)
        mShapeBSFragment.setPropertiesChangeListener(this)
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView).build()
        mPhotoEditor.setOnPhotoEditorListener(this)
        mSaveFileHelper = FileSaveHelper(this)
    }

    private fun handleIntentImage(source: ImageView) {
        if (intent == null) {
            return
        }
        when (intent.action) {
            Intent.ACTION_EDIT, ACTION_NEXTGEN_EDIT -> {
                try {
                    val uri = intent.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            else -> {
                val intentType = intent.type
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri = intent.data
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    private fun initViews() {
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mRvTools = findViewById(R.id.shape)
        mRootView = findViewById(R.id.rootView)

        mRvTools.setOnClickListener {
            mPhotoEditor.setBrushDrawingMode(true)
            mShapeBuilder = ShapeBuilder()
            mPhotoEditor.setShape(mShapeBuilder)
            showBottomSheetDialogFragment(mShapeBSFragment)
        }
        val imgUndo: ImageView = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)

        val imgRedo: ImageView = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)

        val imgSave: Button = findViewById(R.id.guardarBulderEditor)
        imgSave.setOnClickListener(this)

        val imgCamera: ImageView = findViewById(R.id.imgCamera)
        imgCamera.setOnClickListener(this)

        val imgGallery: ImageView = findViewById(R.id.imgGallery)
        imgGallery.setOnClickListener(this)

    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType?) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onTouchSourceImage(event: MotionEvent?) {
        Log.d(TAG, "onTouchView() called with: event = [$event]")
    }

    @SuppressLint("NonConstantResourceId", "MissingPermission")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgUndo -> mPhotoEditor.undo()
            R.id.imgRedo -> mPhotoEditor.redo()
            R.id.guardarBulderEditor -> saveImage()
            R.id.imgCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            R.id.imgGallery -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST)
            }
        }
    }

    private fun goMainActivity() {
        val intent = Intent(baseContext, MainActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "AddFragment")
            //putExtra("IMAGEN_EDITADA", bitmap)
        }
        startActivity(intent)
    }

    private fun saveImage() {
        lifecycleScope.launch {
            val bitmap = mPhotoEditor.saveAsBitmap()
            fotoEditada = bitmap
            goMainActivity( )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor.clearAllViews()
                    val photo = data?.extras?.get("data") as Bitmap?
                    val scaledBitmap = Bitmap.createScaledBitmap(photo!!, photo.width*9, photo.height*9, true)
                    mPhotoEditorView.source.setImageBitmap(scaledBitmap)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor.clearAllViews()
                    val uri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver, uri
                    )
                    mPhotoEditorView.source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode))
    }
    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity))
    }
    override fun onShapeSizeChanged(shapeSize: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize.toFloat()))
    }
    override fun onShapePicked(shapeType: ShapeType) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType))
    }
    @SuppressLint("MissingPermission")
    override fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        if (isGranted) {
            saveImage()
        }
    }
    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }
    companion object {

        private const val TAG = "EditImageActivity"
        private const val CAMERA_REQUEST = 52
        private const val PICK_REQUEST = 53
        const val ACTION_NEXTGEN_EDIT = "action_nextgen_edit"
    }
}