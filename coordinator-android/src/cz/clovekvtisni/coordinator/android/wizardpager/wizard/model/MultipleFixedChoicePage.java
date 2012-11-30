package cz.clovekvtisni.coordinator.android.wizardpager.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import cz.clovekvtisni.coordinator.android.wizardpager.wizard.ui.MultipleChoiceFragment;

/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
public class MultipleFixedChoicePage extends SingleFixedChoicePage {
    public MultipleFixedChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return MultipleChoiceFragment.create(getKey());
    }

    @Override
    public boolean isCompleted() {
        ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        return selections != null && selections.size() > 0;
    }
}
