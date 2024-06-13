//
//  GameHub.h
//  gameSDK
//
//  Created by Leon on 2024/4/24.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "GError.h"

NS_ASSUME_NONNULL_BEGIN

@interface GameHub : NSObject

/// 加載SDK
+ (GError *_Nullable)loadGame;

///登录
/// - Parameter agentName: 平台名称
/// - Parameter token: 用户token
/// type ==1 成功 type =1000（desc：您当前还在其他游戏中）type =1001 （desc：token验证失败）type =1002 （desc：余额不足）type =1005（desc：当前服务器正在维护）type =200（desc：其他情况）
+ (GError *_Nullable)loginGameWithAgentName:(NSString *_Nullable)agentName token:(NSString *)token block:(void (^)(int type,NSString *decStr))paramBlock;

/// 进入直播間
/// - Parameter liveId: 直播間id
/// - Parameter gIds: 遊戲ids
/// - Parameter data_p: 透传资料（转抛）
/// type ==1 成功
+ (GError *_Nullable)enterLive:(NSString *_Nonnull)liveId
                       gameIds:(NSMutableArray *)gIds data_p:(NSString *)data_p block:(void (^)(int type,NSString *decStr))paramBlock;

/// 离开直播間
/// - Parameter liveId: 直播間id
+ (GError *_Nullable)leaveLive:(NSString *_Nonnull)liveId block:(void (^)(int type,NSString *decStr))paramBlock; //type ==1 成功

///注销sdk
+ (GError *_Nullable)cancelGame;

/// 是否彈出遊戲框
/// - Parameter block: 回調
+ (void)gameFloatingDetailViewStatusWithBlock:(void (^)(BOOL isShowUp))block;

/// 是否允許下注
/// - Parameter isAllow: 默認true
+ (void)allowedBet:(BOOL)isAllow;

//app 接受token失效接口
+(void)getTokenLoseEffectivenessWithblock:(void (^)(void))paramBlock;

/// 點擊投注記錄回調
+ (void)historyOfBetAction:(void (^)(void))block;

/// 點擊客服回調
+ (void)customerServiceAction:(void (^)(void))block;

/// 隱藏控制器
+ (void)dismissFloatingController;

/// 漂浮窗視圖
+ (UIView *)floatingView;

/// 结果视图
+ (UIView *)resultView;


@end

NS_ASSUME_NONNULL_END
