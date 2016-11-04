package au.edu.anu.scoap.dspace.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.edu.anu.scoap.dspace.DSpaceObject;

/**
 * Transforms a MARC21 record into a Map that we can use for populating DSpace.
 * 
 * @author Genevieve Turner
 */
public class DSpaceObjectParser {
	public Map<String, Set<String>> getDSpaceValues(DSpaceObject record) throws InvocationTargetException
			, IllegalAccessException {
		
		Map<String, Set<String>> values = new HashMap<String, Set<String>>();
		
		Class<?> clazz = record.getClass();
		Method[] methods = clazz.getMethods();
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(DSpaceField.class)) {
				DSpaceField field = method.getAnnotation(DSpaceField.class);
				@SuppressWarnings("unchecked")
				List<String> getValue = (List<String>)method.invoke(record);
				values.put(field.value(), new LinkedHashSet<String>(getValue));
			}
		}
		
		return values;
	}
}
