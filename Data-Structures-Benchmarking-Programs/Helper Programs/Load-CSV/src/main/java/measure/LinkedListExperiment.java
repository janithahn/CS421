package measure;

import datastructures.LinkedList;
import datastructures.Membership;
import load.ExperimentData;

public class LinkedListExperiment extends Experiment{
    public LinkedListExperiment(ExperimentData data, String[] classIdentifiers) {
        super(data, classIdentifiers);
    }

    @Override
    public String getOutputName() {
        return "LinkedList";
    }

    @Override
    protected Membership getNewInstance() {
        return new LinkedList();
    }
}
