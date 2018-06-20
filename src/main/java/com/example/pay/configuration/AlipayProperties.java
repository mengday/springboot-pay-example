package com.example.pay.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * 支付宝支付的参数配置
 *
 * @author mengday zhang
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "pay.alipay")
public class AlipayProperties {

	/** 支付宝gatewayUrl */
	private String gatewayUrl;
	/** 商户应用id */
	private String appid;
	/** RSA私钥，用于对商户请求报文加签 */
	private String appPrivateKey;
	/** 支付宝RSA公钥，用于验签支付宝应答 */
	private String alipayPublicKey;
	/** 签名类型 */
	private String signType = "RSA2";

	/** 格式 */
	private String formate = "json";
	/** 编码 */
	private String charset = "UTF-8";

	/** 同步地址 */
	private String returnUrl;

	/** 异步地址 */
	private String notifyUrl;

	/** 最大查询次数 */
	private static int maxQueryRetry = 5;
	/** 查询间隔（毫秒） */
	private static long queryDuration = 5000;
	/** 最大撤销次数 */
	private static int maxCancelRetry = 3;
	/** 撤销间隔（毫秒） */
	private static long cancelDuration = 3000;

	private AlipayProperties() {}

	/**
	 * PostContruct是spring框架的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法。
	 */
	@PostConstruct
	public void init() {
		log.info(description());
	}

	public String description() {
		StringBuilder sb = new StringBuilder("\nConfigs{");
		sb.append("支付宝网关: ").append(gatewayUrl).append("\n");
		sb.append(", appid: ").append(appid).append("\n");
		sb.append(", 商户RSA私钥: ").append(getKeyDescription(appPrivateKey)).append("\n");
		sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(alipayPublicKey)).append("\n");
		sb.append(", 签名类型: ").append(signType).append("\n");

		sb.append(", 查询重试次数: ").append(maxQueryRetry).append("\n");
		sb.append(", 查询间隔(毫秒): ").append(queryDuration).append("\n");
		sb.append(", 撤销尝试次数: ").append(maxCancelRetry).append("\n");
		sb.append(", 撤销重试间隔(毫秒): ").append(cancelDuration).append("\n");
		sb.append("}");
		return sb.toString();
	}

	private String getKeyDescription(String key) {
		int showLength = 6;
		if (StringUtils.isNotEmpty(key) && key.length() > showLength) {
			return new StringBuilder(key.substring(0, showLength)).append("******")
					.append(key.substring(key.length() - showLength)).toString();
		}
		return null;
	}
}
