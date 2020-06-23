package com.giot.eco_building.utils;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Author: pyt
 * @Date: 2020/6/10 16:57
 * @Description:
 */
@Component
public class SnowflakeId implements IdentifierGenerator {
    private SnowflakeIdWorker worker;
    public SnowflakeId(){
        worker = SnowflakeIdWorker.getInstance(0l);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return worker.nextId();
    }
}
