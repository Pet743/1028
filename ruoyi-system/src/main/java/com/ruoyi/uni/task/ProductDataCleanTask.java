package com.ruoyi.uni.task;

import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.uni.model.Enum.ProductCategoryEnum;
import com.ruoyi.uni.model.Enum.ShippingMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 商品数据清洗定时任务
 */
@Component("productDataCleanTask")
public class ProductDataCleanTask {

    @Autowired
    private IAlseProductService alseProductService;

    private final Random random = new Random();
    private final String[] names = {"王梓轩", "李嘉诚", "张雨琪", "陈思颖", "刘俊杰", "赵雅芝", "杨紫薇", "周子豪", "吴欣怡", "孙浩然"};
    private final String[] phonePrefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "188", "189"};
    private final String[] shopNames = {"优品优选", "京东旗舰店", "天猫专卖", "品质生活馆", "优选百货店", "家居优品", "数码先锋", "时尚精品坊", "美妆世界", "厨具之家", "宝妈优选", "文具天地", "运动专家", "健康优选", "潮牌集合店"};

    // 商品标题前缀列表
    private final String[] productTitlePrefixes = {
            "【新品首发】", "【限时特惠】", "【火爆热销】", "【官方正品】", "【品质保证】", "【顺丰包邮】", "【买一送一】", "【爆款直降】",
            "【明星同款】", "【超值套装】", "【独家定制】", "【特别推荐】", "【抢购价】", "【工厂直销】", "【限量版】"
    };

    // 更真实的商品标题集合
    private final String[] productTitles = {
            "苹果iPhone 13 Pro Max 256GB 5G全网通 超视网膜XDR显示屏",
            "华为Mate 50 Pro 8GB+256GB 昆仑破晓 超感知徕卡影像 5G全网通",
            "小米电视大师 82英寸 4K超高清HDR 120Hz高刷 杜比视界 MEMC运动补偿",
            "戴森吸尘器V15 Detect 无线手持吸尘器家用除螨",
            "nike耐克运动鞋 男鞋AIR FORCE 1低帮运动板鞋休闲鞋",
            "beats Studio3 Wireless 头戴式蓝牙无线降噪耳机",
            "阿玛尼迷情挚爱女士香水持久淡香清新自然花果香调",
            "巴黎欧莱雅小黑管丝绒唇釉 哑光口红持久保湿不易脱色",
            "SK-II神仙水精华液 护肤套装礼盒 补水保湿提亮肤色",
            "索尼Alpha 7R IV全画幅微单相机 6100万像素机身",
            "飞利浦空气炸锅家用无油低脂健康大容量电炸锅",
            "三星Galaxy Tab S8 Ultra平板电脑 骁龙8旗舰芯片 14.6英寸2K屏",
            "UNIQLO优衣库男装 摇粒绒拉链开衫卫衣休闲保暖外套",
            "乐高星球大战系列 千年隼号 拼插积木玩具礼物",
            "无印良品舒适软体沙发 多功能可拆洗懒人沙发",
            "博世洗碗机家用全自动嵌入式8套智能洗烘一体机",
            "卡西欧G-SHOCK系列 防水防震多功能电子表运动手表",
            "新款Switch OLED游戏机 7英寸有机EL屏幕主机",
            "松下负离子吹风机家用大功率恒温护发电吹风",
            "海尔冰箱双开门风冷无霜变频一级能效家用电冰箱",
            "北欧简约实木餐桌椅组合 小户型家用饭桌",
            "曼秀雷敦薄荷润唇膏SPF15 补水保湿防晒修护",
            "回力帆布鞋经典男女情侣款小白鞋休闲板鞋",
            "奥克斯空调挂机3匹变频冷暖家用节能静音壁挂式",
            "澳洲进口安佳牛奶1L*12盒整箱装高钙全脂纯牛奶",
            "西铁城光动能男表 商务休闲防水机械表",
            "松下4K投影仪家用高清智能家庭影院投影机",
            "宜家北欧风格床头柜 简约现代储物柜",
            "美的电饭煲IH电磁加热家用智能多功能电饭锅",
            "榨汁机家用水果小型便携式多功能全自动果汁机",
            "麦瑞克椭圆机静音家用室内太空漫步机健身器材",
            "格力空气净化器家用卧室内除甲醛除菌除异味",
            "雅诗兰黛特润修护肌透精华露 小棕瓶精华液",
            "普拉达黑色尼龙Re-Edition 2005肩背手提两用包",
            "dyson戴森Airwrap美发造型器卷发棒吹风机"
    };

    // 更真实的商品描述
    private final String[] productDescriptions = {
            "采用最新工艺制造，品质卓越，性能稳定。精选优质材料，环保健康，安全可靠。时尚简约设计，美观大方，适合各种场合使用。功能强大，操作简便，老少皆宜。性价比高，让您物超所值！",
            "本产品为官方正品，品质保证，假一赔十。精湛工艺，细节处理完美，彰显品质生活。功能丰富，满足您的多样化需求。持久耐用，长期使用依然如新。售后无忧，购买即享受一年质保服务。",
            "独特设计，引领潮流，彰显个性。采用环保材质，呵护家人健康。功能多样，满足日常各种需求。经久耐用，品质保证，值得信赖。简约时尚，适合各类家居环境，给您带来愉悦体验。",
            "专业级性能，满足专业用户需求。智能化设计，操作简便，功能强大。耐用材质，经久不衰，长期使用依然稳定。人性化细节，提升使用体验。限量发售，独特稀有，彰显品位。",
            "高端品质，匠心制作，每一个细节都经过精心打磨。创新科技，解决传统产品痛点，带来全新体验。人体工学设计，使用舒适，减轻疲劳。多功能设计，一机多用，经济实惠。现代简约风格，融入任何环境。"
    };

    // 商品图片前缀
    private final String[] imageUrlPrefixes = {
            "https://img.alicdn.com/imgextra/",
            "https://img.alicdn.com/bao/uploaded/",
            "https://img14.360buyimg.com/n0/",
            "https://image.suning.cn/uimg/b2c/newcatentries/",
            "https://m.360buyimg.com/mobilecms/s1265x1265_jfs/"
    };

    /**
     * 执行商品数据清洗（无参）
     */
    public void cleanProductData() {
        cleanProductDataWithParams("50");
    }

    /**
     * 执行商品数据清洗（带参数）
     *
     * @param count 要生成的商品数量
     */
    public void cleanProductDataWithParams(String count) {
        try {
            int productCount = Integer.parseInt(count);

            System.out.println("开始执行商品数据导入任务");
            System.out.println("计划生成商品数量: " + productCount);

            // 生成商品数据
            List<AlseProduct> products = new ArrayList<>();

            for (int i = 0; i < productCount; i++) {
                AlseProduct product = new AlseProduct();

                // 设置基本信息
                String productTitle = getRandomProductTitle();
                product.setProductTitle(productTitle);
                product.setProductCategory(getRandomCategory());

                // 真实图片链接
                String coverImgPrefix = imageUrlPrefixes[random.nextInt(imageUrlPrefixes.length)];
                String detailImgPrefix = imageUrlPrefixes[random.nextInt(imageUrlPrefixes.length)];
                String imgId1 = String.format("%03d", random.nextInt(1000));
                String imgId2 = String.format("%03d", random.nextInt(1000));

                product.setProductCoverImg(coverImgPrefix + "product_" + imgId1 + ".jpg");
                product.setProductDetailImgs("[\"" + detailImgPrefix + "detail_" + imgId1 + ".jpg\",\"" + detailImgPrefix + "detail_" + imgId2 + ".jpg\"]");

                product.setProductDescription(getRandomProductDescription());

                // 更真实的价格区间
                int basePrice = random.nextInt(5) + 1;
                if (basePrice == 1) {
                    // 低价商品 10-99
                    product.setProductPrice(new BigDecimal(random.nextInt(90) + 10).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else if (basePrice == 2) {
                    // 中低价 100-499
                    product.setProductPrice(new BigDecimal(random.nextInt(400) + 100).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else if (basePrice == 3) {
                    // 中价 500-999
                    product.setProductPrice(new BigDecimal(random.nextInt(500) + 500).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else if (basePrice == 4) {
                    // 中高价 1000-3999
                    product.setProductPrice(new BigDecimal(random.nextInt(3000) + 1000).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    // 高价 4000-10000
                    product.setProductPrice(new BigDecimal(random.nextInt(6000) + 4000).setScale(2, BigDecimal.ROUND_HALF_UP));
                }

                product.setShippingMethod(getRandomShippingMethod());

                // 更真实的销售数量分布
                int salesBase = random.nextInt(5) + 1;
                if (salesBase == 1) {
                    // 低销量 0-50
                    product.setSalesCount((long) random.nextInt(51));
                } else if (salesBase == 2) {
                    // 中低销量 51-200
                    product.setSalesCount((long) (random.nextInt(150) + 51));
                } else if (salesBase == 3) {
                    // 中等销量 201-500
                    product.setSalesCount((long) (random.nextInt(300) + 201));
                } else if (salesBase == 4) {
                    // 中高销量 501-2000
                    product.setSalesCount((long) (random.nextInt(1500) + 501));
                } else {
                    // 高销量 2001-10000
                    product.setSalesCount((long) (random.nextInt(8000) + 2001));
                }

                product.setProductStatus("0"); // 默认上架
                product.setPublisherId((long)(random.nextInt(100) + 1)); // 随机发布人ID
                product.setPublisherName(getRandomName());
                product.setPublisherPhone(getRandomPhone());

                // 更真实的评分 - 大部分商品评分在4-5之间，少数在3-4之间
                if (random.nextInt(10) < 8) {
                    // 80%的几率是4-5分
                    product.setProductRating(new BigDecimal(4 + random.nextDouble()).setScale(1, BigDecimal.ROUND_HALF_UP));
                } else {
                    // 20%的几率是3-4分
                    product.setProductRating(new BigDecimal(3 + random.nextDouble()).setScale(1, BigDecimal.ROUND_HALF_UP));
                }

                product.setShopName(getRandomShopName());
                product.setStatus("0"); // 默认正常

                // 设置基础字段
                product.setCreateBy("system");
                product.setCreateTime(new Date());
                product.setUpdateBy("system");
                product.setUpdateTime(new Date());

                products.add(product);
            }

            // 批量插入数据
            System.out.println("开始导入商品数据...");
            for (AlseProduct product : products) {
                alseProductService.insertAlseProduct(product);
            }
            System.out.println("商品数据导入任务完成，共生成" + productCount + "条数据");

        } catch (Exception e) {
            System.out.println("商品数据导入任务执行失败：" + e.getMessage());
        }
    }

    private String getRandomProductTitle() {
        String prefix = productTitlePrefixes[random.nextInt(productTitlePrefixes.length)];
        String title = productTitles[random.nextInt(productTitles.length)];
        return prefix + title;
    }

    private String getRandomProductDescription() {
        return productDescriptions[random.nextInt(productDescriptions.length)];
    }

    private String getRandomCategory() {
        ProductCategoryEnum[] categories = ProductCategoryEnum.values();
        return categories[random.nextInt(categories.length)].getCode();
    }

    private String getRandomShippingMethod() {
        ShippingMethodEnum[] methods = ShippingMethodEnum.values();
        return methods[random.nextInt(methods.length)].getCode();
    }

    private String getRandomName() {
        return names[random.nextInt(names.length)];
    }

    private String getRandomPhone() {
        String prefix = phonePrefixes[random.nextInt(phonePrefixes.length)];
        String suffix = String.format("%08d", random.nextInt(100000000));
        return prefix + suffix;
    }

    private String getRandomShopName() {
        return shopNames[random.nextInt(shopNames.length)];
    }
}
