package com.example.parkingfeecalculator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
private static final String DATABASE_NAME = &quot;parking.db&quot;;
private static final int DATABASE_VERSION = 2;
private static final String TABLE_NAME = &quot;parking_records&quot;;
public DatabaseHelper(Context context) {
super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db) {
String query = &quot;CREATE TABLE &quot; + TABLE_NAME + &quot; (&quot; +
&quot;id INTEGER PRIMARY KEY AUTOINCREMENT, &quot; +
&quot;vehicle_number TEXT NOT NULL, &quot; +
&quot;vehicle_type TEXT NOT NULL, &quot; +
&quot;duration REAL NOT NULL, &quot; +
&quot;fee REAL NOT NULL, &quot; +
&quot;date TEXT NOT NULL)&quot;;
db.execSQL(query);
}
@Override
public void onUpgrade(
SQLiteDatabase db,
int oldVersion,
int newVersion) {
if (oldVersion &lt; 2) {
// SQLite can store decimal values in an INTEGER-affinity
// column, but this migration makes the fee column REAL.
db.execSQL(
&quot;ALTER TABLE &quot; + TABLE_NAME +
&quot; RENAME TO parking_records_old&quot;
);
String createQuery = &quot;CREATE TABLE &quot; + TABLE_NAME + &quot; (&quot; +
&quot;id INTEGER PRIMARY KEY AUTOINCREMENT, &quot; +
&quot;vehicle_number TEXT NOT NULL, &quot; +
&quot;vehicle_type TEXT NOT NULL, &quot; +
&quot;duration REAL NOT NULL, &quot; +

&quot;fee REAL NOT NULL, &quot; +
&quot;date TEXT NOT NULL)&quot;;
db.execSQL(createQuery);
db.execSQL(
&quot;INSERT INTO &quot; + TABLE_NAME +
&quot; (id, vehicle_number, vehicle_type, duration,
fee, date) &quot; +
&quot;SELECT id, vehicle_number, vehicle_type,
duration, fee, date &quot; +
&quot;FROM parking_records_old&quot;
);
db.execSQL(&quot;DROP TABLE parking_records_old&quot;);
}
}
public long insertRecord(
String number,
String type,
double duration,
double fee,
String date) {
SQLiteDatabase db = getWritableDatabase();
ContentValues values = new ContentValues();
values.put(&quot;vehicle_number&quot;, number);
values.put(&quot;vehicle_type&quot;, type);
values.put(&quot;duration&quot;, duration);
values.put(&quot;fee&quot;, fee);
values.put(&quot;date&quot;, date);
return db.insert(TABLE_NAME, null, values);
}
public Cursor getAllRecords() {
SQLiteDatabase db = getReadableDatabase();
return db.query(
TABLE_NAME,
new String[]{
&quot;id&quot;,
&quot;vehicle_number&quot;,
&quot;vehicle_type&quot;,
&quot;duration&quot;,
&quot;fee&quot;,
&quot;date&quot;
},
null,
null,
null,
null,
&quot;id DESC&quot;

);
}
}
