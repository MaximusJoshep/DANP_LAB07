package org.danp.orientationexample

import android.content.Context
import android.hardware.Sensor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var brujula : Sensor
    private lateinit var acelerometro : Sensor
    private lateinit var sensorManager : SensorManager
    private lateinit var listener: SensorEventListener


    private var vlrsbrujula = FloatArray(3)
    private var vlrsgravedad= FloatArray(3)
    private var angulosDeOrientacion= FloatArray(3)
    private var matrixDeRotacion= FloatArray(9)

    private var ultimoGrado = 0f




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imageView : ImageView =findViewById(R.id.imageView)
        var t1: TextView=findViewById(R.id.t1)
        var t2: TextView=findViewById(R.id.t2)
        var t3: TextView=findViewById(R.id.t3)


        /** listamos los sensores**/
        sensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensores: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensores.forEach {sensor->
            Log.i("Sensores",sensor.toString())

        }

        /** ponemos los sensores especificos**/
        acelerometro=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        brujula=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if(brujula!= null){
            Log.i("Sensores","0 dispositivo tiene brujula ")
        }else{
            Log.i("Sensores","Dispositivo no tiene brujula")

        }

        /** captura la informacion del sensor**/
        listener=object :SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {

                when(event?.sensor?.type){
                    Sensor.TYPE_ACCELEROMETER ->{
                        vlrsgravedad=event.values.clone()
                        var x = event.values[0]
                        var y = event.values[1]
                        var z = event.values[2]
                        //Log.i("Sensores","Sensor.TYPE_ACCELEROMETER -> x = $x, y=$y, z=$z")
                        t1.setText("Acelerómetro -> x = $x, y=$y, z=$z")

                    }
                    Sensor.TYPE_MAGNETIC_FIELD ->{
                        vlrsbrujula=event.values.clone()
                        var x = event.values[0]
                        var y = event.values[1]
                        var z = event.values[2]

                        t2.setText("Magnetómetro -> x = $x, y=$y, z=$z")
                        //Log.i("Sensores","Sensor.TYPE_MAGNETIC_FIELD")
                    }
                }


                /* Calcula la matriz de inclinación null así como la matriz de rotación matrixDeRotacion
                transformando un vector del sistema de coordenadas del dispositivo
                al sistema de coordenadas del mundo que se define*/
                SensorManager.getRotationMatrix(matrixDeRotacion,null,vlrsgravedad,vlrsbrujula)
                /*Calcula la orientación del dispositivo en función de la matriz de rotación.*/
                SensorManager.getOrientation(matrixDeRotacion,angulosDeOrientacion)
                /*Covertimos la matriz de rotacion de radianes a hexadecimales*/
                var x = (Math.toDegrees(angulosDeOrientacion[0].toDouble())+360).toFloat() %360
                var y = (Math.toDegrees(angulosDeOrientacion[1].toDouble())+360).toFloat() %360
                var z = (Math.toDegrees(angulosDeOrientacion[2].toDouble())+360).toFloat() %360

                t3.setText("Angulos de Orientación -> x = $x, y=$y, z=$z")
                val radian: Float=angulosDeOrientacion[0]
                val gradoActual= (Math.toDegrees(radian.toDouble())+ 360).toFloat() % 360

                var rotacionar = RotateAnimation(
                    ultimoGrado, -gradoActual,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                rotacionar.duration = 250
                rotacionar.fillAfter=true


                imageView.startAnimation(rotacionar)
                ultimoGrado= -gradoActual
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listener,acelerometro,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener,brujula,SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }
}