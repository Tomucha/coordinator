package cz.clovekvtisni.coordinator.android.wizardpager.wizard.model;

/**
 * Callback interface connecting {@link Page}, {@link AbstractWizardModel}, and model container
 * objects (e.g. {@link com.RegisterActivity.android.wizardpager.MainActivity}.
 */
public interface ModelCallbacks {
    void onPageDataChanged(Page page);
    void onPageTreeChanged();
}
