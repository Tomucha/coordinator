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
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ApiUtils {
	public static final JsonParser PARSER = new JsonParser();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Date.class, new JsonDateDeserializer())
			.registerTypeAdapter(Date.class, new JsonDateSerializer()).create();

	private static class JsonDateDeserializer implements JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context)
				throws JsonParseException {
			Date d = new Date(json.getAsJsonPrimitive().getAsLong());
			return d;
		}
	}

	private static class JsonDateSerializer implements JsonSerializer<Date> {
		@Override
		public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
			return date == null ? null : new JsonPrimitive(date.getTime());
		}
	}
}
