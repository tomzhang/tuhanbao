package com.tuhanbao.base.util.exception;

import com.tuhanbao.base.Constants;
import com.tuhanbao.base.util.log.LogManager;

/**
 * 通过错误码表示的错误
 * @author tuhanbao
 *
 */
public class MyException extends RuntimeException
{
    private static final long serialVersionUID = -3500012360865491884L;
    
    private int errCode = -1;
    
    private String[] args = null;

    public MyException()
    {
        this(BaseErrorCode.ERROR);
    }
    
    public MyException(int errCode)
    {
        super();
        this.errCode = errCode;
    }
    
    public MyException(int errCode, String ... args)
    {
        super();
        this.errCode = errCode;
        this.args = args;
    }
    
    public MyException(String msg)
    {
        this(BaseErrorCode.ERROR, msg);
    }
    
    public static MyException getMyException(Throwable t)
    {
        if (t instanceof MyException)
        {
            return (MyException) t;
        }
        else
        {
        	LogManager.error(t);
            return new MyException(BaseErrorCode.ERROR, t.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return MyException.class.getName() + Constants.COLON + getMessage();
    }

    public int getErrCode()
    {
        return errCode;
    }
    
    public String[] getArgs()
    {
        return args;
    }

    @Override
    public String getMessage()
    {
        return ErrorCodeMsgManager.getErrMsg(errCode, args);
    }
}
