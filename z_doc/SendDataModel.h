//
//  SendDataModel.h
//  gameSDK
//
//  Created by admin on 2024/4/18.
//

#import <Foundation/Foundation.h>
#import "ClientReq.pbobjc.h"
#import "ClientRes.pbobjc.h"
#include "CryptoManager.h"
#import "GameRes.pbobjc.h"
#import "GameReq.pbobjc.h"
NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSInteger, socketStatusType) {
    socketConnectedType, //连接
    socketDisconnectedType, //断开
    socketcanCellType, //取消
    socketErrorType, //错误
};
@interface SendDataModel : NSObject
+ (instancetype)shared; //单例对象
//流程先登录->进入房间坐下->进入group->进入小游戏
-(void)sendLogin:(LoginReq *)model; //登录接口
-(void)sendEnterRoom; //进入房间坐下
-(void)sendEnterGroup:(EnterGroup *)model;//进入group
-(void)sendheartBeat; //心跳
-(void)getSeverMessage:(DecryResult*)resultModel; //获取到服务器消息处理
-(void)sendLevaeGroup:(EnterGroup *)model; //离开直播间
-(void)sendEnterMiniGame:(EnterMiniGame *)model; //进入小游戏
-(void)sendLevaeMiniGame:(LeaveMiniGamesReq *)model; //离开小游戏
-(void)sendBet:(BetReq *)model; //下注单
-(void)sendRefreshScore;//刷新金钱


#pragma mark ------------数据返回block -----------------
typedef void (^loginBackBlock)(InfoAfterLoginSuccess *successModel,ErrorMessage *errormodel);
typedef void (^changeSocketStatusBlock)(socketStatusType type);
typedef void (^enterRoomBackBlock)(EnterInfo *enterInfoModel,ErrorMessage *errormodel);
typedef void (^groupInfoBackBlock)(GroupInfo *enterInfoModel,id errormodel);
typedef void (^leaveGroupBackBlock)(LeaveGroup *enterInfoModel,id errormodel);
typedef void (^LevaeMiniGameBackBlock)(LeaveMiniGames *leaveMiniGamesModel,id errormodel);
typedef void (^enterMiniGameBackBlock)(EnterMiniGameInfo *enterMiniGameInfo,id errormodel);
typedef void (^userBetResultBackBlock)(MyMiniGameBetResult *myMiniGameBetResult,id errormodel);
typedef void (^refreshUserPropsBackBlock)(RefreshUserScore *refreshUserScore,id errormodel);
typedef void (^beginNewRoundBackBlock)(BeginNewRound *beginNewRound,id errormodel);
typedef void (^beginSettleBackBlock)(BeginSettle *beginSettle,id errormodel);
typedef void (^syncAreaBetInfoBackBlock)(SyncAreaBetInfo *syncAreaBetInfo,id errormodel);
typedef void (^beginDealBackBlock)(BeginDeal *beginDeal);
typedef void (^clearTrendsBackBlock)(ClearTrends *clearTrends);
typedef void (^appLoginBackBlock)(int type,NSString *decStr);
typedef void (^appgroupInfoBackBlock)(int type,NSString *decStr);
typedef void (^appleaveGroupBackBlock)(int typel,NSString *decStr);
typedef void (^tokenLoseEffectivenessBlock)(void);


@property (nonatomic, copy)loginBackBlock loginBackBlock;//登录成功返回
@property (nonatomic, copy)changeSocketStatusBlock changeSocketStatusBlock;//socket状态修改回调
@property (nonatomic, copy)enterRoomBackBlock enterRoomBackBlock;//进入房间返回
@property (nonatomic, copy)groupInfoBackBlock groupInfoBackBlock;//进入直播间返回
@property (nonatomic, copy)leaveGroupBackBlock leaveGroupBackBlock;//离开直播间返回
@property (nonatomic, copy)LevaeMiniGameBackBlock LevaeMiniGameBackBlock;//离开小游戏返回
@property (nonatomic, copy)enterMiniGameBackBlock enterMiniGameBackBlock;//进入小游戏返回
@property (nonatomic, copy)userBetResultBackBlock userBetResultBackBlock;//投注结果返回
@property (nonatomic, copy)refreshUserPropsBackBlock refreshUserPropsBackBlock;//刷新金币返回
@property (nonatomic, copy)beginNewRoundBackBlock beginNewRoundBackBlock;//开始新局
@property (nonatomic, copy)beginSettleBackBlock beginSettleBackBlock;//进入结算
@property (nonatomic, copy)syncAreaBetInfoBackBlock syncAreaBetInfoBackBlock; //同步注区下注信息
@property (nonatomic, copy)beginDealBackBlock beginDealBackBlock; //开始开牌
@property (nonatomic, copy)clearTrendsBackBlock clearTrendsBackBlock; //清除开奖历史

#pragma mark ------------app返回block -----------------
@property(nonatomic,strong) appLoginBackBlock appLoginBackBlock; //type ==1 成功 type =1000（desc：您当前还在其他游戏中）type =1001 （token验证失败）type =1002 （余额不足）type =1005（当前服务器正在维）type =200（其他情况）
@property(nonatomic,strong) appgroupInfoBackBlock appgroupInfoBackBlock;//type ==1 成功
@property(nonatomic,strong) appleaveGroupBackBlock appleaveGroupBackBlock;//type ==1 成功
@property(nonatomic,strong) tokenLoseEffectivenessBlock tokenLoseEffectivenessBlock;//token失效接口
#pragma mark ------------错误信息返回block -----------------
typedef void (^loginErrorMessageBlock)(ErrorMessage *loginErrormessage);
typedef void (^gameErrorMessageBlock)(ErrorMessage *gameErrormessage);
typedef void (^serverMaintenanceBlock)(void);
typedef void (^roomOutTimeBlock)(void);
typedef void (^multiUserBlock)(void);

@property (nonatomic, copy)loginErrorMessageBlock loginErrorMessageBlock;//登录错误message返回 code =1000（desc：您当前还在其他游戏中，是否立刻回到该游戏？）code =1001 （token验证失败）code =1002 （余额不足）code =1005（当前服务器正在维）code =200（其他情况）
@property (nonatomic, copy)gameErrorMessageBlock gameErrorMessageBlock;//游戏错误message返回 code =200（1.desc 离开游戏无效；2.desc 该类型房间已满，请选择其他房间；3.desc 请选择全押。；4.全押之后无法弃牌哦。） code =1 （desc =您已经被请出房间了，请重新登录房间）code =1003（视讯百家乐游戏版本过旧）
@property (nonatomic, copy)serverMaintenanceBlock serverMaintenanceBlock; //服务器维护，踢出用户
@property (nonatomic, copy)roomOutTimeBlock roomOutTimeBlock;//停留房间超时，踢出用户
@property (nonatomic, copy)multiUserBlock multiUserBlock; //当前连接被另一个登录顶掉了,踢出用户
@end

NS_ASSUME_NONNULL_END
