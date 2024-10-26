package com.nikhil.finalabgec;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.nikhil.finalabgec.Fragment.Profile;
import com.nikhil.finalabgec.Model.UserDataModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class Edit extends AppCompatActivity {

    String selectedImagePath="";
    public ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;

    Dialog dialog;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    String gender, uName, uBranch, uBatch, uDob, uCountry, uState, uCity;
    String uBio, uDesignation, uOrganization, uPhone, uEmail;
    String uFacebook, uInstagram, uLinkedin, uTwitter;
    boolean isContactVisible;


    ImageView backButton;
    ConstraintLayout upload_pic;
    SimpleDraweeView shopImage;
    TextView male, female;
    EditText name, branch, batch, dob, country, state, city;
    EditText bio, designation, organization, phone, email;
    EditText facebook, instagram, linkedin, twitter;
    Button saveButton, removeProfilePic;
    CheckBox contactVisibility;
    UserDataModel userDataModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Window window = Edit.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Edit.this, R.color.white));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        backButton = findViewById(R.id.backButton);
        upload_pic = findViewById(R.id.upload_picture);
        shopImage = findViewById(R.id.image);
        name = findViewById(R.id.editName);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        branch = findViewById(R.id.editBranch);
        batch = findViewById(R.id.editBatch);
        dob = findViewById(R.id.editDob);
        country = findViewById(R.id.editCountry);
        state = findViewById(R.id.editState);
        city = findViewById(R.id.editCity);
        bio = findViewById(R.id.editBio);
        designation = findViewById(R.id.editDesignation);
        organization = findViewById(R.id.editOrganization);
        phone = findViewById(R.id.editPhone);
        email = findViewById(R.id.editEmail);
        facebook = findViewById(R.id.editFacebook);
        instagram = findViewById(R.id.editInstagram);
        linkedin = findViewById(R.id.editLinkedin);
        twitter = findViewById(R.id.editTwitter);
        saveButton = findViewById(R.id.btnSave);
        contactVisibility = findViewById(R.id.checkboxContactVisibility);
        removeProfilePic = findViewById(R.id.remove_profile_pic_button);

//        spino = findViewById(R.id.spinner);
//        upload_pic = findViewById(R.id.profile_pic);
//        gend = findViewById(R.id.gender);
//        shopImage = findViewById(R.id.image);
//        layout_personal = findViewById(R.id.personal_layout);
//        personal_btn = findViewById(R.id.personal_btn);
//        date_txt = findViewById(R.id.date);
//        social_btn = findViewById(R.id.social);
//        layout_social = findViewById(R.id.social_links);
//        layout_occupation = findViewById(R.id.occupation_layout);
//        male = findViewById(R.id.male);
//        female = findViewById(R.id.female);
//        occupation_btn = findViewById(R.id.occupation);
//        back_btn = findViewById(R.id.back);
//        //year = findViewById(R.id.)
//        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
//        //occupation = findViewById(R.id.occupation_edt);
//        lay = findViewById(R.id.lay1);
//        //Editext
//        submit = findViewById(R.id.submit);
//        name = findViewById(R.id.name);
//        state = findViewById(R.id.state);
//        city = findViewById(R.id.city);
//        country = findViewById(R.id.country);
//        //mobile_no = view.findViewById(R.id.mobile_no);
//        branch = findViewById(R.id.country_p);
//        passout_yr = findViewById(R.id.passout_yr);
//        organiztion = findViewById(R.id.company);
//        designation = findViewById(R.id.designation);
//        insta = findViewById(R.id.instagram);
//        linkidin = findViewById(R.id.linkedin);
//        fb = findViewById(R.id.facebook);
//        twitter = findViewById(R.id.twitter);
//        bio = findViewById(R.id.bio);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        //spinner
//        spino.setOnItemSelectedListener(this);
//        ad = new ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_item,
//                values);
//        ad.setDropDownViewResource(
//                android.R.layout
//                        .simple_spinner_dropdown_item);
//        spino.setAdapter(ad);

        //ValueGetting();

        valueGetting();

        male.setOnClickListener(v -> {
            male.setBackgroundResource(R.drawable.bg_selector);
            female.setBackgroundResource(R.drawable.bg_male);
            gender = "Male";
        });

        female.setOnClickListener(v -> {
            female.setBackgroundResource(R.drawable.bg_selector);
            male.setBackgroundResource(R.drawable.bg_male);
            gender = "Female";
        });

        saveButton.setOnClickListener(v -> {
            uploadImage();
        });

        dob.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    Edit.this,
                    mDateSetListener,
                    year, month, day);
            dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            dialog.show();
        });

        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = day + "/" + month + "/" + year;
            dob.setText(date);
        };

        backButton.setOnClickListener(v -> {
            finish();
        });

//        upload_pic.setOnClickListener(view -> {
//            //Ask for permission
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(Edit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
//            } else {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                resultLauncher.launch(intent);
//            }
//        });
//
//        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                if (result.getData() != null) {
//                    imageUri = result.getData().getData();
//                    shopImage.setVisibility(View.VISIBLE);
//                    shopImage.setImageURI(imageUri);
//                    addImageNote(imageUri);
//                }
//            }
//        });

        // Declare your ActivityResultLauncher to handle the image selection result
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        shopImage.setVisibility(View.VISIBLE);
                        shopImage.setImageURI(imageUri);
                        addImageNote(imageUri);
                        openImagePicker();
                    }
                });

        // Set the click listener for the upload_pic view
        upload_pic.setOnClickListener(view -> {
            // Check if the OS is Android 13 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check if permission to read media images has been granted
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission to read media images if not granted
                    ActivityCompat.requestPermissions(Edit.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    // Permission granted, open image picker
                    openImagePicker();
                }
            } else {
                // For devices running Android 12 and below
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission to read external storage if not granted
                    ActivityCompat.requestPermissions(Edit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    // Permission granted, open image picker
                    openImagePicker();
                }
            }
        });

        removeProfilePic.setOnClickListener(view -> {
            shopImage.setVisibility(View.GONE);
            selectedImagePath = "";
            reference.child("dp_link").get().addOnSuccessListener(dataSnapshot -> {
                String imageUrl = dataSnapshot.getValue(String.class);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Create a reference to the existing image in Firebase Storage
                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                    // Delete the image from Firebase Storage
                    imageRef.delete().addOnSuccessListener(aVoid -> {
                        // Clear the dp_link field in the database
                        reference.child("dp_link").removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Edit.this, "Profile picture removed successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Edit.this, "Failed to update database.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Edit.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(Edit.this, "No profile picture to remove.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(Edit.this, "Failed to retrieve profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }


    private void valueGetting() {

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userDataModel = snapshot.getValue(UserDataModel.class);
                    assert userDataModel != null;
                    gender = userDataModel.getGender() != null ? userDataModel.getGender() : "Male";
                    uDob = userDataModel.getDob() != null ? userDataModel.getDob() : "";
                    uName = userDataModel.getName() != null ? userDataModel.getName() : "";
                    uBranch = userDataModel.getBranch() != null ? userDataModel.getBranch() : "";
                    uBatch = userDataModel.getBatch() != null ? userDataModel.getBatch() : "";
                    uCountry = userDataModel.getCountry() != null ? userDataModel.getCountry() : "";
                    uState = userDataModel.getState() != null ? userDataModel.getState() : "";
                    uCity = userDataModel.getCity() != null ? userDataModel.getCity() : "";
                    uBio = userDataModel.getBio() != null ? userDataModel.getBio() : "";
                    uDesignation = userDataModel.getDesignation() != null ? userDataModel.getDesignation() : "";
                    uOrganization = userDataModel.getOrganization() != null ? userDataModel.getOrganization() : "";
                    uPhone = userDataModel.getPhone() != null ? userDataModel.getPhone() : "";
                    uEmail = userDataModel.getEmail() != null ? userDataModel.getEmail() : "";
                    uFacebook = userDataModel.getFb() != null ? userDataModel.getFb() : "";
                    uInstagram = userDataModel.getInsta() != null ? userDataModel.getInsta() : "";
                    uLinkedin = userDataModel.getLinkedin() != null ? userDataModel.getLinkedin() : "";
                    uTwitter = userDataModel.getTwitter() != null ? userDataModel.getTwitter() : "";
                    isContactVisible = userDataModel.isContact_visibility();

                    String dp_uri = userDataModel.getDp_link() != null ? userDataModel.getDp_link() : "";
                    if (!dp_uri.equals("")) {
                        shopImage.setVisibility(View.VISIBLE);
                        try {
                            Uri uri = Uri.parse(dp_uri);
                            shopImage.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // setting values
                    if (gender.equals("Male")) {
                        male.setBackgroundResource(R.drawable.bg_selector);
                        female.setBackgroundResource(R.drawable.bg_male);
                        gender = "Male";
                    } else {
                        female.setBackgroundResource(R.drawable.bg_selector);
                        male.setBackgroundResource(R.drawable.bg_male);
                        gender = "Female";
                    }

                    name.setText(uName);
                    branch.setText(uBranch);
                    batch.setText(uBatch);
                    dob.setText(uDob);
                    country.setText(uCountry);
                    state.setText(uState);
                    city.setText(uCity);
                    bio.setText(uBio);
                    designation.setText(uDesignation);
                    organization.setText(uOrganization);
                    phone.setText(uPhone);
                    email.setText(uEmail);
                    facebook.setText(uFacebook);
                    instagram.setText(uInstagram);
                    linkedin.setText(uLinkedin);
                    twitter.setText(uTwitter);
                    contactVisibility.setChecked(isContactVisible);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadImage() {
        if (!selectedImagePath.isEmpty()) {
            dialog = new Dialog(Edit.this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.loading);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();


            String pushkey = reference.push().getKey();


            //for image storing
            String imagepath = "Profile/" + user.getUid() + pushkey + ".png";

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);
                                /*final String randomKey = UUID.randomUUID().toString();
                                BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
                                Bitmap bitmap_up = drawable.getBitmap();
                                String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

            try {
                InputStream stream = new FileInputStream(new File(selectedImagePath));

                storageReference.putStream(stream)
                        .addOnSuccessListener(taskSnapshot ->
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                        task -> {
                                            String image_link = Objects.requireNonNull(task.getResult()).toString();
                                            reference.child("dp_link").setValue(image_link);

                                                    /*MotionToast.Companion.darkColorToast(Edit.this,
                                                            "Posted Successfully!!",
                                                            "Hurray\uD83C\uDF89\uD83C\uDF89",
                                                            MotionToast.TOAST_SUCCESS,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.LONG_DURATION,
                                                            ResourcesCompat.getFont(Edit.this, R.font.lexend));*/
                                            //back();
                                        }));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    dialog.dismiss();
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    //progressDialog.dismiss();
                                    // animation.setVisibility(View.GONE);
                                    finish();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            //animation.setVisibility(View.GONE);

                            Toast
                                    .makeText(Edit.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                        /*.addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    // Progress Listener for loading
                                    // percentage on the dialog box
                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        double progress
                                                = (100.0
                                                * taskSnapshot.getBytesTransferred()
                                                / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage(
                                                "Uploaded "
                                                        + (int)progress + "%");
                                    }
                                });*/
        }
        else {
            dialog = new Dialog(Edit.this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.loading);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    finish();
                }
            }, 1000);
        }
        dataSend();
    }

    private void dataSend() {

        //uploadImage();
        reference.child("gender").setValue(gender);
        reference.child("dob").setValue(dob.getText().toString());
        reference.child("bio").setValue(bio.getText().toString());
        reference.child("fb").setValue(facebook.getText().toString());
        reference.child("insta").setValue(instagram.getText().toString());
        reference.child("twitter").setValue(twitter.getText().toString());
        reference.child("linkedin").setValue(linkedin.getText().toString());
        reference.child("organization").setValue(organization.getText().toString());
        reference.child("designation").setValue(designation.getText().toString());
        reference.child("country").setValue(country.getText().toString());
        reference.child("state").setValue(state.getText().toString());
        reference.child("city").setValue(city.getText().toString());
        reference.child("phone").setValue(phone.getText().toString());
        reference.child("contact_visibility").setValue(contactVisibility.isChecked());

    }


    private void addImageNote(Uri imageUri) {

        shopImage.setVisibility(View.VISIBLE);
        selectedImagePath = compressImage(imageUri + "");
        shopImage.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        //findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

    }


//    private void back(){
//        if(((FragmentActivity) Edit.this).getSupportFragmentManager().findFragmentById(R.id.drawer) != null) {
//            ((FragmentActivity) Edit.this).getSupportFragmentManager()
//                    .beginTransaction().
//                    remove(Objects.requireNonNull(((FragmentActivity) Edit.this).getSupportFragmentManager().findFragmentById(R.id.drawer))).commit();
//        }
//        ((FragmentActivity) Edit.this).getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container,new Profile())
//                .commit();
//    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(Uri.parse(imageUri),Edit.this);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight+1;
        int actualWidth = options.outWidth+1;

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
        File file = new File(Edit.this.getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }

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
            int maxBufferSize = 1 * 1024 * 1024;
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

//    private String getRealPathFromURI(String contentURI) {
//        Uri contentUri = Uri.parse(contentURI);
//        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
//        if (cursor == null) {
//            return contentUri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            Log.e("column",index+"");
//            return cursor.getString(index)+"";
//        }
//    }

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


}
