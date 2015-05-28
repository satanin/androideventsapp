package com.example.satanin.vlctechhub.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
/**
 * Created by satanin on 26/5/15.
 */
public class EventDbHelper extends SQLiteOpenHelper {

    //Datos de la BD
    final private static String NAME = "eventapp";
    final static String TABLE_NAME = "event";
    final static String ID = "_id";
    public final static String C_EVENT_ID = "event_id";
    public final static String C_TITLE = "title";
    public final static String C_DESCRIPTION = "description";
    public final static String C_LINK = "link";
    public final static String C_DATE = "date";

    //Modos edicion
    public static final String C_MODO  = "modo" ;
    public static final int C_VISUALIZAR = 551 ;
    public static final int C_CREAR = 552 ;
    public static final int C_EDITAR = 553 ;

    // Comandos
    final static String[] db_columns = {ID, C_EVENT_ID,C_TITLE,C_DESCRIPTION,C_LINK,C_DATE};
    final private static String CREATE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + C_EVENT_ID + " TEXT NOT NULL,"
                    + C_TITLE + " TEXT NOT NULL,"
                    + C_DESCRIPTION + " TEXT NOT NULL,"
                    + C_LINK+ " TEXT NOT NULL,"
                    + C_DATE + " TEXT NOT NULL )";

    private static final int DATABASE_VERSION = 1;
    final private Context mContext;



    public EventDbHelper(Context context) {
        super(context, NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor leerBaseDeDatos(SQLiteDatabase db, Integer l){
        String consulta = "";
        if (l == 0){
            consulta = " my_status = 0 ";
        }else if(l == 1){
            consulta = " my_status = 1 ";
        }
        return db.query(TABLE_NAME, db_columns, consulta, new String[] {}, null, null, null );
    }

    public Cursor getRegistro(String title, String date) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(true, TABLE_NAME, db_columns, C_TITLE + " = '" + title +"' AND "+ C_DATE + " = '" + date +"'", null, null, null, null,null);

        if (c!= null){
            c.moveToFirst();
        }
        return c;
    }

    public long insert(ContentValues reg){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLE_NAME, null, reg);
    }


    public long update(ContentValues reg){
        SQLiteDatabase db = this.getWritableDatabase();
        if (reg.containsKey(ID)){
            long id = reg.getAsLong(ID);
            reg.remove(ID);
            return db.update(TABLE_NAME, reg, "_id = " + id, null);
        }

        return db.insert(TABLE_NAME, null, reg);
    }

}
