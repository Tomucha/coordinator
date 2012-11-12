package cz.clovekvtisni.coordinator.android.api;

import roboguice.inject.ContextSingleton;

/**
 * Abychom nemeli instance {@link ApiCall} rozstrkane vsude po kodu, neni
 * konstruktor {@link ApiCall} verejny a vsechny instance se musi vytvaret zde.
 * 
 * @author tomucha
 */
@ContextSingleton
public class ApiCallFactory {

	public ApiCall<Void, Void> dummy() {
		return new ApiCall<Void, Void>("dummyEntity", "dummyOperation", Void.class);
	}

}