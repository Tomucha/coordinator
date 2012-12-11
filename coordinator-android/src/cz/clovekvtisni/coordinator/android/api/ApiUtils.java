package cz.clovekvtisni.coordinator.android.api;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class ApiUtils {
	public static final JsonParser PARSER = new JsonParser();
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class,
			new JsonDateDeserializer()).create();

	private static class JsonDateDeserializer implements JsonDeserializer<Date> {
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Date d = new Date(json.getAsJsonPrimitive().getAsLong());
			return d;
		}
	}
}
