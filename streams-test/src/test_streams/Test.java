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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Test {
	
	private List<String> lines;

	public Test(List<String> lines) {
		this.lines = lines;
	}

	public GrepResult collectionSequential(String regex, HashSet<Long> tids) {
		GrepResult result = new DataSource(lines).stream(false, true).map(GrepMapper.mapper(regex, tids)).collect(GrepResult.collector());
		return result;
	}

	public GrepResult collectionParallel(String regex, HashSet<Long> tids) {
		GrepResult result = new DataSource(lines).stream(true, true).map(GrepMapper.mapper(regex, tids)).collect(GrepResult.collector());
		return result;
	}

	public GrepResult customSequential(String regex, HashSet<Long> tids) {
		GrepResult result = new DataSource(lines).stream(false, false).map(GrepMapper.mapper(regex, tids)).collect(GrepResult.collector());
		return result;
	}

	public GrepResult customParallel(String regex, HashSet<Long> tids) {
		GrepResult result = new DataSource(lines).stream(true, false).map(GrepMapper.mapper(regex, tids)).collect(GrepResult.collector());
		return result;
	}

	public static void main(String[] args) {
		final String[] data = {
				"this is the first line",
				"and the second...",
				"and the third...",
				"and the fourth...",
				"and the fifth...",
				"and the sixth...",
				"and the seventh...",
				"and the eighth...",
				"and the nineth...",
				"and the tenth...",
		};
		Test test = new Test(Arrays.asList(data));
		String regex = ".*(third|second|first|tenth|eigth).*";
		int size = 0;
		HashSet<Long> tids = new HashSet<>();

		// parallel
		System.out.println("parallel stream start...");
		long start = System.currentTimeMillis();
		GrepResult result = test.customParallel(regex, tids);
		long end = System.currentTimeMillis();
		System.out.println("done in " + (end - start) + " ms");
		for (GrepChunk chunk : result.chunks) {
			 size = size + chunk.lines.size();
		}
		System.out.println("grep lines: " + size);
		printTids(tids);
		System.out.println();
		
		// sequential
		tids.clear();
		System.out.println("sequential stream start...");
		start = System.currentTimeMillis();
		result = test.customSequential(regex, tids);
		end = System.currentTimeMillis();
		System.out.println("done in " + (end - start) + " ms");
		size = 0;
		for (GrepChunk chunk : result.chunks) {
			 size = size + chunk.lines.size();
		}
		System.out.println("grep lines: " + size);
		printTids(tids);
	}

	private static void printTids(HashSet<Long> tids) {
		System.out.print("tids: {");
		for (Long tid : tids) {
			System.out.print(" " + tid);
		}
		System.out.println("}");
	}
}
