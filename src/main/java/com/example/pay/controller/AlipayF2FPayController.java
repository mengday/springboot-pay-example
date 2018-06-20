package com.example.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.example.pay.configuration.AlipayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 支付宝-当面付 控制器.
 * <p>
 * https://openclub.alipay.com/read.php?tid=1720&fid=40
 *
 * https://docs.open.alipay.com/203/105910
 *
 * @author Mengday Zhang
 * @version 1.0
 * @since 2018/6/4
 */
@Slf4j
@RestController
@RequestMapping("/alipay/f2fpay")
public class AlipayF2FPayController {

    @Autowired
    private AlipayTradeService alipayTradeService;

    @Autowired
    private AlipayProperties aliPayProperties;


    /**
     * 当面付-条码付
     *
     * 商家使用扫码工具(扫码枪等)扫描用户支付宝的付款码
     *
     * @param authCode
     */
    @PostMapping("/barCodePay")
    public String barCodePay(String authCode){
        // 实际使用时需要根据商品id查询商品的基本信息并计算价格(可能还有什么优惠)，这里只是写死值来测试

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        String outTradeNo = UUID.randomUUID().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
        String subject = "测试订单";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品2件共20.05元";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = "0.01";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";


        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "全麦小面包", 1, 1);
        goodsDetailList.add(goods1);
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "黑人牙刷", 1, 2);
        goodsDetailList.add(goods2);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";


        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
                .setOutTradeNo(outTradeNo)
                .setSubject(subject)
                .setBody(body)
                .setTotalAmount(totalAmount)
                .setAuthCode(authCode)
                .setTotalAmount(totalAmount)
                .setStoreId(storeId)
                .setOperatorId(operatorId)
                .setGoodsDetailList(goodsDetailList)
                .setTimeoutExpress(timeoutExpress);

        // 当面付，面对面付，face to face pay -> face 2 face pay -> f2f pay
        // 同步返回支付结果
        AlipayF2FPayResult f2FPayResult = alipayTradeService.tradePay(builder);
        // 注意：一定要处理支付的结果，因为不是每次支付都一定会成功，可能会失败
        switch (f2FPayResult.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝支付成功: )");
                break;

            case FAILED:
                log.error("支付宝支付失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }

        /**
         * {
         *   "alipay_trade_pay_response": {
         *     "code": "10000",
         *     "msg": "Success",
         *     "buyer_logon_id": "ekf***@sandbox.com",
         *     "buyer_pay_amount": "0.01",
         *     "buyer_user_id": "2088102176027680",
         *     "buyer_user_type": "PRIVATE",
         *     "fund_bill_list": [
         *       {
         *         "amount": "0.01",
         *         "fund_channel": "ALIPAYACCOUNT"
         *       }
         *     ],
         *     "gmt_payment": "2018-06-10 14:54:16",
         *     "invoice_amount": "0.01",
         *     "out_trade_no": "91fbd3fa-8558-443a-82c2-bd8e941d7e71",
         *     "point_amount": "0.00",
         *     "receipt_amount": "0.01",
         *     "total_amount": "0.01",
         *     "trade_no": "2018061021001004680200326495"
         *   },
         *   "sign": "BNgMmA2t8fwFZNSa39kyEKgL6hV45DVOKOsdyyzTzsQnX8HEkKOzVevQEDyK083dNYewip1KK/K92BTDY2KMAsrOEqcCNxsk9NLAvK9ZQVxQzFbAFKqs5EBAEzncSWnChJcb7VMhDakUxHZFmclHg38dLJiHE2bEcF8ar9R1zj0p4V0Jr+BXO10kLtaSTc9NeaCwJZ89sPHKitNnUWRroU7t0xPHc1hWpstObwixKmAWnsFyb9eyGwPQnqNBsUVNSNWCJ7Pg3rb03Tx6J3zNsqH5f0YhWilMi09npPe33URFc6zG1HJSfhEm4Gq1zwQrPoA/anW8BbdmEUUmNo1dEw=="
         * }
         */
        String result = f2FPayResult.getResponse().getBody();

        return result;
    }

    /**
     * 当面付-扫码付
     *
     * 扫码支付，指用户打开支付宝钱包中的“扫一扫”功能，扫描商户针对每个订单实时生成的订单二维码，并在手机端确认支付。
     *
     * 发起预下单请求，同步返回订单二维码
     *
     * 适用场景：商家获取二维码展示在屏幕上，然后用户去扫描屏幕上的二维码
     * @return
     * @throws AlipayApiException
     */
    @PostMapping("/precreate")
    public void precreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 实际使用时需要根据商品id查询商品的基本信息并计算价格(可能还有什么优惠)，这里只是写死值来测试

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        String outTradeNo = UUID.randomUUID().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
        String subject = "测试订单";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品2件共20.05元";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = "0.01";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";


        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "全麦小面包", 1, 1);
        goodsDetailList.add(goods1);
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "黑人牙刷", 1, 2);
        goodsDetailList.add(goods2);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";

        AlipayTradePrecreateRequestBuilder builder =new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject)
                .setTotalAmount(totalAmount)
                .setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount)
                .setSellerId(sellerId)
                .setBody(body)
                .setOperatorId(operatorId)
                .setStoreId(storeId)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl(aliPayProperties.getNotifyUrl())
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
        String qrCodeUrl = null;
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse res = result.getResponse();
                File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "images/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String absolutePath = file.getAbsolutePath();
                String fileName = String.format("%sqr-%s.png", File.separator, res.getOutTradeNo());
                String filePath = new StringBuilder(absolutePath).append(fileName).toString();

                // 这里只是演示将图片写到服务器中，实际可以返回二维码让前端去生成
                String basePath = request.getScheme()+ "://"+request.getServerName()+":"+ request.getServerPort()+request.getContextPath()+"/";
                qrCodeUrl = basePath + fileName;
                response.getWriter().write("<img src=\"" + qrCodeUrl + "\" />");
                ZxingUtils.getQRCodeImge(res.getQrCode(), 256, filePath);
                break;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }



    /**
     * 退款
     * @param orderNo 商户订单号
     * @return
     */
    @PostMapping("/refund")
    public String refund(String orderNo){
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(orderNo)
                .setRefundAmount("0.01")
                .setRefundReason("当面付退款测试")
                .setOutRequestNo(String.valueOf(System.nanoTime()))
                .setStoreId("A1");
        AlipayF2FRefundResult result = alipayTradeService.tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                break;

            case FAILED:
                log.error("支付宝退款失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单退款状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }

        return result.getResponse().getBody();
    }
}
