package com.marked.pixsee.data;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public interface Mapper<From, To> {
	To map(From from);
}
