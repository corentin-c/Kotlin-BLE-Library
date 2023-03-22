package no.nordicsemi.android.kotlin.ble.profile.rscs

import no.nordicsemi.android.kotlin.ble.profile.common.ByteData
import no.nordicsemi.android.kotlin.ble.profile.common.IntFormat
import no.nordicsemi.android.kotlin.ble.profile.common.LongFormat
import no.nordicsemi.android.kotlin.ble.profile.rscs.data.RSCSData

object RSCSDataParser {

    fun parse(byteArray: ByteArray): RSCSData? {
        val data = ByteData(byteArray)

        if (data.size() < 4) {
            return null
        }

        var offset = 0
        val flags: Int = data.getIntValue(IntFormat.FORMAT_UINT8, offset) ?: return null
        val instantaneousStrideLengthPresent = flags and 0x01 != 0
        val totalDistancePresent = flags and 0x02 != 0
        val statusRunning = flags and 0x04 != 0
        offset += 1

        val speed = data.getIntValue(IntFormat.FORMAT_UINT16_LE, offset)?.toFloat()?.let {
            it / 256f // [m/s]
        } ?: return null

        offset += 2
        val cadence: Int = data.getIntValue(IntFormat.FORMAT_UINT8, offset) ?: return null
        offset += 1

        if (data.size() < (4 + (if (instantaneousStrideLengthPresent) 2 else 0) + if (totalDistancePresent) 4 else 0)) {
            return null
        }

        var strideLength: Int? = null
        if (instantaneousStrideLengthPresent) {
            strideLength = data.getIntValue(IntFormat.FORMAT_UINT16_LE, offset)
            offset += 2
        }

        var totalDistance: Long? = null
        if (totalDistancePresent) {
            totalDistance = data.getLongValue(LongFormat.FORMAT_UINT32_LE, offset)
            // offset += 4;
        }

        return RSCSData(statusRunning, speed, cadence, strideLength, totalDistance)
    }
}