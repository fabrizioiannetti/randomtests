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
package test_streams.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import test_streams.GrepResult;

class PerfTests {
	private static final String SIMPLE_REGEX = "occhi";
	private static final String COMPLEX_REGEX = "(?<=(\\s|^))(che|per|paura|cose)+\\s+(sonno|e[a-z]+)(?=\\s)";
	private static test_streams.Test underTest;
	private static HashSet<Long> tids;
	
	@BeforeAll
	static void setup() {
		final ArrayList<String> lines = new ArrayList<>();
		try(Scanner s = new Scanner(PerfTests.class.getResourceAsStream("commedia"))) {
			s.useDelimiter("\\R").forEachRemaining((String line) -> {lines.add(line);});
		}
		underTest = new test_streams.Test(lines);
		tids = new HashSet<>();
	}

	@BeforeEach
	void prepareTest() {
		tids.clear();
	}

	@Test
	void testCollectionSequential() {
		GrepResult result = underTest.collectionSequential(SIMPLE_REGEX, tids);
		assertEquals(14310, result.size());
		assertEquals(1, tids.size());
	}

	@Test
	void testCollectionParallel() {
		GrepResult result = underTest.collectionParallel(SIMPLE_REGEX, tids);
		assertEquals(14310, result.size());
		assertTrue(tids.size() > 1);
	}

	@Test
	void testCustomSequential() {
		GrepResult result = underTest.customSequential(SIMPLE_REGEX, tids);
		assertEquals(14310, result.size());
		assertEquals(1, tids.size());
	}

	@Test
	void testCustomParallel() {
		GrepResult result = underTest.customParallel(SIMPLE_REGEX, tids);
		assertEquals(14310, result.size());
		assertTrue(tids.size() > 1);
	}

	@Test
	void testCollectionSequentialComplex() {
		GrepResult result = underTest.collectionSequential(COMPLEX_REGEX, tids);
		assertEquals(1720, result.size());
		assertEquals(1, tids.size());
	}

	@Test
	void testCollectionParallelComplex() {
		GrepResult result = underTest.collectionParallel(COMPLEX_REGEX, tids);
		assertEquals(1720, result.size());
		assertTrue(tids.size() > 1);
	}

	@Test
	void testCustomSequentialComplex() {
		GrepResult result = underTest.customSequential(COMPLEX_REGEX, tids);
		assertEquals(1720, result.size());
		assertEquals(1, tids.size());
	}

	@Test
	void testCustomParallelComplex() {
		GrepResult result = underTest.customParallel(COMPLEX_REGEX, tids);
		assertEquals(1720, result.size());
		assertTrue(tids.size() > 1);
	}

}
