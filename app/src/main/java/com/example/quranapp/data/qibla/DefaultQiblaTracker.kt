package com.example.quranapp.data.qibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.quranapp.domain.qibla.QiblaTracker
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class DefaultQiblaTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : QiblaTracker {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    override fun startTracking(): Flow<Float> = callbackFlow {
        var lastGravity: FloatArray? = null
        var lastGeomagnetic: FloatArray? = null

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    lastGravity = event.values.clone()
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    lastGeomagnetic = event.values.clone()
                }

                if (lastGravity != null && lastGeomagnetic != null) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, i, lastGravity, lastGeomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        // Convert azimuth from radians to degrees (-180 to 180)
                        var azimuthInDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        // Normalize to 0-360
                        azimuthInDegrees = (azimuthInDegrees + 360) % 360
                        trySend(azimuthInDegrees)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    override fun stopTracking() {
        // Handled by awaitClose in callbackFlow when collect is cancelled
    }
}
