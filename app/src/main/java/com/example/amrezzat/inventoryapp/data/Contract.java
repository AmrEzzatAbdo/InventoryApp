package com.example.amrezzat.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AmrEzzat on 12/3/2017.
 */

public class Contract {
    //package name
    public static final String CONTENT_AUTHORITY = "com.example.amrezzat.inventoryapp";
    //communication whith provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Whole table path
    public static final String PATH_PRODUCTS = "productsApp";

    //spic table DB content
    public static final class EntryData implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        //table Name
        public static final String NAME_OF_TABLE = "productsApp";
        //Product Column Id
        public static final String ID = BaseColumns._ID;
        //Product Column name
        public static final String COLUMN_NAME = "product_name";
        //Product Column quantity
        public static final String COLUMN_QUANTITY = "product_quantity";
        //Product Column price
        public static final String COLUMN_PRICE = "product_price";
        //Product Column pictur 'as Text'
        public static final String COLUMN_PICTURE = "product_picture";
        //Descr. Column
        public static final String COLUMN_DISCRIPTION = "product_discription";
        //handle connection path
        public static final String CURSOR_DIR_BASE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CURSOR_ITEM_BASE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

    }
}
