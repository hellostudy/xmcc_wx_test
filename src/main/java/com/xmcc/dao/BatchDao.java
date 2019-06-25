package com.xmcc.dao;

import java.util.List;

//批量增加
public interface BatchDao<T> {
    void batchInsert(List<T> list);

}
