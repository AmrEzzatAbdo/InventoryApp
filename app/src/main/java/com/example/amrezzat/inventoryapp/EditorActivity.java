package com.example.amrezzat.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.amrezzat.inventoryapp.data.Contract;

import java.io.File;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //Widgets
    private ImageView ProductImage;
    private EditText ProductName;
    private EditText ProductPrice;
    private EditText ProductQuantity;
    private EditText ProductDescription;
    private Button deleteBtn, orderMoreBtn;
    private ImageButton decreaseQ, increseQ;
    //used in decreased btn , increased btn and Email Intent.
    public String Quantity;
    private String name;
    int id;
    //Uri photo
    private String PhotoURI;
    Drawable oldDrawable;
    //projection product Query
    public final String[] ProjectionQueryProduct = {
            Contract.EntryData.ID
            , Contract.EntryData.COLUMN_NAME
            , Contract.EntryData.COLUMN_QUANTITY
            , Contract.EntryData.COLUMN_PRICE
            , Contract.EntryData.COLUMN_PICTURE
            , Contract.EntryData.COLUMN_DISCRIPTION
    };
    //Product Column index
    final int CName = 1;
    final int CQuantity = 2;
    final int CPrice = 3;
    final int CPicture = 4;
    final int CDescription = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //find widgets View
        ProductImage = (ImageView) findViewById(R.id.Editor_ProductImage);
        ProductName = (EditText) findViewById(R.id.Editor_ProductName);
        ProductPrice = (EditText) findViewById(R.id.Editor_ProductPrice);
        ProductQuantity = (EditText) findViewById(R.id.Editor_ProductQuantity);
        ProductDescription = (EditText) findViewById(R.id.Editor_ProductDescription);
        deleteBtn = (Button) findViewById(R.id.EditorBtn_delete);
        orderMoreBtn = (Button) findViewById(R.id.EditorBtn_OrderMore);
        decreaseQ = (ImageButton) findViewById(R.id.IncreaseBtn_quantity);
        increseQ = (ImageButton) findViewById(R.id.decreaseBtn_quantity);
        //to check image validation
        oldDrawable = ProductImage.getDrawable();
        //perform UI widgets
        if (getIntent().getData() == null) {
            setTitle("Insert new product");
            decreaseQ.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            increseQ.setVisibility(View.GONE);
            orderMoreBtn.setVisibility(View.GONE);
        } else {
            setTitle("Update " + name + " product");
            decreaseQ.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            increseQ.setVisibility(View.VISIBLE);
            orderMoreBtn.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(0, null, this);
        }
    }

    //if no empty values,inserting data
    public void checkMissingFieldValidationAndInsertorUpdatedata() {
        if (TextUtils.isEmpty(ProductName.getText().toString().trim()) || TextUtils.isEmpty(ProductQuantity.getText().toString().trim()) || TextUtils.isEmpty(ProductPrice.getText().toString().trim()) || TextUtils.isEmpty(ProductDescription.getText().toString().trim()) || oldDrawable == ProductImage.getDrawable()) {
            Toast.makeText(this, "Please insert all Required fields", Toast.LENGTH_SHORT).show();
        } else {
            //to get data from editTexts and put it in 'EntryData'
            ContentValues values = new ContentValues();
            values.put(Contract.EntryData.COLUMN_NAME, ProductName.getText().toString().trim());
            values.put(Contract.EntryData.COLUMN_DISCRIPTION, ProductDescription.getText().toString().trim());
            values.put(Contract.EntryData.COLUMN_PICTURE, PhotoURI);
            values.put(Contract.EntryData.COLUMN_QUANTITY, ProductQuantity.getText().toString().trim());
            values.put(Contract.EntryData.COLUMN_PRICE, ProductPrice.getText().toString().trim());
            //insert new row if intent haven't data
            if (getIntent().getData() == null) {
                Uri insertRow = getContentResolver().insert(Contract.EntryData.CONTENT_URI, values);
                if (insertRow != null) {
                    Toast.makeText(this, "Insert Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
            }
            //if item list 'intent' have data update it
            else {
                int rowUpdated = getContentResolver().update(getIntent().getData(), values, null, null);
                if (rowUpdated != 0) {
                    Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditorActivity.this, MainActivity.class));
                } else
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Action Increase quantity button
    public void Btn_IncreaseQuantity(View view) {
        ContentValues values = new ContentValues();
        try {
            int quantity = Integer.parseInt(Quantity);
            values.put(Contract.EntryData.COLUMN_QUANTITY, ++quantity);
            view.getContext().getContentResolver().update(ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, id), values, null, null);
            getContentResolver().notifyChange(ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, id), null);
        } catch (Exception e) {
            Log.e("EditorActivity", "Btn_IncreaseQuantity: ", e);
        }
    }

    //Action decrease quantity button
    public void Btn_DecreaseQuantity(View view) {
        ContentValues values = new ContentValues();
        try {
            int quantity = Integer.parseInt(Quantity);
            if (quantity > 0) {
                values.put(Contract.EntryData.COLUMN_QUANTITY, --quantity);
                view.getContext().getContentResolver().update(ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, id), values, null, null);
                getContentResolver().notifyChange(ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, id), null);
            } else {
                Toast.makeText(this, "Out of range", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("EditorActivity", "Btn_DecreaseQuantity: ", e);
        }
    }

    //Action Btn delete
    public void Btn_delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage("Are u sur delete?!");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmationDelete();
                startActivity(new Intent(EditorActivity.this, MainActivity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //to confirmation delete
    public void confirmationDelete() {
        try {
            int rowDeletedId = getContentResolver().delete(getIntent().getData(), null, null);
            if (rowDeletedId != 0) {
                Toast.makeText(this, "Deletion operation Successful", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Failed deletion operation", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Editor Activity", "confirmationDelete: ", e);
        }
    }

    //Action Btn order More
    public void Btn_OrderMore(View view) {
        Toast.makeText(this, "Order More", Toast.LENGTH_SHORT).show();
        try {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("message/text");
            email.putExtra(Intent.EXTRA_EMAIL, "amr@yahoo.com");
            email.putExtra(Intent.EXTRA_SUBJECT, "order more from" + name);
            email.putExtra(Intent.EXTRA_TEXT, "that's Quantity in inventory " + Quantity + "From product :" + name + " " + " can u send more from it");
            startActivity(email);
        } catch (Exception e) {
            Log.e("EditorActivity ", "Btn_OrderMore: ", e);
        }
    }

    //Action btn save
    public void Btn_Save(View view) {
        checkMissingFieldValidationAndInsertorUpdatedata();
    }

    //Image Click Action
    public void ImageAction(View view) {
        try {
            Intent PickAction = new Intent(Intent.ACTION_PICK);
            File directoryAccess = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            PickAction.setDataAndType(Uri.parse(directoryAccess.getPath()), "image/*");
            startActivityForResult(PickAction, 11);
        } catch (Exception e) {
            Log.e("EditorActivity", "ImageAction: ", e);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, getIntent().getData(), ProjectionQueryProduct, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst() || data.getCount() < 1 || data == null) {
            return;
        } else {
            //update value of quantity
            id = data.getInt(data.getColumnIndex(Contract.EntryData.ID));
            //used in Email message contents
            name = data.getString(CName);
            Quantity = String.valueOf(data.getInt(CQuantity));
            PhotoURI = data.getString(CPicture);
            //Update's UI data
            ProductName.setText(name);
            ProductQuantity.setText(Quantity);
            ProductPrice.setText(String.valueOf(data.getFloat(CPrice)));
            Glide.with(this).load(PhotoURI).into(ProductImage);
            ProductDescription.setText(data.getString(CDescription));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //update/get Photo uri and preview by 'Glide'
        try {
            PhotoURI = data.getData().toString();
            Glide.with(this).load(PhotoURI).into(ProductImage);
        } catch (Exception e) {
            Log.e("EditorActivity", "onActivityResult: ", e);
        }
    }
}
