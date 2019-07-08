package cordova.plugin.android.enumeratedevices;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;

import android.media.MicrophoneInfo;
import android.media.AudioManager;
import android.media.AudioDeviceInfo;
import java.util.ArrayList;



import android.app.Activity;
import android.content.Context;


/**
 * This class echoes a string called from JavaScript.
 */
public class EnumerateDevicesPlugin extends CordovaPlugin {
    private Context context;
    private Activity activity;
    JSONArray devicesArray = new JSONArray();



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("enumerateDevices")){

            this.enumerateDevices(args, callbackContext);

            return true;
        }
        return false;
    }

    private void enumerateDevices(JSONArray args, CallbackContext callback){

        this.context = cordova.getActivity().getApplicationContext();
        this.activity = cordova.getActivity();
        this.devicesArray = new JSONArray();

        this.getMics();
        this.getCams();

        callback.success(this.devicesArray);

    }

    private void getMics () {
        AudioManager audioManager = (AudioManager) this.activity.getSystemService(this.context.AUDIO_SERVICE);
        ArrayList<String> str = new ArrayList();
        AudioDeviceInfo[] mics = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        String label = "";
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        for (int i = 0; i < mics.length; i++) {
            Integer type = mics[i].getType();
            if( (type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO || type == AudioDeviceInfo.TYPE_BUILTIN_MIC || type == AudioDeviceInfo.TYPE_WIRED_HEADSET || type == AudioDeviceInfo.TYPE_USB_DEVICE) && mics[i].isSource()) {
                JSONObject device = new JSONObject();

                label = this.getAudioType(mics[i]);
                try {
                    device.put("deviceId", Integer.toString( mics[i].getId()));
                    device.put("groupId", "");
                    device.put("kind", "audioinput");
                    device.put("label", label);
                    this.devicesArray.put(device);

                } catch (JSONException e) {
                    System.out.println("ERROR JSONException " + e.toString());
                }
            }

        }

    }


    private void getCams () {
        // Video inputs
        CameraManager camera = (CameraManager) this.activity.getSystemService(this.context.CAMERA_SERVICE);

        try {
            String[] cameraId = camera.getCameraIdList();
            CameraCharacteristics characteristics;
            String label  =  "";

            for(int i = 0; i < cameraId.length; i++) {
                JSONObject device = new JSONObject();
                characteristics  = camera.getCameraCharacteristics(cameraId[i]);
                label = this.getVideoType(characteristics);
                device.put("deviceId", cameraId[i]);
                device.put("groupId", "");
                device.put("kind", "videoinput");
                device.put("label", label);
                this.devicesArray.put(device);
            }

        } catch (CameraAccessException e) {
            System.out.println("ERROR IOException " + e.toString());

        } catch (JSONException e) {
            System.out.println("ERROR IOException " + e.toString());
        }

    }


    private String getAudioType(AudioDeviceInfo input){
        String deviceType = "";

        switch (input.getType()) {
            case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                deviceType = input.getProductName().toString() + " Bluetooth";
                break;
            case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                deviceType = input.getProductName().toString() + " Built-in Microphone";
                break;
            case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                deviceType = input.getProductName().toString() + " Wired Microphone";
                break;
            case AudioDeviceInfo.TYPE_USB_DEVICE:
                deviceType = input.getProductName().toString() + " USB Microphone";
                break;
            default:
                deviceType = "Unknown device";
        }

        return deviceType;
    }

    private String getVideoType(CameraCharacteristics input){
        String deviceType = "";
        String num = "";

        try {
            for (int i = 0; i < this.devicesArray.length(); ++i) {
                JSONObject obj = this.devicesArray.getJSONObject(i);
                String id = obj.getString("label");
                if (id.contains("External")) {
                    num = Integer.toString(Integer.parseInt(num) + 1);
                }
            }
        } catch (JSONException e) {
            System.out.println("ERROR JSONException " + e.toString());
        }

        switch (input.get(CameraCharacteristics.LENS_FACING)) {
            case CameraCharacteristics.LENS_FACING_FRONT:
                deviceType = "Front Camera";
                break;
            case CameraCharacteristics.LENS_FACING_BACK:
                deviceType = "Back Camera";
                break;
            case CameraCharacteristics.LENS_FACING_EXTERNAL:
                deviceType = "External Camera" + num;
                break;
            default:
                deviceType = "Unknown device";
        }

        return deviceType;

    }

}
