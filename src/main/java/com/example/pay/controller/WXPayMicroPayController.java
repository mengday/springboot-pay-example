package com.example.pay.controller;

import com.example.pay.configuration.WXPayClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付-刷卡支付.
 * <p>
 * detailed description
 *
 * @author Mengday Zhang
 * @version 1.0
 * @since 2018/6/18
 */
@Slf4j
@RestController
@RequestMapping("/wxpay/microPay")
public class WXPayMicroPayController {

    @Autowired
    private WXPayClient wxPayClient;

    /**
     * 刷卡支付(类似支付宝的条码支付)
     *
     * 和支付宝的好像不一样，支付宝有支付通知，但是微信好像没，微信有退款通知
     *
     * 微信支付后台系统收到支付请求，根据验证密码规则判断是否验证用户的支付密码，不需要验证密码的交易直接发起扣款，
     * 需要验证密码的交易会弹出密码输入框。支付成功后微信端会弹出成功页面，支付失败会弹出错误提示
     * 注意该接口有可能返回错误码为USERPAYING用户支付中
     *
     * 验证密码规则
     * ◆ 支付金额>1000元的交易需要验证用户支付密码
     * ◆ 用户账号每天最多有5笔交易可以免密，超过后需要验证密码
     * ◆ 微信支付后台判断用户支付行为有异常情况，符合免密规则的交易也会要求验证密码
     *
     * 用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头
     */
    @PostMapping("")
    public Object microPay(String authCode) throws Exception {
        Map<String, String> reqData = new HashMap<>();
        // 商户订单号
        reqData.put("out_trade_no", String.valueOf(System.nanoTime()));
        // 订单总金额，单位为分，只能为整数
        reqData.put("total_fee", "1010");
        // 授权码
        reqData.put("auth_code", authCode);
        // 商品描述
        reqData.put("body", "测试");
        Map<String, String> resultMap = wxPayClient.microPayWithPOS(reqData);
        log.info(resultMap.toString());

        return resultMap;
    }

}
