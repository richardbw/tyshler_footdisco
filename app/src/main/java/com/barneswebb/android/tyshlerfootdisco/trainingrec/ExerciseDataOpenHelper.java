/**
 * 
 */
package com.barneswebb.android.tyshlerfootdisco.trainingrec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.barneswebb.android.tyshlerfootdisco.util.Debug;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rbw
 *
 */
public class ExerciseDataOpenHelper extends SQLiteOpenHelper
{

    private static final String tag                 = ExerciseDataOpenHelper.class.getPackage().getName() + "]"
                                                    + ExerciseDataOpenHelper.class.getSimpleName();
    private static final int    DATABASE_VERSION    = 1;
    private static final String TRAININGHISTORY_TABLE_NAME = "myTrainingHistory";
    Context                     context;
    public static final String[] FIELD_NAMES            = new String[]{"id", "userName", "excerzDate", "excerzDur", "program", "comments"};
    private static final String PRIMARY_KEY              = FIELD_NAMES[0];

    public static final SimpleDateFormat ISO8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public ExerciseDataOpenHelper(Context context)
    {
        super(context, TRAININGHISTORY_TABLE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }



    public Context getContext()
    {
        return context;
    }



    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TRAININGHISTORY_TABLE_NAME);
        Log.i(tag, "Creating new table: " + TRAININGHISTORY_TABLE_NAME);

        StringBuilder sqlStr = new StringBuilder("CREATE TABLE " + TRAININGHISTORY_TABLE_NAME + "          (");
        
        for (String f: FIELD_NAMES) {
            sqlStr.append(f.equals(PRIMARY_KEY) ?
                             " " + PRIMARY_KEY + "   INTEGER PRIMARY KEY AUTOINCREMENT " :
                             "    ," + f + "         TEXT ");
        }
        sqlStr.append(");");


        db.execSQL(sqlStr.toString());
    }



    @Override public void onUpgrade(SQLiteDatabase db,  int oldVersion, int newVersion)
    {
        Log.w(tag, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TRAININGHISTORY_TABLE_NAME);
        onCreate(db);
    }
    
    
    public List<ExerciseSession> getExerciseRowData()
    {
        Log.d(tag, "Loading exercise data....");

        List<ExerciseSession> retArr = new ArrayList();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cur;
        
        try {
            boolean DISTINCT        = true;
            String  WHERE           = null;
            String[]SELECTIONARGS   = null;
            String  GROUPBY         = null;
            String  HAVING          = null;
            String  ORDERBY         = "excerzDate DESC";
            String  LIMIT           = null;

            cur = db.query(DISTINCT, TRAININGHISTORY_TABLE_NAME, FIELD_NAMES, WHERE, SELECTIONARGS, GROUPBY, HAVING, ORDERBY, LIMIT);
        }
        catch (Exception e)
        {
            Debug.bummer(e, context);
            return retArr;
        }
      
        cur.moveToFirst();
        while ( ! cur.isAfterLast()) {
            retArr.add(
                new ExerciseSession(
                       cur.getInt(0), //id;
                    cur.getString(1), //userName;
                    cur.getString(2), //excerzDate;
                    cur.getString(3), //excerzDur;
                    cur.getString(4), //program;
                    cur.getString(5)  //comments;
                )
            );

            cur.moveToNext();
        }
        
        db.close();
        cur.close();

        Log.d(tag, "Loaded ["+retArr.size()+"] training records");

        return retArr;
    }



    public boolean createExcercise(Map<String,String> data)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowid = db.insert(TRAININGHISTORY_TABLE_NAME, null, initConentValues(data));
        db.close();
   
        return ( rowid >= 0 );
    }

    
    public boolean saveExcercise(Map<String,String> data)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowid = db.update(TRAININGHISTORY_TABLE_NAME, initConentValues(data), PRIMARY_KEY+" = ?", new String[]{data.get(PRIMARY_KEY)});
        db.close();
   
        return ( rowid >= 0 );
    }

    private ContentValues initConentValues(Map<String, String> data) {
        ContentValues values = new ContentValues();
        for (String f: FIELD_NAMES) values.put(f, data.get(f));
        return values;
    }


    public void delAllData()
    {
        Log.w(tag, "Blitzing the lot..");
        onCreate(getWritableDatabase());
        
    }



    public boolean deleteExercise(String idStr)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowcount = db.delete(TRAININGHISTORY_TABLE_NAME, PRIMARY_KEY+" = ?", new String[] {idStr});
        db.close();
   
        return ( rowcount > 0 );
    }


    /**
     * @ see:  https://github.com/sanathp/DatabaseManager_For_Android
     */
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }


}
