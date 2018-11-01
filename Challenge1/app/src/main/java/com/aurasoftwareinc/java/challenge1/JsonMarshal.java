package com.aurasoftwareinc.java.challenge1;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

class JsonMarshal {

    private static final String TAG = "JsonMarshal";

    private static final String PACKAGE_NAME = "com.aurasoftwareinc.java.challenge1.";

    private static Class<?> mClass;

    /**
     * Default constructor.
     */
    private JsonMarshal() {
        // Default constructor - restricts instantiation.
    }

    /**
     * Method to marshal the supplied object and return generated JSON.
     *
     * @param object supplied to determine the object.
     * @return the generated JSON object.
     */
    static JSONObject marshalJSON(Object object) {
        Log.d(TAG, "marshalJSON() called with: object = [" + object + "]");
        JSONObject json = new JSONObject();

        try {
            mClass = Class.forName(PACKAGE_NAME + "SubclassTypes");  // class reflection
            Field[] fields = mClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                final Object currentObj = field.get(object);
                if (!(currentObj instanceof JSONTypes
                        || currentObj instanceof ObjectTypes
                        || currentObj instanceof PrimitiveTypes))
                    continue;

                JSONObject innerJson = new JSONObject();
                Field[] innerFields = currentObj.getClass().getDeclaredFields();
                for (Field innerField : innerFields) {
                    innerField.setAccessible(true);
                    innerJson.put(innerField.getName(), innerField.get(currentObj));
                }
                json.put(field.getName(), innerJson);
            }
        } catch (ClassNotFoundException | IllegalAccessException | JSONException e) {
            Log.e(TAG, "marshalJSON: ", e);
        }
        return json;
    }

    /**
     * Method to unmarshal the supplied json object.
     *
     * @param object supplied object to get assinged values from json.
     * @param json   supplied JSON object to unmarshall to object.
     * @return true if succeeds, false otherwise.
     */
    static boolean unmarshalJSON(Object object, JSONObject json) {
        Log.d(TAG, "unmarshalJSON() called with: object = [" + object + "], json = [" + json + "]");

        try {
           /* mClass = Class.forName(PACKAGE_NAME + "SubclassTypes");  // class reflection
            Field[] fields = mClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!(field.getGenericType() instanceof JSONTypes
                || field.getGenericType() instanceof ObjectTypes
                || field.getGenericType() instanceof PrimitiveTypes))
                    continue;*/

            Field[] allFields = ((SubclassTypes) object).getClass().getDeclaredFields();
            for (Field field : allFields) {
                field.setAccessible(true);

               /* if (!(field.getGenericType() instanceof JSONTypes
               || field.getGenericType() instanceof ObjectTypes
               || field.getGenericType() instanceof PrimitiveTypes))
                    continue;*/

                field.set(field.getGenericType(), json.get(field.getName()));
            }
            // }
            return true;
        } catch (JSONException | IllegalAccessException e) {
            Log.e(TAG, "unmarshalJSON: ", e);
            return false;
        }
    }
}