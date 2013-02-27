package com.gdelight.server.service;

public class PostServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PostServiceException(String message) {
		super(message);
	}
	
	public PostServiceException(Throwable e) {
		super(e);
	}
	
	public PostServiceException(Throwable exception, String message) {
		super(message, exception);
	}
	
}
