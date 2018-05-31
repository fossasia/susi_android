package org.fossasia.susi.ai.skills.skilldetails;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 *
 * Created by arundhati24 on 27/05/2018
 */

public class XAxisValueFormatter implements IAxisValueFormatter {

    private String[] values;

    public XAxisValueFormatter(String[] values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return this.values[(int) value];
    }

    /** this is only needed if numbers are returned, else return 0 */
}