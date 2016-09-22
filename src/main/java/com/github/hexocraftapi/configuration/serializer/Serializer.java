package com.github.hexocraftapi.configuration.serializer;

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

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */


public interface Serializer<Obj>
{
	/**
	 * Serialize the data to an Object class
	 *
	 * @param configuration Configuration
	 * @param object Object to serialize
	 *
	 * @return the serialized {@code object}
	 */
	Object serialize(final Configuration configuration, final Obj object);


	/**
	 * Deserialize the object to a data object
	 *
	 * @param configuration Configuration
	 * @param oClass Class of the object
	 * @param pClass Parameters class of {@code oClass}. Used when {@code oClass} is type of @link #List
	 * @param object Object to serialize
	 *
	 * @return the deserialized {@code object}
	 *
	 * @throws Exception When something went wrong
	 */
	Obj deserialize(final Configuration configuration, final Class<? extends Obj> oClass, final Class<?>[] pClass, final Object object) throws Exception;
}
