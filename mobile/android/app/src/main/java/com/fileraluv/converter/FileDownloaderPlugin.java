package com.fileraluv.converter;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.OutputStream;

@CapacitorPlugin(name = "FileDownloader")
public class FileDownloaderPlugin extends Plugin {
    @PluginMethod
    public void saveToDownloads(PluginCall call) {
        String requestedName = call.getString("fileName");
        String mimeType = call.getString("mimeType", "application/octet-stream");
        String encodedData = call.getString("data");
        if (requestedName == null || encodedData == null) {
            call.reject("A file name and file data are required.");
            return;
        }

        String fileName = requestedName.replaceAll("[^A-Za-z0-9._-]", "_");
        byte[] fileData;
        try {
            fileData = Base64.decode(encodedData, Base64.DEFAULT);
        } catch (IllegalArgumentException exception) {
            call.reject("The converted file data is invalid.");
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            call.reject("Saving to the public Downloads folder requires Android 10 or newer.");
            return;
        }

        Uri fileUri = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
            values.put(MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/FILERALUV");
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            fileUri = getContext().getContentResolver().insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (fileUri == null) throw new IllegalStateException("Could not create the download file.");

            try (OutputStream output = getContext().getContentResolver().openOutputStream(fileUri)) {
                if (output == null) throw new IllegalStateException("Could not open the download file.");
                output.write(fileData);
            }

            ContentValues completed = new ContentValues();
            completed.put(MediaStore.Downloads.IS_PENDING, 0);
            getContext().getContentResolver().update(fileUri, completed, null, null);

            JSObject result = new JSObject();
            result.put("uri", fileUri.toString());
            result.put("fileName", fileName);
            call.resolve(result);
        } catch (Exception exception) {
            if (fileUri != null) getContext().getContentResolver().delete(fileUri, null, null);
            call.reject("Could not save the converted file to Downloads.", exception);
        }
    }
}
