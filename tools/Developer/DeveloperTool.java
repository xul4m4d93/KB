package com.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.resource.NotSupportedException;

/**
 * 
 * @author Kevin
 * @version 2015/11/27
 *
 */
public class DeveloperTool {
	
	//map 轉 javaBean,目前支援的資料型態還不完整
	public static <T> T invokeMapToBean(Class<T> instance,Map<String,? extends Object> map) throws Exception{
		final String DATE_FORMAT_SUPPORT = "yyyy/MM/dd";
		if(instance!=null && map!=null && !map.isEmpty()){
			T newObject = instance.newInstance();
			Field[] fileds = newObject.getClass().getDeclaredFields();
			Class<?> filedType = null;
			for(Field field:fileds){			
				field.setAccessible(true);
				filedType = field.getType();
				String strValue = null;
				for(Entry<String, ? extends Object> set:map.entrySet()){
					if(set.getKey().equals(field.getName())){
						try{
							strValue = String.valueOf(set.getValue());
						}catch(NullPointerException e){
							throw new NullPointerException("invokeMapToBean has null case ,map's key >>> "+set.getKey()+" ,map >>> "+map);
						}
						if(filedType == String.class){
							field.set(newObject, strValue);
						}else if(filedType == Integer.class){
							field.set(newObject, Integer.valueOf(strValue));
						}else if(filedType == Double.class){
							field.set(newObject, Double.valueOf(strValue));
						}else if(filedType == Long.class){
							field.set(newObject, Long.valueOf(strValue));
						}else if(filedType == BigDecimal.class){
							field.set(newObject, new BigDecimal(strValue));
						}else if(filedType == Date.class){
							try{
								field.set(newObject, new SimpleDateFormat(DATE_FORMAT_SUPPORT).parse(strValue));
							}catch(ParseException e){
								throw new ParseException(
										"invokeMapToBean has parse date error case ,format support >>> "+DATE_FORMAT_SUPPORT
										+ " ,map's key >>> "+set.getKey()
										+ " ,map >>> "+map,0);
							}
						}else if(filedType == Boolean.class){
							field.set(newObject, Boolean.valueOf(strValue));
						}else{
							throw new NotSupportedException("invokeMapToBean not support yet at type >>> "+filedType.toString());
						}
					}
				}
			}
			return newObject;
		}
		return null;
	}
	
	//取得非null、非final的成員變數
	public static String getBeanToStringMsg(Object obj){
		StringBuffer sb = new StringBuffer();
		Field[] declaredFields = obj.getClass().getDeclaredFields();
		for(Field field : declaredFields){
			field.setAccessible(true);
			try{
				if(field.get(obj)!=null && 
						Modifier.toString(field.getModifiers()).indexOf("final")==-1){
					sb.append(field.getName()+" >>> "+field.get(obj)+"\n");
				}
			}catch(Exception e){
				//nothing to do
			}
		}
		return sb.toString();
	}
}