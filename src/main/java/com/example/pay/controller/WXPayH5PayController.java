package com.example.pay.controller;

import com.example.pay.configuration.WXPayClient;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付-H5支付.
 * <p>
 * detailed description
 *
 * @author Mengday Zhang
 * @version 1.0
 * @since 2018/6/18
 */
@Slf4j
@RestController
@RequestMapping("/wxpay/h5pay")
public class WXPayH5PayController {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private WXPayClient wxPayClient;

    /**
     * 使用沙箱支付的金额必须是用例中指定的金额，也就是 1.01 元，1.02元等，不能是你自己的商品的实际价格，必须是这个数。
     * 否则会报错：沙箱支付金额(2000)无效，请检查需要验收的case
     * @return
     * @throws Exception
     */
    @PostMapping("/order")
    public Object h5pay() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", String.valueOf(System.nanoTime()));
        reqData.put("trade_type", "MWEB");
        reqData.put("product_id", "1");
        reqData.put("body", "商户下单");
        // 订单总金额，单位为分
        reqData.put("total_fee", "101");
        // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        reqData.put("spbill_create_ip", "14.23.150.211");
        // 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        reqData.put("notify_url", "http://3sbqi7.natappfree.cc/wxpay/h5pay/notify");
        // 自定义参数, 可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
        reqData.put("device_info", "");
        // 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
        reqData.put("attach", "");
        reqData.put("scene_info", "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://3sbqi7.natappfree.cc\",\"wap_name\": \"腾讯充值\"}}");

        Map<String, String> responseMap = wxPay.unifiedOrder(reqData);
        log.info(responseMap.toString());
        String returnCode = responseMap.get("return_code");
        String resultCode = responseMap.get("result_code");
        if (WXPayConstants.SUCCESS.equals(returnCode) && WXPayConstants.SUCCESS.equals(resultCode)) {
            // 预支付交易会话标识
            String prepayId = responseMap.get("prepay_id");
            // 支付跳转链接(前端需要在该地址上拼接redirect_url,该参数不是必须的)
            // 正常流程用户支付完成后会返回至发起支付的页面，如需返回至指定页面，则可以在MWEB_URL后拼接上redirect_url参数，来指定回调页面
            // 需对redirect_url进行urlencode处理

            // TODO 正常情况下这里应该是普通的链接，不知道这里为何是weixin://这样的链接，不知道是不是微信公众平台上的配置少配置了；
            // 由于没有实际账号，还没找到为啥不是普通链接的原因
            String mwebUrl = responseMap.get("mweb_url");
        }

        return responseMap;
    }

    /**
     * 注意：如果是沙箱环境，一提交订单就会立即异步通知，而无需拉起微信支付收银台的中间页面
     * @param request
     * @throws Exception
     */
    @RequestMapping("/notify")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Map<String, String> reqData = wxPayClient.getNotifyParameter(request);
        log.info(reqData.toString());


        String returnCode = reqData.get("return_code");
        String resultCode = reqData.get("result_code");
        if (WXPayConstants.SUCCESS.equals(returnCode) && WXPayConstants.SUCCESS.equals(resultCode)) {
            boolean signatureValid = wxPay.isPayResultNotifySignatureValid(reqData);

            if (signatureValid) {
                // TODO 业务处理

                Map<String, String> responseMap = new HashMap<>(2);
                responseMap.put("return_code", "SUCCESS");
                responseMap.put("return_msg", "OK");
                String responseXml = WXPayUtil.mapToXml(responseMap);

                response.setContentType("text/xml");
                response.getWriter().write(responseXml);
                response.flushBuffer();
            }
        }
    }
}
