package no.nordicsemi.android.kotlin.ble.client.main

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import no.nordicsemi.android.kotlin.ble.client.api.BleGatt
import no.nordicsemi.android.kotlin.ble.client.main.callback.BleGattClient
import no.nordicsemi.android.kotlin.ble.client.mock.BleMockGatt
import no.nordicsemi.android.kotlin.ble.client.real.BluetoothGattClientCallback
import no.nordicsemi.android.kotlin.ble.client.real.BluetoothGattWrapper
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.kotlin.ble.core.RealServerDevice
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.mock.MockEngine

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
suspend fun ServerDevice.connect(
    context: Context,
    options: BleGattConnectOptions = BleGattConnectOptions()
): BleGattClient {
    return when (this) {
        is MockServerDevice -> connectDevice(this, context, options)
        is RealServerDevice -> connectDevice(this, context, options)
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
suspend fun connectDevice(
    device: MockServerDevice,
    context: Context,
    options: BleGattConnectOptions
): BleGattClient {
    val gatt = BleMockGatt(MockEngine, device, options.autoConnect)
    return BleGattClient(gatt)
        .also { MockEngine.connectToServer(device, gatt, options) }
        .also { it.connect() }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
private suspend fun connectDevice(
    device: RealServerDevice,
    context: Context,
    options: BleGattConnectOptions
): BleGattClient {
    return BleGattClient(device.createConnection(context, options)).also {
        it.connect()
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
private fun RealServerDevice.createConnection(
    context: Context,
    options: BleGattConnectOptions,
): BleGatt {
    val gattCallback = BluetoothGattClientCallback()

    val gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        device.connectGatt(
            context,
            options.autoConnect,
            gattCallback,
            BluetoothDevice.TRANSPORT_LE,
            options.phy?.value ?: 0
        )
    } else {
        device.connectGatt(context, options.autoConnect, gattCallback)
    }

    return BluetoothGattWrapper(gatt, gattCallback, options.autoConnect)
}