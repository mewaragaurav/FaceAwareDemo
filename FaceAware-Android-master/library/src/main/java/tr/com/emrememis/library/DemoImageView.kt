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

class DemoImageView constructor(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("DemoWidth", width.toString())
        Log.d("DemoHeight", height.toString())
    }
}