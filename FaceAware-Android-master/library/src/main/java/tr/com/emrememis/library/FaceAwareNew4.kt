package tr.com.emrememis.library

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class FaceAwareNew4 constructor(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val adjustedBitmap = adjustBitmap()
        setImageBitmap(adjustedBitmap)
    }

    private fun adjustBitmap(): Bitmap {
        val bitmap = drawable.let {
            when (it) {
                is BitmapDrawable -> it.bitmap
                is ColorDrawable -> Bitmap.createBitmap(2,2, Bitmap.Config.ARGB_8888)
                else -> Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            }
        }
        val faceCenter = getCenterOfAllFaces(bitmap)
        val dHalfWidth = bitmap.width * .5f
        val vHalfWidth = width * .5f
        val dHalfHeight = bitmap.height * .5f
        val vHalfHeight = height * .5f

        if(bitmap.width > width) {
            if(faceCenter.x > dHalfWidth) {
                val dx = bitmap.width - (faceCenter.x + vHalfWidth)
                if (dx < 0) faceCenter.x = faceCenter.x - dx.absoluteValue
            } else {
                val dx = faceCenter.x - vHalfWidth
                if ( dx < 0 ) faceCenter.x = faceCenter.x + dx.absoluteValue
            }
        }

        if(bitmap.height > height) {
            if(faceCenter.y > dHalfHeight) {
                val dy = bitmap.height - (faceCenter.y + vHalfHeight)
                if(dy < 0) faceCenter.y = faceCenter.y - dy.absoluteValue
            } else {
                val dy = faceCenter.y - vHalfHeight
                if(dy <0 ) faceCenter.y = faceCenter.y + dy.absoluteValue
            }
        }

        return Bitmap.createBitmap(
            bitmap,
            (faceCenter.x - vHalfWidth).roundToInt(),
            (faceCenter.y - vHalfHeight).roundToInt(),
            width,
            height
        )
    }



    private fun getCenterOfAllFaces(bitmap: Bitmap): PointF {
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