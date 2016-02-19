package d0020e.basac;

import java.text.DateFormat;

/**
 * Created by Sebastian on 17/02/2016.
 */
//TODO: figure out how to shoehorn this into a JSON object and push as seperate content to CCN network.
public class UserIncidentReport {
    private int reportType;
    private long timeStamp;
    private String reportMessage;

    public UserIncidentReport(int typeOfReport, String message) {
        this.reportType = typeOfReport; //the kind of accident recorded
        this.timeStamp = System.currentTimeMillis();
        this.reportMessage = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public int getType() {
        return reportType;
    }
}
