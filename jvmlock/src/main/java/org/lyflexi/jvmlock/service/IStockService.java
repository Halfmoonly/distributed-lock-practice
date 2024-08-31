package org.lyflexi.jvmlock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.lyflexi.jvmlock.pojo.Stock;

/**
 * @Description:
 * @Author: lyflexi
 * @project: distributed-lock-practice
 * @Date: 2024/8/31 12:09
 */
public interface IStockService extends IService<Stock> {
    public void deductJvm(Long productId);
}
