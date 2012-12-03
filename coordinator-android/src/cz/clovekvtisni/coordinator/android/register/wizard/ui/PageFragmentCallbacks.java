package cz.clovekvtisni.coordinator.android.register.wizard.ui;

import cz.clovekvtisni.coordinator.android.register.wizard.model.Page;

public interface PageFragmentCallbacks {
    Page onGetPage(String key);
}
