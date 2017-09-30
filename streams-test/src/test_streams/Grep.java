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

public class Grep implements Function<DataChunk, GrepChunk> {

	private final String regex;
	private final HashSet<Long> threadIds;

	public Grep(String regex, HashSet<Long> threadIds) {
		this.regex = regex;
		this.threadIds = threadIds;
	}

	public static Grep mapper(String regex) {
		return new Grep(regex, new HashSet<>());
	}

	public static Grep mapper(String regex, HashSet<Long> threadIds) {
		return new Grep(regex, threadIds);
	}

//	public String grepText(String text) {
//		CharSequence input = text;
//		Matcher m = Pattern.compile(regex).matcher(input);
//		while (m.find()) {
//			m.start();
//		}
//	}

	@Override
	public GrepChunk apply(DataChunk arg0) {
		synchronized (threadIds) {
			threadIds.add(Thread.currentThread().getId());
		}
//		System.out.printf("[%d]grep: %d\n", Thread.currentThread().getId(), arg0.startLine);
		GrepChunk grepChunk = new GrepChunk(arg0.startLine);
		for (String line : arg0.lines) {
			if (line.matches(regex))
				grepChunk.lines.add(line);
		}
		return grepChunk;
	}

	public HashSet<Long> getThreadIds() {
		return threadIds;
	}
}
