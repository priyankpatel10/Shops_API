package com.retail.shop.util;

import java.net.InetSocketAddress;
import java.net.Proxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.retail.shop.model.Shop;

@Service
public class LocationService {

	/**
	 * logger
	 */
	private static final Logger LOG = Logger.getLogger(LocationService.class.getName());

	/**
	 * the context
	 */
	private GeoApiContext context;

	/**
	 * proxy settings
	 */
	private boolean proxy;
	private String proxyaddress;
	private String proxyport;
	private String proxyuser;
	private String proxypassword;

	/**
	 * the api key
	 */
	private String apikey;

	/**
	 * initialize the Geo API Context with API key and request handler etc
	 */
	private void initializeGeoApiContext() {
		if (proxy) {
			LOG.log(Level.INFO, "Proxy settings on :", proxyaddress + ":" + proxyport + "@" + proxyuser);
			AuthenticatedOkHttpRequestHandler requestHandler = new AuthenticatedOkHttpRequestHandler();
			Authenticator authenticator = new Authenticator(proxyuser, proxypassword);
			requestHandler.setAuthenticator(authenticator);
			InetSocketAddress address = new InetSocketAddress(proxyaddress, Integer.valueOf(proxyport));
			Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
			requestHandler.setProxy(proxy);
			context = new GeoApiContext(requestHandler);
		} else {
			context = new GeoApiContext();
		}
		context.setApiKey(apikey);
	}

	/**
	 * return the geocode of the shop
	 * 
	 * @param shop
	 * @return
	 */
	public LatLng getGeocode(Shop shop) {
		initializeGeoApiContext();
		GeocodingResult[] results = null;
		LatLng geocode = null;
		try {
			results = GeocodingApi.geocode(context, shop.getShopAddress()).await();
			geocode = results[0].geometry.location;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception in invoking Google geocoding API :", e.getCause());
		}
		return geocode;
	}

	/**
	 * comma separated string formated address
	 * 
	 * @param shop
	 * @return
	 */
	

	public void setContext(GeoApiContext context) {
		this.context = context;
	}

	public void setProxyaddress(String proxyaddress) {
		this.proxyaddress = proxyaddress;
	}

	public void setProxyport(String proxyport) {
		this.proxyport = proxyport;
	}

	public void setProxyuser(String proxyuser) {
		this.proxyuser = proxyuser;
	}

	public void setProxypassword(String proxypassword) {
		this.proxypassword = proxypassword;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

}
