package com.example.kephas73.meera.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kephas73.meera.Const;
import com.example.kephas73.meera.Database;
import com.example.kephas73.meera.MainActivity;
import com.example.kephas73.meera.Message;
import com.example.kephas73.meera.Model.User;
import com.example.kephas73.meera.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    CircleImageView mProfileImage;
    TextView mUserName;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private StorageTask uploadTask;
    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Mapping(view);
        ShowProfileUser();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });

        return view;
    }

    private void Mapping(View view) {
        mProfileImage =view.findViewById(R.id.profileImage);
        mUserName = view.findViewById(R.id.userName);
    }

    private void ShowProfileUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUserName.setText(user.getUserName());
                if (user.getImageURL().equals("default")) {
                    // Set hình ảnh có sẵn trong máy tính cửa bạn
                    mProfileImage.setImageResource(R.drawable.ic_action_default);
                } else {
                    // Set hình ảnh từ website khá
                    Glide.with(ProfileFragment.this).load(user.getImageURL()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void OpenImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadImage () {
        // Lưu vào thư mục uploads
        storageReference = FirebaseStorage.getInstance().getReference(Database.FOLDER_IMAGE);
        // Lấy user hiện tại
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Hiển thị tiến trình.
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(Message.MESS_PROGRESS_UPLOADS);
        pd.show();

        if (imageUri != null ) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                                    +"."+GetFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
           uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
               @Override
               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if (!task.isSuccessful()) {
                       throw task.getException();
                   }
                   return fileReference.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if (task.isSuccessful()) {
                       Uri downloadUri = task.getResult();
                       String mUri = downloadUri.toString();

                       reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_USER).child(firebaseUser.getUid());
                       HashMap<String, Object> map = new HashMap<>();
                       map.put(Database.TABLE_USER_IMAGE, mUri);
                       reference.updateChildren(map);

                       pd.dismiss();

                   } else {
                       Toast.makeText(getContext(), Message.ERROR_UPDATE_IMAGE, Toast.LENGTH_SHORT).show();
                       pd.dismiss();
                   }
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                   pd.dismiss();
               }
           });
        } else {
            Toast.makeText(getContext(), Message.ERROR_NO_IMAGE, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
     public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                UploadImage();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
