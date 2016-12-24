package com.yanguan.device.util;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.Properties;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-29 11:02)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class})})
public class UpdateIntercept implements Interceptor {

    private String dialect;
    private String sqlId;
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        BaseStatementHandler delegate = (BaseStatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
        BoundSql boundSql = delegate.getBoundSql();
        String sId = mappedStatement.getId();
        if (sId.substring(sId.lastIndexOf(".") + 1).matches(sqlId)) {
            String sql=boundSql.getSql();
            int suffix = sql.lastIndexOf(",");
            ReflectUtil.setFieldValue(boundSql, "sql", sql.substring(0,suffix)+" where "+sql.substring(suffix+1));
//            ReflectUtil.setFieldValue(boundSql, "sql", sql.substring(0,prefix+4)+sql.substring(suffix+1)+" where "+sql.substring(prefix+4,suffix));
//            BoundSql updateBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql.substring(0,prefix+4)+sql.substring(suffix+1)+" where "+sql.substring(prefix+4,suffix), boundSql.getParameterMappings(), ((Map)boundSql.getParameterObject()).get("params"));
//            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), updateBoundSql);
        }
        System.out.println(boundSql.getSql());
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.dialect = properties.getProperty("dialect");
        this.sqlId = properties.getProperty("sqlId");
    }
}
