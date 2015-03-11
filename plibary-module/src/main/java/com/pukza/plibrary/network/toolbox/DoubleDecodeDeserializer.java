package com.pukza.plibrary.network.toolbox;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLDecoder;


/**
 * Created by choihwaseop on 2016. 2. 2..
 */

public class DoubleDecodeDeserializer implements JsonDeserializer{
    private Type mClassType;
    public <T> DoubleDecodeDeserializer(Class<T> classOfT){
        mClassType = classOfT;
    }


    @Override
    public Object deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        Field[] fields = mClassType.getClass().getFields();

        Object theObject = null;

//        ParameterizedType paramType = (ParameterizedType) mClass;
//        theObject = paramType.getActualTypeArguments()[0];

//            theObject = ((Class)((ParameterizedType)mClass).getActualTypeArguments()[0]).newInstance();

        for(Field f : fields )
        {
            try {
                JsonElement element = object.get(f.getName());
                if(f.getType() == String.class )
                {
                    String value = element.getAsString();
                    value = doubleDecoding(value);
                    f.set(theObject , value);
                }else
                {
                    f.set(theObject , element);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }



        return theObject;
    }

    public static String doubleDecoding(String value){
        if(value == null) return null;
        String encodedString = null;
        try {
            encodedString = URLDecoder.decode(value, "UTF-8");
            encodedString = URLDecoder.decode(encodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedString = value;
        }

        encodedString = encodedString.replaceAll("\"\\{", "\\{");
        encodedString = encodedString.replaceAll("\\}\"", "\\}");
        return encodedString;
    }

}