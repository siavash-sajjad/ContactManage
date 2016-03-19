package ir.sadeghlabs.contactmanage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ir.sadeghlabs.library.PersianMonthView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PersianMonthView persianMonthView = (PersianMonthView) findViewById(R.id.persianMonthView);
        persianMonthView.setOnDayClick(new PersianMonthView.OnDayClick() {
            @Override
            public void onClick(String date) {
                Toast.makeText(getBaseContext(),date,Toast.LENGTH_LONG).show();
            }
        });
    }
}
