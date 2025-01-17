/*
 * Copyright (c) 2023, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.kotlin.ble.client.mock

import androidx.annotation.IntRange
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.client.api.GattClientAPI
import no.nordicsemi.android.kotlin.ble.client.api.ClientGattEvent
import no.nordicsemi.android.kotlin.ble.core.ClientDevice
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectionPriority
import no.nordicsemi.android.kotlin.ble.core.data.BleGattPhy
import no.nordicsemi.android.kotlin.ble.core.data.BleWriteType
import no.nordicsemi.android.kotlin.ble.core.data.PhyOption
import no.nordicsemi.android.kotlin.ble.core.wrapper.IBluetoothGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.wrapper.IBluetoothGattDescriptor
import no.nordicsemi.android.kotlin.ble.mock.MockEngine

/**
 * A class for communication with [MockEngine]. It allows for connecting to mock servers registered
 * locally on a device.
 *
 * @property mockEngine An instance of a [MockEngine].
 * @property serverDevice A server device from a connection.
 * @property clientDevice A client device from a connection.
 * @property autoConnect Boolean value passed during connection.
 */
class BleMockGatt(
    private val mockEngine: MockEngine,
    private val serverDevice: MockServerDevice,
    private val clientDevice: ClientDevice,
    override val autoConnect: Boolean
) : GattClientAPI {

    private val _event = MutableSharedFlow<ClientGattEvent>(extraBufferCapacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val event: SharedFlow<ClientGattEvent> = _event.asSharedFlow()

    override val device: ServerDevice
        get() = serverDevice

    override fun onEvent(event: ClientGattEvent) {
        _event.tryEmit(event)
    }

    override fun writeCharacteristic(
        characteristic: IBluetoothGattCharacteristic,
        value: DataByteArray,
        writeType: BleWriteType
    ) {
        mockEngine.writeCharacteristic(serverDevice, clientDevice, characteristic, value, writeType)
    }

    override fun readCharacteristic(characteristic: IBluetoothGattCharacteristic) {
        mockEngine.readCharacteristic(serverDevice, clientDevice, characteristic)
    }

    override fun enableCharacteristicNotification(characteristic: IBluetoothGattCharacteristic) {
        mockEngine.enableCharacteristicNotification(clientDevice, serverDevice, characteristic)
    }

    override fun disableCharacteristicNotification(characteristic: IBluetoothGattCharacteristic) {
        mockEngine.disableCharacteristicNotification(clientDevice, serverDevice, characteristic)
    }

    override fun writeDescriptor(descriptor: IBluetoothGattDescriptor, value: DataByteArray) {
        mockEngine.writeDescriptor(serverDevice, clientDevice, descriptor, value)
    }

    override fun readDescriptor(descriptor: IBluetoothGattDescriptor) {
        mockEngine.readDescriptor(serverDevice, clientDevice, descriptor)
    }

    override fun requestMtu(@IntRange(from = 23, to = 517) mtu: Int) {
        mockEngine.requestMtu(clientDevice, serverDevice, mtu)
    }

    override fun readRemoteRssi() {
        mockEngine.readRemoteRssi(clientDevice, serverDevice)
    }

    override fun readPhy() {
        mockEngine.readPhy(clientDevice, serverDevice)
    }

    override fun discoverServices() {
        mockEngine.discoverServices(clientDevice, serverDevice)
    }

    override fun setPreferredPhy(txPhy: BleGattPhy, rxPhy: BleGattPhy, phyOption: PhyOption) {
        mockEngine.setPreferredPhy(clientDevice, serverDevice, txPhy, rxPhy, phyOption)
    }

    override fun disconnect() {
        mockEngine.cancelConnection(serverDevice, clientDevice)
    }

    override fun clearServicesCache() {
        mockEngine.clearServiceCache(serverDevice, clientDevice)
    }

    override fun close() {
        mockEngine.close(serverDevice, clientDevice)
    }

    override fun beginReliableWrite() {
        mockEngine.beginReliableWrite(serverDevice, clientDevice)
    }

    override fun abortReliableWrite() {
        mockEngine.abortReliableWrite(serverDevice, clientDevice)
    }

    override fun executeReliableWrite() {
        mockEngine.executeReliableWrite(serverDevice, clientDevice)
    }

    override fun requestConnectionPriority(priority: BleGattConnectionPriority) {
        mockEngine.requestConnectionPriority(clientDevice, priority)
    }
}
