package com.xxx.http;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class OKHttpClientBuilder {
	
	private static OkHttpClient client;
	
	 static {
		X509TrustManager x509mgr = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { x509mgr }, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.sslSocketFactory(sslContext.getSocketFactory())
			.followRedirects(true).followSslRedirects(true)
			.connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
			;
		
		Dispatcher dispatcher = new Dispatcher();
		// default 64
		dispatcher.setMaxRequests(64);
		// default 5
		dispatcher.setMaxRequestsPerHost(1);
		builder.dispatcher(dispatcher);
		client = builder.build();
	}
	
	

	 /**
	  * 指定代理IP，获取一个httpclient
	  * @param proxyHost
	  * @param proxyPort
	  * @return
	  */
	public static OkHttpClient getHttpClient(String proxyHost, int proxyPort) {
		return client.newBuilder()
				.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
				.build();
	}
	
	public static OkHttpClient getHttpClient() {
		return client.newBuilder().build();
	}
}
