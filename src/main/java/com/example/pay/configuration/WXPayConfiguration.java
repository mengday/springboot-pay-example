package com.example.pay.configuration;


import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 *
 * @author mengday zhang
 */
@Configuration
@EnableConfigurationProperties(MyWXPayConfig.class)
public class WXPayConfiguration {

	@Autowired
	private MyWXPayConfig wxPayConfig;

	/**
	 * useSandbox 沙盒环境
	 * @return
	 */
	@Bean
	public WXPay wxPay() {
		return new WXPay(wxPayConfig, WXPayConstants.SignType.MD5, wxPayConfig.getUseSandbox() );
	}

	@Bean
	public WXPayClient wxPayClient() {
		return new WXPayClient(wxPayConfig, WXPayConstants.SignType.MD5, wxPayConfig.getUseSandbox());
	}
}
