package d0020e.basac;

/**
 * Created by Sebastian on 17/02/2016.
 */
public class UserIncidentReport {
    private int reportType;
    private long timeStamp;
    private String reportMessage;

    public UserIncidentReport(int typeOfReport, String message) {
        this.reportType = typeOfReport; //the kind of accident recorded
        this.timeStamp = System.currentTimeMillis();
        this.reportMessage = message;
    }

}
