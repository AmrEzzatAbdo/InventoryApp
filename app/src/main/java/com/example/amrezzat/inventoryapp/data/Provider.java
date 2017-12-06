package com.example.amrezzat.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by AmrEzzat on 12/3/2017.
 */

public class Provider extends ContentProvider {
    //to access Db readable
    private DBHelper OpenHelper;
    //2 varios id to dif. betwean Matcher
    private static final int PRODUCTS_TABLE = 100;
    private static final int PRODUCTS_ROW_ID = 101;
    //give no match to matcher
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    //Uri definition
    static {
        //whole table
        MATCHER.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_PRODUCTS, PRODUCTS_TABLE);
        //row in table. '/#'row id come after it
        MATCHER.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_PRODUCTS + "/#", PRODUCTS_ROW_ID);
    }

    @Override
    public boolean onCreate() {
        //DB preparing
        OpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //request type
        Cursor cursor = null;
        try {
            switch (MATCHER.match(uri)) {
                case PRODUCTS_TABLE:
                    cursor = OpenHelper.getReadableDatabase().query(Contract.EntryData.NAME_OF_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case PRODUCTS_ROW_ID:
                    //get selection and selection by id and uri
                    selection = Contract.EntryData.ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    cursor = OpenHelper.getReadableDatabase().query(Contract.EntryData.NAME_OF_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
            }
        } catch (Exception e) {
            Log.e("Provider ", "query: ", e);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case PRODUCTS_TABLE:
                return Contract.EntryData.CURSOR_DIR_BASE_TYPE;
            case PRODUCTS_ROW_ID:
                return Contract.EntryData.CURSOR_ITEM_BASE_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri Ruri = null;
        try {
            //row id validation
            long id = OpenHelper.getWritableDatabase().insert(Contract.EntryData.NAME_OF_TABLE, null, values);
            if (id > 0 && id != 0) {
                Ruri = ContentUris.withAppendedId(Contract.EntryData.CONTENT_URI, id);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            Log.e("Provider class", "", e);
        }
        return Ruri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowDeleted;
        switch (MATCHER.match(uri)) {
            //wipeTable
            case PRODUCTS_TABLE:
                rowDeleted = OpenHelper.getWritableDatabase().delete(Contract.EntryData.NAME_OF_TABLE, selection, selectionArgs);
                break;
            //delete row in table
            case PRODUCTS_ROW_ID:
                selection = Contract.EntryData.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = OpenHelper.getWritableDatabase().delete(Contract.EntryData.NAME_OF_TABLE, selection, selectionArgs);
                break;
            default:
                rowDeleted = 0;
        }
        if (rowDeleted == 0) {
            Toast.makeText(getContext(), "deletion Error", Toast.LENGTH_SHORT).show();
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowUpdated;
        switch (MATCHER.match(uri)) {
            case PRODUCTS_TABLE:
                rowUpdated = OpenHelper.getWritableDatabase().update(Contract.EntryData.NAME_OF_TABLE, values, selection, selectionArgs);
                break;
            case PRODUCTS_ROW_ID:
                selection = Contract.EntryData.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = OpenHelper.getWritableDatabase().update(Contract.EntryData.NAME_OF_TABLE, values, selection, selectionArgs);
                break;
            default:
                rowUpdated = 0;
        }
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Toast.makeText(getContext(), "Updated Error", Toast.LENGTH_SHORT).show();
        }
        return rowUpdated;
    }
}
