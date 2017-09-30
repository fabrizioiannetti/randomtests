/*******************************************************************************
 * Copyright (c) 2017 Fabrizio Iannetti.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Fabrizio Iannetti - initial API and implementation and/or initial documentation
 *******************************************************************************/

package test_streams;

import java.util.HashSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrepMapper implements Function<DataChunk, GrepChunk> {

	private final Pattern regex;
	
	private final HashSet<Long> threadIds;

	public GrepMapper(String regex, HashSet<Long> threadIds) {
		this.regex = Pattern.compile(regex);
		this.threadIds = threadIds;
	}

	public static GrepMapper mapper(String regex) {
		return new GrepMapper(regex, new HashSet<>());
	}

	public static GrepMapper mapper(String regex, HashSet<Long> threadIds) {
		return new GrepMapper(regex, threadIds);
	}

	@Override
	public GrepChunk apply(DataChunk arg0) {
		synchronized (threadIds) {
			threadIds.add(Thread.currentThread().getId());
		}
		GrepChunk grepChunk = new GrepChunk(arg0.startLine);
		Matcher matcher = regex.matcher("");
		for (String line : arg0.lines) {
			if (matcher.reset(line).find())
				grepChunk.lines.add(line);
		}
		return grepChunk;
	}

	public HashSet<Long> getThreadIds() {
		return threadIds;
	}
}
