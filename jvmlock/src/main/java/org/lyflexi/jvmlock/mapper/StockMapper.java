package org.lyflexi.jvmlock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lyflexi.jvmlock.pojo.Stock;

/**
 * @Author: ly
 * @Date: 2024/3/25 21:36
 */
@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    public void updateStock(@Param("stock") Stock stock) ;
}



