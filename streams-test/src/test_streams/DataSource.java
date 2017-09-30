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

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataSource {
	private static final class SpliteratorImplementation implements Spliterator<DataChunk> {
		private List<String> lines;
		private final int chunkSize;
		private int start;
		private int end;

		public SpliteratorImplementation(List<String> lines, int chunkSize, int start, int end) {
			this.lines = lines;
			this.chunkSize = chunkSize;
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean tryAdvance(Consumer<? super DataChunk> action) {
			if (start < end) {
				action.accept(new DataChunk(chunkSize * (start++), lines, chunkSize));
				return true;
			}
			return false;
		}

		@Override
		public Spliterator<DataChunk> trySplit() {
			if (start + 1 < end) {
				start += 1;
				System.out.println("split:" + (start -1 ) + ":" + start);
				return new SpliteratorImplementation(lines, chunkSize, start - 1, start);
			}
			return null;
		}

		@Override
		public long estimateSize() {
			return Long.MAX_VALUE;
		}

		@Override
		public int characteristics() {
			return Spliterator.CONCURRENT;
		}
	}

	List<String> lines = new ArrayList<>();

	public DataSource(List<String> lines) {
		this.lines = lines;
	}

	public Stream<DataChunk> stream(boolean parallel) {
		return stream(parallel, false);
	}

	public Stream<DataChunk> stream(boolean parallel, boolean useList) {
		final int lineNum = 1000000;
		final int chunkSize = 100000;
		if (parallel) {
			ArrayList<DataChunk> arrayList = new ArrayList<>();
			for (int i = 0; i < lineNum; i += chunkSize) {
				DataChunk dataChunk = new DataChunk(i, lines, chunkSize);
				arrayList.add(dataChunk);
			}
			return arrayList.parallelStream();
		} else {
			Spliterator<DataChunk> split = new SpliteratorImplementation(lines, chunkSize, 0, lineNum/chunkSize);
			return StreamSupport.stream(split, parallel);
		}
	}
}
