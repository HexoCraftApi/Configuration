package com.github.hexocraftapi.configuration.helper;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.hexocraftapi.configuration.Configuration;
import com.github.hexocraftapi.configuration.ConfigurationMapObject;
import com.github.hexocraftapi.configuration.annotation.ConfigFooter;
import com.github.hexocraftapi.configuration.annotation.ConfigHeader;
import com.github.hexocraftapi.configuration.annotation.ConfigPath;
import com.github.hexocraftapi.configuration.annotation.ConfigValue;
import com.github.hexocraftapi.configuration.annotation.DelegateSerialization;
import com.github.hexocraftapi.configuration.serializer.*;
import com.github.hexocraftapi.reflection.util.AccessUtil;
import com.github.hexocraftapi.reflection.util.ConstructorUtil;
import com.github.hexocraftapi.reflection.util.MethodUtil;
import com.google.common.primitives.Primitives;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.gson.internal.$Gson$Types.getRawType;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class Annotations
{
	private final Configuration config;
	private final YamlConfiguration yamlConfiguration;
	private PathList paths;

	// Default comments
	private String[] header;
	private String[] footer;



	public Annotations(Configuration config)
	{
		Validate.notNull(config, "ConfigValue cannot be null");

		this.config = config;
		this.yamlConfiguration = config.getYamlConfiguration();
		this.paths = config.getPaths();
	}

	public void load() throws ReflectiveOperationException
	{
		load(this.config);
	}

	private void load(final Configuration configuration) throws ReflectiveOperationException
	{
		ConfigHeader configHeader = configuration.getClass().getAnnotation(ConfigHeader.class);
		ConfigFooter configFooter = configuration.getClass().getAnnotation(ConfigFooter.class);

		if(configHeader != null && header == null) header = configHeader.comment();
		if(configFooter != null && footer == null) footer = configFooter.comment();

		for(Field field : configuration.getClass().getDeclaredFields())
			load(AccessUtil.setAccessible(field), configuration);
	}

	private void load(final Field field, final Configuration configuration) throws ReflectiveOperationException
	{
		Validate.notNull(field, "field cannot be null");

		ConfigPath configPath = field.getAnnotation(ConfigPath.class);
		ConfigValue configValue = field.getAnnotation(ConfigValue.class);
		DelegateSerialization serializer = field.getAnnotation(DelegateSerialization.class);

		if(configPath == null && configValue == null) return;
		if(skipField(field)) return;

		//
		if(configPath != null)
		{
			Validate.notNull(configPath.path(), "path cannot be null");
			Validate.notEmpty(configPath.path(), "path cannot be empty");

			this.paths.add(new Path(field.getDeclaringClass(), configPath.path(), configPath.comment()).value(false));
		}

		//
		if(configValue != null)
		{
			Validate.notNull(configValue.path(), "path cannot be null");
			Validate.notEmpty(configValue.path(), "path cannot be empty");

			Path path = new Path(field.getDeclaringClass(), configValue.path(), configValue.comment()).value(Configuration.class.isAssignableFrom(field.getType())?false:true);
			path = this.paths.add(path);

			// Object from field
			final Object object = field.get(configuration);

			// Get the value from the config file
			Object value = this.yamlConfiguration.get(path.path());
			if(value instanceof List) if(((List)value).size()==0) value = null;
			if(value instanceof Map) if(((Map)value).size()==0) value = null;
			if(value instanceof MemorySection) if(((MemorySection)value).getValues(false).size() == 0) value = null;


			if(object instanceof Configuration)
			{
				// Load
				Configuration configObject = (Configuration) object;
				load(configObject);

				if(configObject.saveOnLoad())
					configuration.saveOnLoad(true);
			}
			else
			{
				// Add the field to the configuration
				if(value == null)
				{
					update(configuration, field);
					configuration.saveOnLoad(true);
				}

				// Load the field from configuration
				else
				{
					Class<?> mainClass = mainClass(field);
					Class<?>[] parameterClass = parameterClass(field);

					if(serializer != null && serializer.deserialize() != null)
					{
						final Object configObject = ConstructorUtil.getConstructor(serializer.deserialize(), JavaPlugin.class).newInstance(config.getPlugin());
						final Object deserialized = MethodUtil.getMethod(serializer.deserialize(), "deserialize").invoke(configObject, configuration, mainClass, parameterClass, value);
						field.set(configuration, deserialized);
					}
					else
					{
						if(List.class.isAssignableFrom(mainClass) && parameterClass.length>0 && Configuration.class.isAssignableFrom(parameterClass[0]))
						{
							// todo
						}
						else if(Map.class.isAssignableFrom(mainClass) && parameterClass.length>1 && String.class.equals(parameterClass[0]) && ConfigurationMapObject.class.isAssignableFrom(parameterClass[1]))
						{
							// Keep a track on the parent path
							String parentPath = path.path();

							//
							Class<? extends ConfigurationMapObject> configClass = parameterClass[1].asSubclass(ConfigurationMapObject.class);
							final ConfigurationSection sections = (ConfigurationSection)value;
							final Map<String, ConfigurationMapObject> deserializedMap = new HashMap<>();

							// Loop through all entries
							for(final String sectionName : sections.getKeys(false))
							{
								// A new path is created for each entry
								this.paths.add(new Path(configClass, parentPath + "." + sectionName, "").value(false));

								// Instantiate new Configuration Object
								final Object configObject = ConstructorUtil.getConstructor(configClass, JavaPlugin.class).newInstance(config.getPlugin());

								// Load config object
								load(configClass.cast(configObject));

								// Update name
								ConfigurationMapObject.class.cast(configObject).setName(sectionName);

								// Add to map
								deserializedMap.put(sectionName, configClass.cast(configObject));
							}
							field.set(configuration, deserializedMap);
						}
						else
						{
							final Object deserialized = configuration.deserialize(configuration, mainClass, parameterClass, value);
							field.set(configuration, deserialized);
						}
					}
				}
			}
		}
	}

	private static Class<?> mainClass(Field field)
	{
		Type type = field.getGenericType();

		if (type instanceof Class<?>) {
			// type is a normal class.
			return (Class<?>) type;

		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			// I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
			// suspects some pathological case related to nested classes exists.
			Type rawType = parameterizedType.getRawType();
			return (Class<?>) rawType;

		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();

		} else if (type instanceof TypeVariable) {
			// We could use the variable's bounds, but that won't work if there are multiple. having a raw
			// type that's more general than necessary is okay.
			return Object.class;

		} else if (type instanceof WildcardType) {
			return getRawType(((WildcardType) type).getUpperBounds()[0]);

		} else {
			String className = type == null ? "null" : type.getClass().getName();
			throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
			+ "GenericArrayType, but <" + type + "> is of type " + className);
		}
	}

	private static Class<?>[] parameterClass(Field field)
	{
		return parameterClass(field, field.getGenericType());
	}

	private static Class<?>[] parameterClass(Field field, Type type)
	{
		if (type instanceof ParameterizedType)
		{
			ParameterizedType pt = (ParameterizedType) type;
			Class<?>[] pc = new Class<?>[pt.getActualTypeArguments().length];

			for(int i=0; i<pt.getActualTypeArguments().length; i++)
				pc[i] = (Class<?>) pt.getActualTypeArguments()[i];

			return pc;
		}

		/*else if(ConfigurationNamedObjectMap.class.isAssignableFrom(field.getType()))
		{
			Type superClassType = field.getType().getGenericSuperclass();

			Class<?>[] pc = new Class<?>[2];
			pc[0] = String.class;
			pc[1] = parameterClass(field, superClassType)[0];

			return pc;
		}*/

		return null;
	}

	public void update() throws ReflectiveOperationException
	{
		update(this.config);
	}

	private void update(final Configuration configuration) throws ReflectiveOperationException
	{
		for(Field field : configuration.getClass().getDeclaredFields())
			update(configuration, AccessUtil.setAccessible(field));
	}

	private void update(final Configuration configuration, final Field field) throws ReflectiveOperationException
	{
		Validate.notNull(field, "field cannot be null");

		ConfigValue configValue = field.getAnnotation(ConfigValue.class);
		DelegateSerialization serializer = field.getAnnotation(DelegateSerialization.class);

		if(configValue == null) return;
		if(skipField(field)) return;

		Validate.notNull(configValue.path(), "path cannot be null");
		Validate.notEmpty(configValue.path(), "path cannot be empty");

		final Object object = field.get(configuration);
		Path path = new Path(field.getDeclaringClass(), configValue.path(), configValue.comment()).value(Configuration.class.isAssignableFrom(field.getType())?false:true);
		path = this.paths.add(path);

		if(!(object instanceof Configuration))
		{
			Class<?> mainClass = mainClass(field);
			Class<?>[] parameterClass = parameterClass(field);

			if(serializer!=null && serializer.serialize()!=null)
			{
				final Object configObject = ConstructorUtil.getConstructor(serializer.serialize(), JavaPlugin.class).newInstance(config.getPlugin());
				final Object serialized = MethodUtil.getMethod(serializer.deserialize(), "serialize").invoke(configObject, configuration, object);
				this.yamlConfiguration.set(path.path(), serialized);
			}
			else
			{
				if(List.class.isAssignableFrom(mainClass) && parameterClass.length>0 && Configuration.class.isAssignableFrom(parameterClass[0]))
				{
					// todo
				}
				else if(Map.class.isAssignableFrom(mainClass) && parameterClass.length>1 && String.class.equals(parameterClass[0]) && ConfigurationMapObject.class.isAssignableFrom(parameterClass[1]))
				{
					// Keep a track on the parent path
					String parentPath = path.path();

					// Cast to Map
					Class<? extends ConfigurationMapObject> configClass = parameterClass[1].asSubclass(ConfigurationMapObject.class);
					Map<String, ConfigurationMapObject> mapObject = (Map<String, ConfigurationMapObject>) object;

					// Loop through all entries
					for(Map.Entry<String, ConfigurationMapObject> entry : mapObject.entrySet())
					{
						// Extract entry data
						String name = entry.getKey();
						ConfigurationMapObject configObject = configClass.cast(entry.getValue());

						// A new path is created for each entry
						this.paths.add(new Path(configClass, parentPath + "." + name, "").value(false));

						// Update
						update(configObject);
					}
				}
				else
				{
					final Object serialized = configuration.serialize(configuration, object);
					this.yamlConfiguration.set(path.path(), serialized);
				}
			}
		}
	}

	private boolean skipField(Field field)
	{
		return Modifier.isTransient(field.getModifiers());
	}

	public Object serialize(final Configuration configuration, final Object object)
	{
		if(object == null)
			return null;

		//if(object instanceof Serializer)
		//	return ((Serializer) object).serialize(configuration, object);

		else if(object instanceof String)
			return StringSerializer.get().serialize(configuration, (String)object);

		else if(object instanceof Enum)
			return EnumSerializer.get().serialize(configuration, (Enum<?>)object);

		else if(object instanceof List)
			return ListSerializer.get().serialize(configuration, (List<?>)object);

		else if(object instanceof Map)
			return MapSerializer.get().serialize(configuration, (Map<?, ?>)object);

		else if(object instanceof Location)
			return LocationSerializer.get().serialize(configuration, (Location)object);

		else if(object instanceof Chunk)
			return ChunkSerializer.get().serialize(configuration, (Chunk)object);

		else if(object instanceof Vector)
		return VectorSerializer.get().serialize(configuration, (Vector)object);

		else if(object instanceof ItemStack)
			return ItemStackSerializer.get().serialize(configuration, (ItemStack)object);

		else
			return object;
	}

	public final Object deserialize(final Configuration configuration, final Class<?> oClass, final Class<?>[] pClass, final Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException
	{
		if(object == null)
			return null;

		//else if(Serializer.class.isAssignableFrom(aClass) || object instanceof Serializer)
		//{
		//	final Class<?> parentClass = aClass.getDeclaringClass();
		//	if(parentClass != null && parentClass != aClass)
		//	{
		//		final Object parentObject = ConstructorUtil.getConstructor(parentClass, JavaPlugin.class).newInstance(configuration.getPlugin());
		//		final Object configObject = ConstructorUtil.getConstructor(aClass, parentClass, JavaPlugin.class).newInstance(parentObject, config.getPlugin());
		//		return MethodUtil.getMethod(aClass, "deserialize").invoke(configObject, configuration, object.getClass(), object);
		//	}
		//	else
		//	{
		//		final Object configObject = ConstructorUtil.getConstructor(aClass, JavaPlugin.class).newInstance(config.getPlugin());
		//		return MethodUtil.getMethod(aClass, "deserialize").invoke(configObject, configuration, object.getClass(), object);
		//	}
		//}

		else if(oClass.isPrimitive()) {
			return Primitives.wrap(oClass).getMethod("valueOf", String.class).invoke(this, object.toString());
		}

		else if(Primitives.isWrapperType(oClass)) {
			return oClass.getMethod("valueOf", String.class).invoke(this, object.toString());
		}

		else if(oClass.isEnum() || object instanceof Enum)
			return EnumSerializer.get().deserialize(configuration, (Class<? extends Enum>)oClass, pClass, object);

		else if(List.class.isAssignableFrom(oClass) || object instanceof List)
			return ListSerializer.get().deserialize(configuration, (Class<? extends List<?>>)oClass, pClass, object);

		else if(Map.class.isAssignableFrom(oClass) || object instanceof Map)
			return MapSerializer.get().deserialize(configuration, (Class<? extends Map<?,?>>)oClass, pClass, object);

		else if(Location.class.isAssignableFrom(oClass) || object instanceof Location)
			return LocationSerializer.get().deserialize(configuration, (Class<? extends Location>)oClass, pClass, object);

		else if(Chunk.class.isAssignableFrom(oClass) || object instanceof Chunk)
			return ChunkSerializer.get().deserialize(configuration, (Class<? extends Chunk>)oClass, pClass, object);

		else if(Vector.class.isAssignableFrom(oClass) || object instanceof Vector)
			return VectorSerializer.get().deserialize(configuration, (Class<? extends Vector>)oClass, pClass, object);

		else if(ItemStack.class.isAssignableFrom(oClass) || object instanceof ItemStack)
			return ItemStackSerializer.get().deserialize(configuration, (Class<? extends ItemStack>)oClass, pClass, object);

		return ChatColor.translateAlternateColorCodes('&', object.toString());
	}

	public String[] getDefaultHeaderComment() { return this.header; }
	public String[] getDefaultFooterComment() { return this.footer; }
	public String[] getDefaultComment(String path) {
		Path p = this.paths.get(path);
		return p != null ? p.comments() : null;
	}
}
