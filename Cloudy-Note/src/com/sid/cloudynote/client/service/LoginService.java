package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.User;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
  public User login(String requestUri);
}