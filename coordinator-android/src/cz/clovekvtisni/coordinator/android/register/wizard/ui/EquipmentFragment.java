package cz.clovekvtisni.coordinator.android.register.wizard.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.register.wizard.model.EquipmentPage;
import cz.clovekvtisni.coordinator.android.register.wizard.model.Page;

public class EquipmentFragment extends ListFragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private EquipmentPage page;

    public static EquipmentFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        EquipmentFragment fragment = new EquipmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EquipmentFragment() {
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_register, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	Bundle args = getArguments();
    	page = (EquipmentPage) mCallbacks.onGetPage(args.getString(ARG_KEY));

        final List<String> mChoices = new ArrayList<String>();
        for (int i = 0; i < page.getEquipmentsList().size(); i++) {
            mChoices.add(page.getEquipmentsList().get(i).getName());
        }
    	
    	((TextView) getView().findViewById(R.id.title)).setText(page.getTitle());

        if (mChoices.isEmpty()) {
            getView().findViewById(R.id.empty_info).setVisibility(View.VISIBLE);
        }


        final ListView listView = (ListView) getView().findViewById(android.R.id.list);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                android.R.id.text1,
                mChoices));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> selectedItems = page.getData().getIntegerArrayList(Page.SIMPLE_DATA_KEY);
                if (selectedItems == null || selectedItems.size() == 0) {
                    return;
                }

                Set<Integer> selectedSet = new HashSet<Integer>(selectedItems);

                for (int i = 0; i < mChoices.size(); i++) {
                    if (selectedSet.contains(i)) {
                        listView.setItemChecked(i, true);
                    }
                }
            }
        });
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SparseBooleanArray checkedPositions = getListView().getCheckedItemPositions();
        ArrayList<Integer> selections = new ArrayList<Integer>();
        for (int i = 0; i < page.getEquipmentsList().size(); i++) {
            if (checkedPositions.get(i)) {
                selections.add(i);
            }
        }

        page.getData().putIntegerArrayList(Page.SIMPLE_DATA_KEY, selections);
        page.notifyDataChanged();
    }
}
