package com.rexense.wholehouse.utility;

import android.app.Service;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.rexense.wholehouse.contract.CBLE;

/**
 * Creator: xieshaobing
 * creat time:  14:16
 * Description: 低功耗蓝牙服务(用于蓝牙设备连接与数据收发)
 */
public class BLEService extends Service {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt = null;
    private boolean mConnectStatus = false;

    // 定义GATT回调
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 广播连接状态;
                intentAction = CBLE.ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
                Logger.i("Connected to GATT server.");
                Logger.i("Attempting to start service discovery: " + mBluetoothGatt.discoverServices());
                mConnectStatus = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 广播断开状态
                intentAction = CBLE.ACTION_GATT_DISCONNECTED;
                Logger.e("Disconnected from GATT server.");
                broadcastUpdate(intentAction);
                mConnectStatus = false;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 广播发现服务
                broadcastUpdate(CBLE.ACTION_GATT_SERVICES_DISCOVERED);
                Logger.d("Successfully to discovery services.");
            } else {
                Logger.e("Failed to discovery services!");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 广播读特征(实现数据接收)
                broadcastUpdate(CBLE.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // 广播特征变化(实现数据接收)
            broadcastUpdate(CBLE.ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        String uuid_s = characteristic.getService().getUuid().toString().toUpperCase();
        String uuid_c = characteristic.getUuid().toString().toUpperCase();
        intent.putExtra(CBLE.EXTRA_SERVICE_UUID, uuid_s);
        intent.putExtra(CBLE.EXTRA_CHARACTERISTIC_UUID, uuid_c);

        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X", byteChar));
            }
            Logger.i("Received " + data.length + " bytes from the BLE device: " + stringBuilder.toString());
            intent.putExtra(CBLE.EXTRA_DATA, data);
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    // 初始化
    public boolean initialize() {
        // 获取BluetoothManager
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                Logger.e("Unable to initialize BluetoothManager.");
                return false;
            }
        }

        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null) {
            Logger.e("Unable to get a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    // 连接蓝牙设备
    public boolean connect(final String address) {
        if (this.mBluetoothAdapter == null || address == null) {
            Logger.e("BluetoothAdapter not initialized or unspecified address, can not connect.");
            return false;
        }

        // 已经连接过设备重试连接
        if (this.mBluetoothDeviceAddress != null && address.equals(this.mBluetoothDeviceAddress)
                && this.mBluetoothGatt != null) {
            Logger.d("Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                Logger.d("BluetoothAdapter connected with the device[" + address + "].");
                return true;
            } else {
                Logger.e("BluetoothAdapter failed to connect with the device[" + address + "]!");
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Logger.e("The device[" + address + "] not found and unable to connect!");
            return false;
        }
        // 连接
        this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
        if (this.mBluetoothGatt != null) {
            Logger.d("Successfully connected with the device[" + address + "].");
            this.mBluetoothDeviceAddress = address;
            return true;
        } else {
            Logger.e("Failed to connect with the device[" + address + "].");
            this.mBluetoothDeviceAddress = "";
            return false;
        }
    }

    // 断开连接
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.e("BluetoothAdapter not initialized and can not disconnect");
            return;
        }
        Logger.d("Disconnected with the device[" + this.mBluetoothDeviceAddress + "].");
        mBluetoothGatt.disconnect();
    }

    // 关闭(使用过后必须关闭)
    public void close() {
        Logger.d("Closed BLE");
        if (this.mBluetoothGatt == null) {
            return;
        }
        this.mBluetoothGatt.close();
        this.mBluetoothGatt = null;
    }

    // 读特征值
    public boolean readCharacteristic(String service_uuid, String characteristic_uuid) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.e("BluetoothAdapter not initialized and can not read characteristic");
            return false;
        }
        // 构造特征
        BluetoothGattService bleService = this.getGattService(service_uuid);
        BluetoothGattCharacteristic characteristic = bleService.getCharacteristic(UUID.fromString(characteristic_uuid));
        return this.mBluetoothGatt.readCharacteristic(characteristic);
    }

    // 设置特征通知
    public boolean setCharacteristicNotification(String service_uuid, String characteristic_uuid, boolean enabled) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Logger.e("BluetoothAdapter not initialized and can not set characteristic notification!");
            return false;
        }

        // 构造特征
        BluetoothGattService bleService = this.getGattService(service_uuid);
		if (bleService != null) {
			BluetoothGattCharacteristic characteristic = bleService.getCharacteristic(UUID.fromString(characteristic_uuid));
			// 设置
			boolean isEnableNotification = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
			if (isEnableNotification) {
				List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
				if (descriptorList != null && descriptorList.size() > 0) {
					for (BluetoothGattDescriptor descriptor : descriptorList) {
						descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
						mBluetoothGatt.writeDescriptor(descriptor);
					}
					Logger.d("Successfully set characteristic notification.");
					return true;
				}
			}
		}
		Logger.e("Failed to set characteristic notification!");
		return false;
	}

    // 写特征值
    public boolean writeCharacteristic(String service_uuid, String characteristic_uuid, byte[] value) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Logger.e("BluetoothAdapter not initialized and can not write characteristic!");
            return false;
        }

        if (!this.mConnectStatus) {
            Logger.e("Disconnected from GATT server and can not write characteristic!");
            return false;
        }

        // 构造特征
        BluetoothGattService bleService = this.getGattService(service_uuid);
        BluetoothGattCharacteristic characteristic = bleService.getCharacteristic(UUID.fromString(characteristic_uuid));
        // 设置监听
        //this.setCharacteristicNotification(characteristic, true);
        // 写特征值
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic.setValue(value);
        boolean r = this.mBluetoothGatt.writeCharacteristic(characteristic);
        final StringBuilder stringBuilder = new StringBuilder(value.length);
        for (byte byteChar : value) {
            stringBuilder.append(String.format("%02X", byteChar));
        }
        if (r) {
            Logger.d("Successfully sent " + value.length + " bytes to the BLE device:\r\n    Service UUID: " + service_uuid + "\r\n    Characteristic UUID: " + characteristic_uuid + "\r\n    Value: " + stringBuilder.toString());
        } else {
            Logger.e("Failed send " + value.length + " bytes to the BLE device:\r\n    Service UUID: " + service_uuid + "\r\n    Characteristic UUID: " + characteristic_uuid + "\r\n    Value: " + stringBuilder.toString());
        }
        return r;
    }

    // 获取所支持的服务
    public List<BluetoothGattService> getSupportedGattServices() {
        if (this.mBluetoothGatt == null) {
            return null;
        }

        return this.mBluetoothGatt.getServices();
    }

    // 获取指定的服务
    public BluetoothGattService getGattService(String uuid) {
        if (this.mBluetoothGatt == null) {
            return null;
        }

        List<BluetoothGattService> services = this.getSupportedGattServices();
        if (services == null || services.size() == 0) {
            return null;
        }
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getUuid().equals(UUID.fromString(uuid))) {
                return services.get(i);
            }
        }

        return null;
    }

    // 获取连接状态
    public boolean getConnectStatus() {
        return this.mConnectStatus;
    }
}
