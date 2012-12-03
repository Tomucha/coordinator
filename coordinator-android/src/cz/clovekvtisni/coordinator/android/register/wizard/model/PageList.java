package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;

/**
 * Represents a list of wizard pages.
 */
@SuppressWarnings("serial")
public class PageList extends ArrayList<Page> implements PageTreeNode {
	public PageList(Page... pages) {
		for (Page page : pages) {
			add(page);
		}
	}

	@Override
	public Page findByKey(String key) {
		for (Page childPage : this) {
			Page found = childPage.findByKey(key);
			if (found != null) {
				return found;
			}
		}

		return null;
	}

	@Override
	public void flattenCurrentPageSequence(ArrayList<Page> dest) {
		for (Page childPage : this) {
			childPage.flattenCurrentPageSequence(dest);
		}
	}
}
