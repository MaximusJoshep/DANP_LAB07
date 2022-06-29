package org.danp.orientationexample

import android.content.Context
import android.hardware.Sensor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.danp.orientationexample.R
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var bussola : Sensor
    private lateinit var acelerometro : Sensor
    private lateinit var sensorManager : SensorManager
    private lateinit var listener: SensorEventListener

    private var ultimoGrau = 0f
    private var vlrsBussola = FloatArray(3)
    private var vlrsGravidade= FloatArray(3)
    private var angulosDeOrientacion= FloatArray(3)
    private var matrixDeRotacion= FloatArray(9)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imageView : ImageView =findViewById(R.id.imageView)

        /** listamos los sensores**/
        sensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensores: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensores.forEach {sensor->
            Log.i("Sensores",sensor.toString())

        }

        /** ponemos los sensores especificos**/
        acelerometro=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        bussola=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if(bussola!= null){
            Log.i("Sensores","0 dispositivo tiene brujula ")
        }else{
            Log.i("Sensores","Dispositivo no tiene brujula")

        }

        /** captura la informacion del sensor**/
        listener=object :SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {

                when(event?.sensor?.type){
                    Sensor.TYPE_ACCELEROMETER ->{
                        vlrsGravidade=event.values.clone()
                        var x = event.values[0]
                        var y = event.values[1]
                        var z = event.values[2]
                        //Log.i("Sensores","Sensor.TYPE_ACCELEROMETER -> x = $x, y=$y, z=$z")

                    }
                    Sensor.TYPE_MAGNETIC_FIELD ->{
                        vlrsBussola=event.values.clone()
                        //Log.i("Sensores","Sensor.TYPE_MAGNETIC_FIELD")
                    }
                }
                SensorManager.getRotationMatrix(matrixDeRotacion,null,vlrsGravidade,vlrsBussola)
                SensorManager.getOrientation(matrixDeRotacion,angulosDeOrientacion)

                val radiano: Float=angulosDeOrientacion[0]
                val grauActual= (Math.toDegrees(radiano.toDouble())+ 360).toFloat() % 360

                var rotacionar = RotateAnimation(
                    ultimoGrau, -grauActual,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                rotacionar.duration = 250
                rotacionar.fillAfter=true

                imageView.startAnimation(rotacionar)
                ultimoGrau= -grauActual
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listener,acelerometro,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener,bussola,SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }
}