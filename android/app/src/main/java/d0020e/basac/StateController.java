package d0020e.basac;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Sebastian on 04/12/2015.
 */
public class StateController implements Observer {
    private DataModel dataModel;

    public StateController(DataModel dataModel) {
        this.dataModel = dataModel;
        this.dataModel.addObserver(this);
    }

    @Override
    /* Is called when Datamodel is updated. checks if any thresholds are exceeded and subsequently
    * sets warning status/flags to their proper alert level */
    public void update(Observable observable, Object data) {
        Log.e("StateController", "data updated");
        if((dataModel.getTestValue() > 30) && (dataModel.getWarningState()==false)) {
            dataModel.toggleWarning();
        }
    }
}
