package com.example.todoapp.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.todoapp.domain.model.GravityData
import com.example.todoapp.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart

class SensorRepositoryImpl(
    context: Context,
) : SensorRepository {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    private val gravityFlow = MutableSharedFlow<GravityData>(replay = 1)

    private val listener =
        object : SensorEventListener {
            private val gravity = FloatArray(3)
            private val alpha = 0.8F

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int,
            ) = Unit

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
                    for (i in 0..2) gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i]
                    val rawX = gravity[0] / SensorManager.GRAVITY_EARTH
                    val rawY = gravity[1] / SensorManager.GRAVITY_EARTH
                    gravityFlow.tryEmit(GravityData(rawX.coerceIn(-1F, 1F), rawY.coerceIn(-1F, 1F)))
                }
            }
        }

    override fun getGravityFlow(): Flow<GravityData> {
        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        return gravityFlow.asSharedFlow().onStart { emit(GravityData(0F, 0F)) }
    }

    fun unregister() = sensorManager.unregisterListener(listener)
}
