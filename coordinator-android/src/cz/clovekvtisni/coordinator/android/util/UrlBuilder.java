package cz.clovekvtisni.coordinator.android.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builder class for creating nice HTTP urls.
 * 
 * @author tomucha
 *
 */
public class UrlBuilder {
	
	private String url;
	
	private List<Action> actions = new LinkedList<Action>();
	
	private String result;

	private Map<String, List<String>> params = new HashMap<String, List<String>>();
	
	public UrlBuilder(String url) {
		super();
		this.url = url;
	}

	public UrlBuilder add(String param, String value) {
		result = null;
		actions.add(new Action(Operation.ADD, param, value));
		return this;
	}

	public UrlBuilder add(String[] params, String[] values) {
		result = null;
		for (int i = 0; i < params.length; i++) {
			actions.add(new Action(Operation.ADD, params[i], values[i]));
		}
		return this;
	}

	public UrlBuilder remove(String param) {
		result = null;
		actions.add(new Action(Operation.DEL, param, null));
		return this;
	}

	public UrlBuilder remove(String[] params) {
		result = null;
		for (int i = 0; i < params.length; i++) {
			actions.add(new Action(Operation.DEL, params[i], null));
		}
		return this;
	}

	public void fetchParams() {
		params.clear();
		url = toString();
		actions.clear();
		if (url == null || !url.contains("?")) return;
		final String[] urlParts = url.split("\\?");
		if (urlParts == null || urlParts.length == 1) {
			/* nema query cast*/
			return;
		}
		final String queryPart;
		if (urlParts.length > 2) {
			throw new IllegalArgumentException("Not a valid url, contains more than one ? symbol");
		}
		queryPart = urlParts[1];

		final String[] paramsWithValues = queryPart.split("&");
		if (paramsWithValues == null || paramsWithValues.length == 0){
			/* zadne query */
			return;
		}
		for (String pamValue : paramsWithValues) {
			if (!pamValue.contains("=")) {
				/* je to jen jmeno parametru bez hodnoty */
				addParameter(pamValue, null);
			} else {
				int pos = pamValue.indexOf("=");
				/* vse za prvnim = je hodnota */
				final String name = pamValue.substring(0, pos);
				final String value = pamValue.substring(pos + 1, pamValue.length());
				addParameter(name, value);
			}
		}
	}

	/**
	 * vrati hodnoty parametru daneho jmena, pokud parametr neexistuje, vraci null
	 *
	 * @param paramName
	 * @return
	 */
	public List<String> getParamValues(final String paramName) {
		return params.get(paramName);
	}

	public String getParamFirstValue(final String paramName) {
		final List<String> allValues = getParamValues(paramName);
		if (allValues == null || allValues.isEmpty()) return null;
		return allValues.get(0);
	}

	public String getParamToString(final String paramName) {
		List<String> values = getParamValues(paramName);
		if (values == null) return "";
		final Iterator<String> it = values.iterator();
		String result = "";
		while(it.hasNext()) {
			result += paramName + "=";
			final String currentVal = it.next();
			if (currentVal != null)
				result += currentVal;
			if (it.hasNext()) {
				result += "&";
			}
		}
		return result;
	}

	private void addParameter(String name, String value) {
		if ("".equals(value)) value = null;
		if (value != null) value = decode(value);
		if (!params.containsKey(name)) {
			final List<String> values = new ArrayList<String>();
			values.add(value);
			params.put(name, values);
			return;
		}
		List<String> currentValues = params.get(name);
		if (currentValues == null) currentValues = new ArrayList<String>();
		currentValues.add(value);
		params.put(name, currentValues);
	}

	@Override
	public String toString() {
		if (result == null) {
			List<Action> actionsNew = new LinkedList<Action>();
			int i = url.indexOf('?');
			if (i >= 0) {
				if (i+1 < url.length()) {
					String[] params = url.substring(i + 1).split("&");
					for (int j = 0; j < params.length; j++) {
						String prm = params[j];
						if (prm == "" || prm == "=") continue;
						int k = prm.indexOf('=');
						if (k < 0)
							actionsNew.add(new Action(Operation.ADD, decode(prm), ""));
						else if (k > 0) {
							actionsNew.add(new Action(
									Operation.ADD, 
									decode(prm.substring(0, k)), 
									k + 1 < prm.length() ? decode(prm.substring(k+1)) : ""
									));
						}
					}
				}
				url = url.substring(0, i);
			}
			for (Action a: actions) {
				if (a.op == Operation.ADD) {
					actionsNew.add(a);
				}
				else {
					for (Iterator<Action> it = actionsNew.iterator(); it.hasNext();) {
						Action aNew = it.next();
						if (a.param.equals(aNew.param))
							it.remove();
					}
				}
			}
			actions = actionsNew;
			if (actions.size() > 0) {
				StringBuilder r = new StringBuilder();
				r.append(url);
				char sep = '?';
				for (Action a: actions) {
					r.append(sep);
					r.append(encode(a.param));
					r.append('=');
					r.append(encode(a.value));
					if (sep == '?') sep = '&';
				}
				result = r.toString();
			}
			else
				result = url;
		}
		return result;
	}
	
	public static String encode(String string) {
		try {
			return URLEncoder.encode(string, "utf8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String decode(String string) {
		try {
			return URLDecoder.decode(string, "utf8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	private static enum Operation {
		ADD,
		DEL,
	}
	
	private static class Action {
		Operation	op;
		String		param;
		String		value;
		
		public Action(Operation op, String param, String value) {
			super();
			this.op = op;
			this.param = param;
			this.value = value;
		}
	}
}
