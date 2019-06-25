package com.xmcc.dao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class AbstractBatchDao<T> implements BatchDao<T> {
    @PersistenceContext
    protected EntityManager em;

    @Transactional
    @Override
    public void batchInsert(List<T> list) {
       long length=list.size();
       for(int i=0;i<length;i++){
           em.persist(list.get(i));
           if(i%100==0||i==length-1){
               em.flush();
               em.clear();
           }
       }
    }
}
