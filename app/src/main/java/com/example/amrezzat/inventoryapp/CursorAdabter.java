package com.example.amrezzat.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.amrezzat.inventoryapp.data.Contract;


/**
 * Created by AmrEzzat on 12/3/2017.
 */

public class CursorAdabter extends CursorAdapter {
    private ImageView ProductImage;
    private TextView ProductName;
    private TextView ProductDescription;
    private TextView ProductPrice;
    private TextView ProductQuantity;
    private Button SaleBtn;

    //Default Constructor
    public CursorAdabter(Context context, Cursor c) {
        super(context, c);
    }

    //convert view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //listview n. view
        View view = LayoutInflater.from(context).inflate(R.layout.products_item, parent, false);
        return view;
    }

    //widgets new value
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //get pos. for C.item
        final Uri CurrentPosition = ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, cursor.getInt(cursor.getColumnIndex(Contract.EntryData.ID)));
        //widgets views
        ProductImage = (ImageView) view.findViewById(R.id.Product_Image);
        ProductName = (TextView) view.findViewById(R.id.Product_Name);
        ProductDescription = (TextView) view.findViewById(R.id.Product_Description);
        ProductPrice = (TextView) view.findViewById(R.id.Product_Price);
        ProductQuantity = (TextView) view.findViewById(R.id.Product_Quantity);
        SaleBtn = (Button) view.findViewById(R.id.Sale_Button);
        //btn sale contents
        final int FIQuantity = cursor.getInt(cursor.getColumnIndex(Contract.EntryData.COLUMN_QUANTITY));
        //get value con. by index,data UI preview
        Glide.with(context).load(Uri.parse(cursor.getString(cursor.getColumnIndex(Contract.EntryData.COLUMN_PICTURE)))).into(ProductImage);
        ProductName.setText(cursor.getString(cursor.getColumnIndex(Contract.EntryData.COLUMN_NAME)));
        ProductDescription.setText(cursor.getString(cursor.getColumnIndex(Contract.EntryData.COLUMN_DISCRIPTION)));
        ProductPrice.setText(cursor.getString(cursor.getColumnIndex(Contract.EntryData.COLUMN_PRICE)) + " LE");
        ProductQuantity.setText(String.valueOf(FIQuantity) + " Piece");
        //btn Sale Anonym. Action
        SaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                if (FIQuantity != 0 && FIQuantity > 0) {
                    int DecreasedQ = FIQuantity;
                    values.put(Contract.EntryData.COLUMN_QUANTITY, --(DecreasedQ));
                    v.getContext().getContentResolver().update(CurrentPosition, values, null, null);
                    context.getContentResolver().notifyChange(CurrentPosition, null);
                } else {
                    Toast.makeText(context, "'0' least num.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
