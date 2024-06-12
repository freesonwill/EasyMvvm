package com.luxury.lib_base.base.repository

/**
 * Description: 仓库基类
 * author       : baoyuedong
 * createTime   : 2024/6/12 13:42
 **/
class BaseRepositoryBoth<L : IRepositoryLocalSource, R : IRepositoryRemoteSource>(
    val localSource: L,
    val remoteSource: R
) : IRepository

class BaseRepositoryLocal<L : IRepositoryLocalSource>(val localSource: L) : IRepository

class BaseRepositoryRemote<R : IRepositoryRemoteSource>(val remoteSource: R) : IRepository

class BaseRepositoryNothing() : IRepository


