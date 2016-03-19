package ir.sadeghlabs.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sadegh-Pc on 3/17/2016.
 */
public class PersianMonthView extends LinearLayout {
    private Context context;
    private View monthViewLayout;
    private String whiteColor = "#ffffff";


    private PersianCalendar persianCalendar = new PersianCalendar();
    private int persianYear;
    private int persianMonth;
    private int persianDay;
    private String currentPersianMonthName = "";


    /**
     * default peroperties
     */
    private int defaultTitleColor = Color.parseColor("#c1c1c1");
    private float defaultTitleFontSize = 16;
    private int defaultDaysColor = Color.parseColor("#c1c1c1");
    private float defaultDaysFontSize = 14;

    /**
     * title properties
     */
    private Typeface titleTypeface;
    private int titleColor;
    private float titleFontSize;

    /**
     * days properties
     */
    private Typeface daysTypeface;
    //private int daysColor;
    private float daysFontSize;


    private OnDayClick onDayClick;


    public PersianMonthView(Context context) {
        super(context);

        if (isInEditMode())
            return;

        createView(context);
    }

    public PersianMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode())
            return;

        createView(context);
    }

    private View createView(Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        monthViewLayout = inflater.inflate(R.layout.month_layout, this);

        calculateFirstPersianDay();

        return monthViewLayout;
    }

    private void calculateFirstPersianDay() {
        persianYear = persianCalendar.getIranianYear();
        persianMonth = persianCalendar.getIranianMonth();
        persianDay = persianCalendar.getIranianDay();
        currentPersianMonthName = persianCalendar.getIranianMonthName(persianMonth);

        persianCalendar.setIranianDate(persianYear, persianMonth, 1);

        int year = persianCalendar.getGregorianYear();
        int month = persianCalendar.getGregorianMonth();
        int day = persianCalendar.getGregorianDay();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        setMonthTitle();

        invisibleWeekRows();

        int startDayInWeekIndex = getDayOfWeekIndex();

        setMonthDaysInLayout(startDayInWeekIndex);

        markCurrentDay();
    }

    private void setMonthTitle() {
        TextView lblMonthTitle = (TextView) monthViewLayout.findViewById(R.id.lblMonthTitle);

        lblMonthTitle.setText(currentPersianMonthName + " - " + persianYear);
    }

    private void invisibleWeekRows() {
        for (int i = 1; i <= 42; i++) {
            LinearLayout weekContainer = (LinearLayout) monthViewLayout.findViewWithTag("dayOfMonthContainer" + i);
            TextView lblDay = (TextView) monthViewLayout.findViewWithTag("lblNumber_" + i);

            weekContainer.setVisibility(INVISIBLE);
            lblDay.setVisibility(INVISIBLE);
        }
    }

    private int getDayOfWeekIndex() {
        String x = persianCalendar.getWeekDayStr();
        int dayOfWeekIndex = 0;
        switch (x) {
            case "Monday":
                dayOfWeekIndex = 3;
                break;
            case "Tuesday":
                dayOfWeekIndex = 4;
                break;
            case "Wednesday":
                dayOfWeekIndex = 5;
                break;
            case "Thursday":
                dayOfWeekIndex = 6;
                break;
            case "Friday":
                dayOfWeekIndex = 7;
                break;
            case "Saturday":
                dayOfWeekIndex = 1;
                break;
            case "Sunday":
                dayOfWeekIndex = 2;
                break;
        }

        return dayOfWeekIndex;
    }

    private void setMonthDaysInLayout(int dayInWeekIndex) {
        try {
            int monthDayCount = getMonthDayCount(this.persianMonth);

            setPastDay(dayInWeekIndex);

            for (int i = 1; i <= monthDayCount; i++, dayInWeekIndex++) {
                LinearLayout weekContainer = (LinearLayout) monthViewLayout.findViewWithTag("dayOfMonthContainer" + dayInWeekIndex);

                weekContainer.setVisibility(VISIBLE);


                TextView lblDay = (TextView) monthViewLayout.findViewWithTag("lblNumber_" + dayInWeekIndex);

                lblDay.setVisibility(VISIBLE);
                lblDay.setText(String.valueOf(i));
                //lblDay.setTextSize(daysFontSize);

                if (dayInWeekIndex % 7 == 0) {
                    lblDay.setTextColor(Color.parseColor(whiteColor));
                } else {
                    lblDay.setTextColor(Color.parseColor("#989898"));
                }

                String date = persianYear + "/" + persianMonth + "/" + i;

                weekContainer.setOnClickListener(new DayOfMonthSelected(date));
            }

            setNextDay(dayInWeekIndex);

            visibleLayout();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void setPastDay(int dayInWeekIndex) {
        int pastMonthIndex = persianMonth - 1;
        int pastMonthDayCount = getMonthDayCount(pastMonthIndex);

        dayInWeekIndex = dayInWeekIndex - 1;

        if (dayInWeekIndex > 1) {
            for (int past = dayInWeekIndex; past > 0; past--, dayInWeekIndex--, pastMonthDayCount--) {
                LinearLayout weekContainer = (LinearLayout) monthViewLayout.findViewWithTag("dayOfMonthContainer" + dayInWeekIndex);

                weekContainer.setVisibility(VISIBLE);
                weekContainer.setBackgroundColor(Color.parseColor("#E6E6E6"));

                TextView lblDay = (TextView) monthViewLayout.findViewWithTag("lblNumber_" + dayInWeekIndex);

                lblDay.setVisibility(VISIBLE);
                lblDay.setText(String.valueOf(pastMonthDayCount));

                if (dayInWeekIndex % 7 == 0) {
                    lblDay.setTextColor(Color.parseColor(whiteColor));
                } else {
                    lblDay.setTextColor(Color.parseColor("#989898"));
                }

                int pastMonth = (persianMonth - 1) < 0 ? 12 : (persianMonth - 1);
                int pastYear = pastMonth == 12 ? (persianYear - 1) : persianYear;

                String date = pastYear + "/" + pastMonth + "/" + past;

                weekContainer.setOnClickListener(new DayOfMonthSelected(date));
            }
        }
    }

    private void setNextDay(int dayInWeekIndex) {
        int startDayOfWeekIndex = getDayOfWeekIndex();

        for (int i = 1; i <= (7 - startDayOfWeekIndex); i++, dayInWeekIndex++) {
            LinearLayout weekContainer = (LinearLayout) monthViewLayout.findViewWithTag("dayOfMonthContainer" + dayInWeekIndex);

            weekContainer.setVisibility(VISIBLE);

            TextView lblDay = (TextView) monthViewLayout.findViewWithTag("lblNumber_" + dayInWeekIndex);

            lblDay.setVisibility(VISIBLE);
            lblDay.setText(String.valueOf(i));

            if (dayInWeekIndex % 7 == 0) {
                lblDay.setTextColor(Color.parseColor(whiteColor));
            } else {
                weekContainer.setBackgroundColor(Color.parseColor("#E6E6E6"));

                lblDay.setTextColor(Color.parseColor("#989898"));
            }

            int nextMonth = (persianMonth + 1) > 12 ? 1 : (persianMonth + 1);
            int nextYear = nextMonth == 1 ? (persianYear + 1) : persianYear;

            String date = nextYear + "/" + nextMonth + "/" + i;

            weekContainer.setOnClickListener(new DayOfMonthSelected(date));
        }
    }

    private void visibleLayout() {
        LinearLayout llWeekContainer = (LinearLayout) monthViewLayout.findViewById(R.id.llWeekContainer);
        LinearLayout weekRow = (LinearLayout) monthViewLayout.findViewWithTag("weekRow6");

        TextView lblDay = (TextView) monthViewLayout.findViewWithTag("lblNumber_" + 36);
        if (lblDay.getVisibility() == INVISIBLE) {
            weekRow.setVisibility(GONE);

            llWeekContainer.setWeightSum(5);
        } else {
            weekRow.setVisibility(VISIBLE);

            llWeekContainer.setWeightSum(6);
        }
    }

    private int getMonthDayCount(int persianMonth) {
        int monthDayCount = 31;

        if (persianMonth <= 6) {
            monthDayCount = 31;
        } else if (persianMonth > 6 && persianMonth <= 11) {
            monthDayCount = 30;
        } else {
            boolean isLeapYear = persianCalendar.IsLeap(persianYear);

            if (isLeapYear) {
                monthDayCount = 30;
            } else {
                monthDayCount = 29;
            }
        }

        return monthDayCount;
    }

    private void markCurrentDay() {
        int startDayOfWeekIndex = getDayOfWeekIndex();

        int index = (persianDay + startDayOfWeekIndex) - 1;

        LinearLayout weekContainer = (LinearLayout) monthViewLayout.findViewWithTag("dayOfMonthContainer" + index);

        weekContainer.setVisibility(VISIBLE);
        weekContainer.setBackgroundColor(Color.parseColor("#c1d2e3"));

        TextView lblDay = (TextView) monthViewLayout.findViewWithTag("lblNumber_" + index);

        lblDay.setVisibility(VISIBLE);
    }


    /**
     * interfaces
     */
    public interface OnDayClick {
        void onClick(String date);
    }


    /**
     * title properties setter
     */
    public void setTitleTypeface(Typeface titleTypeface) {
        TextView lblMonthTitle = (TextView) monthViewLayout.findViewById(R.id.lblMonthTitle);
        lblMonthTitle.setTypeface(titleTypeface);
    }

    public void setTitleColor(int titleColor) {
        TextView lblMonthTitle = (TextView) monthViewLayout.findViewById(R.id.lblMonthTitle);
        lblMonthTitle.setTextColor(titleColor);
    }

    public void setTitleFontSize(float titleFontSize) {
        TextView lblMonthTitle = (TextView) monthViewLayout.findViewById(R.id.lblMonthTitle);
        lblMonthTitle.setTextSize(titleFontSize);
    }


    /**
     * days properties setter
     */
    public void setDaysTypeface(Typeface daysTypeface) {
        this.daysTypeface = daysTypeface;
    }

    public void setDaysColor(int daysColor) {
        //this.daysColor = daysColor;
    }

    public void setDaysFontSize(float daysFontSize) {
        this.daysFontSize = daysFontSize;
    }


    /**
     * interfaces setter
     */
    public void setOnDayClick(OnDayClick onDayClick) {
        this.onDayClick = onDayClick;
    }


    private class DayOfMonthSelected implements OnClickListener {
        private String date;

        public DayOfMonthSelected(String date) {
            this.date = date;
        }

        @Override
        public void onClick(View view) {
            onDayClick.onClick(date);
        }
    }
}
