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

public class Test {
	public static void main(String[] args) {
		String regex = ".*(third|second|first|tenth|eigth).*";
		int size = 0;
		HashSet<Long> tids = new HashSet<>();

		// parallel
		System.out.println("parallel stream start...");
		long start = System.currentTimeMillis();
		GrepResult result = DataSource.stream(true).map(Grep.mapper(regex, tids)).collect(GrepResult.collector());
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
		result = DataSource.stream(false).map(Grep.mapper(regex, tids)).collect(GrepResult.collector());
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
