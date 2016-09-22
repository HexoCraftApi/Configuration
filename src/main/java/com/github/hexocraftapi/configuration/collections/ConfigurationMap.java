package com.github.hexocraftapi.configuration.collections;

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
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public abstract class ConfigurationMap<K, V> extends Configuration
{
	/**
	 * The map.
	 */
	private HashMap<K,V> map = new HashMap<K,V>();

	/**
	 * Constructor.
	 *
	 * @param plugin The plugin that this object belong to.
	 */
	public ConfigurationMap(JavaPlugin plugin)
	{
		super(plugin, "");
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for the key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 *
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 */
	public boolean add(K key, V value)
	{
		if (!map.containsKey(key))
			return map.put(key, value) == null ? true : false;

		return false;
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param  key key whose mapping is to be removed from the map
	 *
	 * @return <tt>true</tt> if the {@code key} is removed.
	 */
	public boolean remove(K key)
	{
		if(map.containsKey(key))
			return map.remove(key)== null ? false : true;

		return false;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the
	 * specified key.
	 *
	 * @param   key   The key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 */
	public boolean exist(K key)
	{
		return map.containsKey(key);
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if this map contains no mapping for the key.
	 *
	 * @param  key the key you ar looking for.
	 *
	 * @return The value associated to the key
	 */
	public V get(K key)
	{
		if(exist(key))
			return map.get(key);
		return null;
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map
	 */
	public int size()
	{
		return map.size();
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	public Set<Map.Entry<K, V>> entrySet()
	{
		return map.entrySet();
	}

	@Override
	public Object serialize(Configuration configuration, Object object)
	{
		return super.serialize(configuration, object);
	}

	@Override
	public Object deserialize(Configuration configuration, Class<?> oClass, final Class<?>[] pClass, Object object)
	throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		return super.deserialize(configuration, oClass, pClass, object);
	}
}
