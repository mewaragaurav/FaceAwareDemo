package tr.com.emrememis.library

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import kotlin.math.absoluteValue

class FaceAwareNewNew constructor(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        adjustBitmap(drawable, height, width)
    }

    private fun adjustBitmap(drawable: Drawable, vHeight: Int, vWidth: Int) {
        Log.d("Width", vWidth.toString())
        Log.d("Height", vHeight.toString())
        val faceCenter = getCenterOfAllFaces(drawable)
        var dwidth = drawable.intrinsicWidth
        var dheight = drawable.intrinsicHeight

        Log.d("dWidth", dwidth.toString())
        Log.d("dHeight", dheight.toString())

        var dHalfWidth = drawable.intrinsicWidth * .5f
        var vHalfWidth = vWidth * .5f
        var dHalfHeight = drawable.intrinsicHeight * .5f
        var vHalfHeight = vHeight * .5f

        var scale = 0f
        var ndWidth = dwidth.toFloat()
        var ndHeight = dheight.toFloat()
        var nx = 0f
        var ny = 0f

        if (drawable.intrinsicWidth * vHeight > vWidth * drawable.intrinsicHeight) {
            scale = vHeight.toFloat() / dheight.toFloat()
            ndWidth = dwidth * scale
            dHalfWidth = ndWidth * .5f
            faceCenter.x = faceCenter.x * scale
            nx = (vWidth - dwidth * scale) * 0.5f
        } else {
            scale = vWidth.toFloat() / dwidth.toFloat()
            ndHeight = dheight * scale
            dHalfHeight = ndHeight * .5f
            faceCenter.y = faceCenter.y * scale
            ny = (vHeight - dheight * scale) * 0.5f
        }

        if(drawable.intrinsicWidth > width) {
            if(faceCenter.x > dHalfWidth) {
                val dx = drawable.intrinsicWidth - (faceCenter.x + vHalfWidth)
                if (dx < 0) faceCenter.x = faceCenter.x - dx.absoluteValue
            } else {
                val dx = faceCenter.x - vHalfWidth
                if ( dx < 0 ) faceCenter.x = faceCenter.x + dx.absoluteValue
            }
        }

        if(drawable.intrinsicHeight > height) {
            if(faceCenter.y > dHalfHeight) {
                val dy = drawable.intrinsicHeight - (faceCenter.y + vHalfHeight)
                if(dy < 0) faceCenter.y = faceCenter.y - dy.absoluteValue
            } else {
                val dy = faceCenter.y - vHalfHeight
                if(dy <0 ) faceCenter.y = faceCenter.y + dy.absoluteValue
            }
        }

        val dx = faceCenter.x - vHalfWidth
        val dy = faceCenter.y - vHalfHeight
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        imageMatrix = matrix
    }



    private fun getCenterOfAllFaces(drawable: Drawable): PointF {
        val bitmap = when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is ColorDrawable -> Bitmap.createBitmap(2,2, Bitmap.Config.ARGB_8888)
            else -> Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        val frame = Frame.Builder()
            .setBitmap(bitmap)
            .build()

        val detector = FaceDetector.Builder(context)
            .setTrackingEnabled(false)
            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
            .build()

        val faces = detector.detect(frame)
        var flx = 0f
        var fux = 0f
        var fly = 0f
        var fuy = 0f
        for (i in 0 until faces.size()) {
            val face = faces[faces.keyAt(i)]
            if (i == 0) {
                flx = face.position.x
                fux = face.position.x + face.width
                fly = face.position.y
                fuy = face.position.y + face.height
            } else {
                val lx = face.position.x
                val ux = face.position.x + face.width
                val ly = face.position.y
                val uy = face.position.y + face.height
                if (lx < flx) flx = lx
                if (ux > fux) fux = ux
                if (ly < fly) fly = ly
                if (uy > fuy) fuy = uy
            }
        }
        detector.release()
        return PointF((flx + fux) * .5f, (fly + fuy) * .5f)
    }
}