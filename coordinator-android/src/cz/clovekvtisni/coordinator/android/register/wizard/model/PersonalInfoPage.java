package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.fhucho.android.workers.Workers;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.PersonalInfoFragment;
import cz.clovekvtisni.coordinator.domain.User;

public class PersonalInfoPage extends Page {
    public static final String NAME_DATA_KEY = "name";
    public static final String EMAIL_DATA_KEY = "email";
    public static final String PHONE_DATA_KEY = "phone";
    
    private static final String[] STRING_DATA_KEYS = { NAME_DATA_KEY, EMAIL_DATA_KEY, PHONE_DATA_KEY };

    public PersonalInfoPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return PersonalInfoFragment.newInstance(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Jméno", mData.getString(NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("E-mail", mData.getString(EMAIL_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Telefon", mData.getString(PHONE_DATA_KEY), getKey(), -1));
    }
    
    @Override
	public boolean isCompleted() {
		for (String dataKey : STRING_DATA_KEYS) {
			if (TextUtils.isEmpty(mData.getString(dataKey))) return false;
		}
		return true;
	}
    
    @Override
    public void saveToUser(User user) {
    	String[] name = mData.getString(NAME_DATA_KEY).split(" ", 2);
    	user.setFirstName(name[0]);
        if (name.length == 1) {
    	    user.setLastName("");
        } else {
            user.setLastName(name[1]);
        }
    	
    	user.setEmail(mData.getString(EMAIL_DATA_KEY));
    	
    	user.setPhone(mData.getString(PHONE_DATA_KEY));





    }



    @Override
    public void loadFromUser(User user) {
        mData.putString(NAME_DATA_KEY, user.getFullName());
        mData.putString(EMAIL_DATA_KEY, user.getEmail());
        mData.putString(PHONE_DATA_KEY, user.getPhone());
    }
}
