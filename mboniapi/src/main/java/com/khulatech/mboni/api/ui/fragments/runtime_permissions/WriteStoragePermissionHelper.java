package com.khulatech.mboni.api.ui.fragments.runtime_permissions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.khulatech.mboni.api.R;


/**
 * Created by Joane SETANGNI on 05/07/2016.
 */
public class WriteStoragePermissionHelper extends Fragment {
    private static final int REQUEST_PERMISSIONS = 13;
    public static final String TAG = "CamerasWrtieStoragePerm";

    private WriteStoragePermissionCallback mCallback;
    private Context mContext;
    private String[] neededPermissions;

    public static WriteStoragePermissionHelper newInstance(WriteStoragePermissionCallback callback, Context context) {
        WriteStoragePermissionHelper instance = new WriteStoragePermissionHelper();
        instance.setContextCallback(callback, context);
        return instance;
    }

    public WriteStoragePermissionHelper() {
        // Required empty public constructor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            neededPermissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }else {
            neededPermissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    private void setContextCallback(@NonNull WriteStoragePermissionCallback callback,@NonNull Context context) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(mCallback != null) {
            checkPermissions();
        } else {
            throw new IllegalArgumentException("Callback is null " +
                    "Perhaps you didn't use the "+WriteStoragePermissionHelper.class+"#newInstance(Context contextCallback)");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private boolean checkshouldShowRequestPermissionRationale(){
        for(String permissions : neededPermissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions)){
                return true;
            }
        }
        return false;
    }

    public void checkPermissions() {
        if (PermissionUtil.hasSelfPermission(mContext, neededPermissions)) {
            mCallback.onWriteStoragePermissionGranted();
        } else {
            if (checkshouldShowRequestPermissionRationale()) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.mboni_api_permission);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(neededPermissions, REQUEST_PERMISSIONS);
                    }
                });
                builder.setNegativeButton(R.string.mboni_api_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setMessage(getString(R.string.mboni_api_permission_write_external_dir_explanation));
                dialog.show();
            } else {
                requestPermissions(neededPermissions, REQUEST_PERMISSIONS);
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                mCallback.onWriteStoragePermissionGranted();
            } else {
                mCallback.onWriteStoragePermissionDenied();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public interface WriteStoragePermissionCallback {
        void onWriteStoragePermissionGranted();
        void onWriteStoragePermissionDenied();
    }

}
