package com.example.quranapp.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow

class CompassSensor(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    fun azimuthFlow(): Flow<Float> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val r = FloatArray(9)
                    val orientation = FloatArray(3)
                    SensorManager.getRotationMatrixFromVector(r, event.values)
                    SensorManager.getOrientation(r, orientation)
                    val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    trySend((azimuth + 360f) % 360f)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, rotation, SensorManager.SENSOR_DELAY_GAME)
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
