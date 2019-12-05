package com.example.collectdata.exception;

import com.example.collectdata.tools.ConstTools;

public class PageInitException extends Exception {

    public static final String message = ConstTools.EXCEPTION_PAGE_INIT;

    private PageInitException(String message) {
        super(message);
    }

    public static PageInitException getExceptionInstance(){
        return new PageInitException(message);
    }

}
