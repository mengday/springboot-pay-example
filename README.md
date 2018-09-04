# 功能
该example主要集成了支付宝支付和微信支付，其中支付宝支付使用的是沙箱环境，微信支付使用的是demo中的测试账号，目前已经接入的支付类型如下：


支付宝支付接口:

- AlipayController 支付宝-通用接口(包含对账)
- AlipayF2FPayController 支付宝-当面付
- AlipayPagePayController 支付宝-电脑网站支付
- AlipayWAPPayController 支付宝-手机网站支付

微信支付接口:

- WXPayController 微信支付-通用接口
- WXPayMicroPayController 微信支付-刷卡支付
- WXPayPrecreateController 微信支付-扫码支付
- WXPayH5PayController 微信支付-H5支付


# 关于测试账号
支付宝支付的账号可以自己在开放平台上直接申请，个人很容易通过，通过后直接使用沙箱环境即可。

微信支付如果要申请账号必须是服务号，还要每年缴300元，申请过程比较麻烦，这里直接使用demo中的账号用来测试,如果公司已经申请了账号最好使用公司的账号来测试


# 支付集成步骤
支付宝支付和微信支付具体集成步骤已经详细的记录在博客里了，如果不熟悉支付宝集成或者微信支付集成的，请移步下面博客

注意：微信支付坑很多很多，而且sdk写过于简陋，很多必要的功能更都没有给出实现，关于坑和一些必要的逻辑实现在example和博客中都指出来。 

- [Spring Boot入门教程(三十五):支付宝集成-准备工作](https://blog.csdn.net/vbirdbest/article/details/80635194)

- [Spring Boot入门教程(三十六):支付宝集成-当面付](https://blog.csdn.net/vbirdbest/article/details/80655716)
- [Spring Boot入门教程(三十七):支付宝集成-手机网站支付](https://blog.csdn.net/vbirdbest/article/details/80684460)
- [Spring Boot入门教程(三十八):支付宝集成-电脑网站支付](https://blog.csdn.net/vbirdbest/article/details/80696690)
- [Spring Boot入门教程(三十九):微信支付集成-申请服务号和微信支付](https://blog.csdn.net/vbirdbest/article/details/80717905)
- [Spring Boot入门教程(四十):微信支付集成-刷卡支付](https://blog.csdn.net/vbirdbest/article/details/80720138)
- [Spring Boot入门教程(四十一):微信支付集成-扫码支付](https://blog.csdn.net/vbirdbest/article/details/80723991)
- [Spring Boot入门教程(四十二):微信支付集成-H5支付](https://blog.csdn.net/vbirdbest/article/details/80726616)


