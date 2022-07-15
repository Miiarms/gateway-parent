package com.miiarms.cloud.constants;

/**
 *
 *网关缓冲区辅助类
 * @author Miiarms
 * @date 2022/5/19 0:09
 */
public interface BufferHelper {

	String FLUSHER = "FLUSHER";
	
	String MPMC = "MPMC";
	
	static boolean isMpmc(String bufferType) {
		return MPMC.equals(bufferType);
	}
	
	static boolean isFlusher(String bufferType) {
		return FLUSHER.equals(bufferType);
	}
	
}
