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

package no.nordicsemi.android.kotlin.ble.server.main.service

import no.nordicsemi.android.kotlin.ble.core.ClientDevice
import no.nordicsemi.android.kotlin.ble.core.provider.MtuProvider
import no.nordicsemi.android.kotlin.ble.core.wrapper.IBluetoothGattService
import no.nordicsemi.android.kotlin.ble.server.api.GattServerAPI
import no.nordicsemi.android.kotlin.ble.server.api.ServerGattEvent
import java.util.UUID

/**
 * A class which groups service's characteristic on a server side.
 *
 * @property server [GattServerAPI] for communication with a client devices.
 * @property device A client device.
 * @property service Identifier of a service.
 * @property mtuProvider For providing mtu value established per connection.
 */
@Suppress("MemberVisibilityCanBePrivate")
data class ServerBleGattService internal constructor(
    private val server: GattServerAPI,
    private val device: ClientDevice,
    private val service: IBluetoothGattService,
    private val mtuProvider: MtuProvider
) {

    /**
     * [UUID] of the characteristic.
     */
    val uuid = service.uuid

    /**
     * All characteristics of a service.
     */
    val characteristics = service.characteristics.map {
        ServerBleGattCharacteristic(server, device, it, mtuProvider)
    }

    /**
     * Finds characteristic based on [uuid] and eventually [instanceId].
     *
     * @param uuid An [UUID] of a characteristic.
     * @param instanceId Instance id.
     * @return Characteristic or null if not found.
     */
    fun findCharacteristic(uuid: UUID, instanceId: Int? = null): ServerBleGattCharacteristic? {
        return characteristics.firstOrNull { characteristic ->
            characteristic.uuid == uuid && instanceId?.let { characteristic.instanceId == it } ?: true
        }
    }

    /**
     * Propagates GATT events to all of it's characteristics. Each characteristic and descriptor is
     * responsible to decide if it's the receiver of an event.
     *
     * @param event A GATT event.
     */
    internal fun onEvent(event: ServerGattEvent.ServiceEvent) {
        characteristics.onEach { it.onEvent(event) }
    }
}
