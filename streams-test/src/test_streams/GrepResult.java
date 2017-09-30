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
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class GrepResult {
	public ArrayList<GrepChunk> chunks = new ArrayList<>();

	public static void accumulate(GrepResult r, GrepChunk c) {
//		System.out.printf("[%d]accumulate: %s <- %s\n", Thread.currentThread().getId(), r, c);
		r.chunks.add(c);
	}

	public static GrepResult combine(GrepResult one, GrepResult other) {
//		System.out.printf("[%d]combine:    %s <- %s\n", Thread.currentThread().getId(), one, other);
		one.chunks.addAll(other.chunks);
		return one;
	}

	public static Collector<GrepChunk, GrepResult, GrepResult> collector() {
		Collector<GrepChunk, GrepResult, GrepResult> collector = new Collector<GrepChunk, GrepResult, GrepResult>() {

			@Override
			public BiConsumer<GrepResult, GrepChunk> accumulator() {
				return GrepResult::accumulate;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return EnumSet.noneOf(Characteristics.class);
			}

			@Override
			public BinaryOperator<GrepResult> combiner() {
				return GrepResult::combine;
			}

			@Override
			public Function<GrepResult, GrepResult> finisher() {
				return Function.identity();
			}

			@Override
			public Supplier<GrepResult> supplier() {
				return () -> { return new GrepResult(); };
			}
			
		};
		return collector;
	}
}
