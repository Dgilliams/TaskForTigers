package com.damosdesigns.taskfortigers.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.damosdesigns.taskfortigers.db.TaskDBHelper;
import com.damosdesigns.taskfortigers.db.TaskTable;

import java.sql.Array;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by damosdesigns on 4/8/15.
 */
public class TaskContentProvider extends ContentProvider {

    private TaskDBHelper db;

    static private final String AUTHORITY = "com.damosdesigns.taskfortigers.provider";

    static private final String BASE_PATH = " tasks";

    static private final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/tasks";

    static private final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/task";

    static public final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + BASE_PATH );

//set up the URIMatcher
    static private final UriMatcher sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH );
    //this says, that by default, this shit matches nothing

    static private final int TASKS = 1;
    static private final int TASK_ID = 2;

    static {
        sURIMatcher.addURI( AUTHORITY, BASE_PATH, TASKS );
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/#", TASK_ID );

    }

    @Override
    public boolean onCreate() {
        db = new TaskDBHelper( getContext() );
        return false;
    }

    //projection defines which columns of the DB are returned
    @Override
    public Cursor query( Uri uri,
                         String[] projection,
                         String selection,
                         String[] selectionArgs,
                         String sortOrder ) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns( projection ); //verify that all the requested columns exist

        queryBuilder.setTables( TaskTable.TASK_TABLE );

        int uriType = sURIMatcher.match( uri );
        switch( uriType ) {
            case TASKS:
                break;
            case TASK_ID:
                queryBuilder.appendWhere( TaskTable.COLUMN_ID + "=" + uri.getLastPathSegment() );
                break;
            default:
                throw new IllegalArgumentException( "Unknown URI " + uri );
        }

        SQLiteDatabase wdb = db.getWritableDatabase();
        Cursor cursor = queryBuilder.query( wdb,
                                            projection,
                                            selection,
                                            selectionArgs,
                                            null,
                                            null,
                                            sortOrder );
        cursor.setNotificationUri( getContext().getContentResolver(), uri );

        return cursor;
    }

    @Override
    public String getType( Uri uri ) {

        return null;
    }

    @Override
    public Uri insert( Uri uri, ContentValues values ) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase wdb = db.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;

        switch ( uriType ){
            case TASKS:
                id = wdb.insert( TaskTable.TASK_TABLE , null, values );
                break;
            default:
                throw new IllegalArgumentException( "Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange( uri, null );

        //dave will change this later
        return Uri.parse( BASE_PATH + "/" + id );
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match( uri );
        SQLiteDatabase wdb = db.getWritableDatabase();
        int rowsDeleted = 0;

        switch( uriType ){
            case TASKS:
                rowsDeleted = wdb.delete(TaskTable.TASK_TABLE, selection, selectionArgs);
                break;
            case TASK_ID:
                String id = uri.getLastPathSegment();

                if(TextUtils.isEmpty(selection)) {
                    rowsDeleted = wdb.delete( TaskTable.TASK_TABLE, TaskTable.COLUMN_ID + "=" + id, null);
                }
                else{
                    rowsDeleted = wdb.delete( TaskTable.TASK_TABLE, TaskTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException( "Unknown Uri: " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match( uri );
        SQLiteDatabase wdb = db.getWritableDatabase();
        int rowsUpdated = 0;

        switch( uriType ){
            case TASKS:
                rowsUpdated = wdb.update(TaskTable.TASK_TABLE, values, selection, selectionArgs);
                break;
            case TASK_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    rowsUpdated = wdb.update(TaskTable.TASK_TABLE, values, TaskTable.COLUMN_ID + "=" + id, null);
                }
                else{
                    rowsUpdated = wdb.update(TaskTable.TASK_TABLE, values, TaskTable.COLUMN_ID + "=" + id + " and " + selection, null);
                }
                break;
            default:
                throw new IllegalArgumentException( "Unknown Uri: " + uri);
        }

        return rowsUpdated;
    }

    private void checkColumns( String[] projection ){
        String[] available = { TaskTable.COLUMN_ID,
                               TaskTable.COLUMN_PRIORITY,
                               TaskTable.COLUMN_SUMMARY,
                               TaskTable.COLUMN_DESCRIPTION };
        if ( projection != null ) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList( projection ) );
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList( available ) );

            if ( !availableColumns.containsAll( requestedColumns ) ){
                throw new IllegalArgumentException( "Unknown columns in projection ");
            }
        }
    }

}
