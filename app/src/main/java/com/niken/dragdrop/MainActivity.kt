package com.niken.dragdrop

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.DragEvent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.niken.dragdrop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val maskDragMessage = "Mask Added"
    private val maskOn = "Bingo! Mask On"
    private val maskOff = "Mask off"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        attachViewDragListener()
        binding.maskDropArea.setOnDragListener(maskDragListener)

    }


    private val maskDragListener = View.OnDragListener { view, dragEvent ->

        //2
        val draggableItem = dragEvent.localState as View

        //3
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                binding.maskDropArea.alpha = 0.3f
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                binding.maskDropArea.alpha = 1.0f
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                binding.maskDropArea.alpha = 1.0f
                if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    val draggedData = dragEvent.clipData.getItemAt(0).text
                    //
                }

                draggableItem.x = dragEvent.x - (draggableItem.width / 2)

                draggableItem.y = dragEvent.y - (draggableItem.height / 2)


                val parent = draggableItem.parent as ConstraintLayout

                parent.removeView(draggableItem)

                val dropArea = view as ConstraintLayout

                dropArea.addView(draggableItem)

                checkIfMaskIsOnFace(dragEvent)
                //3
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun checkIfMaskIsOnFace(dragEvent: DragEvent) {
        //1
        val faceXStart = binding.faceArea.x
        val faceYStart = binding.faceArea.y

        //2
        val faceXEnd = faceXStart + binding.faceArea.width
        val faceYEnd = faceYStart + binding.faceArea.height
        //3
        val toastMsg = if (dragEvent.x in faceXStart..faceXEnd && dragEvent.y in faceYStart..faceYEnd){
            maskOn
        } else {
            maskOff
        }
        //4
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show()
    }

    private fun attachViewDragListener() {
        binding.mask.setOnLongClickListener {
            val item = ClipData.Item(maskDragMessage)
            val dataToDrag = ClipData(
                maskDragMessage,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            val maskShadow = DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //support pre-Nougat versions
                @Suppress("DEPRECATION")
                it.startDrag(dataToDrag, maskShadow, it, 0)
            } else {
                //supports Nougat and beyond
                it.startDragAndDrop(dataToDrag, maskShadow, it, 0)
            }

            // 6
            it.visibility = View.INVISIBLE

            //7
            true
        }
    }


    private class DragShadowBuilder(view: View) : View.DragShadowBuilder(view) {
        private val shadow = ResourcesCompat.getDrawable(
            view.context.resources,
            R.drawable.ic_mask,
            view.context.theme
        )

        override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
            val width = view.width
            val height = view.height

            shadow?.setBounds(0, 0, width, height)

            outShadowSize?.set(width, height)
            outShadowTouchPoint?.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas?) {
            canvas?.let { shadow?.draw(it) }
        }
    }
}