package com.alipay.android.app.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.android.app.sdk.AliPay;

public class AlipayHelper {

	public static int RQF_PAY = 1;

	static String partner = Keys.DEFAULT_PARTNER;
	static String seller = Keys.DEFAULT_SELLER;
	static String publicKey = Keys.PUBLIC;

	static Handler defaultHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			result.parseResult();
		};
	};

	public static void init(String partner, String seller, String publicKey) {
		AlipayHelper.partner = partner;
		AlipayHelper.seller = seller;
		AlipayHelper.publicKey = publicKey;
	}

	public static void pay(final Activity context, String privateKey,
			String info, final Handler mHandler) {
		try {
			String sign = Rsa.sign(info, privateKey);
			sign = URLEncoder.encode(sign, "UTF-8");
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("Alipay", "start pay. info = " + info);

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(context, mHandler);

					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Log.i("Alipay", "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void payWithSign(final Activity context, String sign,
			String info, final Handler mHandler) {
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("Alipay", "start pay with sign. info = " + info);

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(context, mHandler);

					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Log.i("Alipay", "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getOrderInfo(String order_id, String title,
			String price, String callbackUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(partner);
		sb.append("\"&out_trade_no=\"");
		sb.append(order_id);
		sb.append("\"&subject=\"");
		sb.append(title);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		try {
			sb.append(URLEncoder.encode(callbackUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		try {
			sb.append(URLEncoder.encode("http://m.alipay.com", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(seller);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	private static String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
