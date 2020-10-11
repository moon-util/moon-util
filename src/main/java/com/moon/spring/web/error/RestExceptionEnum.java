package com.moon.spring.web.error;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.MapUtil;
import com.moon.core.util.SetUtil;
import com.moon.data.identifier.LongSequenceIdentifier;
import org.hibernate.TransientObjectException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.Map;

import static com.moon.spring.web.error.ErrorUtil.forErrorsByBindingResult;

/**
 * @author moonsky
 */
public enum RestExceptionEnum implements RestExceptionHandler {
    // mvc 表单提交验证错误
    onBingException("org.springframework.validation.BindException") {
        @Override
        public Object toMessage(Throwable ex) { return forErrorsByBindingResult((BindException) ex); }
    },
    // Hibernate-validator 验证错误
    onConstraintViolationException(ConstraintViolationException.class) {
        @Override
        public Map<String, String> toMessage(Throwable throwable) {
            ConstraintViolationException ex = (ConstraintViolationException) throwable;
            return SetUtil.reduce(ex.getConstraintViolations(), (messages, violation, idx) -> {
                String property = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                messages.put(property, message);
                return messages;
            }, MapUtil.newHashMap());
        }
    },
    // 参数校验错误，缺少 query 参数
    onMissingServletRequestParameterException("org.springframework.web.bind.MissingServletRequestParameterException") {
        @Override
        public Object toMessage(Throwable t) {
            MissingServletRequestParameterException e = (MissingServletRequestParameterException) t;
            return String.format("缺少参数: %s", e.getParameterName());
        }
    },
    // 页面请求时，@RequestBody 参数校验错误，包含所有错误字段
    onMethodArgumentNotValidException(MethodArgumentNotValidException.class, 400) {
        @Override
        public Object toMessage(Throwable t) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) t;
            return forErrorsByBindingResult(e.getBindingResult());
        }
    },
    onHttpMessageNotReadableException("org.springframework.http.converter.HttpMessageNotReadableException",
        "参数错误，缺少: RequestBody"),
    onHttpMessageNotWritableException("org.springframework.http.converter.HttpMessageNotWritableException"),
    onHttpMediaTypeNotSupportedException("org.springframework.web.HttpMediaTypeNotSupportedException"),
    onHttpRequestMethodNotSupportedException("org.springframework.web.HttpRequestMethodNotSupportedException"),
    onTemplateInputException("org.thymeleaf.exceptions.TemplateInputException", "页面不存在: {}"),
    // 主要是在ManyToOne级联操作时遇到，
    // 比如new了一个新对象，在未保存之前将它保存进了一个新new的对象（也即不是持久态）
    onTransientObjectException(TransientObjectException.class, "{}"),
    onEntityNotFoundException("javax.persistence.EntityNotFoundException", "数据不存在", 400),
    // sql 语法错误
    onSQLSyntaxErrorException(SQLSyntaxErrorException.class, "数据库语法错误"),
    /**
     * 删除或更新时数据库对关联外键数据进行了错误操作
     * 如：当前数据被外键关联的数据没删除，就执行删除当前数据
     */
    onSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException.class, "操作失败，当前数据已被引用"),
    onMySQLTransactionRollbackException("com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException", "数据库事务错误"),
    onSQLException(SQLException.class, "数据库错误"),
    onUnsupportedOperationException(UnsupportedOperationException.class, "不支持当前操作，请联系管理员或等待支持"),
    onIllegalArgumentException(IllegalArgumentException.class, "参数错误."),
    onIllegalStateException(IllegalStateException.class, "数据错误."),
    onMalformedURLException(MalformedURLException.class, "URL格式错误：{}", 400),
    onNullPointerException(NullPointerException.class, "参数或内部数据错误(NPE): null."),
    ;

    private final static transient LongSequenceIdentifier worker = LongSequenceIdentifier.of(29, 29);

    public static String nextSerialAsString() {
        return String.valueOf(worker.nextId());
    }

    private final String classname;
    private final String text;
    private final int status;

    RestExceptionEnum(Class type) { this(type, null); }

    RestExceptionEnum(Class type, int status) { this(type, null, status); }

    RestExceptionEnum(String classname) { this(classname, null); }

    RestExceptionEnum(String classname, int status) { this(classname, null, status); }

    RestExceptionEnum(Class type, String text) { this(type.getName(), text); }

    RestExceptionEnum(Class type, String text, int status) { this(type.getName(), text, status); }

    RestExceptionEnum(String classname, String text) { this(classname, text, 500); }

    RestExceptionEnum(String classname, String text, int status) {
        ExceptionService.doRegistry(this.classname = classname, this);
        this.status = status;
        this.text = text;
    }

    public String getConstMessage() { return text; }

    @Override
    public ResponseEntity onThrowable(HttpServletRequest request, HttpServletResponse response, Throwable ex)
        throws Throwable {
        return onThrowable(ex);
    }

    public Object toMessage(Throwable throwable) {
        return StringUtil.concat("[", nextSerialAsString(), "] ",

            StringUtil.format(getConstMessage(), throwable.getMessage()));
    }

    @Override
    @SuppressWarnings("all")
    public ResponseEntity onThrowable(Throwable ex) throws Throwable {
        try {
            Object result = toMessage(ex);
            return result instanceof ResponseEntity//
                   ? (ResponseEntity) result//
                   : ResponseEntity.status(status).body(result);
        } finally {
            // TODO logger 日志
            ex.printStackTrace();
        }
    }

    public static RestExceptionHandler forType(Class type) { return forType(type.getName()); }

    public static RestExceptionHandler forType(String classname) {
        return MvcExceptionUtil.getDefaultInstance().findRestExceptionHandler(classname);
    }
}
