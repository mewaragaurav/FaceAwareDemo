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

class FaceAwareNew5 constructor(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        adjustBitmap(drawable, height, width)
    }

    private fun adjustBitmap(drawable: Drawable, vHeight: Int, vWidth: Int) {
        Log.d("Width", vWidth.toString())
        Log.d("Height", vHeight.toString())
        val faceCenter = getCenterOfAllFaces(drawable)
        Log.d("FaceCenterX", faceCenter.x.toString())
        Log.d("FaceCenterY", faceCenter.y.toString())
        var dwidth = drawable.intrinsicWidth
        var dheight = drawable.intrinsicHeight

        Log.d("dWidthBeforeScale", dwidth.toString())
        Log.d("dHeightBeforeScale", dheight.toString())

        var dHalfWidth = drawable.intrinsicWidth * .5f
        var vHalfWidth = vWidth * .5f
        var dHalfHeight = drawable.intrinsicHeight * .5f
        var vHalfHeight = vHeight * .5f

        var scale = 0f
        var ndWidth = dwidth.toFloat()
        var ndHeight = dheight.toFloat()
        var nx = 0f
        var ny = 0f
        Log.d("dHeightBeforeScale", dheight.toString())

        if (dwidth * vHeight > vWidth * dheight) {
            scale = vHeight.toFloat() / dheight.toFloat()
            ndWidth = dwidth * scale
            dHalfWidth = ndWidth * .5f
            faceCenter.x = faceCenter.x * scale
            nx = (vWidth - dwidth * scale) * 0.5f
            Log.d("ScaleX", scale.toString())
        } else {
            scale = vWidth.toFloat() / dwidth.toFloat()
            ndHeight = dheight * scale
            dHalfHeight = ndHeight * .5f
            faceCenter.y = faceCenter.y * scale
            ny = (vHeight - dheight * scale) * 0.5f
            Log.d("ScaleY", scale.toString())
        }

        Log.d("dHalfWidthAfterScale", dHalfWidth.toString())
        Log.d("dHalfHeightAFterScale", dHalfHeight.toString())
        Log.d("FaceCenterX", faceCenter.x.toString())
        Log.d("FaceCenterY", faceCenter.y.toString())

        if(ndWidth > width) {
            if(faceCenter.x > dHalfWidth) {
                val dx = ndWidth - (faceCenter.x + vHalfWidth)
                if (dx < 0) faceCenter.x = faceCenter.x - dx.absoluteValue
            } else {
                val dx = faceCenter.x - vHalfWidth
                if ( dx < 0 ) faceCenter.x = faceCenter.x + dx.absoluteValue
            }
        }

        if(ndHeight > height) {
            if(faceCenter.y > dHalfHeight) {
                val dy = ndHeight - (faceCenter.y + vHalfHeight)
                if(dy < 0) faceCenter.y = faceCenter.y - dy.absoluteValue
            } else {
                val dy = faceCenter.y - vHalfHeight
                if(dy <0 ) faceCenter.y = faceCenter.y + dy.absoluteValue
            }
        }

        val dx = faceCenter.x - vHalfWidth
        val dy = faceCenter.y - vHalfHeight
        val matrix = Matrix()
        matrix.setTranslate(dx, dy)
        matrix.setScale(scale, scale)
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