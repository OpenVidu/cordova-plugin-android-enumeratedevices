var exec = require('cordova/exec');

module.exports.getEnumerateDevices  = function (arg0, success, error) {

    if (typeof arguments[0] !== 'function') {
		isPromise = true;
	} else {
		isPromise = false;
		callback = arguments[0];
    }
    
    if (isPromise) {
		return new Promise(function (resolve) {
			function onResultOK(devices) {
				console.info('enumerateDevices() | success');
				resolve(getMediaDeviceInfos(devices));
			}

            exec(onResultOK, error, 'EnumerateDevicesPlugin', 'enumerateDevices', [arg0]);

		});
	}

    function onResultOK(devices) {
		console.info('enumerateDevices() | success');
		callback(getMediaDeviceInfos(devices));
    }
    exec(onResultOK, error, 'EnumerateDevicesPlugin', 'enumerateDevices', [arg0]);

}

/** 
 * Private API
 */
function getMediaDeviceInfos(devices) {
	console.info('getMediaDeviceInfos() ', devices);

	var id,
		mediaDeviceInfos = [];

	for (id in devices) {
		if (devices.hasOwnProperty(id)) {
			mediaDeviceInfos.push(mediaDeviceInfo(devices[id]));
		}
	}

	return devices;
}

function mediaDeviceInfo(data) {
	data = data || {};
	
	return {
		// MediaDeviceInfo spec.
		deviceId: {
			value: data.deviceId
		},
		kind: {
			value: data.kind
		},
		label: {
			value: data.label
		},
		groupId: {
			value: data.groupId || ''
		}
	};
}