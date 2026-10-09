package com.fileraluv.converter;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CapacitorPlugin(name = "FileDownloader")
public class FileDownloaderPlugin extends Plugin {
    private final ConcurrentHashMap<String, PendingDownload> downloads = new ConcurrentHashMap<>();

    @PluginMethod
    public void startDownload(PluginCall call) {
        String requestedName = call.getString("fileName");
        String mimeType = call.getString("mimeType", "application/octet-stream");
        if (requestedName == null || requestedName.isEmpty()) {
            call.reject("A file name is required.");
            return;
        }

        String fileName = requestedName.replaceAll("[^A-Za-z0-9._-]", "_");
        Uri uri = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
            values.put(MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/FILERALUV");
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            uri = getContext().getContentResolver().insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new IOException("Could not create the download file.");
            OutputStream output = getContext().getContentResolver().openOutputStream(uri);
            if (output == null) throw new IOException("Could not open the download file.");

            String downloadId = UUID.randomUUID().toString();
            downloads.put(downloadId, new PendingDownload(uri, output));
            JSObject result = new JSObject();
            result.put("downloadId", downloadId);
            call.resolve(result);
        } catch (Exception exception) {
            if (uri != null) getContext().getContentResolver().delete(uri, null, null);
            call.reject("Could not create a file in Downloads/FILERALUV.", exception);
        }
    }

    @PluginMethod
    public void appendDownloadChunk(PluginCall call) {
        String downloadId = call.getString("downloadId");
        String encodedChunk = call.getString("data");
        PendingDownload pending = downloads.get(downloadId);
        if (pending == null || encodedChunk == null) {
            call.reject("The download is no longer available.");
            return;
        }

        try {
            byte[] bytes = Base64.decode(encodedChunk, Base64.DEFAULT);
            synchronized (pending) {
                pending.output.write(bytes);
            }
            call.resolve();
        } catch (Exception exception) {
            cancelDownload(downloadId);
            call.reject("Could not write the converted file.", exception);
        }
    }

    @PluginMethod
    public void finishDownload(PluginCall call) {
        String downloadId = call.getString("downloadId");
        PendingDownload pending = downloads.remove(downloadId);
        if (pending == null) {
            call.reject("The download is no longer available.");
            return;
        }

        try {
            synchronized (pending) {
                pending.output.close();
            }
            ContentValues completed = new ContentValues();
            completed.put(MediaStore.Downloads.IS_PENDING, 0);
            getContext().getContentResolver().update(pending.uri, completed, null, null);
            JSObject result = new JSObject();
            result.put("uri", pending.uri.toString());
            call.resolve(result);
        } catch (Exception exception) {
            getContext().getContentResolver().delete(pending.uri, null, null);
            call.reject("Could not finish saving the converted file.", exception);
        }
    }

    @PluginMethod
    public void cancelDownload(PluginCall call) {
        String downloadId = call.getString("downloadId");
        cancelDownload(downloadId);
        call.resolve();
    }

    private void cancelDownload(String downloadId) {
        PendingDownload pending = downloads.remove(downloadId);
        if (pending == null) return;
        try {
            synchronized (pending) {
                pending.output.close();
            }
        } catch (IOException ignored) {
        }
        getContext().getContentResolver().delete(pending.uri, null, null);
    }

    private static final class PendingDownload {
        final Uri uri;
        final OutputStream output;

        PendingDownload(Uri uri, OutputStream output) {
            this.uri = uri;
            this.output = output;
        }
    }
}
