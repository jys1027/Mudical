package com.example.figmamudical

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService




typealias EmotionListener = (emotion: MutableList<Double>) -> Unit

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (!Python.isStarted()) {
            val android = AndroidPlatform(this)
            Python.start(android)
        }

        // Request permissions on starting app
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private class EmotionAnalyzer(private val listener: EmotionListener) : ImageAnalysis.Analyzer {
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy) {

            tries ++

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }

            val pixelStr = pixels.toString()

            val pythonInstance = Python.getInstance()
            val pythonScript = pythonInstance.getModule("faceEmotionAnalyzer")
            val emotion = ( pythonScript.callAttr("get_emotion", image.width, image.height, pixelStr) )

            Log.d(cameraTAG, "data: $emotion")

            val emotionList = emotion.toJava(DoubleArray::class.java).toMutableList()
            when (emotionList.size) {
                7 -> {
                    listener(emotionList)
                }
                1 -> {
                    Log.d(cameraTAG, "no face")
                }
                else -> {
                    Log.d(cameraTAG, "ERROR analyze failed")
                }
            }

            image.close()
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        var sizeA = 0
        var sizeB = 0
        var emotionA = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0) // ('angry', 'disgust', 'fear', 'happy', 'sad', 'surprise', 'neutral')
        var emotionB = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0) // sad, angry, disgust, fear, happy, neutral

        var tries = 0

        var emotionSaved = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0) // happy, sad, angry, fear, neutral

        const val cameraTAG = "CameraX"
        const val audioTAG = "MediaRecorder"

        //private var cameraInstance: Camera? = null
        //lateinit var cameraProvider: ProcessCameraProvider
        lateinit var cameraExecutor: ExecutorService
        //PLEASE cameraExecutor.shutdown() on destroy
        /*
        override fun onDestroy() {
            super.onDestroy()
            cameraExecutor.shutdown()
        }
        */

        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()


        fun startCameraOnFragment(safeContext: Context?, safeLifeCycle: LifecycleOwner) {

            if(safeContext == null) {
                Log.d(cameraTAG, "context is null")
                return
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider = cameraProviderFuture.get()

                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, EmotionAnalyzer { emotion ->
                            var i = 0
                            while(i<7){
                                emotionA[i] = ( emotionA[i] * sizeA + emotion[i] ) / (sizeA + 1)
                                i ++
                            }
                            sizeA ++
                        })
                    }

                // Select back camera
                //val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                // Select front camera for music therapy
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    //val cameraInstance = cameraProvider.bindToLifecycle(safeLifeCycle, cameraSelector, imageAnalyzer)
                    cameraProvider.bindToLifecycle(safeLifeCycle, cameraSelector, imageAnalyzer)
                } catch (exc: Exception) {
                    Log.e(cameraTAG, "Use case binding failed #1", exc)
                    try {
                        cameraProvider.bindToLifecycle(safeLifeCycle, CameraSelector.DEFAULT_FRONT_CAMERA, imageAnalyzer)
                    }
                    catch(exc: Exception) {
                        Log.e(cameraTAG, "Use case binding failed #2", exc)
                    }
                }

            }, ContextCompat.getMainExecutor(safeContext))
        }

        private const val fileNameM4a: String = "temp_audio"
        private const val fileNameWav: String = "temp_audio_file.wav"

        private const val sleepInterval: Long = 10000 // in ms

        private var recordVoiceThread: Thread? = null

        fun toggleAudioThreading(safeContext: Context?) {

            if(safeContext == null){
                Log.d(audioTAG, "context is null")
                return
            }
            //else if(recordVoiceThread == null)
                //return

            if(recordVoiceThread == null){
                Log.d(audioTAG, "started new Runnable Thread")
                val recordVoice = Runnable {
                    try {
                        while (!Thread.currentThread().isInterrupted) {
                            val recorder = MediaRecorder()
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                            //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                            val fileM4a = File(safeContext.filesDir, fileNameM4a)
                            Log.d(audioTAG, "isFile = " + fileM4a.isFile.toString())

                            recorder.setOutputFile(fileM4a.absolutePath)
                            recorder.prepare()
                            recorder.start()

                            Thread.sleep(sleepInterval)

                            recorder.stop()
                            recorder.reset()
                            recorder.release()

                            val fileWav = File(safeContext.filesDir, fileNameWav)
                            Log.d(audioTAG, "isFileWav = " + fileWav.isFile.toString())

                            when (val returnCode =
                                FFmpeg.execute("-i ${fileM4a.absolutePath} ${fileWav.absolutePath} -y")) {
                                Config.RETURN_CODE_SUCCESS -> {
                                    Log.d(audioTAG, "Command execution completed successfully.")
                                }
                                Config.RETURN_CODE_CANCEL -> {
                                    Log.d(audioTAG, "Command execution cancelled by user.")
                                }
                                else -> {
                                    Log.d(
                                        audioTAG,
                                        "Command execution failed with return code = $returnCode and the output below."
                                    )
                                }
                            }

                            val pythonInstance = Python.getInstance()
                            val pythonScript = pythonInstance.getModule("voiceEmotionAnalyzer")
                            val emotion =
                                (pythonScript.callAttr("get_emotion", fileWav.absolutePath))
                            val emotionList =
                                emotion.toJava(DoubleArray::class.java).toMutableList()

                            when (emotionList.size) {
                                6 -> Log.d(audioTAG, "analyze success")
                                else -> Log.d(audioTAG, "analyze failed")
                            }

                            Log.d(audioTAG, "emotion = $emotionList")

                            var i = 0
                            while (i < 6) {
                                emotionB[i] = (emotionB[i] * sizeB + emotionList[i]) / (sizeB + 1)
                                i++
                            }
                            sizeB++

                            Thread.sleep(sleepInterval)
                        }
                    }
                    catch(exc: InterruptedException){
                        Log.d(audioTAG, "Thread stopped (interrupted)");
                    }
                    catch(exc: Exception) {
                        Log.d(audioTAG, "unknown error", exc);
                    }

                }
                recordVoiceThread = Thread(recordVoice)
                recordVoiceThread!!.start()
            }
            else{
                Log.d(audioTAG, "try stopping Thread")

                recordVoiceThread!!.interrupt()
                recordVoiceThread = null
            }

        }

        fun initEmotion() {
            tries = 0
            sizeA = 0
            sizeB = 0
            var i = 0
            while (i < 7) {
                emotionA[i] = 0.0
                i++
            }
            i = 0
            while (i < 6){
                emotionB[i] = 0.0
                i++
            }
        }

        fun getEmotion(): MutableList<Double> {
            Log.d("emotion analyzed: ", "$emotionA (size $sizeA)")
            Log.d("emotion analyzed: ", "$emotionB (size $sizeB)")

            emotionSaved = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0)
            emotionSaved[0] = emotionA[3] * 0.9 + emotionB[4] * 0.1    //apply time interval gap
            emotionSaved[1] = emotionA[4] * 0.9 + emotionB[0] * 0.1
            emotionSaved[2] = emotionA[0] * 0.9 + emotionB[1] * 0.1
            emotionSaved[3] = emotionA[2] * 0.9 + emotionB[3] * 0.1
            emotionSaved[4] = emotionA[6] * 0.9 + emotionB[5] * 0.1
            var emotionTotal = 0.0
            var i = 0
            while(i < 5){
                Log.d("DEBUG", "is fine? $i")
                emotionTotal += emotionSaved[i]
                i ++
            }
            Log.d("emotion","sum = $emotionTotal")
            if(emotionTotal < 1e-6){
                Log.d("emotion analyzed: ", "failed (size = 0)")
                i = 0
                while(i < 5){
                    emotionSaved[i] = -1.0
                    i ++
                }
                return emotionSaved
            }
            i = 0
            while(i < 5){
                try {
                    emotionSaved[i] /= emotionTotal
                }
                catch(e: Exception) {
                    Log.d("emotion", e.toString())
                    var j = 0
                    while(j < 5){
                        emotionSaved[j] = -1.0
                        j ++
                    }
                    break
                }
                i ++
            }
            Log.d("emotion calculated: ", "$emotionSaved")
            return emotionSaved
        }

        fun getDialogContext(id: Int): Int {
            Log.d("companion", "finding context")
            val emotionList = getEmotion()
            if(id == 1){
                return when((1..3).random()){
                    1 -> R.layout.segment_question_box_1_1
                    2 -> R.layout.segment_question_box_1_2
                    3 -> when{
                        emotionList[0] > 0.5 -> R.layout.segment_question_box_0_1
                        emotionList[1] > 0.5 -> R.layout.segment_question_box_0_2
                        emotionList[2] > 0.5 -> R.layout.segment_question_box_0_3
                        emotionList[3] > 0.5 -> R.layout.segment_question_box_0_4
                        emotionList[4] > 0.5 -> R.layout.segment_question_box_0_5
                        else -> R.layout.segment_question_box_0_0
                    }
                    else -> R.layout.segment_question_box
                }
            }
            else if(id == 2){
                return when((1..4).random()){
                    1 -> R.layout.segment_question_box_2_1
                    2 -> R.layout.segment_question_box_2_2
                    3 -> R.layout.segment_question_box_2_3
                    4 -> when{
                        emotionList[0] > 0.5 -> R.layout.segment_question_box_0_1
                        emotionList[1] > 0.5 -> R.layout.segment_question_box_0_2
                        emotionList[2] > 0.5 -> R.layout.segment_question_box_0_3
                        emotionList[3] > 0.5 -> R.layout.segment_question_box_0_4
                        emotionList[4] > 0.5 -> R.layout.segment_question_box_0_5
                        else -> R.layout.segment_question_box_0_0
                    }
                    else -> R.layout.segment_question_box
                }
            }
            return R.layout.segment_question_box
        }

    }

}



