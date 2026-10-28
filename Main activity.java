package com.example.parkingfeecalculator;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
public class MainActivity extends Activity {
EditText etVehicleNumber, etDuration;
Spinner spVehicleType;
Button btnCalculate, btnHistory;
TextView tvResult;
DatabaseHelper databaseHelper;
private final NumberFormat currencyFormat =
NumberFormat.getCurrencyInstance(new Locale(&quot;en&quot;, &quot;IN&quot;));
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
etVehicleNumber = findViewById(R.id.etVehicleNumber);
etDuration = findViewById(R.id.etDuration);
spVehicleType = findViewById(R.id.spVehicleType);
btnCalculate = findViewById(R.id.btnCalculate);
btnHistory = findViewById(R.id.btnHistory);
tvResult = findViewById(R.id.tvResult);
databaseHelper = new DatabaseHelper(this);
String[] vehicles = {&quot;Bike&quot;, &quot;Car&quot;, &quot;Van&quot;};
ArrayAdapter&lt;String&gt; adapter = new ArrayAdapter&lt;&gt;(
this,
android.R.layout.simple_spinner_item,
vehicles
);
adapter.setDropDownViewResource(
android.R.layout.simple_spinner_dropdown_item
);
spVehicleType.setAdapter(adapter);
btnCalculate.setOnClickListener(v -&gt; calculateFee());
btnHistory.setOnClickListener(v -&gt; showHistory());
}
private void calculateFee() {
String number = etVehicleNumber.getText()
.toString()
.trim()
.toUpperCase(Locale.ROOT);
String durationText = etDuration.getText()
.toString()
.trim();

if (number.isEmpty() || durationText.isEmpty()) {
Toast.makeText(
this,
&quot;Enter vehicle number and duration&quot;,
Toast.LENGTH_SHORT
).show();
return;
}
double duration;
try {
duration = Double.parseDouble(durationText);
} catch (NumberFormatException e) {
Toast.makeText(
this,
&quot;Enter a valid duration&quot;,
Toast.LENGTH_SHORT
).show();
return;
}
if (!Double.isFinite(duration)
|| duration &lt;= 0
|| duration &gt; 10000) {
Toast.makeText(
this,
&quot;Duration must be greater than 0 and at most 10000
hours&quot;,
Toast.LENGTH_SHORT
).show();
return;
}
String type = spVehicleType.getSelectedItem().toString();
double firstHour;
double extraHour;
switch (type) {
case &quot;Bike&quot;:
firstHour = 10;
extraHour = 5;
break;
case &quot;Car&quot;:
firstHour = 20;
extraHour = 10;
break;
case &quot;Van&quot;:
firstHour = 30;
extraHour = 15;
break;
default:

Toast.makeText(
this,
&quot;Select a valid vehicle type&quot;,
Toast.LENGTH_SHORT
).show();
return;
}
// Calculate fee proportionally using the actual duration.
double calculatedFee;
if (duration &lt;= 1) {
calculatedFee = duration * firstHour;
} else {
calculatedFee = firstHour
+ (duration - 1) * extraHour;
}
// Round the final fee to the nearest paise.
double fee = Math.round(calculatedFee * 100.0) / 100.0;
String date = new SimpleDateFormat(
&quot;yyyy-MM-dd HH:mm:ss&quot;,
Locale.getDefault()
).format(new Date());
long result = databaseHelper.insertRecord(
number,
type,
duration,
fee,
date
);
if (result == -1) {
Toast.makeText(
this,
&quot;Could not save parking record&quot;,
Toast.LENGTH_SHORT
).show();
return;
}
String formattedFee = currencyFormat.format(fee);
tvResult.setText(
&quot;Vehicle: &quot; + number +
&quot;\nType: &quot; + type +
&quot;\nDuration: &quot; + duration + &quot; hours&quot; +
&quot;\nParking Fee: &quot; + formattedFee +
&quot;\nSaved to SQLite successfully&quot;
);
Toast.makeText(
this,
&quot;Parking record saved successfully&quot;,
Toast.LENGTH_SHORT

).show();
}
private void showHistory() {
StringBuilder history = new StringBuilder();
Cursor cursor = databaseHelper.getAllRecords();
try {
if (!cursor.moveToFirst()) {
Toast.makeText(
this,
&quot;No parking records found&quot;,
Toast.LENGTH_SHORT
).show();
return;
}
do {
double fee = cursor.getDouble(4);
history.append(&quot;Record ID: &quot;)
.append(cursor.getInt(0))
.append(&quot;\nVehicle: &quot;)
.append(cursor.getString(1))
.append(&quot;\nType: &quot;)
.append(cursor.getString(2))
.append(&quot;\nDuration: &quot;)
.append(cursor.getDouble(3))
.append(&quot; hours\nFee: &quot;)
.append(currencyFormat.format(fee))
.append(&quot;\nDate: &quot;)
.append(cursor.getString(5))
.append(&quot;\n\n--------------------\n\n&quot;);
} while (cursor.moveToNext());
} finally {
cursor.close();
}
new AlertDialog.Builder(this)
.setTitle(&quot;Parking History&quot;)
.setMessage(history.toString())
.setPositiveButton(&quot;Close&quot;, null)
.show();
}
@Override
protected void onDestroy() {
super.onDestroy();
if (databaseHelper != null) {
databaseHelper.close();
}
}

}
