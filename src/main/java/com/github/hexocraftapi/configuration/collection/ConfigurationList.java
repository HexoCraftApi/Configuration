package com.github.hexocraftapi.configuration.collection;

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

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class ConfigurationList<E extends ConfigurationObject> extends ArrayList<E>
{
	public ConfigurationList()
	{
	}

	public ConfigurationList(int initialCapacity)
	{
		super(initialCapacity);
	}

	public ConfigurationList(Collection<? extends E> c)
	{
		super(c);
	}
}
