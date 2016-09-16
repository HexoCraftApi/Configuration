package com.github.hexocraftapi.configuration.helper;

/*
 * Copyright 2015 hexosse
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


import java.util.ArrayList;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class PathList
{
	private ArrayList<Path> paths = new ArrayList<Path>();


	public PathList() { }

	public Path add(Path path)
	{
		if(path.top().path().equals("*") && size() > 0)
		{
			Path lastPath = null;
			for(int i = size()-1; i >= 0 && lastPath==null; i--)
			{
				if(get(i).value()==false)
					lastPath = get(i);
			}

			if(lastPath!=null)
				return add(new Path(path.owner(), path.path().replace("*", lastPath.path()), path.comments()).origin(path.origin()).value(path.value()));
		}

		if(paths.add(path))	return path;
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

	Path get(Class owner, String path)
	{
		for(int i=0; i<size();i++)
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
