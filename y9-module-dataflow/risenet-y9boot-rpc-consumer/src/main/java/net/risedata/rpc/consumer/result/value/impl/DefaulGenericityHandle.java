package net.risedata.rpc.consumer.result.value.impl;

import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.factory.model.ReturnType;

/**
 * @description: 处理同步的单个泛型
 * @Author lb176
 * @Date 2021/8/4==15:14
 */
public class DefaulGenericityHandle implements ReturnValueHandle {
    @Override
    public boolean isHandle(ReturnType returnType) {
        return !returnType.isReturnTypeClass();
    }

    @Override
    public Object getValue(SyncResult result, ReturnType returnType, Object[] args) {
        return result.getResult().getValue((Class<?>) args[returnType.getIndex()]);
    }


}
