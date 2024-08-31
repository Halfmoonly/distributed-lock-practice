package org.lyflexi.mysqlock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lyflexi.entity.Stock;

/**
 * @Description:
 * @Author: lyflexi
 * @project: distributed-lock-practice
 * @Date: 2024/8/31 12:09
 */
public interface IStockService extends IService<Stock> {
    /**
     * mysql自动加锁场景：count = count -1写在sql中
     * @param productId
     */
    public void deductOneSql(Long productId);

    /**
     * mysql悲观锁：for update
     * @param productId
     */
    public void deductForUpdate(Long productId);

    /**
     * mysql乐观锁：版本号机制
     * @param productId
     */
    public void deductOptimistic(Long productId);

}
