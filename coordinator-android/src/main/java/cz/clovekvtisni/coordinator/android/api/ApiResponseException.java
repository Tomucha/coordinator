package cz.clovekvtisni.coordinator.android.api;

import cz.clovekvtisni.coordinator.api.response.ApiResponse;

/**
 * Tahle vyjimka nam slouzi k tomu, aby zpropagovala error odpovedi na vyssi vrstvy.
 * 
 * @author tomucha
 *
 */
public class ApiResponseException extends RuntimeException {
	
	private ApiResponse response;

	public ApiResponseException(ApiResponse response) {
		super();
		this.response = response;
	}

	public ApiResponse getResponse() {
		return response;
	}

}