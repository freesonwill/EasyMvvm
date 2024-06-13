//
//  SendDataModel.m
//  gameSDK
//
//  Created by admin on 2024/4/18.
//

#import "SendDataModel.h"
//#import "gameSDK-Swift.h"
#import "WebSocketManager.h"
typedef NS_ENUM(NSInteger, ClientAndGameResType) {
    ClientLoginResType, //登录
    clientLoginerrorType, //登录错误
    EnterInfoResType, //进入房间
    GroupInfoResType, //进入group
    LevaeGroupType,//离开group
    EnterMiniGameInfoType, //进入小游戏
    LevaeMiniGameType, //离开小游戏
    UserBetResultType, //投注结果
    RefreshUserPropsType,//获取刷新金钱
    BeginNewRoundType, //开始新局
    BeginSettleType, //进入结算
    SyncAreaBetInfoType, //同步注区下注信息
    BeginDealType, //开始开牌
    ClearTrendsType,//清除投注历史
    GameErrorType, //游戏错误
    ServerMaintenanceType,//服务器维护
    RoomOutTimeType,//房间超时踢出
    MultiUserLoginType,//多用户登录
    ClientUnKnow,
};

@interface SendDataModel()
@property(nonatomic,strong)WebSocketManager *socketManager;
@end
@implementation SendDataModel
static SendDataModel *_instance = nil;
+ (instancetype)shared{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _instance = [[self alloc]init];
    });
    _instance.socketManager =[WebSocketManager shared];
    _instance.socketManager.socketConnectedClosure = ^{
        [_instance sendSocketStause:socketConnectedType];
    };
    _instance.socketManager.socketDisconnectedClosure = ^{
        [_instance sendSocketStause:socketDisconnectedType];
    };
    _instance.socketManager.socketCanceledClosure = ^{
        [_instance sendSocketStause:socketcanCellType];
    };
    _instance.socketManager.socketCanceledClosure = ^{
        [_instance sendSocketStause:socketErrorType];
    };
    return _instance;
}
-(void)sendLogin:(LoginReq *)model{
    CryptoManager *manager =[CryptoManager shared];
    NSData *modelData =[model data];
    NSString *modelStr =[[NSString alloc] initWithData:modelData encoding:NSUTF8StringEncoding];
    NSData *data =[manager pack:7 sid:7 str:modelData];
    [self.socketManager sendDataToServer:data];
}
-(void)sendEnterRoom{
    CryptoManager *manager =[CryptoManager shared];
    NSData *data =[manager pack:8 sid:7 str:[NSData data]];
    [self.socketManager sendDataToServer:data];
}
-(void)sendEnterGroup:(EnterGroup *)model{
    CryptoManager *manager =[CryptoManager shared];
//    NSLog(@"%@",[model description]);
    NSData *modelData =[model data];
    NSString *modelStr =[[NSString alloc] initWithData:modelData encoding:NSUTF8StringEncoding];
    NSData *data =[manager pack:500 sid:1001 str:modelData];
    [self.socketManager sendDataToServer:data];
}
-(void)sendLevaeGroup:(EnterGroup *)model{
    CryptoManager *manager =[CryptoManager shared];
    NSData *modelData =[model data];
    NSString *modelStr =[[NSString alloc] initWithData:modelData encoding:NSUTF8StringEncoding];
    NSData *data =[manager pack:500 sid:1003 str:modelData];
    [self.socketManager sendDataToServer:data];
}
-(void)sendEnterMiniGame:(EnterMiniGame *)model{
    CryptoManager *manager =[CryptoManager shared];
    NSData *modelData =[model data];
    NSString *modelStr =[[NSString alloc] initWithData:modelData encoding:NSUTF8StringEncoding];
    NSData *data =[manager pack:500 sid:1005 str:modelData];
    [self.socketManager sendDataToServer:data];
}
-(void)sendLevaeMiniGame:(EnterMiniGame *)model{
    CryptoManager *manager =[CryptoManager shared];
    NSData *modelData =[model data];
    NSString *modelStr =[[NSString alloc] initWithData:modelData encoding:NSUTF8StringEncoding];
    NSData *data =[manager pack:500 sid:1007 str:modelData];
    [self.socketManager sendDataToServer:data];
}
-(void)sendBet:(BetReq *)model{
    CryptoManager *manager =[CryptoManager shared];
    NSData *modelData =[model data];
    NSLog(@"%@",modelData);
//    NSError *error;
//    BetReq *groupInfo =[BetReq parseFromData:modelData error:&error];
//    NSLog(@"-----登录错误message返回------\n，返回对象:%@",[groupInfo description]);
//    NSString *modelStr = [modelData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
//    NSMutableString *hexString = [NSMutableString string];
//    const unsigned char *bytes = modelData.bytes;
//    for (NSUInteger i = 0; i < modelData.length; i++) {
//        [hexString appendFormat:@"%02x", bytes[i]];
//    }
//    NSLog(@"十六进制形式的数据: %@", hexString);
    NSString *modelStr =[[NSString alloc] initWithData:modelData encoding:NSUTF8StringEncoding];
    NSData *data =[manager pack:500 sid:1030 str:modelData];
    [self.socketManager sendDataToServer:data];
}
-(void)sendRefreshScore{
    CryptoManager *manager =[CryptoManager shared];;
    NSData *data =[manager pack:500 sid:1035 str:[NSData data]];
    [self.socketManager sendDataToServer:data];
}
-(void)sendheartBeat{
    CryptoManager *manager =[CryptoManager shared];
    NSData *data =[manager pack:0 sid:2 str:[NSData data]];
    [self.socketManager sendDataToServer:data];
}
-(void)getSeverMessage:(DecryResult*)resultModel{
    switch ([self getClientTypemMid:resultModel.mid sid:resultModel.sid]){
        case ClientLoginResType:{
            NSError *error;
            InfoAfterLoginSuccess *success =[InfoAfterLoginSuccess parseFromData:resultModel.data error:&error];
            NSLog(@"------登录接口------\n，返回对象:%@",[success description]);
            if(error ==nil){
                if (self.loginBackBlock != nil)  self.loginBackBlock(success, nil);
                if (self.appLoginBackBlock != nil) self.appLoginBackBlock(1, @"");
            }
        }
            break;
        case clientLoginerrorType:{
            NSError *error;
            ErrorMessage *message =[ErrorMessage parseFromData:resultModel.data error:&error];
            NSLog(@"-----登录错误message返回------\n，返回对象:%@",[message description]);
            if (self.loginErrorMessageBlock) {
                self.loginErrorMessageBlock(message);
            }
            if (self.appLoginBackBlock) {
                self.appLoginBackBlock(message.code, message.desc);
            }
        }
            break;
        case EnterInfoResType:{
            NSError *error;
            EnterInfo *enter =[EnterInfo parseFromData:resultModel.data error:&error];
            NSLog(@"-----进入房间统一返回------\n，返回对象:%@",[enter description]);
            if (self.enterRoomBackBlock) {
                self.enterRoomBackBlock(enter, nil);
            }
        }
            break;
        case GroupInfoResType:{
            NSError *error;
            GroupInfo *groupInfo =[GroupInfo parseFromData:resultModel.data error:&error];
            NSLog(@"-----进入group后返回------\n，返回对象:%@",[groupInfo description]);
            if (self.groupInfoBackBlock) {
                self.groupInfoBackBlock(groupInfo, nil);
            }
            if (self.appgroupInfoBackBlock) {
                self.appgroupInfoBackBlock(1, @"");
            }
        }
            break;
        case LevaeGroupType:{
            NSError *error;
            LeaveGroup *leaveGroup =[LeaveGroup parseFromData:resultModel.data error:&error];
            NSLog(@"-----离开group后返回------\n，返回对象:%@",[leaveGroup description]);
            if (self.leaveGroupBackBlock) {
                self.leaveGroupBackBlock(leaveGroup, nil);
            }
            if (self.appLoginBackBlock) {
                self.appLoginBackBlock(1, @"");
            }
        }
            
            break;
        case EnterMiniGameInfoType:{
            NSError *error;
            EnterMiniGameInfo *enterMiniGameInfo =[EnterMiniGameInfo parseFromData:resultModel.data error:&error];
            NSLog(@"-----进入小游戏后返回------\n，返回对象:%@",[enterMiniGameInfo description]);
            if (self.enterMiniGameBackBlock) {
                self.enterMiniGameBackBlock(enterMiniGameInfo, nil);
            }
        }
            break;
        case LevaeMiniGameType:{
            NSError *error;
            LeaveMiniGames *leaveMiniGames =[LeaveMiniGames parseFromData:resultModel.data error:&error];
            NSLog(@"-----离开小游戏后返回------\n，返回对象:%@",[leaveMiniGames description]);
            if (self.LevaeMiniGameBackBlock) {
                self.LevaeMiniGameBackBlock(leaveMiniGames, nil);
            }
        }
            break;
        case UserBetResultType:{
            NSError *error;
            MyMiniGameBetResult *myMiniGameBetResult =[MyMiniGameBetResult parseFromData:resultModel.data error:&error];
            NSLog(@"-----投注结果后返回------\n，返回对象:%@",[myMiniGameBetResult description]);
//            NSLog(@"%lu",(unsigned long)myMiniGameBetResult.betResultInfoListArray.count);
            if (self.userBetResultBackBlock) {
                self.userBetResultBackBlock(myMiniGameBetResult, nil);
            }
        }
            break;
        case RefreshUserPropsType:{
            NSError *error;
            RefreshUserScore *refreshUserScore =[RefreshUserScore parseFromData:resultModel.data error:&error];
            NSLog(@"-----获取刷新金钱后返回------\n，返回对象:%@",[refreshUserScore description]);
            if (self.refreshUserPropsBackBlock) {
                self.refreshUserPropsBackBlock(refreshUserScore, nil);
            }
        }
            break;
        case BeginNewRoundType:{
            NSError *error;
            BeginNewRound *beginNewRound =[BeginNewRound parseFromData:resultModel.data error:&error];
            NSLog(@"-----开始新局返回------\n，返回对象:%@",[beginNewRound description]);
            if (self.beginNewRoundBackBlock) {
                self.beginNewRoundBackBlock(beginNewRound, nil);
            }
        }
            break;
        case BeginSettleType:{
            NSError *error;
            BeginSettle *beginSettle =[BeginSettle parseFromData:resultModel.data error:&error];
            NSLog(@"-----进入结算返回------\n，返回对象:%@",[beginSettle description]);
            NSLog(@"开牌中 ------%@--------",[NSDate date]);
            if (self.beginSettleBackBlock) {
                self.beginSettleBackBlock(beginSettle, nil);
            }
        }
            break;
        case SyncAreaBetInfoType:{
            NSError *error;
            SyncAreaBetInfo *syncAreaBetInfo =[SyncAreaBetInfo parseFromData:resultModel.data error:&error];
            NSLog(@"-----同步下注区信息返回------\n，返回对象:%@",[syncAreaBetInfo description]);
            if (self.syncAreaBetInfoBackBlock) {
                self.syncAreaBetInfoBackBlock(syncAreaBetInfo, nil);
            }
        }
            break;
        case BeginDealType:{
            NSError *error;
            BeginDeal *beginDeal =[BeginDeal parseFromData:resultModel.data error:&error];
            NSLog(@"-----开始开牌信息返回------\n，返回对象:%@",[beginDeal description]);
            NSLog(@"开牌中 ------%@--------",[NSDate date]);
            if (self.beginDealBackBlock) {
                self.beginDealBackBlock(beginDeal);
            }
        }
            break;
        case ClearTrendsType:{
            NSError *error;
            ClearTrends *clearTrends =[ClearTrends parseFromData:resultModel.data error:&error];
            NSLog(@"-----清除开奖历史返回------\n，返回对象:%@",[clearTrends description]);
            if (self.beginDealBackBlock) {
                self.clearTrendsBackBlock(clearTrends);
            }
        }
        case GameErrorType:{
            NSError *error;
            ErrorMessage *message =[ErrorMessage parseFromData:resultModel.data error:&error];
            NSLog(@"-----游戏错误message返回------\n，返回对象:%@",[message description]);
            if (self.gameErrorMessageBlock) {
                self.gameErrorMessageBlock(message);
            }
        }
            break;
        case ServerMaintenanceType:{
            NSLog(@"-----服务器维护返回------\n，无返回对象，踢出用户");
            if (self.serverMaintenanceBlock) {
                self.serverMaintenanceBlock();
            }
        }
            break;
        case RoomOutTimeType:{
            NSLog(@"-----停留房间超时返回------\n，无返回对象，踢出用户");
            if (self.roomOutTimeBlock) {
                self.roomOutTimeBlock();
            }
        }
            break;
        case MultiUserLoginType:{
            NSLog(@"-----被其他设备挤掉返回------\n，无返回对象，踢出用户");
            if (self.multiUserBlock) {
                self.multiUserBlock();
            }
            if (self.tokenLoseEffectivenessBlock) {
                self.tokenLoseEffectivenessBlock();
            }
        }
            break;
        case ClientUnKnow:
            break;
    }
}
-(ClientAndGameResType)getClientTypemMid:(unsigned short)mid sid:(unsigned short)sid{
    if(mid ==7&&sid ==106){
        return ClientLoginResType;
    }else if (mid ==500 &&sid ==1000){
        return EnterInfoResType;
    }else if (mid ==7 &&sid ==107){
        return clientLoginerrorType;
    }else if (mid ==500 &&sid ==1001){
        return GroupInfoResType;
    }else if (mid ==500 &&sid ==1003){
        return EnterMiniGameInfoType;
    }else if (mid ==500 &&sid ==1008){
        return LevaeGroupType;
    }else if (mid ==500 &&sid ==1006){
        return UserBetResultType;
    }else if (mid ==500 &&sid ==1050){
        return RefreshUserPropsType;
    }else if (mid ==500 &&sid ==1004){
        return BeginNewRoundType;
    }else if (mid ==500 &&sid ==1005){
        return BeginSettleType;
    }else if (mid ==500 &&sid ==1007){
        return SyncAreaBetInfoType;
    }else if (mid ==500 &&sid ==1009){
        return LevaeMiniGameType;
    }else if (mid ==500 &&sid ==1010){
        return BeginDealType;
    }else if (mid ==500 &&sid ==1011){
        return ClearTrendsType;
    }else if (mid ==7 &&sid ==600){
        return GameErrorType;
    }else if (mid ==500 &&sid ==700){
        return ServerMaintenanceType;
    }else if (mid ==500 &&sid ==701){
        return RoomOutTimeType;
    }else if (mid ==500 &&sid ==702){
        return MultiUserLoginType;
    }
    return ClientUnKnow;
}
-(void)sendSocketStause:(socketStatusType)type{
    if (self.changeSocketStatusBlock) {
        self.changeSocketStatusBlock(type);
    }
}
@end
