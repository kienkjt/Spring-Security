package com.kjt.springsecurity.service;

import com.kjt.springsecurity.entity.Document;
import com.kjt.springsecurity.entity.User;

public interface PolicyEvaluatorService {
    boolean checkDocumentAccess(User user, Document document, String action);
}
