package tr.com.emrememis.faceaware_android

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.gms.vision.Frame.Builder
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import tr.com.emrememis.faceaware_android.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var detector : FaceDetector
    private var center: PointF? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detector = FaceDetector.Builder(this).setTrackingEnabled(false)
            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
            .build()
        /*
         binding.faceAware.setImageResource(R.drawable.image)

         binding.faceAware.setImageDrawable(ActivityCompat.getDrawable(this,R.drawable.image))

         binding.faceAware.setImageBitmap(BitmapFactory.decodeResource(resources,R.drawable.test))

         Glide.with(this).load(R.drawable.image).into(binding.faceAware)
        */
        binding.iv2.setImageResource(R.drawable.demo18)
        binding.faceAware.setImageResource(R.drawable.demo18)

    }

    @SuppressLint("ResourceType")
    fun getFile(): File? {
        return try
        {
            val f = File("file_name")
            val inputStream = resources.openRawResource(R.drawable.demo)
            val out = FileOutputStream(f)
            var buff = ByteArray(1024)
            var len = inputStream.read(buff)
            while(len > 0) {
                out.write(buff, 0, len)
                len = inputStream.read(buff)
            }
            out.close()
            inputStream.close()
            f

        }
        catch (e: IOException){
            e.printStackTrace()
            null
        }
    }

    private fun findFaceCenters(bitmap: Bitmap): Collection<PointF> {
        if (detector.isOperational) {
            val faces: SparseArray<Face> = detector.detect(
                Builder()
                    .setBitmap(bitmap)
                    .build()
            )
            val centers: MutableCollection<PointF> = ArrayList()
            for (i in 0 until faces.size()) {
                val face: Face = faces[faces.keyAt(i)]
                centers.add(
                    PointF(
                        face.getPosition().x + face.getWidth() / 2f,
                        face.getPosition().y + face.getHeight() / 2f
                    )
                )
            }
            return centers
        }
        return Collections.emptyList()
    }

    fun findFaceCenter(file: File): PointF {
        //val scale: Int = findDownSamplingScale(file)
        val scale: Int = 1
        val options = BitmapFactory.Options()
        options.inSampleSize = scale
        val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options)
        val centers = findFaceCenters(bitmap)
        if (centers.isEmpty()) return PointF(bitmap.width / 2f, bitmap.height / 2f)
        var sumX = 0f
        var sumY = 0f
        for (center in centers) {
            sumX += center.x
            sumY += center.y
        }
        return PointF(scale * sumX / centers.size, scale * sumY / centers.size)
    }

    /*private fun findDownSamplingScale(file: File): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        var width = options.outWidth
        var height = options.outHeight
        var scale = 1
        while (width * height > FACE_DETECTION_SIZE_MAX) {
            width /= 2
            height /= 2
            scale *= 2
        }
        return scale
    }*/
}
