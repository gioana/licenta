package com.diploma.android.iruntracking.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.helpers.RunDatabaseHelper.RunCursor;
import com.diploma.android.iruntracking.model.Run;
import com.diploma.android.iruntracking.utils.Calculator;
import com.diploma.android.iruntracking.utils.Formatter;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A straightforward example of using AndroidPlot to plot some data.
 */
public class ChartsActivity extends Activity {
    private List<Long> mDates;
    private List<Float> mDistances;
    private List<Long> mDuration;
    private List<Long> mCalories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        RunManager runManager = RunManager.get(this);
        setRunsList(runManager.queryRuns());

        TextView weekDistanceTextView = (TextView)findViewById(R.id.week_distance_textView);
        TextView weekDurationTextView = (TextView)findViewById(R.id.week_duration_textView);
        TextView weekCaloriesTextView = (TextView)findViewById(R.id.week_calories_textView);

        weekDistanceTextView.setText(Formatter.formatDistance(runManager.queryCurrentWeekDistance()) + "km");
        weekDurationTextView.setText(Formatter.formatDuration(runManager.queryCurrentWeekDuration()));
        weekCaloriesTextView.setText(String.valueOf(runManager.queryCurrentWeekCalories()));

        // initi65alize our XYPlot reference:
        XYPlot distancePlot = (XYPlot) findViewById(R.id.distanceXYPlot);
        XYPlot durationPlot = (XYPlot) findViewById(R.id.durationXYPlot);
        XYPlot caloriesPlot = (XYPlot) findViewById(R.id.caloriesXYPlot);
        // set series:
        XYSeries distanceSeries = new SimpleXYSeries(mDates, mDistances, "");
        XYSeries durationSeries = new SimpleXYSeries(mDates, mDuration, "");
        XYSeries caloriesSeries = new SimpleXYSeries(mDates, mCalories, "");
        // set range format
        DecimalFormat distanceFormat = new DecimalFormat("0.00");
        DecimalFormat caloriesFormat = new DecimalFormat("0");
        // set plot
        setPlot(distancePlot, distanceSeries, distanceFormat);
        setPlot(durationPlot, durationSeries, null);
        setPlot(caloriesPlot, caloriesSeries, caloriesFormat);

    }

    private void setRunsList(RunCursor cursor) {
        mDates = new ArrayList<>();
        mDistances = new ArrayList<>();
        mDuration = new ArrayList<>();
        mCalories = new ArrayList<>();
        while (cursor.moveToNext()) {
            Run run = cursor.getRun();
            mDates.add(run.getStartDate().getTime());
            mDistances.add(Calculator.getDistanceKm(run.getDistance()));
            mDuration.add(run.getDuration());
            mCalories.add(run.getCalories());
        }
    }

    private void setPlot(XYPlot plot, XYSeries series, final Format format) {
        plot.getBackgroundPaint().setColor(getResources().getColor(R.color.light_grey));
        // Set the colors of the graph, title and background
        plot.getGraphWidget().getBackgroundPaint().setColor(getResources().getColor(R.color.light_grey));
        plot.getTitleWidget().getLabelPaint().setColor(getResources().getColor(R.color.blue));
        plot.getDomainLabelWidget().getLabelPaint().setColor(getResources().getColor(R.color.blue));
        plot.getRangeLabelWidget().getLabelPaint().setColor(getResources().getColor(R.color.blue));
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.setPointLabelFormatter(new PointLabelFormatter());
        formatter.configure(getApplicationContext(),
                R.xml.line_point_formatter);

        // add a new series' to the xyplot:
        plot.addSeries(series, formatter);

        // set formats
        formatter.setPointLabeler(new PointLabeler() {
            @Override
            public String getLabel(XYSeries series, int index) {
                Number number = series.getY(index);
                if (format == null) {
                    return Formatter.formatDuration(number.longValue());
                }
                return format.format(number);
            }
        });

        if (format == null) {
            plot.setRangeValueFormat(new Format() {
                @Override
                public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
                    long number = ((Number)object).longValue();
                    return new StringBuffer(Formatter.formatDuration(number));
                }

                @Override
                public Object parseObject(String string, ParsePosition position) {
                    return null;
                }
            });
        } else {
            plot.setRangeValueFormat(format);
        }
        plot.setDomainValueFormat(new Format() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");

            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer buffer, @NonNull FieldPosition field) {
                Date date = new Date(((Number)obj).longValue());
                return dateFormat.format(date, buffer, field);
            }

            @Override
            public Object parseObject(String string, @NonNull ParsePosition position) {
                return null;
            }
        });

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setTicksPerDomainLabel(series.size());
        plot.getGraphWidget().setDomainLabelOrientation(-45);

        plot.getLegendWidget().setVisible(false);
    }
}