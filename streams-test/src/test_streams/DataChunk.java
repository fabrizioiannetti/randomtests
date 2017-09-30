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

public class DataChunk {
	public int startLine;
	public ArrayList<String> lines;

	public DataChunk(int startLine, List<String> srcLines, int size) {
		this.startLine = startLine;
		lines = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			lines.add(srcLines.get(i%srcLines.size()));
		}
	}

}
