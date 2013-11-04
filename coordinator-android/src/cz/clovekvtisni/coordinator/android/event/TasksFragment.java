package cz.clovekvtisni.coordinator.android.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.util.BetterArrayAdapter;
import cz.clovekvtisni.coordinator.android.util.FindView;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;

public class TasksFragment extends SherlockFragment {

	private EventActivity activity;
	private PoisAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		activity = (EventActivity) getActivity();

		adapter = new PoisAdapter(new ArrayList<Poi>());
		ListView listView = new ListView(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.showPoiOnMap(adapter.getItem(position));
			}
		});

		return listView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tasks, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_tasks:
			activity.showPoiFilterDialog();
			break;
		}
		return true;
	}

    public void setFilteredPois(List<Poi> pois, Map<PoiCategory, Bitmap> poiIcons) {
        adapter.setIconsMap(poiIcons);
        adapter.clear();

        List<Poi> notImportant = new ArrayList<Poi>();
        for (Iterator<Poi> iterator = pois.iterator(); iterator.hasNext(); ) {
            Poi next = iterator.next();

            PoiCategory poiCategory = ((EventActivity)getActivity()).findPoiCategory(next.getPoiCategoryId());

            if (next.isCanDoTransition() || !poiCategory.isImportant()) {
                notImportant.add(next);
            }
        }

        adapter.addAll(notImportant);
    }

	private class PoisAdapter extends BetterArrayAdapter<Poi> {

        private Map<PoiCategory, Bitmap> iconsMap;

        public PoisAdapter(List<Poi> pois) {
			super(getActivity(), R.layout.item_with_icon);
		}

		@Override
		protected void setUpView(Poi poi, View view) {
			FindView.textView(view, R.id.title).setText(poi.getName());
			FindView.textView(view, R.id.short_description).setText(poi.getDescription());
            PoiCategory poiCategory = ((EventActivity)getActivity()).findPoiCategory(poi.getPoiCategoryId());
            FindView.imageView(view,R.id.icon).setImageBitmap(iconsMap.get(poiCategory));

            boolean editable = poi.isCanEdit();
            boolean important = findPoiCategory(poi).isImportant();
            boolean transition = poi.isCanDoTransition();

            ImageView i2 = FindView.imageView(view,R.id.icon2);
            i2.setVisibility(View.VISIBLE);
            if (transition) {
                i2.setImageResource(R.drawable.ic_work);
            } else {
                if (important) {
                    i2.setImageResource(R.drawable.ic_info);
                } else {
                    i2.setVisibility(View.INVISIBLE);
                }
            }

		}

        public void setIconsMap(Map<PoiCategory,Bitmap> iconsMap) {
            this.iconsMap = iconsMap;
        }
    }

    private PoiCategory findPoiCategory(Poi poi) {
        return ((EventActivity)getActivity()).findPoiCategory(poi.getPoiCategoryId());
    }

    /**
     * This is a terrible hack of:
     * http://stackoverflow.com/questions/14516804/nullpointerexception-android-support-v4-app-fragmentmanagerimpl-savefragmentbasi
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DO NOT CRASH", "OK");
        setUserVisibleHint(true);
        super.onSaveInstanceState(outState);
    }

}
