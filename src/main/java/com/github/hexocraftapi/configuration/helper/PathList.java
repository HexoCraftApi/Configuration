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


import java.util.ArrayList;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class PathList
{
	private ArrayList<Path> paths = new ArrayList<Path>();


	public PathList() { }

	public Path add(Path path)
	{
		if(path.top().path().equals("*"))
		{
			Path lastPath = null;
			for(int i = size() - 1; i >= 0 && lastPath == null; i--)
				if(get(i).value() == false)
					lastPath = get(i);

			if(lastPath!=null)
				return add(new Path(path.owner(), path.path().replace("*", lastPath.path()), path.comments()).origin(path.origin()).value(path.value()));
			else
				return add(new Path(path.owner(), path.path().replace("*.", ""), path.comments()).origin(path.origin()).value(path.value()));
		}

		// Check if path exist
		Path exist = get(path.owner(), path);
		if(exist != null) return exist;

		// Add path to map
		if(paths.add(path)) return path;
		else return null;
	}

	Path get(int index)
	{
		return paths.get(index);
	}

	Path get(String fullPath)
	{
		for(int i=0; i<size();i++)
		{
			if(get(i).path().equals(fullPath))
				return get(i);
		}
		return null;
	}

	Path get(Class owner, Path path)
	{
		// Try to find the path with the full path
		Path found = get(owner, path.path());
		if(found != null)
			return found;

		// Try to find the path with the origin
		found = get(owner, path.origin());
		if(found != null)
			return found;

		return null;
	}

	Path get(Class owner, String path)
	{
		for(int i = size() - 1; i >= 0; i--)
		{
			if(get(i).owner().equals(owner))
				if(get(i).origin().equals(path) || get(i).path().equals(path))
					return get(i);
		}
		return null;
	}

	Path remove(int index)
	{
		return paths.remove(index);
	}

	int size()
	{
		return paths.size();
	}
}
