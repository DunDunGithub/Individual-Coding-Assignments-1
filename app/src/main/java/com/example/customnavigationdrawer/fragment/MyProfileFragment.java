package com.example.customnavigationdrawer.fragment;

import static com.example.customnavigationdrawer.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.customnavigationdrawer.MainActivity;
import com.example.customnavigationdrawer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyProfileFragment extends Fragment {

    private View mView;
    private ImageView imgSetAvatar;
    private EditText edtSetFullName, edtSetEmail;
    private Button btnUpdateProfile;
    private Uri mUri;
    private MainActivity mMainActivity;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        mMainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        initUI();
        setUserInformation();
        initListener();
        
        return mView;
    }


    private void initUI(){
        imgSetAvatar = mView.findViewById(R.id.img_setAvatar);
        edtSetEmail = mView.findViewById(R.id.edt_setEmail);
        edtSetFullName = mView.findViewById(R.id.edt_setName);
        btnUpdateProfile = mView.findViewById(R.id.btn_update_profile);
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

        edtSetFullName.setText(user.getDisplayName());
        edtSetEmail.setText(user.getEmail());
        Glide.with((getActivity())).load(user.getPhotoUrl()).error(R.drawable.defaultavatar).into(imgSetAvatar);
    }

    private void initListener(){
        imgSetAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermistion();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });
    }

    private void onClickRequestPermistion() {

        if(mMainActivity == null){
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mMainActivity.openGallery();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mMainActivity.openGallery();
        }
        else{
            String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgSetAvatar.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri){
        this.mUri = mUri;
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return;
        }

        progressDialog.setTitle("Updating your profile...");
        progressDialog.show();

        String strFullName = edtSetFullName.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullName)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.cancel();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update Profile Success!", Toast.LENGTH_SHORT).show();
                            mMainActivity.showUserInformation();
                        }
                    }
                });
    }


}
