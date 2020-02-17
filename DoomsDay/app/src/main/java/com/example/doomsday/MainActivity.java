package com.example.doomsday;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // Constants
    static final int CENTURY = 100;
    static final String[] MONTHS = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    static final int NUM_MONTHS = 12;
    static final int WEEK = 7;
    static final int GREGORIAN = 1582;
    // Page elements
    private Calendar calendar;
    private CheckBox cbxGo;
    private int day;
    private Spinner dropMonth;
    private int month;
    private TextView txtDate;
    private TextView txtDay;
    private TextView txtInputDay;
    private TextView txtInputYear;
    private int year;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // Grab Elements, fill default and wait for input
        setContentView(R.layout.activity_main);
        dropMonth = findViewById(R.id.drpMonth);
        txtDate = findViewById(R.id.txtDate);
        txtDay = findViewById(R.id.txtDay);
        txtInputDay = findViewById(R.id.txtInputDay);
        txtInputYear = findViewById(R.id.txtInputYear);
        cbxGo = findViewById(R.id.cbxGo);
        calendar = Calendar.getInstance();
        year = calendar.get(calendar.YEAR);
        month = calendar.get(calendar.MONTH);
        day = calendar.get(calendar.DAY_OF_MONTH);
        FillData();
        ShowDate(1 + month, day, year);
        FindDay(1 + month, day, year);
        cbxGo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            public void onCheckedChanged(CompoundButton compoundButton, boolean bl) {
                boolean isValid = true;
                boolean isEmpty = false;
                int inputDay = 0;
                int inputYear = 0;
                int inputMonth = 0;
                String inMonth="";
                try {       // check for empty data or overflow
                    isEmpty = txtInputDay.getText().toString().equals("");
                    if (!isEmpty) {
                        inputDay = Integer.parseInt(txtInputDay.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter date", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }
                    isEmpty = txtInputYear.getText().toString().equals("");

                    if (!isEmpty) {
                        inputYear = Integer.parseInt(txtInputYear.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter date", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }
                    inMonth = dropMonth.getSelectedItem().toString();
                }
                catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "MUST BE LESS THAN 10000", Toast.LENGTH_SHORT).show();
                }
                if (isValid) {
                    inputMonth = FindMonth(inMonth);
                    if (isValid(inputDay, inputMonth, inputYear)) {
                        ShowDate(inputMonth, inputDay, inputYear);
                        FindDay(inputMonth, inputDay, inputYear);
                    } else {
                        Toast.makeText(getApplicationContext(), "INVALID DATE", Toast.LENGTH_SHORT).show();
                    }
                } // end if valid
                cbxGo.toggle(); // uncheck the box
            } // end checkListener
        }); // end setListener
    } // end onCreate

    //---------------------------------------------------------------------------------------------------

    private void FindDay(int month, int day, int year) {
        int yearCode = year / CENTURY;              // get what century
        int yearD12 = year % CENTURY / NUM_MONTHS;  // B
        int d12Remainder = year % CENTURY % NUM_MONTHS; // C
        int leapYearShift = d12Remainder / 4; // D
        int centuryCode = FindCode(yearCode); // A
        int total = centuryCode + yearD12 + d12Remainder + leapYearShift; // E = A + B + C + D
        boolean isLeap = isLeap(year);
        int daysFromDoom = FindDaysFromDoom(isLeap, month, day); // each month has a day that matches century code

        while (daysFromDoom < 0){ // similar to absolute value, but for weekdays
            daysFromDoom += WEEK;
        }
        total += daysFromDoom; // similar to absolute value, but for weekdays
        while (total > 6) {
            total -= WEEK;
        }
        ShowDay(total);
    } // end FindDay

    //---------------------------------------------------------------------------------------------------

    private void FillData() {// fill drop down menu
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_month, MONTHS);
        dropMonth.setAdapter(adapter);
        dropMonth.setSelection(month, true);
    }

    //---------------------------------------------------------------------------------------------------

    private int FindCode(int yearCode ) { // these codes match with certain days of each month
        int code = yearCode % 4;
        switch (code) {
            case 0: return 2;
            case 1: return 0;
            case 2: return 5;
            default: return 3;
        } // end switch
    } // end FindCode

    //---------------------------------------------------------------------------------------------------

    private int FindMonth(String month) { // find corresponding string month to number month
        for (int i = 0; i < MONTHS.length; ++i) {
            if (MONTHS[i].equals(month)) {
                return i + 1;
            }
        } // end for
        return -1;
    } // end FindMonth

    //---------------------------------------------------------------------------------------------------

    private int FindDaysFromDoom(Boolean isLeap, int month, int day) {
        switch (month) {
            // every month has a doomsday, this calculates the distance from it
            case 1: if (isLeap){return (day - 4) % WEEK;} else return (day - 3) % WEEK;
            case 2: if (isLeap){return (day - 29) % WEEK; }else return (day - 28) % WEEK;
            case 3: return (day - 14) % WEEK;
            case 4: return (day - 4) % WEEK;
            case 5: return (day - 9) % WEEK;
            case 6: return (day - 6) % WEEK;
            case 7: return (day - 11) % WEEK;
            case 8: return (day - 8) % WEEK;
            case 9: return (day- 5) % WEEK;
            case 10:return (day - 10) % WEEK;
            case 11:return (day - 7) % WEEK;
            case 12:return (day - 12) % WEEK;
            default: return 1000; // will never reach in this program
        } // end switch
    } // end days from doom

    //---------------------------------------------------------------------------------------------------

    private boolean isLeap(int year) {
        if (year % 400 == 0) { // a leap year can be divisible by 400
            return true;
        }
        if (year % 100 == 0) { // but not 100
            return false;
        }
        return year % 4 == 0; // and if not those than any divided by 4 can be
    } // end isLeap

    //---------------------------------------------------------------------------------------------------

    private boolean isValid(int day, int month, int year) {
        boolean isValid = true;
        if (day == 0 || day > 31) { // filter out most problems
            Toast.makeText(getApplicationContext(), "INVALID DAY", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (year == 0 || year > 9999) { // filter out most problems
            Toast.makeText(getApplicationContext(), "INVALID YEAR", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (!isValid) {  // here to display both errors
            return false;
        }
        if ( year < GREGORIAN) { // only for user to let them know theres an accuracy issue with history
            Toast.makeText(getApplicationContext(), "WARNING: This date is prior to GREGORIAN calender, dates may be affected", Toast.LENGTH_SHORT).show();
        }
        switch (month) {
            case 2: // february specifiably must be checked for leap year
                if (day < 29) {
                    return true;
                }
                if (day == 29) {
                    if (isLeap(year)) {
                        return true;
                    }
                    Toast.makeText(getApplicationContext(),"INVALID LEAP YEAR", Toast.LENGTH_SHORT).show();
                    return false;
                } // end if 29
                return false;
            case 4: // for all months with 30 days
            case 6:
            case 9:
            case 11: return day < 31;
            default: return true; // for all months with 31 days
        } // end switch
    } // end isValid

    //---------------------------------------------------------------------------------------------------
    private void ShowDay(int day) {
        String dayOfWeek;
        // converts to string
        switch (day) {
            case 0: dayOfWeek = "SUNDAY";break;
            case 1: dayOfWeek = "MONDAY";break;
            case 2: dayOfWeek = "TUESDAY";break;
            case 3: dayOfWeek = "WEDNESDAY";break;
            case 4: dayOfWeek = "THURSDAY";break;
            case 5: dayOfWeek = "FRIDAY";break;
            case 6: dayOfWeek = "SATURDAY";break;
            default:dayOfWeek = "ERROR";break;
        } // end switch
        // displays the day
        TextView textView = txtDay;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dayOfWeek);
        textView.setText(stringBuilder);
    } // end ShowDay

    //---------------------------------------------------------------------------------------------------

    private void ShowDate(int month, int day, int year) {
        // displays the date
        TextView textView = txtDate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(month);
        stringBuilder.append("/");
        stringBuilder.append(day);
        stringBuilder.append("/");
        stringBuilder.append(year);
        textView.setText(stringBuilder);
    } // end ShowDate

    //---------------------------------------------------------------------------------------------------

} // end class
