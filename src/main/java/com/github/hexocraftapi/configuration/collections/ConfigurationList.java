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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public abstract class ConfigurationList<E> extends Configuration
{
	/**
	 * The list.
	 */
	private ArrayList<E> list = new ArrayList<E>();

	/**
	 * Constructor.
	 *
	 * @param plugin The plugin that this object belong to.
	 */
	public ConfigurationList(JavaPlugin plugin)
	{
		super(plugin, "");
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param element element to be appended to this list
	 * @return <tt>true</tt> (as specified by {@link Collection#add})
	 */
	public boolean add(E element)
	{
		if (!list.contains(element))
			return list.add(element);

		return false;
	}

	/**
	 * Removes the first occurrence of the specified element from this list,
	 * if it is present.  If the list does not contain the element, it is
	 * unchanged.  More formally, removes the element with the lowest index
	 * <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
	 * (if such an element exists).  Returns <tt>true</tt> if this list
	 * contained the specified element (or equivalently, if this list
	 * changed as a result of the call).
	 *
	 * @param element element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(E element)
	{
		return list.remove(element);
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 * More formally, returns <tt>true</tt> if and only if this list contains
	 * at least one element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param element element whose presence in this list is to be tested
	 * @return <tt>true</tt> if this list contains the specified element
	 */
	public boolean exist(E element)
	{
		return list.contains(element);
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public int size()
	{
		return list.size();
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 *
	 * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
	 *
	 * @return an iterator over the elements in this list in proper sequence
	 */
	public Iterator<E> iterator() {
		return list.iterator();
	}

	/**
	 * Returns the internal list.
	 *
	 * @return the number of elements in this list
	 */
	public ArrayList<E> getList()
	{
		return list;
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
