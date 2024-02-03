package com.nikhil.finalabgec.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.nikhil.finalabgec.Fragment.AddJob.REQUEST_CODE_STORAGE_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nikhil.finalabgec.R;
import com.nikhil.finalabgec.FCM.topic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import www.sanju.motiontoast.MotionToast;


public class AddPost extends Fragment {
    public String name = "", dp = "", branch = "";
    View view;
    Context contextNullSafe;
    SimpleDraweeView imageNote;
    String selectedImagePath = "";
    LinearLayout addImage;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference, user_ref;
    ConstraintLayout lay;
    Dialog dialog;
    ImageView back;
    TextView save;
    EditText title, description, link;
    ActivityResultLauncher<Intent> startActivityForImage;

    private static String getRealPathFromURI(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    public static boolean check_URL(String str) {
        try {
            new URL(str).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("ShowToast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_post, container, false);


        addImage = view.findViewById(R.id.linearLayout1);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.body);
        imageNote = view.findViewById(R.id.image);
        link = view.findViewById(R.id.link);
        save = view.findViewById(R.id.submit);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        lay = view.findViewById(R.id.layout);
        back = view.findViewById(R.id.back);

        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        startActivityForImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Uri selectedImageUri = result.getData().getData();
                        addImageNote(selectedImageUri);
                    }
                }
        );


        back.setOnClickListener(v -> back());

        addImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                selectImage();
            }
        });

        save.setOnClickListener(v -> {

            if (!title.getText().toString().trim().equals("")) {
                if (!description.getText().toString().trim().equals("")) {
                    if (!link.getText().toString().trim().equals("")) {
                        String url = link.getText().toString().trim();

                        //Matching the given phone number with regular expression


                        if (check_URL(url)) {
                            dataPush();
                        }
                        else {
                            MotionToast.Companion.darkColorToast(requireActivity(),
                                    "Error",
                                    "Please add a valid Link",
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(requireActivity(), R.font.poppins));
                        }
                    }else{
                        dataPush();
                    }

                } else {
                    description.setError("Empty");
                    Snackbar.make(lay, "Please add a link for applying to job", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#171746"))
                            .setTextColor(Color.parseColor("#FF7F5C"))
                            .setBackgroundTint(Color.parseColor("#171746"));
                }
            } else {
                title.setError("Empty");
                Snackbar.make(lay, "Please Add Job's Title", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }


        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        return view;
    }

    private void dataPush() {


        if (!selectedImagePath.equals("")) {

            dialog = new Dialog(getContextNullSafety());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.loading);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            reference = FirebaseDatabase.getInstance().getReference().child("posts");
            String pushkey = reference.push().getKey();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(selectedImagePath);
                            /*final String randomKey = UUID.randomUUID().toString();
                            BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
                            Bitmap bitmap_up = drawable.getBitmap();
                            String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

            try {
                InputStream stream = new FileInputStream(selectedImagePath);

                storageReference.putStream(stream)
                        .addOnSuccessListener(taskSnapshot ->
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                        task -> {
                                            String image_link = Objects.requireNonNull(task.getResult()).toString();


                                            //for image storing
                                            String imagepath = "Posts/" + title.getText().toString().trim() + pushkey + ".png";


                                            //uploadImage();

                                            Calendar cal = Calendar.getInstance();
                                            SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                                            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {


                                                        name = snapshot.child(user.getUid()).child("name").getValue(String.class);
                                                        dp = snapshot.child(user.getUid()).child("dp_link").getValue(String.class);
                                                        assert pushkey != null;
                                                        reference.child(pushkey).child("pushkey").setValue(pushkey);
                                                        reference.child(pushkey).child("name").setValue(name);
                                                        reference.child(pushkey).child("title").setValue(title.getText().toString());
                                                        reference.child(pushkey).child("description").setValue(description.getText().toString());
                                                        reference.child(pushkey).child("link").setValue(link.getText().toString());
                                                        reference.child(pushkey).child("image_link").setValue(image_link);
                                                        reference.child(pushkey).child("like").setValue("0");
                                                        reference.child(pushkey).child("dp_link").setValue(dp);
                                                        reference.child(pushkey).child("date").setValue(simpleDateFormat_time.format(cal.getTime()) + "");
                                                        reference.child(pushkey).child("uid").setValue(user.getUid());

                                                        topic topic = new topic();
                                                        topic.noti(name.toUpperCase() + " posted ", title.getText().toString().toUpperCase() + ", Tap to open.", "fromjob");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });


                                            dialog.dismiss();
                                            MotionToast.Companion.darkColorToast(requireActivity(),
                                                    "Posted Successfully!!",
                                                    "Hurray\uD83C\uDF89\uD83C\uDF89",
                                                    MotionToast.TOAST_SUCCESS,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(getContextNullSafety(), R.font.poppins));
                                            back();
                                        }));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            MotionToast.Companion.darkColorToast(requireActivity(),
                    "Error",
                    "Please select an image",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContextNullSafety(), R.font.poppins));
        }
//        } else {
//            reference = FirebaseDatabase.getInstance().getReference().child("posts");
//            String pushkey = reference.push().getKey();
//
//            Calendar cal = Calendar.getInstance();
//            SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
//
//
//
//
//            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.child(user.getUid()).child("token").exists()) {
//                        assert pushkey != null;
//                        reference.child(pushkey).child("pushkey").setValue(pushkey);
//                        name = snapshot.child(user.getUid()).child("name").getValue(String.class);
//                        dp = snapshot.child(user.getUid()).child("dp_link").getValue(String.class);
//
//                        reference.child(pushkey).child("name").setValue(name);
//                        reference.child(pushkey).child("title").setValue(title.getText().toString());
//                        reference.child(pushkey).child("description").setValue(description.getText().toString());
//                        reference.child(pushkey).child("link").setValue(link.getText().toString());
//                        reference.child(pushkey).child("image_link").setValue("");
//                        reference.child(pushkey).child("like").setValue("0");
//                        reference.child(pushkey).child("dp_link").setValue(dp);
//                        reference.child(pushkey).child("date").setValue(simpleDateFormat_time.format(cal.getTime()) + "");
//
//                        //topic topic = new topic();
//                        //topic.noti(name.toUpperCase() + " posted ", title.getText().toString().toUpperCase() + ", Tap to open.", "fromjob");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });
//
//
//        }


    }

    private void back() {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(AddPost.this).commit();
    }

    private void selectImage() {
        // If you have access to the external storage, do whatever you need
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForImage.launch(intent);
    }

    private void addImageNote(Uri imageUri) {

        imageNote.setVisibility(View.VISIBLE);
        selectedImagePath = compressImage(imageUri + "");
        imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        //view.findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(Uri.parse(imageUri), getContextNullSafety());
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight + 1;
        int actualWidth = options.outWidth + 1;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            assert scaledBitmap != null;
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(getContextNullSafety().getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContextNullSafety().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            Log.e("column", index + "");
            return cursor.getString(index) + "";
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}