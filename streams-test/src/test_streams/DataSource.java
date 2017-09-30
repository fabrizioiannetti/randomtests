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
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataSource {
	private static final class SpliteratorImplementation implements Spliterator<DataChunk> {
		private final int chunkSize;
		private int start;
		private int end;
		public SpliteratorImplementation(int chunkSize, int start, int end) {
			this.chunkSize = chunkSize;
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean tryAdvance(Consumer<? super DataChunk> action) {
			if (start < end) {
				action.accept(new DataChunk(chunkSize * (start++), data, chunkSize));
				return true;
			}
			return false;
		}

		@Override
		public Spliterator<DataChunk> trySplit() {
			if (start + 1 < end) {
				start += 1;
				System.out.println("split:" + (start -1 ) + ":" + start);
				return new SpliteratorImplementation(chunkSize, start - 1, start);
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

	private static final String[] data = {
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

	public static Stream<DataChunk> stream(boolean parallel) {
		return stream(parallel, false);
	}

	public static Stream<DataChunk> stream(boolean parallel, boolean useList) {
		final int lineNum = 10000000;
		final int chunkSize = 100000;
		if (parallel) {
			ArrayList<DataChunk> arrayList = new ArrayList<>();
			for (int i = 0; i < lineNum; i += chunkSize) {
				DataChunk dataChunk = new DataChunk(i, data, chunkSize);
				arrayList.add(dataChunk);
			}
			return arrayList.parallelStream();
		} else {
//			Iterator<DataChunk> iterator = new Iterator<DataChunk>() {
//				int count = 0;
//				@Override
//				public boolean hasNext() {
//					return count < (lineNum / chunkSize);
//				}
//				@Override
//				public DataChunk next() {
//					return new DataChunk(chunkSize * (count++), data, chunkSize);
//				}
//			};
			Spliterator<DataChunk> split = new SpliteratorImplementation(chunkSize, 0, lineNum/chunkSize);
			return StreamSupport.stream(split, parallel);
		}
	}
}
