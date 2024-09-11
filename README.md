# distributed-lock-practice

# mysql锁
MySQL自身有三种分布式锁方案：
1. InnoDB存储引擎在执行如conut=count-1的update脚本的时候会自动添加排他锁。利用这一特性，我们可以解决超卖问题，具体SQL如下：

```sql
update stock set count = count - 1 where product_id = 1 and count > 0;
```

2. for update显式加排他锁 + 普通的update语句
```sql
<select id="selectStockForUpdate" resultType="org.lyflexi.entity.Stock">
    select * from stock where product_id = #{productId} for update
</select>

<update id="updateStock">
    update stock set count = #{stock.count} where product_id = #{stock.productId}
</update>
```

3. 版本号字段
```sql
<update id="updateStockOptimistic">
    update stock
    set count = #{stock.count} ,version = version +1
    where product_id = #{stock.productId} and version = #{stock.version}
</update>
```


MySQL虽然能解决超卖的问题，但是会受限于MySQL软件本身，在高并发的场景下支持不是很理想。

# jvm锁
特别注意在使用jvm锁的时候，要习惯性的缩小同步代码块的范围 ，锁一定要加载事务方法外面:

否则T1锁释放之后，事务还未提交

此时T2获得锁，在RR隔离级别下是可以重复读取的，因此在T1-commit之前进行数据库修改操作,依然会造成线程不安全

> 最后，即使是jvm锁，依然无法解决多服务副本场景下的并发问题

# redis锁

分布式锁最佳实践

# zk锁

分布式锁最佳实践



