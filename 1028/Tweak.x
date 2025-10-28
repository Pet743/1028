#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <objc/runtime.h>

// ============================================
// 二维码显示窗口
// ============================================
@interface QRCodeWindow : UIWindow
@property (nonatomic, strong) UIImageView *qrImageView;
@property (nonatomic, strong) UILabel *statusLabel;
@property (nonatomic, strong) UILabel *uuidLabel;
@property (nonatomic, strong) UIButton *closeButton;
@property (nonatomic, strong) UIButton *refreshButton;
- (void)showQRCode:(UIImage *)image uuid:(NSString *)uuid;
- (void)showError:(NSString *)error;
- (void)showLoading;
@end

@implementation QRCodeWindow

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.windowLevel = UIWindowLevelAlert + 1;
        self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];  // 降低透明度，能看到游戏
        self.userInteractionEnabled = YES;
        
        // 容器视图（缩小尺寸）
        CGFloat containerWidth = 280;
        CGFloat containerHeight = 400;
        UIView *container = [[UIView alloc] initWithFrame:CGRectMake(
            (frame.size.width - containerWidth) / 2,
            (frame.size.height - containerHeight) / 2,
            containerWidth,
            containerHeight
        )];
        container.backgroundColor = [[UIColor whiteColor] colorWithAlphaComponent:0.95];
        container.layer.cornerRadius = 15;
        container.clipsToBounds = YES;
        container.userInteractionEnabled = YES;
        [self addSubview:container];
        
        // 标题
        UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 15, containerWidth, 30)];
        titleLabel.text = @"王者荣耀 - 微信登录";
        titleLabel.textAlignment = NSTextAlignmentCenter;
        titleLabel.font = [UIFont boldSystemFontOfSize:18];
        titleLabel.textColor = [UIColor blackColor];
        [container addSubview:titleLabel];
        
        // 二维码图片视图（缩小）
        CGFloat qrSize = 220;
        self.qrImageView = [[UIImageView alloc] initWithFrame:CGRectMake(
            (containerWidth - qrSize) / 2, 50, qrSize, qrSize)];
        self.qrImageView.backgroundColor = [UIColor whiteColor];
        self.qrImageView.contentMode = UIViewContentModeScaleAspectFit;
        self.qrImageView.layer.borderWidth = 1;
        self.qrImageView.layer.borderColor = [UIColor lightGrayColor].CGColor;
        [container addSubview:self.qrImageView];
        
        // UUID标签
        self.uuidLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 280, containerWidth - 20, 20)];
        self.uuidLabel.textAlignment = NSTextAlignmentCenter;
        self.uuidLabel.textColor = [UIColor grayColor];
        self.uuidLabel.font = [UIFont systemFontOfSize:10];
        [container addSubview:self.uuidLabel];
        
        // 状态标签
        self.statusLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 305, containerWidth - 20, 35)];
        self.statusLabel.textAlignment = NSTextAlignmentCenter;
        self.statusLabel.textColor = [UIColor colorWithRed:0.2 green:0.6 blue:1.0 alpha:1.0];
        self.statusLabel.font = [UIFont systemFontOfSize:16];
        self.statusLabel.numberOfLines = 2;
        [container addSubview:self.statusLabel];
        
        // 刷新按钮
        self.refreshButton = [UIButton buttonWithType:UIButtonTypeSystem];
        self.refreshButton.frame = CGRectMake(20, 350, 110, 35);
        [self.refreshButton setTitle:@"🔄 刷新" forState:UIControlStateNormal];
        self.refreshButton.titleLabel.font = [UIFont boldSystemFontOfSize:16];
        [self.refreshButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.refreshButton.backgroundColor = [UIColor colorWithRed:0.2 green:0.6 blue:1.0 alpha:1.0];
        self.refreshButton.layer.cornerRadius = 8;
        [self.refreshButton addTarget:self action:@selector(refresh) forControlEvents:UIControlEventTouchUpInside];
        [container addSubview:self.refreshButton];
        
        // 关闭按钮
        self.closeButton = [UIButton buttonWithType:UIButtonTypeSystem];
        self.closeButton.frame = CGRectMake(containerWidth - 130, 350, 110, 35);
        [self.closeButton setTitle:@"❌ 关闭" forState:UIControlStateNormal];
        self.closeButton.titleLabel.font = [UIFont boldSystemFontOfSize:16];
        [self.closeButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.closeButton.backgroundColor = [UIColor colorWithRed:0.9 green:0.3 blue:0.3 alpha:1.0];
        self.closeButton.layer.cornerRadius = 8;
        [self.closeButton addTarget:self action:@selector(close) forControlEvents:UIControlEventTouchUpInside];
        [container addSubview:self.closeButton];
    }
    return self;
}

- (void)showQRCode:(UIImage *)image uuid:(NSString *)uuid {
    self.qrImageView.image = image;
    self.uuidLabel.text = [NSString stringWithFormat:@"UUID: %@", uuid];
    self.statusLabel.text = @"✅ 请用微信扫描二维码登录\n📱 扫码后在手机上确认";
    self.statusLabel.textColor = [UIColor colorWithRed:0.2 green:0.7 blue:0.3 alpha:1.0];
}

- (void)showError:(NSString *)error {
    self.qrImageView.image = nil;
    self.uuidLabel.text = @"";
    self.statusLabel.text = error;
    self.statusLabel.textColor = [UIColor redColor];
}

- (void)showLoading {
    self.qrImageView.image = nil;
    self.uuidLabel.text = @"";
    self.statusLabel.text = @"⏳ 正在获取二维码...\n请稍候";
    self.statusLabel.textColor = [UIColor colorWithRed:0.2 green:0.6 blue:1.0 alpha:1.0];
}

- (void)refresh {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"RefreshQRCode" object:nil];
}

- (void)close {
    NSLog(@"[WeChatQR] 关闭窗口");
    self.hidden = YES;
    self.qrImageView.image = nil;
    self.statusLabel.text = @"";
    self.uuidLabel.text = @"";
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    // 点击背景区域（窗口边缘）关闭
    UITouch *touch = [touches anyObject];
    CGPoint location = [touch locationInView:self];
    
    // 检查是否点击在容器外
    BOOL clickedOutside = YES;
    for (UIView *subview in self.subviews) {
        if (CGRectContainsPoint(subview.frame, location)) {
            clickedOutside = NO;
            break;
        }
    }
    
    if (clickedOutside) {
        NSLog(@"[WeChatQR] 点击背景关闭");
        [self close];
    }
}

@end

// ============================================
// 触发按钮（悬浮球）
// ============================================
@interface FloatingButton : UIButton
@property (nonatomic, assign) CGPoint lastLocation;
@end

@implementation FloatingButton

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor colorWithRed:0.2 green:0.6 blue:1.0 alpha:0.8];
        self.layer.cornerRadius = frame.size.width / 2;
        self.clipsToBounds = YES;
        [self setTitle:@"📱" forState:UIControlStateNormal];
        self.titleLabel.font = [UIFont systemFontOfSize:30];
        
        // 添加拖动手势
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePan:)];
        [self addGestureRecognizer:pan];
    }
    return self;
}

- (void)handlePan:(UIPanGestureRecognizer *)recognizer {
    CGPoint translation = [recognizer translationInView:self.superview];
    CGPoint newCenter = CGPointMake(recognizer.view.center.x + translation.x,
                                    recognizer.view.center.y + translation.y);
    
    // 限制在屏幕范围内
    CGFloat radius = self.frame.size.width / 2;
    newCenter.x = MAX(radius, MIN(newCenter.x, self.superview.frame.size.width - radius));
    newCenter.y = MAX(radius + 20, MIN(newCenter.y, self.superview.frame.size.height - radius - 20));
    
    recognizer.view.center = newCenter;
    [recognizer setTranslation:CGPointZero inView:self.superview];
}

@end

// ============================================
// 微信二维码管理器
// ============================================
@interface WeChatQRManager : NSObject
@property (nonatomic, strong) QRCodeWindow *qrWindow;
@property (nonatomic, strong) FloatingButton *floatingButton;
@property (nonatomic, strong) NSString *capturedURL;
@property (nonatomic, assign) BOOL hasHookedRequest;
@property (nonatomic, copy) NSString* (^signatureGenerator)(NSDictionary *params);  // 签名生成函数

+ (instancetype)shared;
- (void)showFloatingButton;
- (void)fetchQRCodeWithCapturedParams;
- (void)fetchQRCodeDirectly;
- (void)fetchQRCodeWithGeneratedSignature;
- (NSString *)generateNonceStr;
@end

@implementation WeChatQRManager

+ (instancetype)shared {
    static WeChatQRManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[WeChatQRManager alloc] init];
    });
    return instance;
}

- (instancetype)init {
    if (self = [super init]) {
        CGRect screenBounds = [UIScreen mainScreen].bounds;
        self.qrWindow = [[QRCodeWindow alloc] initWithFrame:screenBounds];
        self.hasHookedRequest = NO;
        
        // 监听刷新通知
        [[NSNotificationCenter defaultCenter] addObserver:self 
                                                 selector:@selector(handleRefresh:)
                                                     name:@"RefreshQRCode" 
                                                   object:nil];
    }
    return self;
}

- (void)handleRefresh:(NSNotification *)notification {
    NSLog(@"[WeChatQR] 刷新二维码");
    [self fetchQRCodeWithCapturedParams];
}

- (void)showFloatingButton {
    if (self.floatingButton) {
        // 如果已存在但不可见，重新显示
        self.floatingButton.hidden = NO;
        [self.floatingButton.superview bringSubviewToFront:self.floatingButton];
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // 创建独立的窗口来显示悬浮球
        UIWindow *buttonWindow = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
        buttonWindow.windowLevel = UIWindowLevelStatusBar + 1;  // 保持在最前面
        buttonWindow.backgroundColor = [UIColor clearColor];
        buttonWindow.userInteractionEnabled = YES;
        buttonWindow.hidden = NO;
        [buttonWindow makeKeyAndVisible];
        
        CGRect screenBounds = [UIScreen mainScreen].bounds;
        CGFloat size = 60;
        CGFloat margin = 20;
        
        self.floatingButton = [[FloatingButton alloc] initWithFrame:CGRectMake(
            screenBounds.size.width - size - margin,
            screenBounds.size.height / 2,
            size,
            size
        )];
        
        [self.floatingButton addTarget:self 
                                action:@selector(floatingButtonTapped)
                      forControlEvents:UIControlEventTouchUpInside];
        
        [buttonWindow addSubview:self.floatingButton];
        
        // 保存buttonWindow引用，防止被释放
        objc_setAssociatedObject(self, "buttonWindow", buttonWindow, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        
        NSLog(@"[WeChatQR] 悬浮按钮已添加到独立窗口");
        
        // 定时检查悬浮球是否还在，如果消失就重新显示
        [NSTimer scheduledTimerWithTimeInterval:2.0 repeats:YES block:^(NSTimer *timer) {
            if (self.floatingButton && self.floatingButton.hidden) {
                NSLog(@"[WeChatQR] 检测到悬浮球被隐藏，重新显示");
                self.floatingButton.hidden = NO;
                [buttonWindow bringSubviewToFront:self.floatingButton];
            }
        }];
    });
}

- (void)floatingButtonTapped {
    NSLog(@"[WeChatQR] 悬浮按钮被点击");
    
    // 先尝试用签名生成（如果有）
    if (self.signatureGenerator) {
        [self fetchQRCodeWithGeneratedSignature];
    }
    // 然后尝试用缓存的URL（如果有）
    else if (self.capturedURL && [self.capturedURL containsString:@"qrconnect"]) {
        NSLog(@"[WeChatQR] 使用缓存的URL");
        dispatch_async(dispatch_get_main_queue(), ^{
            self.qrWindow.hidden = NO;
            [self.qrWindow makeKeyAndVisible];
            [self.qrWindow showLoading];
        });
        [self fetchWithURL:self.capturedURL];
    }
    // 最后才自动触发游戏登录
    else {
        NSLog(@"[WeChatQR] 首次使用，自动触发游戏登录");
        [self fetchQRCodeDirectly];
    }
}

// 生成随机字符串
- (NSString *)generateNonceStr {
    NSString *chars = @"abcdefghijklmnopqrstuvwxyz0123456789";
    NSMutableString *result = [NSMutableString string];
    for (int i = 0; i < 10; i++) {
        uint32_t index = arc4random_uniform((uint32_t)chars.length);
        [result appendFormat:@"%C", [chars characterAtIndex:index]];
    }
    return result;
}

// 使用生成的签名直接获取二维码
- (void)fetchQRCodeWithGeneratedSignature {
    NSLog(@"[WeChatQR] 尝试使用签名生成函数获取二维码...");
    
    dispatch_async(dispatch_get_main_queue(), ^{
        self.qrWindow.hidden = NO;
        [self.qrWindow makeKeyAndVisible];
        [self.qrWindow showLoading];
    });
    
    // 如果有签名生成函数，直接生成新签名
    if (self.signatureGenerator) {
        NSLog(@"[WeChatQR] 使用Hook到的签名函数");
        
        NSString *noncestr = [self generateNonceStr];
        NSString *timestamp = [NSString stringWithFormat:@"%ld", (long)[[NSDate date] timeIntervalSince1970]];
        NSString *appid = @"wx95a3a4d7c627e07d";
        NSString *scope = @"snsapi_userinfo,snsapi_friend,snsapi_message";
        
        NSDictionary *params = @{
            @"appid": appid,
            @"noncestr": noncestr,
            @"timestamp": timestamp,
            @"scope": scope
        };
        
        // 调用游戏的签名函数
        NSString *signature = self.signatureGenerator(params);
        
        if (signature && signature.length == 40) {
            NSLog(@"[WeChatQR] ✅ 生成签名成功: %@", signature);
            
            // 构建URL
            NSString *urlString = [NSString stringWithFormat:
                @"https://open.weixin.qq.com/connect/sdk/qrconnect?appid=%@&noncestr=%@&timestamp=%@&scope=%@&signature=%@&scheme_data=(null)",
                appid, noncestr, timestamp, 
                [scope stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]], 
                signature];
            
            [self fetchWithURL:urlString];
        } else {
            NSLog(@"[WeChatQR] ❌ 签名生成失败");
            [self fetchQRCodeDirectly];  // 回退到拦截方案
        }
    } else {
        NSLog(@"[WeChatQR] 签名函数未Hook，使用拦截方案");
        [self fetchQRCodeWithCapturedParams];
    }
}

// 使用拦截到的真实参数获取二维码
- (void)fetchQRCodeWithCapturedParams {
    NSLog(@"[WeChatQR] 开始获取二维码...");
    
    dispatch_async(dispatch_get_main_queue(), ^{
        self.qrWindow.hidden = NO;
        [self.qrWindow makeKeyAndVisible];
        [self.qrWindow showLoading];
    });
    
    // 优先尝试生成签名
    if (self.signatureGenerator) {
        [self fetchQRCodeWithGeneratedSignature];
        return;
    }
    
    // 如果有拦截到的URL，直接使用
    if (self.capturedURL && [self.capturedURL containsString:@"qrconnect"]) {
        NSLog(@"[WeChatQR] 使用拦截到的URL: %@", self.capturedURL);
        [self fetchWithURL:self.capturedURL];
    } else {
        NSLog(@"[WeChatQR] 未拦截到有效请求，使用默认方式");
        [self fetchQRCodeDirectly];
    }
}

// 使用完整URL获取二维码
- (void)fetchWithURL:(NSString *)urlString {
    NSURL *url = [NSURL URLWithString:urlString];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"GET";
    
    // 设置请求头（模拟游戏客户端）
    [request setValue:@"smoba/0004 CFNetwork/1107.1 Darwin/19.0.0" forHTTPHeaderField:@"User-Agent"];
    [request setValue:@"*/*" forHTTPHeaderField:@"Accept"];
    [request setValue:@"zh-tw" forHTTPHeaderField:@"Accept-Language"];
    [request setValue:@"gzip, deflate, br" forHTTPHeaderField:@"Accept-Encoding"];
    [request setValue:@"keep-alive" forHTTPHeaderField:@"Connection"];
    
    NSLog(@"[WeChatQR] 发送请求...");
    
    NSURLSessionDataTask *task = [[NSURLSession sharedSession] dataTaskWithRequest:request 
        completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        
        [self handleResponse:data response:response error:error];
    }];
    
    [task resume];
}

// 直接获取（如果没有拦截到请求，自动触发游戏登录）
- (void)fetchQRCodeDirectly {
    NSLog(@"[WeChatQR] 未找到缓存请求，尝试自动触发游戏登录...");
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.qrWindow showLoading];
    });
    
    // 设置标记，准备拦截
    self.hasHookedRequest = YES;
    
    // 尝试自动触发游戏的微信登录
    dispatch_async(dispatch_get_main_queue(), ^{
        [self triggerGameWeChatLogin];
    });
}

// 触发游戏的微信登录流程
- (void)triggerGameWeChatLogin {
    NSLog(@"[WeChatQR] 尝试触发游戏的微信登录...");
    
    // 查找游戏中的微信登录按钮
    UIWindow *gameWindow = nil;
    for (UIWindow *window in [UIApplication sharedApplication].windows) {
        if (window != self.qrWindow && !window.hidden) {
            gameWindow = window;
            break;
        }
    }
    
    if (gameWindow) {
        // 遍历查找包含"微信"或"WeChat"的按钮
        UIButton *wechatButton = [self findWeChatButtonInView:gameWindow];
        
        if (wechatButton) {
            NSLog(@"[WeChatQR] 找到微信登录按钮，自动触发");
            [wechatButton sendActionsForControlEvents:UIControlEventTouchUpInside];
            
            // 等待拦截请求，3秒后检查
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                if (self.capturedURL) {
                    NSLog(@"[WeChatQR] 已拦截到请求，使用拦截的URL");
                    [self fetchWithURL:self.capturedURL];
                } else {
                    NSLog(@"[WeChatQR] 未拦截到请求");
                    [self.qrWindow showError:@"⚠️ 自动触发失败\n\n请手动点击游戏的\"微信登录\"按钮\n然后再点击悬浮球"];
                }
            });
        } else {
            NSLog(@"[WeChatQR] 未找到微信登录按钮");
            [self.qrWindow showError:@"⚠️ 未找到登录按钮\n\n请先进入游戏登录界面\n手动点击\"微信登录\"一次\n之后即可直接使用悬浮球"];
        }
    }
}

// 递归查找微信登录按钮
- (UIButton *)findWeChatButtonInView:(UIView *)view {
    if ([view isKindOfClass:[UIButton class]]) {
        UIButton *button = (UIButton *)view;
        NSString *title = button.titleLabel.text;
        
        if (title && ([title containsString:@"微信"] || 
                     [title containsString:@"WeChat"] ||
                     [title containsString:@"wechat"])) {
            return button;
        }
    }
    
    for (UIView *subview in view.subviews) {
        UIButton *found = [self findWeChatButtonInView:subview];
        if (found) return found;
    }
    
    return nil;
}

// 处理响应数据
- (void)handleResponse:(NSData *)data response:(NSURLResponse *)response error:(NSError *)error {
    if (error) {
        NSLog(@"[WeChatQR] 请求失败: %@", error.localizedDescription);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.qrWindow showError:[NSString stringWithFormat:@"❌ 请求失败\n%@", error.localizedDescription]];
        });
        return;
    }
    
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
    NSLog(@"[WeChatQR] HTTP状态码: %ld", (long)httpResponse.statusCode);
    
    if (!data) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.qrWindow showError:@"❌ 无响应数据"];
        });
        return;
    }
    
    NSError *jsonError;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
    
    if (jsonError) {
        NSLog(@"[WeChatQR] JSON解析失败: %@", jsonError);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.qrWindow showError:@"❌ 数据解析失败"];
        });
        return;
    }
    
    NSNumber *errcode = json[@"errcode"];
    NSLog(@"[WeChatQR] errcode: %@", errcode);
    
    if (errcode && [errcode intValue] == 0) {
        // 获取成功
        NSDictionary *qrcodeDict = json[@"qrcode"];
        NSString *qrcodeBase64 = qrcodeDict[@"qrcodebase64"];
        NSString *uuid = json[@"uuid"];
        
        NSLog(@"[WeChatQR] ✅ 获取成功! UUID: %@", uuid);
        
        if (qrcodeBase64) {
            // 解码Base64并显示二维码
            NSData *imageData = [[NSData alloc] initWithBase64EncodedString:qrcodeBase64 
                                                                     options:NSDataBase64DecodingIgnoreUnknownCharacters];
            UIImage *qrImage = [UIImage imageWithData:imageData];
            
            if (qrImage) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.qrWindow showQRCode:qrImage uuid:uuid];
                });
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.qrWindow showError:@"❌ 二维码图片解析失败"];
                });
            }
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.qrWindow showError:@"❌ 二维码数据为空"];
            });
        }
    } else {
        NSString *errmsg = json[@"errmsg"] ?: @"未知错误";
        NSLog(@"[WeChatQR] ❌ 获取失败: %@ (errcode: %@)", errmsg, errcode);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.qrWindow showError:[NSString stringWithFormat:@"❌ 错误 %@\n%@", errcode, errmsg]];
        });
    }
}

@end

// ============================================
// Hook NSURLSession - 拦截游戏的微信登录请求并提取签名逻辑
// ============================================
%hook NSURLSession

- (NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request 
                             completionHandler:(void (^)(NSData *, NSURLResponse *, NSError *))completionHandler {
    
    NSString *urlString = request.URL.absoluteString;
    
    // 检测是否是微信登录请求
    if ([urlString containsString:@"open.weixin.qq.com"] && 
        [urlString containsString:@"qrconnect"]) {
        
        NSLog(@"");
        NSLog(@"============================================================");
        NSLog(@"[WeChatQR] 🎯 拦截到微信登录请求!");
        NSLog(@"[WeChatQR] URL: %@", urlString);
        
        // 解析URL获取签名参数
        NSURL *url = [NSURL URLWithString:urlString];
        NSURLComponents *components = [NSURLComponents componentsWithURL:url resolvingAgainstBaseURL:NO];
        
        NSString *capturedSignature = nil;
        NSString *capturedNoncestr = nil;
        NSString *capturedTimestamp = nil;
        
        for (NSURLQueryItem *item in components.queryItems) {
            if ([item.name isEqualToString:@"signature"]) {
                capturedSignature = item.value;
            } else if ([item.name isEqualToString:@"noncestr"]) {
                capturedNoncestr = item.value;
            } else if ([item.name isEqualToString:@"timestamp"]) {
                capturedTimestamp = item.value;
            }
        }
        
        NSLog(@"[WeChatQR] 拦截参数: noncestr=%@, timestamp=%@, signature=%@", 
              capturedNoncestr, capturedTimestamp, capturedSignature);
        NSLog(@"============================================================");
        NSLog(@"");
        
        // 保存拦截到的URL
        [[WeChatQRManager shared] setCapturedURL:urlString];
        
        // 尝试分析签名规律，创建签名生成器
        // 这里我们先保存URL，后续通过多次拦截分析规律
        static int interceptCount = 0;
        interceptCount++;
        
        if (interceptCount >= 1) {
            NSLog(@"[WeChatQR] 已拦截%d次请求，可以使用", interceptCount);
        }
        
        // 如果插件窗口打开，立即使用这个URL获取二维码
        if (![WeChatQRManager shared].qrWindow.isHidden) {
            NSLog(@"[WeChatQR] 插件窗口已打开，立即获取二维码");
            [[WeChatQRManager shared] fetchWithURL:urlString];
        }
        
        // 继续原始请求（不影响游戏正常登录流程）
        NSLog(@"[WeChatQR] 继续执行游戏原始请求");
    }
    
    return %orig;
}

%end

// ============================================
// Hook NSURLConnection（兼容老版本SDK）
// ============================================
%hook NSURLConnection

+ (NSData *)sendSynchronousRequest:(NSURLRequest *)request 
                 returningResponse:(NSURLResponse **)response 
                             error:(NSError **)error {
    
    NSString *urlString = request.URL.absoluteString;
    
    if ([urlString containsString:@"open.weixin.qq.com"] && 
        [urlString containsString:@"qrconnect"]) {
        
        NSLog(@"");
        NSLog(@"============================================================");
        NSLog(@"[WeChatQR] 🎯 拦截到微信登录请求（同步）!");
        NSLog(@"[WeChatQR] URL: %@", urlString);
        NSLog(@"============================================================");
        NSLog(@"");
        
        [[WeChatQRManager shared] setCapturedURL:urlString];
        
        if (![WeChatQRManager shared].qrWindow.isHidden) {
            [[WeChatQRManager shared] fetchWithURL:urlString];
        }
    }
    
    return %orig;
}

%end

// ============================================
// Hook UIApplication - 添加悬浮按钮
// ============================================
%hook UIApplication

- (void)setStatusBarHidden:(BOOL)hidden withAnimation:(UIStatusBarAnimation)animation {
    %orig;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [[WeChatQRManager shared] showFloatingButton];
        });
    });
}

%end

// ============================================
// 构造函数
// ============================================
%ctor {
    @autoreleasepool {
        NSLog(@"");
        NSLog(@"========================================================================");
        NSLog(@"[WeChatQR] 王者荣耀微信登录二维码插件已加载");
        NSLog(@"[WeChatQR] 版本: 2.0");
        NSLog(@"[WeChatQR] ");
        NSLog(@"[WeChatQR] 使用方法:");
        NSLog(@"[WeChatQR] 1. 打开游戏后会出现蓝色悬浮球（📱图标）");
        NSLog(@"[WeChatQR] 2. 点击悬浮球打开二维码窗口");
        NSLog(@"[WeChatQR] 3. 第一次使用：需要先在游戏中点击\"微信登录\"");
        NSLog(@"[WeChatQR] 4. 插件会自动拦截并保存有效的登录参数");
        NSLog(@"[WeChatQR] 5. 之后点击悬浮球即可直接获取有效二维码");
        NSLog(@"[WeChatQR] ");
        NSLog(@"[WeChatQR] 提示：悬浮球可以拖动位置");
        NSLog(@"========================================================================");
        NSLog(@"");
        
        // 延迟3秒显示悬浮按钮
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [[WeChatQRManager shared] showFloatingButton];
        });
    }
}
