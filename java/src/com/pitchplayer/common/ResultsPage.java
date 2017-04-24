package com.pitchplayer.common;

import java.util.List;

/**
 * A page of database results.
 * @author robd
 *
 */
public class ResultsPage {

	private List results;
	private int totalResults;
	private int offset;
	private int pageSize;
	
	public ResultsPage(List results, int totalResults, int offset) {
		this.results = results;
		this.totalResults = totalResults;
		this.offset = offset;
	}
	
	public List getResults() {
		return results;
	}
	public int getTotalResults() {
		return totalResults;
	}
	public int getOffset() {
		return offset;
	}
	public int getPageSize() {
		return pageSize;
	}
	
}
