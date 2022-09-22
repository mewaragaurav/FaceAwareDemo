package tr.com.emrememis.faceaware_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlin.math.absoluteValue
import kotlin.math.sign


fun faceAwareBitmap(context: Context, drawable: Drawable, vWidth: Float, vHeight: Float) {
    val dWidth = drawable.intrinsicWidth
    val dHeight = drawable.intrinsicHeight
    val faceCenter = getCenterOfAllFaces(context, drawable)
    val dHalfWidth = dWidth * .5f
    val vHalfWidth = vWidth * .5f
    val dHalfHeight = dHeight * .5f
    val vHalfHeight = vHeight * .5f

    if(dWidth > vWidth) {
        if(faceCenter.x > dHalfWidth) {
            val dx = dWidth - (faceCenter.x + vHalfWidth)
            if (dx < 0) faceCenter.x = faceCenter.x - dx.absoluteValue
        } else {
            val dx = faceCenter.x - vHalfWidth
            if ( dx < 0 ) faceCenter.x = faceCenter.x + dx.absoluteValue
        }
    }

    if(dHeight > vHeight) {
        if(faceCenter.y > dHalfHeight) {
            val dy = dHeight - (faceCenter.y + vHalfHeight)
            if(dy < 0) faceCenter.y = faceCenter.y - dy.absoluteValue
        } else {
            val dy = faceCenter.y - vHalfHeight
            if(dy <0 ) faceCenter.y = faceCenter.y + dy.absoluteValue
        }
    }

}

fun getCenterOfAllFaces(context: Context, drawable: Drawable): PointF {
    drawable.let {
        val bitmap = when (it) {
            is BitmapDrawable -> it.bitmap
            is ColorDrawable -> Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
            else -> Bitmap.createBitmap(
                it.intrinsicWidth,
                it.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
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
        return PointF((flx + fux) * .5f, (fly + fuy) * .5f)
    }
}

