package cz.clovekvtisni.coordinator.android.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Obalecka nad Gsonem, kterou pouzivame hlavne proto, abychom meli Gson
 * nainicializovany na jednom miste a pokazde stejne, napr. kvuli formatu datumu
 * apod.
 * 
 * Taky se to bude hodit na vlastni serializatory nejakych slozitejsich typu.
 * 
 * @author tomucha
 * 
 */
public class GsonTool {
	
	private static JsonParser parser = new JsonParser();
	private static Gson gson;
	static {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
	}
	
	public static JsonElement parse(InputStream is) {
		try {
			return parser.parse(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static String getString(JsonObject object, String name) {
		if (object == null) return null;
		if (name == null) return null;
		if (!object.has(name)) return null;
		return object.get(name).getAsString();
	}

	public static Long getLong(JsonObject object, String name) {
		if (object == null) return null;
		if (name == null) return null;
		if (!object.has(name)) return null;
		return object.get(name).getAsLong();
	}

	public static JsonObject render(Object ... pairs) {
		JsonObject res = new JsonObject();
		for (int a=0; a<pairs.length; a+=2){
			res.add((String) pairs[a], toJson(pairs[a+1]));
		}
		return res;
	}

	public static JsonElement parse(byte[] data) {
		try {
			return (JsonElement) parse(new ByteArrayInputStream(data));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static JsonElement toJson(Object object) {
		return gson.toJsonTree(object);
	}

	public static <BEAN> BEAN fromJson(JsonElement params, Class<? extends BEAN> clas) {
		return gson.fromJson(params, clas);
	}

	public static JsonElement parse(String responseBody) {
		try {
			return parse(responseBody.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}